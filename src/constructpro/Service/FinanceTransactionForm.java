package constructpro.Service;

import com.toedter.calendar.JDateChooser;
import constructpro.DTO.FinancialTransaction;
import javax.swing.*;
import java.awt.*;
import java.time.ZoneId;
import java.util.Date;

public class FinanceTransactionForm extends JDialog {
    private JDateChooser dateChooser;
    private JTextField amountField;
    private JTextField methodField;
    private JButton saveButton;
    private JButton cancelButton;
    private boolean confirmed = false;

    private FinancialTransaction transaction;

    public FinanceTransactionForm(Window parent, String title) {
        super(parent, title, ModalityType.APPLICATION_MODAL);
        initComponents();
        setupLayout();
        setupActions();

        setSize(400, 350);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        dateChooser = new JDateChooser(new Date());
        dateChooser.setDateFormatString("dd/MM/yyyy");

        amountField = new JTextField(15);

        methodField = new JTextField(15);

        saveButton = new JButton("Enregistrer");
        cancelButton = new JButton("Annuler");

        transaction = new FinancialTransaction();
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Date
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Date de paiement:"), gbc);
        gbc.gridx = 1;
        formPanel.add(dateChooser, gbc);
        row++;

        // Amount
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Montant (DA):"), gbc);
        gbc.gridx = 1;
        formPanel.add(amountField, gbc);
        row++;

        // Method
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Méthode:"), gbc);
        gbc.gridx = 1;
        formPanel.add(methodField, gbc);
        row++;

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(new JScrollPane(formPanel), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupActions() {
        saveButton.addActionListener(e -> {
            if (validateFields()) {
                populateTransaction();
                confirmed = true;
                dispose();
            }
        });

        cancelButton.addActionListener(e -> dispose());
    }

    private boolean validateFields() {
        if (dateChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this, "La date est obligatoire.");
            return false;
        }
        try {
            Double.parseDouble(amountField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Montant invalide.");
            return false;
        }
        return true;
    }

    private void populateTransaction() {
        transaction.setPaymentDate(dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        transaction.setAmount(Double.parseDouble(amountField.getText()));
        transaction.setMethod(methodField.getText());
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public FinancialTransaction getTransaction() {
        return transaction;
    }
}
