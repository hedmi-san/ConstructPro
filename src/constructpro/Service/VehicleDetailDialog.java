package constructpro.Service;

import constructpro.DTO.Vehicle;
import constructpro.DTO.vehicleSystem.VehicleAssignment;
import constructpro.DAO.vehicleSystem.VehicleAssignmentDAO;
import constructpro.DTO.vehicleSystem.Maintainance;
import constructpro.DAO.vehicleSystem.MaintainanceDAO;
import constructpro.DTO.vehicleSystem.VehicleRental;
import constructpro.DAO.vehicleSystem.VehicleRentalDAO;
import constructpro.DAO.ConstructionSiteDAO;
import constructpro.DAO.WorkerDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;

public class VehicleDetailDialog extends JDialog {

    private Vehicle currentVehicle;
    private VehicleAssignment vehicleAssignment;
    private Maintainance maintainance;
    private VehicleRental vehicleRental;
    private VehicleAssignmentDAO vehicleAssignmentDAO;
    private MaintainanceDAO maintainanceDAO;
    private VehicleRentalDAO vehicleRentalDAO;
    private WorkerDAO workerDAO;
    private ConstructionSiteDAO siteDAO;

    // Color scheme for dark theme
    private static final Color DARK_BACKGROUND = new Color(45, 45, 45);
    private static final Color DARKER_BACKGROUND = new Color(35, 35, 35);
    private static final Color ACCENT_COLOR = new Color(0, 120, 215);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);
    private static final Color LABEL_COLOR = new Color(180, 180, 180);

    // Components
    private JTabbedPane tabbedPane;
    private JLabel nameLabel;

    // Info Panel
    private JPanel infoPanel;
    private JLabel nameValue, plateNumberValue, assignedSiteValue, driverNameValue;

    // Maintenance Panel
    private JPanel maintenancePanel;
    private JTable maintenanceTable;
    private JButton addMaintenanceButton, editMaintenanceButton, deleteMaintenanceButton;
    private DefaultTableModel maintenanceTableModel;
    private JPanel maintenanceTotalsPanel;

    // Rent Panel
    private JPanel rentPanel;
    private JLabel ownerNameValue, ownerPhoneValue;
    private JTextField dailyRateField;
    private JButton addRentButton, editRentButton;
    private JTable rentTable;
    private DefaultTableModel rentTableModel;
    private JPanel rentTotalsPanel;

    private Connection conn;

    public VehicleDetailDialog(JFrame parent, Vehicle vehicle, Connection connection) throws SQLException {
        super(parent, "Vehicle Details", true);
        this.currentVehicle = vehicle;
        this.conn = connection;
        this.maintainanceDAO = new MaintainanceDAO(connection);
        this.vehicleRentalDAO = new VehicleRentalDAO(connection);
        this.vehicleAssignmentDAO = new VehicleAssignmentDAO(connection);
        this.workerDAO = new WorkerDAO(connection);
        this.siteDAO = new ConstructionSiteDAO(connection);

        initializeComponents();
        setupLayout();
        setupStyling();
        populateData();

        setSize(900, 650);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initializeComponents() {
        // Header
        nameLabel = new JLabel();
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

        // Tabbed pane
        tabbedPane = new JTabbedPane();

        // Info panel
        infoPanel = new JPanel();
        infoPanel.setLayout(new GridBagLayout());

        // Initialize info value labels
        nameValue = createValueLabel();
        plateNumberValue = createValueLabel();
        assignedSiteValue = createValueLabel();
        driverNameValue = createValueLabel();

        // Initialize maintenance components
        initializeMaintenanceComponents();

        // Initialize rent components
        initializeRentComponents();
    }

    private void initializeMaintenanceComponents() {
        // Create table with columns
        String[] columns = { "Type", "Date", "Coût", "Chantier" };
        maintenanceTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        maintenanceTable = new JTable(maintenanceTableModel);
        styleTable(maintenanceTable);

        // Create totals panel
        maintenanceTotalsPanel = createTotalsPanel();

        // Create buttons
        addMaintenanceButton = createStyledButton("Ajouter", new Color(0, 123, 255));
        editMaintenanceButton = createStyledButton("Modifier", new Color(255, 193, 7));
        deleteMaintenanceButton = createStyledButton("Supprimer", new Color(220, 53, 69));

        // Add action listeners
        addMaintenanceButton.addActionListener(e -> {
            try {
                addMaintenanceRecord();
            } catch (SQLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });
        editMaintenanceButton.addActionListener(e -> editMaintenanceRecord());
        deleteMaintenanceButton.addActionListener(e -> deleteMaintenanceRecord());

        // Create maintenance panel
        maintenancePanel = new JPanel(new BorderLayout(10, 10));
        maintenancePanel.setBackground(DARK_BACKGROUND);
    }

    private void initializeRentComponents() {
        // Owner info labels
        ownerNameValue = createValueLabel();
        ownerPhoneValue = createValueLabel();

        // Daily rate field (read-only)
        dailyRateField = new JTextField();
        dailyRateField.setEditable(false);
        dailyRateField.setBackground(DARKER_BACKGROUND);
        dailyRateField.setForeground(TEXT_COLOR);
        dailyRateField.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // Create table with columns
        String[] columns = { "Date de début", "Date de fin", "Chantier", "Tarif quotidien", "Jours travaillés",
                "Frais de transport",
                "Coût" };
        rentTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        rentTable = new JTable(rentTableModel);
        styleTable(rentTable);

        // Create totals panel
        rentTotalsPanel = createTotalsPanel();

        // Create buttons
        addRentButton = createStyledButton("Ajouter", new Color(0, 123, 255));
        editRentButton = createStyledButton("Modifier", new Color(255, 193, 7));

        // Add action listeners
        addRentButton.addActionListener(e -> {
            try {
                addRentalRecord();
            } catch (SQLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });
        editRentButton.addActionListener(e -> editRentalRecord());

        // Create rent panel
        rentPanel = new JPanel(new BorderLayout(10, 10));
        rentPanel.setBackground(DARK_BACKGROUND);
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

    private JPanel createTotalsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(DARK_BACKGROUND);
        panel.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, Color.WHITE));
        panel.setPreferredSize(new Dimension(0, 40));
        return panel;
    }

    private void updateMaintenanceTotalsPanel(double total) {
        maintenanceTotalsPanel.removeAll();

        JLabel totalLabel = createTotalLabel("Coût total: " + String.format("%.2f DA", total));
        maintenanceTotalsPanel.add(totalLabel);

        maintenanceTotalsPanel.revalidate();
        maintenanceTotalsPanel.repaint();
    }

    private void updateRentTotalsPanel(double total) {
        rentTotalsPanel.removeAll();

        JLabel totalLabel = createTotalLabel("Coût total: " + String.format("%.2f DA", total));
        rentTotalsPanel.add(totalLabel);

        rentTotalsPanel.revalidate();
        rentTotalsPanel.repaint();
    }

    private JLabel createTotalLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setBackground(DARK_BACKGROUND);
        label.setOpaque(true);
        return label;
    }

    private void styleTable(JTable table) {
        table.setBackground(DARK_BACKGROUND);
        table.setForeground(Color.WHITE);
        table.setGridColor(Color.WHITE);
        table.setSelectionBackground(Color.DARK_GRAY);
        table.setSelectionForeground(Color.WHITE);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.setShowVerticalLines(true);
        table.setGridColor(Color.WHITE);
        table.getTableHeader().setBackground(Color.BLACK);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setReorderingAllowed(false);

        // Center align all cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setBackground(DARK_BACKGROUND);
        centerRenderer.setForeground(Color.WHITE);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Header panel with name
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(DARKER_BACKGROUND);
        headerPanel.add(nameLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Setup panels (rent panel will be set up after data is loaded)
        setupInfoPanel();
        setupMaintenancePanel();

        // Add tabs
        JScrollPane infoScroll = new JScrollPane(infoPanel);
        infoScroll.setBorder(null);
        infoScroll.getViewport().setBackground(DARK_BACKGROUND);
        tabbedPane.addTab("Information", infoScroll);

        JScrollPane maintenanceScroll = new JScrollPane(maintenancePanel);
        maintenanceScroll.setBorder(null);
        maintenanceScroll.getViewport().setBackground(DARK_BACKGROUND);
        tabbedPane.addTab("Maintenance", maintenanceScroll);

        JScrollPane rentScroll = new JScrollPane(rentPanel);
        rentScroll.setBorder(null);
        rentScroll.getViewport().setBackground(DARK_BACKGROUND);
        tabbedPane.addTab("Loyer", rentScroll);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void setupInfoPanel() {
        infoPanel.setBackground(DARK_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 20, 12, 20);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        addFieldToPanel(infoPanel, gbc, 0, row++, "Nom du véhicule:", nameValue);
        addFieldToPanel(infoPanel, gbc, 0, row++, "Numéro de plaque:", plateNumberValue);
        addFieldToPanel(infoPanel, gbc, 0, row++, "Chantier attribué :", assignedSiteValue);
        addFieldToPanel(infoPanel, gbc, 0, row++, "Nom du chauffeur:", driverNameValue);
    }

    private void setupMaintenancePanel() {
        maintenancePanel.removeAll();

        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(DARK_BACKGROUND);
        JLabel titleLabel = new JLabel("Dossiers de maintenance");
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titlePanel.add(titleLabel);

        // Center panel with table and totals
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(DARK_BACKGROUND);

        JScrollPane scrollPane = new JScrollPane(maintenanceTable);
        scrollPane.getViewport().setBackground(DARK_BACKGROUND);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));

        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(maintenanceTotalsPanel, BorderLayout.SOUTH);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.setBackground(DARK_BACKGROUND);
        buttonsPanel.add(addMaintenanceButton);
        buttonsPanel.add(editMaintenanceButton);
        buttonsPanel.add(deleteMaintenanceButton);

        maintenancePanel.add(titlePanel, BorderLayout.NORTH);
        maintenancePanel.add(centerPanel, BorderLayout.CENTER);
        maintenancePanel.add(buttonsPanel, BorderLayout.SOUTH);
    }

    private void setupRentPanel() {
        rentPanel.removeAll();

        // Check if vehicle is owned or rented
        if ("Possédé".equalsIgnoreCase(currentVehicle.getOwnershipType())) {
            // Show simple message for owned vehicles
            JPanel messagePanel = new JPanel(new GridBagLayout());
            messagePanel.setBackground(DARK_BACKGROUND);

            JLabel messageLabel = new JLabel("Ce véhicule n'est pas loué.");
            messageLabel.setForeground(TEXT_COLOR);
            messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));

            messagePanel.add(messageLabel);
            rentPanel.add(messagePanel, BorderLayout.CENTER);
        } else {
            // Show rental information for rented vehicles
            JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
            mainPanel.setBackground(DARK_BACKGROUND);
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            // Owner info panel
            JPanel ownerInfoPanel = new JPanel(new GridBagLayout());
            ownerInfoPanel.setBackground(DARK_BACKGROUND);
            ownerInfoPanel.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(LABEL_COLOR),
                    "Information Possédé",
                    0, 0,
                    new Font("Segoe UI", Font.BOLD, 14),
                    TEXT_COLOR));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 15, 10, 15);
            gbc.anchor = GridBagConstraints.WEST;

            addFieldToPanel(ownerInfoPanel, gbc, 0, 0, "Nom du propriétaire:", ownerNameValue);
            addFieldToPanel(ownerInfoPanel, gbc, 0, 1, "Téléphone du propriétaire", ownerPhoneValue);

            // Rental records panel
            JPanel recordsPanel = new JPanel(new BorderLayout(0, 10));
            recordsPanel.setBackground(DARK_BACKGROUND);

            JLabel recordsLabel = new JLabel("Dossiers de location");
            recordsLabel.setForeground(TEXT_COLOR);
            recordsLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

            JScrollPane scrollPane = new JScrollPane(rentTable);
            scrollPane.getViewport().setBackground(DARK_BACKGROUND);
            scrollPane.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));

            JPanel tablePanel = new JPanel(new BorderLayout());
            tablePanel.setBackground(DARK_BACKGROUND);
            tablePanel.add(scrollPane, BorderLayout.CENTER);
            tablePanel.add(rentTotalsPanel, BorderLayout.SOUTH);

            recordsPanel.add(recordsLabel, BorderLayout.NORTH);
            recordsPanel.add(tablePanel, BorderLayout.CENTER);

            // Buttons panel
            JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonsPanel.setBackground(DARK_BACKGROUND);
            buttonsPanel.add(addRentButton);
            buttonsPanel.add(editRentButton);

            mainPanel.add(ownerInfoPanel, BorderLayout.NORTH);
            mainPanel.add(recordsPanel, BorderLayout.CENTER);
            mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

            rentPanel.add(mainPanel, BorderLayout.CENTER);
        }
    }

    private void addFieldToPanel(JPanel panel, GridBagConstraints gbc, int startX, int y, String label, JLabel value) {
        gbc.gridy = y;

        gbc.gridx = startX;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(createFieldLabel(label), gbc);

        gbc.gridx = startX + 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(value, gbc);

        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
    }

    private void addFieldToPanel(JPanel panel, GridBagConstraints gbc, int startX, int y, String label,
            JTextField field) {
        gbc.gridy = y;

        gbc.gridx = startX;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(createFieldLabel(label), gbc);

        gbc.gridx = startX + 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(field, gbc);

        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
    }

    private JLabel createValueLabel() {
        JLabel label = new JLabel();
        label.setForeground(TEXT_COLOR);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return label;
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(LABEL_COLOR);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return label;
    }

    private void setupStyling() {
        getContentPane().setBackground(DARK_BACKGROUND);

        // Style the tabbed pane
        tabbedPane.setBackground(DARK_BACKGROUND);
        tabbedPane.setForeground(TEXT_COLOR);
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Custom tab styling
        UIManager.put("TabbedPane.selected", ACCENT_COLOR);
        UIManager.put("TabbedPane.background", DARKER_BACKGROUND);
        UIManager.put("TabbedPane.foreground", TEXT_COLOR);

        SwingUtilities.updateComponentTreeUI(tabbedPane);
    }

    private void populateData() throws SQLException {
        // Set header name
        nameLabel.setText(currentVehicle.getName());

        // Populate info panel
        nameValue.setText(currentVehicle.getName() != null ? currentVehicle.getName() : "N/A");
        plateNumberValue.setText(currentVehicle.getPlateNumber() != null ? currentVehicle.getPlateNumber() : "N/A");

        // Get assigned site name
        String siteName = "N/A";
        if (currentVehicle.getSiteID() > 0) {
            siteName = siteDAO.getSiteNameById(currentVehicle.getSiteID());
            if (siteName == null)
                siteName = "N/A";
        }
        assignedSiteValue.setText(siteName);

        // Get driver name
        String driverName = "N/A";
        if (currentVehicle.getDriverID() > 0) {
            driverName = workerDAO.getDriverNameById(currentVehicle.getDriverID());
            if (driverName == null)
                driverName = "N/A";
        }
        driverNameValue.setText(driverName);

        // Setup rent panel after vehicle data is loaded
        setupRentPanel();

        // Load maintenance records
        loadMaintenanceRecords();

        // Load rental information
        loadRentalInformation();
    }

    private void loadMaintenanceRecords() {
        try {
            maintenanceTableModel.setRowCount(0); // Clear existing data

            List<Maintainance> records = maintainanceDAO.getAllMaintainanceRecords(currentVehicle.getId());

            for (Maintainance record : records) {
                String siteName = siteDAO.getSiteNameById(record.getAssignedSiteId());
                if (siteName == null)
                    siteName = "N/A";

                Object[] row = {
                        record.getMaintainanceType(),
                        record.getRepair_date().toString(),
                        String.format("%.2f", record.getRepairCost()),
                        siteName
                };
                maintenanceTableModel.addRow(row);
            }

            // Update totals
            double total = maintainanceDAO.getTotalMaintainanceCost(currentVehicle.getId());
            updateMaintenanceTotalsPanel(total);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors du chargement des dossiers de maintenance : " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadRentalInformation() {
        try {
            if ("Loué".equalsIgnoreCase(currentVehicle.getOwnershipType())) {
                // Get current rental info
                VehicleRental currentRental = vehicleRentalDAO.getCurrentRentalInfo(currentVehicle.getId());

                if (currentRental != null) {
                    ownerNameValue.setText(currentRental.getOwnerName() != null ? currentRental.getOwnerName() : "N/A");
                    ownerPhoneValue
                            .setText(currentRental.getOwnerPhone() != null ? currentRental.getOwnerPhone() : "N/A");
                    dailyRateField.setText(String.format("%.2f DA", currentRental.getDailyRate()));
                }

                // Load all rental records
                rentTableModel.setRowCount(0);
                List<VehicleRental> records = vehicleRentalDAO.getAllRentalRecords(currentVehicle.getId());

                for (VehicleRental record : records) {
                    // Get site name from rental record's assigned site
                    String siteName = "N/A";
                    if (record.getAssignedSiteId() > 0) {
                        siteName = siteDAO.getSiteNameById(record.getAssignedSiteId());
                        if (siteName == null)
                            siteName = "N/A";
                    }

                    double cost = (record.getDailyRate() * record.getDaysWorked()) + record.getTransferFee();

                    Object[] row = {
                            record.getStartDate().toString(),
                            record.getEndDate() != null ? record.getEndDate().toString() : "En cours",
                            siteName,
                            String.format("%.2f", record.getDailyRate()),
                            record.getDaysWorked(),
                            String.format("%.2f", record.getTransferFee()),
                            String.format("%.2f", cost)
                    };
                    rentTableModel.addRow(row);
                }

                // Update totals
                double total = vehicleRentalDAO.getTotalRentalCost(currentVehicle.getId());
                updateRentTotalsPanel(total);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors du chargement des informations de location :" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Action methods
    private void addMaintenanceRecord() throws SQLException {
        MaintenanceFormDialog dialog = new MaintenanceFormDialog(
                this,
                null,
                currentVehicle.getId(),
                conn);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadMaintenanceRecords(); // Refresh table
        }
    }

    private void editMaintenanceRecord() {
        int selectedRow = maintenanceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un enregistrement d'entretien à modifier.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Get all records and find the selected one
            List<Maintainance> records = maintainanceDAO.getAllMaintainanceRecords(currentVehicle.getId());
            if (selectedRow < records.size()) {
                Maintainance selectedRecord = records.get(selectedRow);

                MaintenanceFormDialog dialog = new MaintenanceFormDialog(
                        this,
                        selectedRecord,
                        currentVehicle.getId(),
                        conn);
                dialog.setVisible(true);

                if (dialog.isSaved()) {
                    loadMaintenanceRecords(); // Refresh table
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de la modification du registre de maintenance :" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteMaintenanceRecord() {
        int selectedRow = maintenanceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un enregistrement de maintenance à supprimer.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir supprimer cet enregistrement de maintenance ?",
                "Confirmer la suppression",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                List<Maintainance> records = maintainanceDAO.getAllMaintainanceRecords(currentVehicle.getId());
                if (selectedRow < records.size()) {
                    Maintainance selectedRecord = records.get(selectedRow);
                    maintainanceDAO.deleteMaintainance(selectedRecord.getId());
                    loadMaintenanceRecords(); // Refresh table
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de la suppression du registre de maintenance :" + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addRentalRecord() throws SQLException {
        RentalRecordFormDialog dialog = new RentalRecordFormDialog(
                this,
                null,
                currentVehicle.getId(),
                conn);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadRentalInformation(); // Refresh table
        }
    }

    private void editRentalRecord() {
        int selectedRow = rentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un enregistrement de location à modifier.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            List<VehicleRental> records = vehicleRentalDAO.getAllRentalRecords(currentVehicle.getId());
            if (selectedRow < records.size()) {
                VehicleRental selectedRecord = records.get(selectedRow);

                RentalRecordFormDialog dialog = new RentalRecordFormDialog(
                        this,
                        selectedRecord,
                        currentVehicle.getId(),
                        conn);
                dialog.setVisible(true);

                if (dialog.isSaved()) {
                    loadRentalInformation(); // Refresh table
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de la modification du dossier de location :" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
