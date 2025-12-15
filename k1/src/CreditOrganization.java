/**
 * Класс CreditOrganization — представляет кредитную организацию,
 * которая управляет коллекцией заемщиков с помощью очереди фиксированной ёмкости.
 * Обеспечивает основные операции: добавление, поиск, удаление, расчёт общей суммы кредитов,
 * а также сохранение и загрузку данных из файла.
 */
public class CreditOrganization {

    // Внутренняя очередь заемщиков, реализующая хранение и базовые операции
    private final BorrowerQueue borrowers;

    /**
     * Конструктор кредитной организации.
     * @param capacity максимальное количество заемщиков, которое может обслуживать организация
     */
    public CreditOrganization(int capacity) {
        this.borrowers = new BorrowerQueue(capacity); // Создаём очередь заданной вместимости
    }

    /**
     * Возвращает заемщиков в организации.
     * @return заемщиков
     */
    public BorrowerQueue getBorrowers() {
        return borrowers;
    }

    /**
     * Добавляет нового заемщика в организацию.
     * Заемщик помещается в конец внутренней очереди.
     * @param b объект заемщика для добавления
     * @return true, если заемщик успешно добавлен;
     *         false, если организация заполнена (достигнут лимит capacity)
     */
    public boolean addBorrower(Borrower b) {
        return borrowers.enqueue(b); // Делегируем операцию очереди
    }

    /**
     * Удаляет заемщика по его фамилии.
     * Удаление производится из любой позиции очереди (не обязательно из начала).
     * @param lastName фамилия заемщика, которого нужно удалить
     * @return true, если заемщик был найден и удалён;
     *         false, если заемщик с такой фамилией не найден
     */
    public boolean removeBorrower(String lastName) {
        return borrowers.removeByLastName(lastName); // Делегируем очередь
    }

    /**
     * Находит заемщика по фамилии.
     * @param lastName фамилия для поиска
     * @return объект Borrower, если найден; null — если не найден
     */
    public Borrower findBorrower(String lastName) {
        return borrowers.findByLastName(lastName); // Поиск делает очередь
    }

    /**
     * Рассчитывает общую сумму долгов всех текущих заемщиков в организации.
     * @return сумма кредитов (в денежных единицах, например, рублях)
     */
    public double totalCredits() {
        double sum = 0;
        // Проходим по всем заемщикам в очереди по индексу
        for (int i = 0; i < borrowers.getSize(); i++) {
            // Накапливаем сумму долга каждого заемщика
            sum += borrowers.getAt(i).getLoanAmount();
        }
        return sum;
    }

    /**
     * Сохраняет текущее состояние кредитной организации в текстовый файл.
     * Используется формат, поддерживаемый BorrowerQueue (секции [ЗАЕМЩИКИ] и [ПЛАТЕЖИ]).
     * @param filename путь к файлу для сохранения
     * @throws java.io.IOException если произошла ошибка записи (например, нет прав или диск переполнен)
     */
    public void save(String filename) throws java.io.IOException {
        borrowers.saveToFile(filename); // Делегируем сохранение очереди
    }

    /**
     * Загружает состояние кредитной организации из текстового файла.
     * Текущее содержимое полностью заменяется данными из файла.
     * @param filename путь к файлу для загрузки
     * @throws java.io.IOException если файл не найден, недоступен или имеет повреждённый формат
     */
    public void load(String filename) throws java.io.IOException {
        borrowers.loadFromFile(filename); // Делегируем загрузку очереди
    }
}