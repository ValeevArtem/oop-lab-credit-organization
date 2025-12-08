import java.time.LocalDate;

/**
 * Класс PaymentList представляет собой упорядоченный односвязный список платежей.
 * Платежи хранятся в порядке возрастания даты (от самых ранних к самым поздним).
 * Обеспечивает операции добавления, удаления, поиска, вычисления общей суммы и вывода.
 */
public class PaymentList {

    // Указатель на первый узел списка (голова). Если null — список пуст.
    private PaymentNode head;

    /**
     * Конструктор: создаёт пустой список платежей.
     */
    public PaymentList() {
        this.head = null;
    }

    /**
     * Проверяет, пуст ли список платежей.
     * @return true, если список не содержит ни одного платежа
     */
    public boolean isEmpty() {
        return head == null;
    }

    /**
     * Возвращает ссылку на первый узел списка.
     * Предназначен для внешнего итерирования (например, при сохранении в файл).
     * @return головной узел или null, если список пуст
     */
    public PaymentNode getHead() {
        return head;
    }

    /**
     * Добавляет новый платёж в список, сохраняя сортировку по дате (по возрастанию).
     * Если платеж с такой датой уже существует — он будет добавлен после всех более ранних,
     * но до более поздних (т.е. допускаются дубликаты по дате).
     * @param payment добавляемый платёж (не должен быть null)
     */
    public void add(Payment payment) {
        // Создаём новый узел для платежа
        PaymentNode newNode = new PaymentNode(payment);

        // Если список пуст ИЛИ новая дата раньше головы — вставляем в начало
        if (isEmpty() || payment.getDate().isBefore(head.getData().getDate())) {
            newNode.setNext(head);  // Новый узел указывает на старую голову
            head = newNode;         // Новый узел становится головой
        } else {
            // Иначе ищем позицию для вставки: после всех дат, строго меньших новой
            PaymentNode current = head;
            while (current.getNext() != null &&
                    current.getNext().getData().getDate().isBefore(payment.getDate())) {
                current = current.getNext();
            }
            // Вставляем newNode между current и current.getNext()
            newNode.setNext(current.getNext());
            current.setNext(newNode);
        }
    }

    /**
     * Удаляет платёж по дате (удаляется первый найденный платёж с указанной датой).
     * @param date дата платежа, который нужно удалить
     * @return true, если платёж найден и удалён; false — если не найден
     */
    public boolean remove(LocalDate date) {
        if (isEmpty()) return false;

        // Особый случай: удаление головы
        if (head.getData().getDate().equals(date)) {
            head = head.getNext();  // Голова сдвигается на следующий узел
            return true;
        }

        // Общий случай: ищем узел, ПОСЛЕ которого идёт удаляемый
        PaymentNode current = head;
        while (current.getNext() != null) {
            if (current.getNext().getData().getDate().equals(date)) {
                // "Перепрыгиваем" через удаляемый узел
                current.setNext(current.getNext().getNext());
                return true;
            }
            current = current.getNext();
        }
        return false; // Платёж с такой датой не найден
    }

    /**
     * Находит платёж по дате (первое совпадение).
     * @param date дата для поиска
     * @return объект Payment или null, если не найден
     */
    public Payment find(LocalDate date) {
        PaymentNode current = head;
        while (current != null) {
            if (current.getData().getDate().equals(date)) {
                return current.getData();
            }
            current = current.getNext();
        }
        return null;
    }

    /**
     * Вычисляет общую сумму всех платежей в списке.
     * @return сумма платежей (в денежных единицах)
     */
    public double totalSum() {
        double sum = 0;
        PaymentNode current = head;
        while (current != null) {
            sum += current.getData().getAmount(); // Накапливаем сумму
            current = current.getNext();
        }
        return sum;
    }

    /**
     * Возвращает количество платежей в списке.
     * @return число узлов (платежей)
     */
    public int size() {
        int count = 0;
        PaymentNode current = head;
        while (current != null) {
            count++;
            current = current.getNext();
        }
        return count;
    }

    /**
     * Выводит все платежи в консоль в порядке возрастания даты (как они хранятся в списке).
     * Использует метод toString() класса Payment для форматированного вывода.
     */
    public void printAll() {
        PaymentNode current = head;
        while (current != null) {
            System.out.println(current.getData()); // Автоматически вызывается Payment.toString()
            current = current.getNext();
        }
    }
}