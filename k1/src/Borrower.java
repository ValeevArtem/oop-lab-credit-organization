import java.time.LocalDate;

public class Borrower {

    private final String lastName;
    private double loanAmount;
    private PaymentList payments;

    public Borrower(String lastName, double loanAmount) {
        this.lastName = lastName;
        this.loanAmount = loanAmount;
        this.payments = new PaymentList();
    }

    public String getLastName() { return lastName; }
    public double getLoanAmount() { return loanAmount; }
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