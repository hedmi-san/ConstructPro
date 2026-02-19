package constructpro.Service;

import constructpro.DAO.PaymentCheckDAO;
import constructpro.DAO.SalaryRecordDAO;
import constructpro.DTO.SalaryRecord;
import constructpro.DTO.Worker;
import java.awt.*;
import java.time.LocalDate;
import javax.swing.*;
import java.sql.*;

public class QuickPayDialog extends JDialog {
    private static final Color DARK_BACKGROUND = new Color(45, 45, 45);
    private PaymentCheckDAO paymentCheckDAO;
    private SalaryRecordDAO salaryRecordDAO;
    private Worker worker;
    private JLabel titleLabel;
    private JTextField paidAmountField, baseSalaryField;
    private JLabel balanceLabel;
    private JButton saveButton;
    private boolean saved = false;

    public QuickPayDialog(JFrame parent, Worker worker, Connection connection) throws SQLException {
        super(parent, "Paiement Rapide", true);

        this.paymentCheckDAO = new PaymentCheckDAO(connection);
        this.salaryRecordDAO = new SalaryRecordDAO(connection);
        this.worker = worker;
        initComponents(worker.getLastName() + " " + worker.getFirstName());
        setupLayout();
        setupActions(worker.getId());
        pack();
        setSize(500, 300);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initComponents(String workerFullName) {
        // Title
        titleLabel = new JLabel(workerFullName, SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        // Balance Label
        balanceLabel = new JLabel("", SwingConstants.CENTER);
        balanceLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        updateBalanceDisplay();

        // Text field
        baseSalaryField = createTextField();
        paidAmountField = createTextField();

        // Auto-calculate baseSalary to match pending debt if any
        try {
            SalaryRecord record = salaryRecordDAO.getSalaryRecordByWorkerId(worker.getId());
            if (record != null) {
                double balance = record.getTotalEarned() - record.getAmountPaid();
                if (balance > 0) {
                    baseSalaryField.setText(String.format("%.0f", balance));
                } else {
                    baseSalaryField.setText("0");
                }
            }
        } catch (SQLException e) {
            baseSalaryField.setText("0");
        }

        // Button
        saveButton = createButton("Enregistrer");
    }

    private void updateBalanceDisplay() {
        try {
            SalaryRecord record = salaryRecordDAO.getSalaryRecordByWorkerId(worker.getId());
            if (record != null) {
                double balance = record.getTotalEarned() - record.getAmountPaid();
                if (balance > 0) {
                    balanceLabel.setText("Dû au travailleur: " + String.format("%.0f", balance));
                    balanceLabel.setForeground(new Color(100, 255, 100)); // Green
                } else if (balance < 0) {
                    balanceLabel.setText("Avance / Dette: " + String.format("%.0f", Math.abs(balance)));
                    balanceLabel.setForeground(new Color(255, 100, 100)); // Red
                } else {
                    balanceLabel.setText("Solde: 0");
                    balanceLabel.setForeground(Color.WHITE);
                }
            }
        } catch (SQLException e) {
            balanceLabel.setText("Erreur solde");
        }
    }

    private JTextField createTextField() {
        JTextField field = new JTextField(20);
        field.setBackground(DARK_BACKGROUND);
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        return field;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(DARK_BACKGROUND);
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
        formPanel.setBackground(DARK_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Title
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(titleLabel, gbc);
        gbc.gridwidth = 1;
        row++;

        // Balance
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        formPanel.add(balanceLabel, gbc);
        gbc.gridwidth = 1;
        row++;

        // Base Salary (Work value)
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(createLabel("Salaire (Valeur travail)"), gbc);
        gbc.gridx = 1;
        formPanel.add(baseSalaryField, gbc);
        row++;

        // Paid Amount
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(createLabel("Montant Versé (Cash)"), gbc);
        gbc.gridx = 1;
        formPanel.add(paidAmountField, gbc);
        row++;

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(DARK_BACKGROUND);
        buttonPanel.add(saveButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupActions(int workerId) {
        saveButton.addActionListener(e -> {
            try {
                // Validate that fields are not empty
                if (paidAmountField.getText().trim().isEmpty() || baseSalaryField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs !");
                    return;
                }

                // Parse amounts
                double paidAmt = Double.parseDouble(paidAmountField.getText().trim());
                double baseSalary = Double.parseDouble(baseSalaryField.getText().trim());

                // Validate that amounts are not negative
                if (paidAmt < 0 || baseSalary < 0) {
                    JOptionPane.showMessageDialog(this, "Les montants ne peuvent pas être négatifs !");
                    return;
                }

                if (paidAmt == 0 && baseSalary == 0) {
                    JOptionPane.showMessageDialog(this, "Veuillez saisir un montant !");
                    return;
                }

                // Get or create salary record
                SalaryRecord record = salaryRecordDAO.getOrCreateSalaryRecord(workerId);

                // Create payment check
                LocalDate paymentDate = LocalDate.now();
                paymentCheckDAO.insertPaymentCheck(
                        record.getId(),
                        worker.getAssignedSiteID(),
                        paymentDate,
                        baseSalary,
                        paidAmt);

                // Update salary record totals
                salaryRecordDAO.updateSalaryRecordTotals(record.getId());

                saved = true;
                JOptionPane.showMessageDialog(this, "Paiement enregistré avec succès !");
                dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer une valeur numérique valide !");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement : " + ex.getMessage());
            }
        });
    }

    public boolean isSaved() {
        return saved;
    }
}
