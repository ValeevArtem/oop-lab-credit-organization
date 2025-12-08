import java.time.LocalDate;

/**
 * Заготовка для курсового проекта по ООП.
 * Тема: Кредитная организация.
 * Студент: Валеев Артём Владиславович (ДЗПИж 305)
 */
public class Main {
    public static void main(String[] args) {

        System.out.println("\nТема 10: Разработка объектной программы для обработки данных кредитной организации.");
        System.out.println("Заготовка проекта создана. Готов к разработке.\n");

        CreditOrganization org = new CreditOrganization(10);

        // =====================================================================
        // === ТЕСТ 1: Добавление заемщиков и расчёт общей суммы кредитов ===
        // =====================================================================
        System.out.println("=== ТЕСТ 1: Добавление заемщиков ===");

        Borrower b1 = new Borrower("Иванов");
        b1.addPayment(new Payment(LocalDate.of(2025, 1, 10), 10000));
        b1.addPayment(new Payment(LocalDate.of(2025, 2, 10), 15000));

        Borrower b2 = new Borrower("Петров");
        b2.addPayment(new Payment(LocalDate.of(2025, 1, 15), 20000));
        b2.addPayment(new Payment(LocalDate.of(2025, 2, 15), 50000));
        b2.addPayment(new Payment(LocalDate.of(2025, 4, 15), 70000));

        Borrower b3 = new Borrower("Сидоров");
        b3.addPayment(new Payment(LocalDate.of(2025, 1, 15), 20000));

        System.out.println("Общая сумма кредитов до добавления: " + org.totalCredits());

        org.addBorrower(b1);
        org.addBorrower(b2);
        org.addBorrower(b3);

        System.out.println("Общая сумма кредитов после добавления: " + org.totalCredits());
        System.out.println();

        // =====================================================================
        // === ТЕСТ 2: Сохранение/загрузка и удаление платежа ===
        // =====================================================================
        System.out.println("=== ТЕСТ 2: Сохранение/загрузка и удаление платежа ===");

        saveAndLoadCreditOrg(org, "credits1.txt");

        System.out.println("Общая сумма кредитов Петрова: " + org.findBorrower("Петров").getPayments().totalSum());
        System.out.println("--- Удаляем платеж Петрова от 2025-01-15 ---");

        Borrower petrov = org.findBorrower("Петров");
        if (petrov != null) {
            System.out.println("Платёж за 15-01-2025 найден -> " + petrov.getPayments().find(LocalDate.of(2025, 1, 15)));
            petrov.removePayment(LocalDate.of(2025, 1, 15));
            System.out.println("Общая сумма кредитов Петрова после удаления: " + petrov.getPayments().totalSum());
        }

        saveAndLoadCreditOrg(org, "credits2.txt");
        System.out.println();

        // =====================================================================
        // === ТЕСТ 3: Удаление заемщика ===
        // =====================================================================
        System.out.println("=== ТЕСТ 3: Удаление заемщика ===");

        System.out.println("Общая сумма кредитов Петрова: " + org.findBorrower("Петров").getPayments().totalSum());
        System.out.println("--- Удаляем Петрова из списка заемщиков ---");

        org.removeBorrower("Петров");
        Borrower petrovAfterDelete = org.findBorrower("Петров");
        if (petrovAfterDelete == null) {
            System.out.println("✅ Петров успешно удалён.");
        } else {
            System.out.println("❌ Ошибка: Петров всё ещё в списке!");
        }

        System.out.println("Общая сумма кредитов после удаления Петрова: " + org.totalCredits());
        System.out.println();

        // =====================================================================
        // === ТЕСТ 4: Поиск и вывод данных заемщика ===
        // =====================================================================
        System.out.println("=== ТЕСТ 4: Поиск и вывод данных Иванова ===");

        Borrower ivanov = org.findBorrower("Иванов");
        if (ivanov != null) {
            System.out.println("✅ Найден: " + ivanov.getLastName() + ", долг: " + ivanov.getLoanAmount());
            System.out.println("Платежи Иванова:");
            ivanov.getPayments().printAll();
        } else {
            System.out.println("❌ Иванов не найден!");
        }
        System.out.println();

        // =====================================================================
        // === ТЕСТ 5: Финальное сохранение состояния после изменений ===
        // =====================================================================
        System.out.println("=== ТЕСТ 5: Финальное сохранение состояния ===");

        saveAndLoadCreditOrg(org, "credits3.txt");
    }

    /**
     * Универсальный метод для сохранения и последующей загрузки организации из файла.
     */
    public static void saveAndLoadCreditOrg(CreditOrganization org, String filename) {
        try {
            org.save(filename);
            System.out.println("💾 Сохранено в " + filename);

            CreditOrganization loaded = new CreditOrganization(10);
            loaded.load(filename);
            System.out.println("📂 Загружено. Общая сумма кредитов: " + loaded.totalCredits());

        } catch (Exception e) {
            System.err.println("❌ Ошибка при сохранении/загрузке файла " + filename + ":");
            e.printStackTrace();
        }
    }
}