import java.time.LocalDate;

/**
 * Класс Payment представляет собой отдельный платёж, совершённый заемщиком.
 * Каждый платёж характеризуется датой и суммой.
 */
public class Payment {

    // Дата совершения платежа (в формате LocalDate — без времени и часового пояса)
    private LocalDate date;

    // Сумма платежа в денежных единицах (например, рублях)
    private double amount;

    /**
     * Конструктор платёжа.
     * @param date   дата платежа
     * @param amount сумма платежа
     */
    public Payment(LocalDate date, double amount) {
        this.date = date;
        this.amount = amount;
    }

    /**
     * Возвращает дату платежа.
     * @return дата в формате java.time.LocalDate
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Возвращает сумму платежа.
     * @return числовое значение суммы (с плавающей точкой)
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Возвращает текстовое представление платежа в формате:
     * "ГГГГ-ММ-ДД: СУММА"
     * Например: "2025-01-10: 10000.00"
     * Используется для вывода в консоль или логирование.
     * @return строка с датой и суммой, отформатированная с двумя знаками после запятой
     */
    @Override
    public String toString() {
        return String.format("%s: %.2f", date, amount);
    }
}