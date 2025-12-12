package constructpro.Service;

import constructpro.DTO.vehicleSystem.Maintainance;
import constructpro.DAO.vehicleSystem.MaintenanceDAO;
import constructpro.DAO.ConstructionSiteDAO;
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class MaintenanceFormDialog extends JDialog {
    private Maintainance maintainance;
    private int vehicleId;
    private Connection conn;
    private boolean saved = false;

    private MaintenanceDAO maintainanceDAO;
    private ConstructionSiteDAO siteDAO;

    // Color scheme
    private static final Color DARK_BACKGROUND = new Color(45, 45, 45);
    private static final Color DARKER_BACKGROUND = new Color(35, 35, 35);
    private static final Color ACCENT_COLOR = new Color(0, 120, 215);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);
    private static final Color LABEL_COLOR = new Color(180, 180, 180);

    // Components
    private JTextField typeField;
    private JDateChooser dateChooser;
    private JTextField costField;
    private JComboBox<String> siteComboBox;
    private JButton saveButton;
    private JButton cancelButton;

    public MaintenanceFormDialog(Window parent, Maintainance maintainance, int vehicleId, Connection conn)
            throws SQLException {
        super(parent,
                maintainance == null ? "Ajouter un enregistrement de maintenance"
                        : "Modifier l'enregistrement de maintenance",
                ModalityType.APPLICATION_MODAL);
        this.maintainance = maintainance;
        this.vehicleId = vehicleId;
        this.conn = conn;
        this.maintainanceDAO = new MaintenanceDAO(conn);
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

        // Date chooser
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        dateChooser.setPreferredSize(new Dimension(200, 30));
        dateChooser.setBackground(DARKER_BACKGROUND);
        dateChooser.setForeground(TEXT_COLOR);
        dateChooser.getCalendarButton().setBackground(ACCENT_COLOR);
        dateChooser.getCalendarButton().setForeground(Color.WHITE);

        // Cost field
        costField = new JTextField(20);
        styleTextField(costField);

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

        // Maintenance Type
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        formPanel.add(createLabel("Type de maintenance:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(typeField, gbc);
        row++;

        // Date
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        formPanel.add(createLabel("Date de réparation:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(dateChooser, gbc);
        row++;

        // Cost
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        formPanel.add(createLabel("Coût (DA):"), gbc);

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

            // Convert LocalDate to Date for JDateChooser
            LocalDate repairDate = maintainance.getRepair_date();
            Date date = Date.from(repairDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            dateChooser.setDate(date);

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
                    "Veuillez entrer le type de maintenance.",
                    "Erreur de validation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (dateChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner la date de réparation.",
                    "Erreur de validation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (costField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez entrer le coût.",
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
            // Convert Date to LocalDate
            Date selectedDate = dateChooser.getDate();
            LocalDate repairDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

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

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Valeur de coût invalide. Veuillez entrer un nombre valide.",
                    "Erreur de validation",
                    JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'enregistrement de la maintenance: " + e.getMessage(),
                    "Erreur de base de données",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de la conversion de la date: " + e.getMessage(),
                    "Erreur de validation",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }

    public Maintainance getMaintainance() {
        return maintainance;
    }
}
