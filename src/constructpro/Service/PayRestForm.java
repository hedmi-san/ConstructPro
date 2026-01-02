package constructpro.Service;

import constructpro.DTO.vehicleSystem.VehicleRental;
import constructpro.DAO.vehicleSystem.VehicleRentalDAO;
import java.awt.*;
import javax.swing.*;
import java.sql.*;

public class PayRestForm extends JDialog {
    private JTextField paymentTextField;
    private JButton saveButton, cancelButton;
    private VehicleRentalDAO vehicleRentDAO;
    private Connection conn;
    private VehicleRental rental;
    private boolean saved = false;

    // Color scheme
    private static final Color DARK_BACKGROUND = new Color(45, 45, 45);
    private static final Color DARKER_BACKGROUND = new Color(35, 35, 35);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);
    private static final Color LABEL_COLOR = new Color(180, 180, 180);

    public PayRestForm(Window parent, VehicleRental rental, int vehicleId, Connection connection) {
        super(parent, "Payer le reste", ModalityType.APPLICATION_MODAL);
        this.conn = connection;
        this.rental = rental;

        vehicleRentDAO = new VehicleRentalDAO(connection);
        initializeComponents();
        setupLayout();

        setSize(455, 200);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initializeComponents() {
        paymentTextField = new JTextField(20);
        styleTextField(paymentTextField);

        saveButton = createStyledButton("Enregistrer", new Color(0, 123, 255));
        cancelButton = createStyledButton("Annuler", new Color(108, 117, 125));

        saveButton.addActionListener(e -> savePayment());
        cancelButton.addActionListener(e -> dispose());
    }

    private void styleTextField(JTextField field) {
        field.setBackground(DARKER_BACKGROUND);
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(TEXT_COLOR);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LABEL_COLOR),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(120, 35));
        return button;
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(DARK_BACKGROUND);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(DARK_BACKGROUND);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Payment amount label
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        formPanel.add(createLabel("Montant du paiement (DA):"), gbc);

        // Payment amount field
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(paymentTextField, gbc);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.setBackground(DARK_BACKGROUND);
        buttonsPanel.add(saveButton);
        buttonsPanel.add(cancelButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(LABEL_COLOR);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return label;
    }

    private void savePayment() {
        // Validation
        if (paymentTextField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez entrer le montant du paiement.",
                    "Erreur de validation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double paymentAmount = Double.parseDouble(paymentTextField.getText().trim());

            if (paymentAmount <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Le montant du paiement doit être supérieur à zéro.",
                        "Erreur de validation",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Get current deposit amount and add the new payment
            double currentDeposit = rental.getDepositAmount();
            double newTotalDeposit = currentDeposit + paymentAmount;

            // Update in database
            vehicleRentDAO.updatePaidAmount(newTotalDeposit, rental.getId());

            // Update the rental object
            rental.setDepositAmount(newTotalDeposit);

            saved = true;
            JOptionPane.showMessageDialog(this,
                    "Paiement enregistré avec succès!",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Valeur numérique invalide. Veuillez entrer un nombre valide.",
                    "Erreur de validation",
                    JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'enregistrement du paiement: " + e.getMessage(),
                    "Erreur de base de données",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
