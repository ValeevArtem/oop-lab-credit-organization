import java.time.LocalDate;

public class Borrower {

    private final String lastName;
    private final PaymentList payments;

    public Borrower(String lastName) {
        this.lastName = lastName;
        this.payments = new PaymentList();
    }

    public double getLoanAmount() {
        return payments.totalSum();
    }

    public String getLastName() {
        return lastName;
    }

    public void addPayment(Payment p) {
        payments.add(p);
    }

    public boolean removePayment(LocalDate date) {
        return payments.remove(date);
    }

    public PaymentList getPayments() {
        return payments;
    }
}