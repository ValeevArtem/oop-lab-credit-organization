import java.time.LocalDate;

public class PaymentList {

    private PaymentNode head;

    public PaymentList() {
        this.head = null;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public PaymentNode getHead() {
        return head;
    }

    public void add(Payment payment) {
        PaymentNode newNode = new PaymentNode(payment);
        if (isEmpty() || payment.getDate().isBefore(head.getData().getDate())) {
            newNode.setNext(head);
            head = newNode;
        } else {
            PaymentNode current = head;
            while (current.getNext() != null &&
                   current.getNext().getData().getDate().isBefore(payment.getDate())) {
                current = current.getNext();
            }
            newNode.setNext(current.getNext());
            current.setNext(newNode);
        }
    }

    public boolean remove(LocalDate date) {
        if (isEmpty()) return false;
        if (head.getData().getDate().equals(date)) {
            head = head.getNext();
            return true;
        }
        PaymentNode current = head;
        while (current.getNext() != null) {
            if (current.getNext().getData().getDate().equals(date)) {
                current.setNext(current.getNext().getNext());
                return true;
            }
            current = current.getNext();
        }
        return false;
    }

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

    public double totalSum() {
        double sum = 0;
        PaymentNode current = head;
        while (current != null) {
            sum += current.getData().getAmount();
            current = current.getNext();
        }
        return sum;
    }

    public int size() {
        int count = 0;
        PaymentNode current = head;
        while (current != null) {
            count++;
            current = current.getNext();
        }
        return count;
    }

    public void printAll() {
        PaymentNode current = head;
        while (current != null) {
            System.out.println(current.getData());
            current = current.getNext();
        }
    }
}