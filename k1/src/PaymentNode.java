public class PaymentNode {

    private Payment data;
    private PaymentNode next;

    public PaymentNode(Payment payment) {
        this.data = payment;
        this.next = null;
    }

    public Payment getData() {
        return data;
    }

    public PaymentNode getNext() {
        return next;
    }

    public void setNext(PaymentNode next) {
        this.next = next;
    }
}