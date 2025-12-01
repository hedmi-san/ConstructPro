package constructpro.Service;

import constructpro.DTO.vehicleSystem.Maintainance;
import constructpro.DAO.vehicleSystem.MaintainanceDAO;
import constructpro.DAO.ConstructionSiteDAO;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class MaintenanceFormDialog extends JDialog {
    private Maintainance maintainance;
    private int vehicleId;
    private Connection conn;
    private boolean saved = false;

    private MaintainanceDAO maintainanceDAO;
    private ConstructionSiteDAO siteDAO;

    // Color scheme
    private static final Color DARK_BACKGROUND = new Color(45, 45, 45);
    private static final Color DARKER_BACKGROUND = new Color(35, 35, 35);
    private static final Color ACCENT_COLOR = new Color(0, 120, 215);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);
    private static final Color LABEL_COLOR = new Color(180, 180, 180);

    // Components
    private JTextField typeField;
    private JTextField dateField;
    private JTextField costField;
    private JComboBox<String> siteComboBox;
    private JButton saveButton;
    private JButton cancelButton;

    public MaintenanceFormDialog(Window parent, Maintainance maintainance, int vehicleId, Connection conn)
            throws SQLException {
        super(parent, maintainance == null ? "Add Maintenance Record" : "Edit Maintenance Record",
                ModalityType.APPLICATION_MODAL);
        this.maintainance = maintainance;
        this.vehicleId = vehicleId;
        this.conn = conn;
        this.maintainanceDAO = new MaintainanceDAO(conn);
        this.siteDAO = new ConstructionSiteDAO(conn);

        initializeComponents();
        setupLayout();
        setupStyling();

        if (maintainance != null) {
            populateFields();
        }

        setSize(450, 350);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initializeComponents() {
        // Type field
        typeField = new JTextField(20);
        styleTextField(typeField);

        // Date field (format: YYYY-MM-DD)
        dateField = new JTextField(20);
        styleTextField(dateField);
        dateField.setToolTipText("Format: YYYY-MM-DD (e.g., 2024-12-01)");

        // Cost field
        costField = new JTextField(20);
        styleTextField(costField);

        // Site combo box
        siteComboBox = new JComboBox<>();
        styleComboBox(siteComboBox);
        loadSites();

        // Buttons
        saveButton = createStyledButton("Save", new Color(0, 123, 255));
        cancelButton = createStyledButton("Cancel", new Color(108, 117, 125));

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

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setBackground(DARKER_BACKGROUND);
        comboBox.setForeground(TEXT_COLOR);
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(100, 35));
        return button;
    }

    private void loadSites() {
        try {
            List<String> siteNames = siteDAO.getAllConstructionSitesNames();
            siteComboBox.addItem("Select Site");
            for (String siteName : siteNames) {
                siteComboBox.addItem(siteName);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading sites: " + e.getMessage(),
                    "Error",
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

        // Maintenance Type
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        formPanel.add(createLabel("Maintenance Type:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(typeField, gbc);
        row++;

        // Date
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        formPanel.add(createLabel("Repair Date:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(dateField, gbc);
        row++;

        // Cost
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        formPanel.add(createLabel("Cost (DH):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(costField, gbc);
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
        if (maintainance != null) {
            typeField.setText(maintainance.getMaintainanceType());
            dateField.setText(maintainance.getRepair_date().toString());
            costField.setText(String.valueOf(maintainance.getRepairCost()));

            try {
                String siteName = siteDAO.getSiteNameById(maintainance.getAssignedSiteId());
                if (siteName != null) {
                    siteComboBox.setSelectedItem(siteName);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveRecord() {
        // Validation
        if (typeField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter maintenance type.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (dateField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter repair date.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (costField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter cost.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (siteComboBox.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a site.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Parse date
            LocalDate repairDate = LocalDate.parse(dateField.getText().trim(), DateTimeFormatter.ISO_LOCAL_DATE);

            // Parse cost
            double cost = Double.parseDouble(costField.getText().trim());

            // Get site ID
            String selectedSite = (String) siteComboBox.getSelectedItem();
            int siteId = siteDAO.getSiteIdByName(selectedSite);

            // Create or update maintenance record
            if (maintainance == null) {
                maintainance = new Maintainance();
                maintainance.setVehicle_id(vehicleId);
            }

            maintainance.setMaintainanceType(typeField.getText().trim());
            maintainance.setRepair_date(repairDate);
            maintainance.setRepairCost(cost);
            maintainance.setAssignedSiteId(siteId);

            // Save to database
            if (maintainance.getId() == 0) {
                maintainanceDAO.addMaintainance(maintainance);
            } else {
                maintainanceDAO.updateMaintainance(maintainance);
            }

            saved = true;
            dispose();

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Invalid date format. Please use YYYY-MM-DD (e.g., 2024-12-01)",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Invalid cost value. Please enter a valid number.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error saving maintenance record: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }

    public Maintainance getMaintainance() {
        return maintainance;
    }
}
