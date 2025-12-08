import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс BorrowerQueue — очередь заемщиков на основе массива фиксированной ёмкости.
 * Поддерживает добавление, извлечение, поиск по фамилии, удаление и сериализацию в файл.
 */
public class BorrowerQueue {

    // Внутренний массив для хранения заемщиков
    private final Borrower[] array;
    // Текущее количество элементов в очереди
    private int size;
    // Максимальная вместимость очереди (неизменяемая после создания)
    private final int capacity;

    /**
     * Конструктор: создаёт очередь заданной вместимости.
     * @param capacity максимальное число заемщиков, которое может хранить очередь
     */
    public BorrowerQueue(int capacity) {
        this.capacity = capacity;               // Сохраняем максимально допустимый размер
        this.array = new Borrower[capacity];    // Создаём массив указанного размера
        this.size = 0;                          // Изначально очередь пуста
    }

    /**
     * Проверяет, пуста ли очередь.
     * @return true, если в очереди нет заемщиков
     */
    public boolean isEmpty() {
        return size == 0;  // Очередь пуста, если ни один элемент не добавлен
    }

    /**
     * Добавляет заемщика в конец очереди (аналог операции "поставить в очередь").
     * @param b заемщик для добавления
     * @return true, если добавление успешно; false, если очередь заполнена
     */
    public boolean enqueue(Borrower b) {
        if (size >= capacity) return false;  // Нельзя добавить, если достигнут лимит
        array[size++] = b;                   // Добавляем в первую свободную ячейку и увеличиваем счётчик
        return true;
    }

    /**
     * Возвращает заемщика по индексу (для внутреннего использования или отладки).
     * @param index индекс в очереди (от 0 до size-1)
     * @return заемщик или null, если индекс некорректен
     */
    public Borrower getAt(int index) {
        if (index < 0 || index >= size) return null;  // Защита от выхода за границы
        return array[index];
    }

    /**
     * Возвращает текущее количество заемщиков в очереди.
     * @return число активных элементов
     */
    public int getSize() {
        return size;  // Просто возвращаем текущий размер
    }

    /**
     * Ищет заемщика по фамилии.
     * @param lastName фамилия для поиска
     * @return найденный заемщик или null, если не найден
     */
    public Borrower findByLastName(String lastName) {
        for (int i = 0; i < size; i++) {
            if (array[i].getLastName().equals(lastName)) {
                return array[i];  // Нашли — сразу возвращаем
            }
        }
        return null;  // Не найдено ни одного совпадения
    }

    /**
     * Удаляет заемщика по фамилии (первое найденное совпадение).
     * @param lastName фамилия заемщика для удаления
     * @return true, если удаление прошло успешно; false — если заемщик не найден
     */
    public boolean removeByLastName(String lastName) {
        for (int i = 0; i < size; i++) {
            if (array[i].getLastName().equals(lastName)) {
                // Найден — удаляем сдвигом влево, начиная с позиции i
                for (int j = i; j < size - 1; j++) {
                    array[j] = array[j + 1];  // Перемещаем каждый следующий элемент на одну позицию назад
                }
                array[size - 1] = null;       // Очищаем последнюю ячейку
                size--;                       // Уменьшаем счётчик
                return true;                  // Успешно удалено
            }
        }
        return false;  // Заемщик с такой фамилией не найден
    }

    /**
     * Сохраняет текущее состояние очереди в текстовый файл.
     * Формат:
     * [ЗАЕМЩИКИ]
     * Фамилия СуммаДолга
     * ...
     *
     * [ПЛАТЕЖИ]
     * Фамилия Дата(ГГГГ-ММ-ДД) СуммаПлатежа
     * ...
     *
     * @param filename имя файла для сохранения
     * @throws IOException если произошла ошибка записи
     */
    public void saveToFile(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Записываем заголовок и данные заемщиков
            writer.println("[ЗАЕМЩИКИ]");
            for (int i = 0; i < size; i++) {
                Borrower b = array[i];
                // Формат: Фамилия Пробел СуммаДолга
                writer.println(b.getLastName() + " " + b.getLoanAmount());
            }

            // Добавляем пустую строку для читаемости
            writer.println();

            // Записываем заголовок и данные платежей
            writer.println("[ПЛАТЕЖИ]");
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            for (int i = 0; i < size; i++) {
                Borrower b = array[i];
                PaymentNode node = b.getPayments().getHead();  // Предполагается, что платежи хранятся в односвязном списке
                while (node != null) {
                    Payment p = node.getData();
                    // Формат: Фамилия Дата Сумма
                    writer.println(b.getLastName() + " " + fmt.format(p.getDate()) + " " + p.getAmount());
                    node = node.getNext();
                }
            }
        } // try-with-resources автоматически закрывает файл
    }

    /**
     * Загружает состояние очереди из текстового файла, созданного методом saveToFile.
     * Полностью перезаписывает текущее содержимое очереди.
     *
     * @param filename имя файла для загрузки
     * @throws IOException если файл не найден, повреждён или имеет неверный формат
     */
    public void loadFromFile(String filename) throws IOException {
        // Очищаем текущее состояние очереди
        for (int i = 0; i < size; i++) {
            array[i] = null;
        }
        size = 0;  // Сбрасываем счётчик

        // Считываем все строки файла в список для удобства обработки
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }

        // Находим начало секций по маркерам
        int borrowersStart = -1, paymentsStart = -1;
        for (int i = 0; i < lines.size(); i++) {
            String trimmed = lines.get(i).trim();
            if (trimmed.equals("[ЗАЕМЩИКИ]")) borrowersStart = i + 1;
            if (trimmed.equals("[ПЛАТЕЖИ]")) paymentsStart = i + 1;
        }

        // Проверка целостности файла
        if (borrowersStart == -1 || paymentsStart == -1) {
            throw new IOException("Файл повреждён: отсутствуют секции [ЗАЕМЩИКИ] или [ПЛАТЕЖИ]");
        }

        // 1. Загружаем заемщиков из секции [ЗАЕМЩИКИ]
        for (int i = borrowersStart; i < paymentsStart - 1; i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;  // Пропускаем пустые строки

            // Разделяем строку на фамилию и долг (ограничиваем до 2 частей на случай пробелов в фамилии — но здесь фамилия без пробелов)
            String[] parts = line.split(" ", 2);
            if (parts.length != 2) continue; // Некорректная строка — пропускаем

            String lastName = parts[0];
            // Создаём нового заемщика и добавляем в очередь, если есть место
            if (size < capacity) {
                array[size] = new Borrower(lastName);
                size++;
            }
            // Игнорируем лишних заемщиков, если файл содержит больше, чем capacity
        }

        // 2. Загружаем платежи из секции [ПЛАТЕЖИ]
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = paymentsStart; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;

            // Разделяем на 3 части: фамилия, дата, сумма
            String[] parts = line.split(" ", 3);
            if (parts.length != 3) continue; // Некорректный формат — пропускаем

            String lastName = parts[0];
            LocalDate date = LocalDate.parse(parts[1], fmt); // Парсим дату в формате yyyy-MM-dd
            double amount = Double.parseDouble(parts[2]);   // Преобразуем сумму в число

            // Ищем заемщика с такой фамилией в уже загруженной очереди
            Borrower target = null;
            for (int j = 0; j < size; j++) {
                if (array[j].getLastName().equals(lastName)) {
                    target = array[j];
                    break;
                }
            }

            // Если заемщик найден — добавляем ему платёж
            if (target != null) {
                target.addPayment(new Payment(date, amount));
            }
        }
    }
}