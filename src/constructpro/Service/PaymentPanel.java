package constructpro.Service;

import javax.swing.*;
import java.sql.*;
import constructpro.DTO.SalaryRecord;
import constructpro.DTO.PaymentCheck;
import constructpro.DAO.PaymentCheckDAO;
import constructpro.DAO.SalaryRecordDAO;
import constructpro.DAO.WorkerDAO;
import constructpro.DTO.Worker;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PaymentPanel extends JDialog {

    private WorkerDAO workerDAO;
    private PaymentCheckDAO paymentCheckDAO;
    private SalaryRecordDAO salaryRecordDAO;
    private JTextField dailySalaryField, workDaysField, paymentAmountField, paidAmountField;
    private JComboBox<String> paymentTypeCombo;
    private JButton saveButton, calculateButton;
    private JLabel titleLabel;
    private double dailySalary, paymentAmount, paidAmount;

    public PaymentPanel(JFrame parent, Worker worker, Connection connection) throws SQLException {
        super(parent, "Paiement - " + worker.getLastName() + " " + worker.getFirstName(), true);
        this.workerDAO = new WorkerDAO(connection);
        this.paymentCheckDAO = new PaymentCheckDAO(connection);
        this.salaryRecordDAO = new SalaryRecordDAO(connection);

        initComponents(worker.getLastName() + " " + worker.getFirstName());
        setupLayout();
        setupActions();

        pack();
        setSize(600, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initComponents(String workerFullName) {
        // Title
        titleLabel = new JLabel(workerFullName, SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        // Payment type combo
        paymentTypeCombo = new JComboBox<>(new String[]{"Par Jour", "Par Tâche"});
        paymentTypeCombo.setBackground(Color.BLACK);
        paymentTypeCombo.setForeground(Color.WHITE);
        paymentTypeCombo.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // Text fields
        dailySalaryField = createTextField();
        workDaysField = createTextField();
        paymentAmountField = createTextField();
        paidAmountField = createTextField();

        // Buttons
        saveButton = createButton("Enregistrer");
        calculateButton = createButton("Calculer");
    }

    private JTextField createTextField() {
        JTextField field = new JTextField(20);
        field.setBackground(Color.BLACK);
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        return field;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        button.setPreferredSize(new Dimension(120, 30));
        return button;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return label;
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.BLACK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(titleLabel, gbc);
        gbc.gridwidth = 1;
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(createLabel("Type de Paiement"), gbc);
        gbc.gridx = 1;
        formPanel.add(paymentTypeCombo, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(createLabel("Salaire Quotidien"), gbc);
        gbc.gridx = 1;
        formPanel.add(dailySalaryField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(createLabel("Jours de travail"), gbc);
        gbc.gridx = 1;
        formPanel.add(workDaysField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(createLabel("Montant du Paiement"), gbc);
        gbc.gridx = 1;
        formPanel.add(paymentAmountField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(createLabel("Le montant payé"), gbc);
        gbc.gridx = 1;
        formPanel.add(paidAmountField, gbc);
        row++;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.add(saveButton);
        buttonPanel.add(calculateButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupActions() {
        paymentTypeCombo.addActionListener(e -> updateFieldAvailability());

        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculatePayment();
            }
        });

        saveButton.addActionListener(e -> {
            // TODO: save logic connection with PayrollService
            dispose();
        });
    }

    private void updateFieldAvailability() {
        String type = (String) paymentTypeCombo.getSelectedItem();

        boolean isDaily = type.equals("Par Jour");
        boolean isTask = type.equals("Par Tâche");

        dailySalaryField.setEnabled(isDaily);
        workDaysField.setEnabled(isDaily);
        paymentAmountField.setEnabled(isTask);
    }

    private void calculatePayment() {
        String type = (String) paymentTypeCombo.getSelectedItem();
        try {
            if (type.equals("Par Jour")) {
                double daily = Double.parseDouble(dailySalaryField.getText().trim());
                int days = Integer.parseInt(workDaysField.getText().trim());
                paymentAmount = daily * days;
                paymentAmountField.setText(String.format("%.2f", paymentAmount));

            } else if (type.equals("Par Tâche")) {
                // TODO:
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer des valeurs valides !", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

// Accessor methods
    public double getDailySalary() {
        return parseField(dailySalaryField);
    }

    public int getWorkDays() {
        return (int) parseField(workDaysField);
    }

    public double getPaymentAmount() {
        return parseField(paymentAmountField);
    }

    public double getPaidAmount() {
        return parseField(paidAmountField);
    }

    private double parseField(JTextField field) {
        try {
            return Double.parseDouble(field.getText().trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

}
