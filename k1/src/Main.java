import java.time.LocalDate;

/**
 * Заготовка для курсового проекта по ООП.
 * Тема: Кредитная организация.
 * Студент: Валеев Артём Владиславович (ДЗПИж 305)
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Тема 10: Разработка объектной программы для обработки данных кредитной организации.");
        System.out.println("Заготовка проекта создана. Готов к разработке.");

        CreditOrganization org = new CreditOrganization(10);

        // === 1. Добавление заемщиков ===
        Borrower b1 = new Borrower("Иванов", 100000);
        b1.addPayment(new Payment(LocalDate.of(2025, 1, 10), 10000));
        b1.addPayment(new Payment(LocalDate.of(2025, 2, 10), 15000));

        Borrower b2 = new Borrower("Петров", 200000);
        b2.addPayment(new Payment(LocalDate.of(2025, 1, 15), 20000));

        Borrower b3 = new Borrower("Сидоров", 300000);
        b2.addPayment(new Payment(LocalDate.of(2025, 1, 15), 20000));

        System.out.println("Общая сумма кредитов до добавления: " + org.totalCredits());

        org.addBorrower(b1);
        org.addBorrower(b2);
        org.addBorrower(b3);

        System.out.println("Общая сумма кредитов после добавления: " + org.totalCredits());
        // Ожидаем: 300_000

        // === 2. Тест сохранения/загрузки (полный дамп) ===
        saveAndLoadCreditOrg(org, "credits1.txt");

        // === 3. Удаление заемщика ===
        System.out.println("\n--- Удаляем Петрова ---");
        org.removeBorrower("Петров");
        Borrower petrovAfterDelete = org.findBorrower("Петров");
        if (petrovAfterDelete == null) {
            System.out.println("✅ Петров успешно удалён.");
        } else {
            System.out.println("❌ Ошибка: Петров всё ещё в списке!");
        }

        System.out.println("Общая сумма кредитов после удаления Петрова: " + org.totalCredits());
        // Ожидаем: 100_000

        // === 4. Поиск и вывод данных Иванова ===
        Borrower ivanov = org.findBorrower("Иванов");
        if (ivanov != null) {
            System.out.println("✅ Найден: " + ivanov.getLastName() + ", долг: " + ivanov.getLoanAmount());
            System.out.println("Платежи Иванова:");
            ivanov.getPayments().printAll();
        } else {
            System.out.println("❌ Иванов не найден!");
        }

        // === 5. Финальное сохранение (после удаления) ===
        saveAndLoadCreditOrg(org, "credits2.txt");
    }

    /**
     * Универсальный метод для сохранения и последующей загрузки организации из файла.
     */
    public static void saveAndLoadCreditOrg(CreditOrganization org, String filename) {
        try {
            org.save(filename);
            System.out.println("\n💾 Сохранено в " + filename);

            CreditOrganization loaded = new CreditOrganization(10);
            loaded.load(filename);
            System.out.println("📂 Загружено. Общая сумма кредитов: " + loaded.totalCredits());

        } catch (Exception e) {
            System.err.println("❌ Ошибка при сохранении/загрузке файла " + filename + ":");
            e.printStackTrace();
        }
    }
}