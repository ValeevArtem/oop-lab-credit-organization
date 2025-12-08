public class CreditOrganization {

    private final BorrowerQueue borrowers;

    public CreditOrganization(int capacity) {
        this.borrowers = new BorrowerQueue(capacity);
    }

    public boolean addBorrower(Borrower b) {
        return borrowers.enqueue(b);
    }

    public boolean removeBorrower(String lastName) {
        return borrowers.removeByLastName(lastName);
    }

    public Borrower findBorrower(String lastName) {
        return borrowers.findByLastName(lastName);
    }

    public double totalCredits() {
        double sum = 0;
        for (int i = 0; i < borrowers.getSize(); i++) {
            sum += borrowers.getAt(i).getLoanAmount();
        }
        return sum;
    }

    public void save(String filename) throws java.io.IOException {
        borrowers.saveToFile(filename);
    }

    public void load(String filename) throws java.io.IOException {
        borrowers.loadFromFile(filename);
    }
}