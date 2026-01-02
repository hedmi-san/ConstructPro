package constructpro.Service;

import javax.swing.*;
import java.sql.*;
import constructpro.DAO.PaymentCheckDAO;
import constructpro.DAO.ConstructionSiteDAO;
import constructpro.DAO.SalaryRecordDAO;
import constructpro.DTO.PaymentCheck;
import constructpro.DTO.SalaryRecord;
import com.toedter.calendar.JDateChooser;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.awt.*;

public class ModifyPaymentCheck extends JDialog {

    private JDateChooser dateChooser;
    private JTextField baseSalaryField, paidAmountField;
    private JButton saveButton, cancelButton;
    private PaymentCheckDAO checkDAO;
    private ConstructionSiteDAO siteDAO;
    private PaymentCheck check;

    private boolean saved = false;
    private SalaryRecordDAO salaryRecordDAO;
    private SalaryRecord salaryRecord;
    private static final Color DARK_BACKGROUND = new Color(45, 45, 45);

    public ModifyPaymentCheck(Window parent, PaymentCheck paymentCheck, Connection connection) throws SQLException {
        super(parent, "Modifier le Cheque", ModalityType.APPLICATION_MODAL);
        this.checkDAO = new PaymentCheckDAO(connection);
        this.siteDAO = new ConstructionSiteDAO(connection);
        this.salaryRecordDAO = new SalaryRecordDAO(connection);
        this.check = paymentCheck;
        this.salaryRecord = salaryRecordDAO.getSalaryRecordById(paymentCheck.getSalaryrecordId());

        initComponents();
        setupLayout();
        setupActions();
        loadFormData();

        setSize(500, 400);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(DARK_BACKGROUND);
    }

    private void initComponents() {
        // Initialize components
        dateChooser = new JDateChooser();
        baseSalaryField = new JTextField(15);
        paidAmountField = new JTextField(15);
        saveButton = new JButton("Enregistrer");
        cancelButton = new JButton("Annuler");

        // Style components
        styleTextField(baseSalaryField);
        styleTextField(paidAmountField);
        styleButton(saveButton);
        styleButton(cancelButton);
        styleDateChooser(dateChooser);

    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));

        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(DARK_BACKGROUND);
        JLabel titleLabel = new JLabel("Modifier le Paiement");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titlePanel.add(titleLabel);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(DARK_BACKGROUND);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;


        // Date
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(createLabel("Date de Paiement:"), gbc);
        gbc.gridx = 1;
        formPanel.add(dateChooser, gbc);

        // Base Salary
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(createLabel("Montant du Paiement:"), gbc);
        gbc.gridx = 1;
        formPanel.add(baseSalaryField, gbc);

        // Paid Amount
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(createLabel("Le montant payé:"), gbc);
        gbc.gridx = 1;
        formPanel.add(paidAmountField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(DARK_BACKGROUND);
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupActions() {
        saveButton.addActionListener(e -> savePaymentCheck());
        cancelButton.addActionListener(e -> dispose());
    }

    private void loadFormData() {
        // Set date
        if (check.getPaymentDay() != null) {
            dateChooser.setDate(Date.from(check.getPaymentDay()
                    .atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        // Set amounts
        baseSalaryField.setText(String.valueOf(check.getBaseSalary()));
        paidAmountField.setText(String.valueOf(check.getPaidAmount()));
    }

    private void savePaymentCheck() {
        try {
            // Validate inputs

            if (dateChooser.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner une date");
                return;
            }

            if (baseSalaryField.getText().trim().isEmpty() || paidAmountField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs");
                return;
            }

            // Get values
            LocalDate paymentDate = dateChooser.getDate().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate();
            double baseSalary = Double.parseDouble(baseSalaryField.getText().trim());
            double paidAmount = Double.parseDouble(paidAmountField.getText().trim());


            // Update payment check
            check.setPaymentDay(paymentDate);
            check.setBaseSalary(baseSalary);
            check.setPaidAmount(paidAmount);
            checkDAO.updatePaymentCheck(check);

           

            saved = true;
            JOptionPane.showMessageDialog(this,
                    "Paiement modifié avec succès",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez entrer des valeurs numériques valides",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de la modification: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        return label;
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(Color.BLACK);
        comboBox.setFont(new Font("Arial", Font.PLAIN, 14));
    }

    private void styleTextField(JTextField field) {
        field.setBackground(Color.WHITE);
        field.setForeground(Color.BLACK);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setCaretColor(Color.BLACK);
    }

    private void styleDateChooser(JDateChooser chooser) {
        chooser.setBackground(Color.WHITE);
        chooser.setForeground(Color.BLACK);
        chooser.setFont(new Font("Arial", Font.PLAIN, 14));
        chooser.getJCalendar().setBackground(Color.WHITE);
    }

    private void styleButton(JButton button) {
        button.setPreferredSize(new Dimension(120, 35));
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        button.setFont(new Font("Arial", Font.PLAIN, 14));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.DARK_GRAY);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.BLACK);
            }
        });
    }


}
