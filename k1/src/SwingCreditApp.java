import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class SwingCreditApp {

    private final CreditOrganization organization = new CreditOrganization(20);
    private JFrame frame;
    private JList<String> borrowersList;
    private JList<String> paymentsList;
    private JTextField lastNameField;
    private JTextField dateField;
    private JTextField amountField;
    private JLabel totalLabel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SwingCreditApp::new);
    }

    /**
     * Конструктор класса. Инициализирует графический интерфейс и обновляет данные.
     */
    public SwingCreditApp() {
        initializeGUI();        // Создание и настройка элементов интерфейса
        refreshBorrowersList(); // Загрузка списка заемщиков в интерфейс
        updateTotalLabel();     // Отображение общей суммы кредитов
    }

    /**
     * Инициализирует графический интерфейс приложения:
     * - Создает основное окно и панели
     * - Настраивает поля ввода, кнопки и списки
     * - Определяет обработчики событий
     * - Отображает окно по центру экрана
     */
    private void initializeGUI() {

        frame = new JFrame("Кредитная организация — Курсовой проект (Валеев А.В.)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 500);
        frame.setLayout(new BorderLayout());

        // === Верхняя панель: добавление заемщика ===
        JPanel topPanel = new JPanel(new FlowLayout());
        lastNameField = new JTextField(15);
        lastNameField.setToolTipText("Фамилия заемщика");
        JButton addBorrowerBtn = new JButton("➕Добавить заемщика");
        addBorrowerBtn.addActionListener(this::addBorrower);
        topPanel.add(new JLabel("Заемщик:"));
        topPanel.add(lastNameField);
        topPanel.add(addBorrowerBtn);

        // === Центр: списки ===
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(250);

        // Слева: заемщики
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Заемщики"));
        borrowersList = new JList<>();
        borrowersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        leftPanel.add(new JScrollPane(borrowersList), BorderLayout.CENTER);
        JButton removeBorrowerBtn = new JButton("Удалить заемщика");
        removeBorrowerBtn.addActionListener(this::removeBorrower);
        leftPanel.add(removeBorrowerBtn, BorderLayout.SOUTH);

        // Справа: платежи
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Платежи"));
        paymentsList = new JList<>();
        rightPanel.add(new JScrollPane(paymentsList), BorderLayout.CENTER);

        JPanel paymentInputPanel = new JPanel(new FlowLayout());
        dateField = new JTextField(10);
        dateField.setToolTipText("ГГГГ-ММ-ДД");
        amountField = new JTextField(10);
        JButton addPaymentBtn = new JButton("➕Добавить платёж");
        addPaymentBtn.addActionListener(this::addPayment);
        JButton removePaymentBtn = new JButton("Удалить платёж");
        removePaymentBtn.addActionListener(this::removePayment);
        paymentInputPanel.add(new JLabel("Дата: "));
        paymentInputPanel.add(dateField);
        paymentInputPanel.add(new JLabel("Сумма: "));
        paymentInputPanel.add(amountField);
        paymentInputPanel.add(addPaymentBtn);
        paymentInputPanel.add(removePaymentBtn);
        rightPanel.add(paymentInputPanel, BorderLayout.SOUTH);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);

        // === Нижняя панель: действия и итог ===
        JPanel bottomPanel = new JPanel(new FlowLayout());
        JButton saveBtn = new JButton("💾 Сохранить");
        saveBtn.addActionListener(this::saveToFile);
        JButton loadBtn = new JButton("📂 Загрузить");
        loadBtn.addActionListener(this::loadFromFile);
        totalLabel = new JLabel("Общая сумма кредитов: 0");
        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD, 14f));
        bottomPanel.add(saveBtn);
        bottomPanel.add(loadBtn);
        bottomPanel.add(totalLabel);

        // Сборка
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(splitPane, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        // Слушатель выбора заемщика
        borrowersList.addListSelectionListener(this::onBorrowerSelected);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Обработчик кнопки "Добавить заемщика":
     * - Проверяет корректность введенной фамилии
     * - Проверяет уникальность заемщика
     * - Добавляет нового заемщика в организацию
     * - Обновляет интерфейс и очищает поле ввода
     */
    private void addBorrower(ActionEvent e) {
        String name = lastNameField.getText().trim();
        if (name.isEmpty()) {
            showError("Пожалуйста, введите фамилию заемщика!");
            return;
        }
        if (organization.findBorrower(name) != null) {
            showError("Заемщик с такой фамилией уже существует, пожалуйста, проверьте список!");
            return;
        }
        organization.addBorrower(new Borrower(name));
        refreshBorrowersList();
        lastNameField.setText("");
        updateTotalLabel();
    }

    /**
     * Обработчик кнопки "Удалить заемщика":
     * - Проверяет выбор заемщика в списке
     * - Удаляет выбранного заемщика из организации
     * - Обновляет списки и очищает платежи
     */
    private void removeBorrower(ActionEvent e) {
        String selected = borrowersList.getSelectedValue();
        if (selected == null) {
            showError("Пожалуйста, выберите заемщика для удаления");
            return;
        }
        organization.removeBorrower(selected);
        refreshBorrowersList();
        paymentsList.setListData(new String[0]);
        updateTotalLabel();
    }

    /**
     * Обработчик кнопки "Добавить платёж":
     * - Проверяет выбор заемщика
     * - Валидирует введенные дату и сумму
     * - Добавляет платеж выбранному заемщику
     * - Обновляет список платежей и общую сумму
     */
    private void addPayment(ActionEvent e) {
        String borrower = borrowersList.getSelectedValue();
        if (borrower == null) {
            showError("Сначала выберите заемщика!");
            return;
        }
        try {
            LocalDate date = LocalDate.parse(dateField.getText());
            double amount = Double.parseDouble(amountField.getText());
            Borrower b = organization.findBorrower(borrower);
            if (b != null) {
                b.addPayment(new Payment(date, amount));
                loadPaymentsForBorrower(borrower);
                dateField.setText("");
                amountField.setText("");
                updateTotalLabel();
            }
        } catch (DateTimeParseException dtpe) {
            showError("Неверный формат даты! Используйте ГГГГ-ММ-ДД");
        } catch (NumberFormatException nfe) {
            showError("Сумма должна быть числом!");
        }
    }

    /**
     * Обработчик кнопки "Удалить платёж":
     * - Проверяет выбор заемщика и платежа
     * - Удаляет платеж по дате из истории заемщика
     * - Обновляет интерфейс и пересчитывает итоги
     */
    private void removePayment(ActionEvent e) {
        String borrower = borrowersList.getSelectedValue();
        String paymentStr = paymentsList.getSelectedValue();
        if (borrower == null || paymentStr == null) {
            showError("Пожалуйста, выберите платёж для удаления!");
            return;
        }
        try {
            String datePart = paymentStr.split(":")[0].trim();
            LocalDate date = LocalDate.parse(datePart);
            Borrower b = organization.findBorrower(borrower);
            if (b != null) {
                b.removePayment(date);
                loadPaymentsForBorrower(borrower);
                updateTotalLabel();
            }
        } catch (Exception ex) {
            showError("Ошибка при удалении платежа");
        }
    }

    /**
     * Обработчик кнопки "Сохранить":
     * - Открывает диалог выбора файла
     * - Сохраняет данные организации в текстовый файл
     * - Отображает результат операции
     */
    private void saveToFile(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("credits.txt")); // .txt вместо .dat
        if (chooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            try {
                organization.save(chooser.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(frame, "✅ Сохранено!", "Успех", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                showError("Ошибка сохранения: " + ex.getMessage());
            }
        }
    }

    /**
     * Обработчик кнопки "Загрузить":
     * - Открывает диалог выбора файла
     * - Загружает данные организации из файла
     * - Полностью обновляет интерфейс
     */
    private void loadFromFile(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            try {
                organization.load(chooser.getSelectedFile().getAbsolutePath());
                refreshBorrowersList();
                paymentsList.setListData(new String[0]);
                updateTotalLabel();
                JOptionPane.showMessageDialog(frame, "✅ Загружено!", "Успех", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                showError("Ошибка загрузки: " + ex.getMessage());
            }
        }
    }

    /**
     * Обработчик выбора заемщика в списке:
     * - Срабатывает при изменении выделения в списке заемщиков
     * - Загружает историю платежей выбранного заемщика
     * - Фильтрует промежуточные события выбора
     */
    private void onBorrowerSelected(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            String selected = borrowersList.getSelectedValue();
            if (selected != null) {
                loadPaymentsForBorrower(selected);
            }
        }
    }

    /**
     * Обновляет список заемщиков в интерфейсе:
     * - Получает актуальный список из организации
     * - Формирует массив строк для отображения
     * - Обновляет JList заемщиков
     */
    private void refreshBorrowersList() {
        BorrowerQueue queue = organization.getBorrowers();
        int size = queue.getSize();
        String[] names = new String[size];
        for (int i = 0; i < size; i++) {
            Borrower b = queue.getAt(i);
            names[i] = (b != null) ? b.getLastName() : "";
        }
        borrowersList.setListData(names);
    }

    /**
     * Загружает платежи выбранного заемщика:
     * - Находит заемщика по фамилии
     * - Формирует строки для отображения платежей (дата: сумма)
     * - Обновляет JList платежей
     */
    private void loadPaymentsForBorrower(String name) {
        Borrower b = organization.findBorrower(name);
        if (b != null) {
            PaymentList list = b.getPayments();
            int count = list.size();
            String[] paymentStrings = new String[count];
            PaymentNode current = list.getHead();
            int i = 0;
            while (current != null && i < count) {
                Payment p = current.getData();
                paymentStrings[i] = p.getDate() + ": " + p.getAmount();
                current = current.getNext();
                i++;
            }
            paymentsList.setListData(paymentStrings);
        }
    }

    /**
     * Обновляет метку общей суммы кредитов:
     * - Получает актуальную сумму из организации
     * - Форматирует и отображает значение
     */
    private void updateTotalLabel() {
        totalLabel.setText("Общая сумма кредитов: " + organization.totalCredits());
    }

    /**
     * Отображает диалоговое окно с ошибкой:
     * - Создает модальное окно с заданным сообщением
     * - Используется для всех валидаций в приложении
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Ошибка", JOptionPane.PLAIN_MESSAGE);
    }
}