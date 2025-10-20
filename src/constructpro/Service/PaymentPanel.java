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
    private JButton saveButton, calculateButton;
    private JLabel titleLabel;
    
    public PaymentPanel(JFrame parent,Worker worker,Connection connection) throws SQLException {
        super(parent, "Histoire", true);
        this.workerDAO = new WorkerDAO(connection);
        this.paymentCheckDAO = new PaymentCheckDAO(connection);
        this.salaryRecordDAO = new SalaryRecordDAO(connection);
        initComponents(worker.getLastName()+" "+ worker.getFirstName());
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
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);

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
        button.setPreferredSize(new Dimension(120, 28));
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

        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(titleLabel, gbc);
        gbc.gridwidth = 1;
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Salaire Quotidien"), gbc);
        gbc.gridx = 1;
        formPanel.add(dailySalaryField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Jours de travail"), gbc);
        gbc.gridx = 1;
        formPanel.add(workDaysField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Montant du Paiement"), gbc);
        gbc.gridx = 1;
        formPanel.add(paymentAmountField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
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
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculatePayment();
            }
        });

        saveButton.addActionListener(e -> {
            //TODO:
            dispose();
        });
    }

    private void calculatePayment() {
        try {
            double daily = Double.parseDouble(dailySalaryField.getText().trim());
            int days = Integer.parseInt(workDaysField.getText().trim());
            double total = daily * days;
            paymentAmountField.setText(String.format("%.2f", total));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer des valeurs valides !");
        }
    }

    public double getDailySalary() {
        return Double.parseDouble(dailySalaryField.getText().trim());
    }

    public int getWorkDays() {
        return Integer.parseInt(workDaysField.getText().trim());
    }

    public double getPaymentAmount() {
        return Double.parseDouble(paymentAmountField.getText().trim());
    }

    public double getPaidAmount() {
        return Double.parseDouble(paidAmountField.getText().trim());
    }
}
