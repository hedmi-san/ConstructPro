package constructpro.Service;

import constructpro.DTO.vehicleSystem.VehicleRental;
import constructpro.DAO.vehicleSystem.VehicleRentalDAO;
import constructpro.DAO.ConstructionSiteDAO;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class RentalRecordFormDialog extends JDialog {
    private VehicleRental rental;
    private int vehicleId;
    private Connection conn;
    private boolean saved = false;

    private VehicleRentalDAO rentalDAO;
    private ConstructionSiteDAO siteDAO;

    // Color scheme
    private static final Color DARK_BACKGROUND = new Color(45, 45, 45);
    private static final Color DARKER_BACKGROUND = new Color(35, 35, 35);
    private static final Color ACCENT_COLOR = new Color(0, 120, 215);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);
    private static final Color LABEL_COLOR = new Color(180, 180, 180);

    // Components
    private JTextField startDateField;
    private JTextField endDateField;
    private JTextField daysWorkedField;
    private JTextField transferFeeField;
    private JTextField dailyRateField;
    private JComboBox<String> siteComboBox;
    private JButton saveButton;
    private JButton cancelButton;
    private JButton calculateDaysButton;

    public RentalRecordFormDialog(Window parent, VehicleRental rental, int vehicleId, Connection conn)
            throws SQLException {
        super(parent,
                rental == null ? "Ajouter un enregistrement de location" : "Modifier l'enregistrement de location",
                ModalityType.APPLICATION_MODAL);
        this.rental = rental;
        this.vehicleId = vehicleId;
        this.conn = conn;
        this.rentalDAO = new VehicleRentalDAO(conn);
        this.siteDAO = new ConstructionSiteDAO(conn);

        initializeComponents();
        setupLayout();
        setupStyling();

        if (rental != null) {
            populateFields();
        }

        setSize(450, 450);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initializeComponents() {
        // Start date field (format: YYYY-MM-DD)
        startDateField = new JTextField(20);
        styleTextField(startDateField);
        startDateField.setToolTipText("Format: AAAA-MM-JJ (par ex., 2024-12-01)");

        // End date field (format: YYYY-MM-DD)
        endDateField = new JTextField(20);
        styleTextField(endDateField);
        endDateField.setToolTipText("Format: AAAA-MM-JJ (par ex., 2024-12-31) ou laisser vide pour en cours");

        // Days worked field
        daysWorkedField = new JTextField(20);
        styleTextField(daysWorkedField);

        // Transfer fee field
        transferFeeField = new JTextField(20);
        styleTextField(transferFeeField);
        transferFeeField.setText("0.0");

        // Daily rate field
        dailyRateField = new JTextField(20);
        styleTextField(dailyRateField);
        dailyRateField.setText("0.0");

        // Calculate days button
        calculateDaysButton = createStyledButton("Calculer les jours", new Color(40, 167, 69));
        calculateDaysButton.addActionListener(e -> calculateDays());

        // Site combo box
        siteComboBox = new JComboBox<>();
        styleComboBox(siteComboBox);
        loadSites();

        // Buttons
        saveButton = createStyledButton("Enregistrer", new Color(0, 123, 255));
        cancelButton = createStyledButton("Annuler", new Color(108, 117, 125));

        saveButton.addActionListener(e -> saveRecord());
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

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setBackground(DARKER_BACKGROUND);
        comboBox.setForeground(TEXT_COLOR);
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    }

    private void loadSites() {
        try {
            List<String> siteNames = siteDAO.getAllConstructionSitesNames();
            siteComboBox.addItem("Sélectionner un site");
            for (String siteName : siteNames) {
                siteComboBox.addItem(siteName);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors du chargement des sites: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
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

        int row = 0;

        // Start Date
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        formPanel.add(createLabel("Date de début:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(startDateField, gbc);
        row++;

        // End Date
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        formPanel.add(createLabel("Date de fin:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(endDateField, gbc);
        row++;

        // Calculate Days Button
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(calculateDaysButton, gbc);
        row++;

        // Days Worked
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(createLabel("Jours travaillés:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(daysWorkedField, gbc);
        row++;

        // Transfer Fee
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        formPanel.add(createLabel("Frais de transfert (DA):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(transferFeeField, gbc);
        row++;

        // Daily Rate
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        formPanel.add(createLabel("Tarif quotidien (DA):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(dailyRateField, gbc);
        row++;

        // Site
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        formPanel.add(createLabel("Site:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(siteComboBox, gbc);

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

    private void setupStyling() {
        // Already styled in component creation
    }

    private void populateFields() {
        if (rental != null) {
            startDateField.setText(rental.getStartDate().toString());
            if (rental.getEndDate() != null) {
                endDateField.setText(rental.getEndDate().toString());
            }
            daysWorkedField.setText(String.valueOf(rental.getDaysWorked()));
            transferFeeField.setText(String.valueOf(rental.getTransferFee()));
            dailyRateField.setText(String.valueOf(rental.getDailyRate()));

            // Set selected site
            try {
                String siteName = siteDAO.getSiteNameById(rental.getAssignedSiteId());
                if (siteName != null) {
                    siteComboBox.setSelectedItem(siteName);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void calculateDays() {
        try {
            String startDateStr = startDateField.getText().trim();
            String endDateStr = endDateField.getText().trim();

            if (startDateStr.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Veuillez d'abord entrer la date de début.",
                        "Erreur de validation",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            LocalDate startDate = LocalDate.parse(startDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate endDate;

            if (endDateStr.isEmpty()) {
                // Use current date if end date is not specified
                endDate = LocalDate.now();
            } else {
                endDate = LocalDate.parse(endDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            }

            long days = ChronoUnit.DAYS.between(startDate, endDate);
            if (days < 0) {
                JOptionPane.showMessageDialog(this,
                        "La date de fin doit être après la date de début.",
                        "Erreur de validation",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            daysWorkedField.setText(String.valueOf(days));

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Format de date invalide. Veuillez utiliser AAAA-MM-JJ (par ex., 2024-12-01)",
                    "Erreur de validation",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void saveRecord() {
        // Validation
        if (startDateField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez entrer la date de début.",
                    "Erreur de validation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (daysWorkedField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez entrer les jours travaillés.",
                    "Erreur de validation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (transferFeeField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez entrer les frais de transfert.",
                    "Erreur de validation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (dailyRateField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez entrer le tarif quotidien.",
                    "Erreur de validation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (siteComboBox.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un site.",
                    "Erreur de validation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Parse dates
            LocalDate startDate = LocalDate.parse(startDateField.getText().trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate endDate = null;
            if (!endDateField.getText().trim().isEmpty()) {
                endDate = LocalDate.parse(endDateField.getText().trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            }

            // Parse days worked
            int daysWorked = Integer.parseInt(daysWorkedField.getText().trim());

            // Parse transfer fee
            double transferFee = Double.parseDouble(transferFeeField.getText().trim());

            // Parse daily rate
            double dailyRate = Double.parseDouble(dailyRateField.getText().trim());

            // Validate dates
            if (endDate != null && endDate.isBefore(startDate)) {
                JOptionPane.showMessageDialog(this,
                        "La date de fin doit être après la date de début.",
                        "Erreur de validation",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Get site ID
            String selectedSite = (String) siteComboBox.getSelectedItem();
            int siteId = siteDAO.getSiteIdByName(selectedSite);

            // Create or update rental record
            if (rental != null) {
                // Update existing record
                rental.setStartDate(startDate);
                rental.setEndDate(endDate);
                rental.setDaysWorked(daysWorked);
                rental.setTransferFee(transferFee);
                rental.setDailyRate(dailyRate);
                rental.setAssignedSiteId(siteId);

                // Save to database
                rentalDAO.updateRentalRecord(rental);

                saved = true;
                dispose();
            } else {
                // Add new rental record
                VehicleRental newRental = new VehicleRental();
                newRental.setVehicle_id(vehicleId);
                newRental.setStartDate(startDate);
                newRental.setEndDate(endDate);
                newRental.setDaysWorked(daysWorked);
                newRental.setTransferFee(transferFee);
                newRental.setDailyRate(dailyRate);
                newRental.setAssignedSiteId(siteId);

                // Get current rental info to copy owner details
                VehicleRental currentRental = rentalDAO.getCurrentRentalInfo(vehicleId);
                if (currentRental != null) {
                    newRental.setOwnerName(currentRental.getOwnerName());
                    newRental.setOwnerPhone(currentRental.getOwnerPhone());
                    newRental.setDepositAmount(currentRental.getDepositAmount());
                } else {
                    // Default values if no previous rental exists
                    newRental.setOwnerName("");
                    newRental.setOwnerPhone("");
                    newRental.setDepositAmount(0.0);
                }

                // Save to database
                rentalDAO.addRentalRecord(newRental);

                saved = true;
                dispose();
            }

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Format de date invalide. Veuillez utiliser AAAA-MM-JJ (par ex., 2024-12-01)",
                    "Erreur de validation",
                    JOptionPane.WARNING_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Valeur numérique invalide. Veuillez entrer des nombres valides pour les jours travaillés et les frais de transfert.",
                    "Erreur de validation",
                    JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'enregistrement de la location: " + e.getMessage(),
                    "Erreur de base de données",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }

    public VehicleRental getRental() {
        return rental;
    }
}
