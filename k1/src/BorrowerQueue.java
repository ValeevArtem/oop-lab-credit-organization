import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BorrowerQueue {

    private final Borrower[] array;
    private int size;
    private final int capacity;

    public BorrowerQueue(int capacity) {
        this.capacity = capacity;
        this.array = new Borrower[capacity];
        this.size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean enqueue(Borrower b) {
        if (size >= capacity) return false;
        array[size++] = b;
        return true;
    }

    public Borrower dequeue() {
        if (isEmpty()) return null;
        Borrower first = array[0];
        // Сдвиг влево
        for (int i = 0; i < size - 1; i++) {
            array[i] = array[i + 1];
        }
        array[size - 1] = null;
        size--;
        return first;
    }

    public Borrower getAt(int index) {
        if (index < 0 || index >= size) return null;
        return array[index];
    }

    public int getSize() {
        return size;
    }

    public Borrower findByLastName(String lastName) {
        for (int i = 0; i < size; i++) {
            if (array[i].getLastName().equals(lastName)) {
                return array[i];
            }
        }
        return null;
    }

    public boolean removeByLastName(String lastName) {
        for (int i = 0; i < size; i++) {
            if (array[i].getLastName().equals(lastName)) {
                // Сдвиг влево от i
                for (int j = i; j < size - 1; j++) {
                    array[j] = array[j + 1];
                }
                array[size - 1] = null;
                size--;
                return true;
            }
        }
        return false;
    }

    // === Сохранение в текстовом формате ===
    public void saveToFile(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Секция заемщиков
            writer.println("[ЗАЕМЩИКИ]");
            for (int i = 0; i < size; i++) {
                Borrower b = array[i];
                writer.println(b.getLastName() + " " + b.getLoanAmount());
            }

            // Пустая строка для читаемости
            writer.println();

            // Секция платежей
            writer.println("[ПЛАТЕЖИ]");
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            for (int i = 0; i < size; i++) {
                Borrower b = array[i];
                PaymentNode node = b.getPayments().getHead();
                while (node != null) {
                    Payment p = node.getData();
                    writer.println(b.getLastName() + " " + fmt.format(p.getDate()) + " " + p.getAmount());
                    node = node.getNext();
                }
            }
        }
    }

    // === Загрузка из текстового формата ===
    public void loadFromFile(String filename) throws IOException {
        // Очистка текущего состояния
        for (int i = 0; i < size; i++) {
            array[i] = null;
        }
        size = 0;

        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }

        // Находим позиции секций
        int borrowersStart = -1, paymentsStart = -1;
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).trim().equals("[ЗАЕМЩИКИ]")) borrowersStart = i + 1;
            if (lines.get(i).trim().equals("[ПЛАТЕЖИ]")) paymentsStart = i + 1;
        }

        if (borrowersStart == -1 || paymentsStart == -1) {
            throw new IOException("Файл повреждён: отсутствуют секции [ЗАЕМЩИКИ] или [ПЛАТЕЖИ]");
        }

        // 1. Загружаем заемщиков
        for (int i = borrowersStart; i < paymentsStart - 1; i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split(" ", 2);
            if (parts.length != 2) continue;

            String lastName = parts[0];
            double loanAmount = Double.parseDouble(parts[1]);

            if (size < capacity) {
                array[size] = new Borrower(lastName, loanAmount);
                size++;
            }
        }

        // 2. Загружаем платежи
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = paymentsStart; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split(" ", 3);
            if (parts.length != 3) continue;

            String lastName = parts[0];
            LocalDate date = LocalDate.parse(parts[1], fmt);
            double amount = Double.parseDouble(parts[2]);

            // Находим заемщика по фамилии
            Borrower target = null;
            for (int j = 0; j < size; j++) {
                if (array[j].getLastName().equals(lastName)) {
                    target = array[j];
                    break;
                }
            }

            if (target != null) {
                target.addPayment(new Payment(date, amount));
            }
        }
    }
}