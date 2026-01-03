package constructpro.Service;

import constructpro.DAO.BillDAO;
import constructpro.DAO.ConstructionSiteDAO;
import constructpro.DAO.WorkerDAO;
import constructpro.DTO.Worker;
import constructpro.DAO.PaymentCheckDAO;
import constructpro.DTO.ConstructionSite;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import java.sql.SQLException;
import java.awt.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;

public class ShowSitesDetails extends JDialog {

    private Connection conn;

    private WorkerDAO workerDao;
    private ConstructionSite site;
    private JFrame parentFrame;
    // Colors
    private static final Color DARK_BACKGROUND = new Color(45, 45, 45);
    // Labels for site info

    // Panels
    private JPanel mainPanel, headerPanel, tabPanel, contentPanel;
    private JPanel workersPanel, costPanel, billsPanel, vehiclesPanel, attachmentPanel;

    // Tab buttons
    private JButton workersTab, costTab, billsTab, attachmentTab, vehiclesTab, supprimerBtn, ajouterBtn;
    private JTable workersTable, vehiclesTable, billsTable;
    private JLabel totalWorkersLabel, totalPaidLabel;
    private JLabel totalBillsLabel, totalBillsCostLabel;
    private JLabel totalVehiclesLabel, totalMaintenanceCostLabel, totalRentCostLabel;
    private JLabel totalCostWorkersLabel, totalCostBillsLabel, totalCostVehiclesLabel, grandTotalCostLabel;
    private CardLayout cardLayout;

    public ShowSitesDetails(JFrame parent, ConstructionSite site, Connection connection) throws SQLException {
        super(parent, "Détails du chantier", true);
        this.site = site;

        this.workerDao = new WorkerDAO(connection);
        this.conn = connection;

        initializeComponents();
        setupLayout();
        setupStyling();
        populateWorkersData();
        populateBillsData();
        populateVehiclesData();
        populateCostData();
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initializeComponents() {
        // Main panels
        mainPanel = new JPanel(new BorderLayout());
        headerPanel = new JPanel(new BorderLayout());
        tabPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

        // Card layout for switching between tabs
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Initialize the tables
        workersTable = new JTable();
        vehiclesTable = new JTable();
        billsTable = new JTable();

        // Create Buttons
        supprimerBtn = new JButton("Supprimer");
        supprimerBtn.setForeground(Color.WHITE);
        ajouterBtn = new JButton("Affecter");
        ajouterBtn.setForeground(Color.WHITE);
        // Create tab panels
        workersPanel = createWorkersPanel();
        costPanel = createCostPanel();
        billsPanel = createBillsPanel();
        vehiclesPanel = createVehiclesPanel();
        attachmentPanel = createAttachmentPanel();

        // Create tab buttons
        workersTab = createTabButton("Travailleurs");
        costTab = createTabButton("Coût");
        billsTab = createTabButton("Factures");
        vehiclesTab = createTabButton("Véhicules");
        attachmentTab = createTabButton("Attachment");

        // Add action listeners to tabs
        workersTab.addActionListener(e -> switchTab("Travailleurs", workersTab));
        costTab.addActionListener(e -> switchTab("Coût", costTab));
        billsTab.addActionListener(e -> switchTab("Factures", billsTab));
        vehiclesTab.addActionListener(e -> switchTab("Véhicules", vehiclesTab));
        attachmentTab.addActionListener(e -> switchTab("Attachment", attachmentTab));

        // Add action listeners to buttons
        supprimerBtn.addActionListener(e -> {
            try {
                unassignWorkers();
            } catch (SQLException ex) {
            }
        });
        ajouterBtn.addActionListener(e -> {
            try {
                assignWorkers();
            } catch (SQLException ex) {
            }
        });

    }

    private JButton createTabButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setPreferredSize(new Dimension(100, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void switchTab(String tabName, JButton selectedButton) {
        cardLayout.show(contentPanel, tabName);

        // Reset all tab button styles
        workersTab.setForeground(Color.GRAY);
        costTab.setForeground(Color.GRAY);
        billsTab.setForeground(Color.GRAY);
        vehiclesTab.setForeground(Color.GRAY);
        attachmentTab.setForeground(Color.GRAY);

        // Highlight selected tab
        selectedButton.setForeground(Color.ORANGE);

        // Refresh cost data if switching to Cost tab
        if (tabName.equals("Coût")) {
            populateCostData();
        }
    }

    private void unassignWorkers() throws SQLException {
        new UnAssignementPanel(parentFrame, site, conn, this);
    }

    private void assignWorkers() throws SQLException {
        new AssignementPanel(parentFrame, site, conn, this);
    }

    private JPanel createWorkersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(DARK_BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(DARK_BACKGROUND);

        // Stats Panel (Left)
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        statsPanel.setBackground(DARK_BACKGROUND);

        totalWorkersLabel = new JLabel("Total Workers: 0");
        totalWorkersLabel.setForeground(Color.WHITE);
        totalWorkersLabel.setFont(new Font("Arial", Font.BOLD, 14));

        totalPaidLabel = new JLabel("Total Paid: 0.0");
        totalPaidLabel.setForeground(Color.WHITE);
        totalPaidLabel.setFont(new Font("Arial", Font.BOLD, 14));

        statsPanel.add(totalWorkersLabel);
        statsPanel.add(totalPaidLabel);

        // Buttons Panel (Right)
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        buttonsPanel.setBackground(DARK_BACKGROUND);
        buttonsPanel.add(supprimerBtn);
        buttonsPanel.add(ajouterBtn);

        bottomPanel.add(statsPanel, BorderLayout.WEST);
        bottomPanel.add(buttonsPanel, BorderLayout.EAST);

        workersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        workersTable.setDefaultEditor(Object.class, null);
        workersTable.setShowVerticalLines(true);
        workersTable.setGridColor(Color.WHITE);
        workersTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(workersTable);
        scrollPane.setBackground(DARK_BACKGROUND);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createCostPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(DARK_BACKGROUND);
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 0, 10, 0);

        totalCostWorkersLabel = createDashboardLabel("Coût Travailleurs (Total Payé): 0.0");
        totalCostBillsLabel = createDashboardLabel("Coût Factures: 0.0");
        totalCostVehiclesLabel = createDashboardLabel("Coût Véhicules (Maint. + Loc.): 0.0");

        grandTotalCostLabel = new JLabel("COÛT TOTAL DU CHANTIER: 0.0");
        grandTotalCostLabel.setForeground(Color.ORANGE);
        grandTotalCostLabel.setFont(new Font("Arial", Font.BOLD, 22));

        panel.add(totalCostWorkersLabel, gbc);
        panel.add(totalCostBillsLabel, gbc);
        panel.add(totalCostVehiclesLabel, gbc);

        gbc.insets = new Insets(30, 0, 10, 0);
        panel.add(grandTotalCostLabel, gbc);

        return panel;
    }

    private JLabel createDashboardLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.PLAIN, 18));
        return label;
    }

    private JPanel createAttachmentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(DARK_BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel label = new JLabel("Attachment information will be displayed here");
        label.setForeground(Color.WHITE);
        panel.add(label, BorderLayout.NORTH);

        return panel;
    }

    private JPanel createBillsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(DARK_BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Stats Panel (Left)
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        statsPanel.setBackground(DARK_BACKGROUND);

        totalBillsLabel = new JLabel("Total Bills: 0");
        totalBillsLabel.setForeground(Color.WHITE);
        totalBillsLabel.setFont(new Font("Arial", Font.BOLD, 14));

        totalBillsCostLabel = new JLabel("Total Cost: 0.0");
        totalBillsCostLabel.setForeground(Color.WHITE);
        totalBillsCostLabel.setFont(new Font("Arial", Font.BOLD, 14));

        statsPanel.add(totalBillsLabel);
        statsPanel.add(totalBillsCostLabel);

        billsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        billsTable.setDefaultEditor(Object.class, null);
        billsTable.setShowVerticalLines(true);
        billsTable.setGridColor(Color.WHITE);
        billsTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(billsTable);
        scrollPane.setBackground(DARK_BACKGROUND);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(statsPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createVehiclesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(DARK_BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Stats Panel (Left)
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        statsPanel.setBackground(DARK_BACKGROUND);

        totalVehiclesLabel = new JLabel("Total Vehicles: 0");
        totalVehiclesLabel.setForeground(Color.WHITE);
        totalVehiclesLabel.setFont(new Font("Arial", Font.BOLD, 14));

        totalMaintenanceCostLabel = new JLabel("Total Maintenance: 0.0");
        totalMaintenanceCostLabel.setForeground(Color.WHITE);
        totalMaintenanceCostLabel.setFont(new Font("Arial", Font.BOLD, 14));

        totalRentCostLabel = new JLabel("Total Rent: 0.0");
        totalRentCostLabel.setForeground(Color.WHITE);
        totalRentCostLabel.setFont(new Font("Arial", Font.BOLD, 14));

        statsPanel.add(totalVehiclesLabel);
        statsPanel.add(totalMaintenanceCostLabel);
        statsPanel.add(totalRentCostLabel);

        vehiclesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        vehiclesTable.setDefaultEditor(Object.class, null);
        vehiclesTable.setShowVerticalLines(true);
        vehiclesTable.setGridColor(Color.WHITE);
        vehiclesTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(vehiclesTable);
        scrollPane.setBackground(DARK_BACKGROUND);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(statsPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void setupLayout() {
        // Header with title and close button
        JLabel titleLabel = new JLabel(site.getName());
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(new EmptyBorder(10, 20, 10, 10));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.setBackground(DARK_BACKGROUND);

        // Tab panel
        tabPanel.setBackground(DARK_BACKGROUND);
        tabPanel.setBorder(new EmptyBorder(0, 10, 0, 0));
        tabPanel.add(workersTab);
        tabPanel.add(billsTab);
        tabPanel.add(vehiclesTab);
        tabPanel.add(attachmentTab);
        tabPanel.add(costTab);

        // Add panels to card layout
        contentPanel.add(workersPanel, "Travailleurs");
        contentPanel.add(billsPanel, "Factures");
        contentPanel.add(vehiclesPanel, "Véhicules");
        contentPanel.add(attachmentPanel, "Attachment");
        contentPanel.add(costPanel, "Coût");

        // Add border to content panel
        contentPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.GRAY));

        // Assemble main panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.BLACK);
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(tabPanel, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.setBackground(Color.BLACK);

        add(mainPanel);
    }

    private void setupStyling() {
        // Set default selected tab
        workersTab.setForeground(Color.WHITE);
        costTab.setForeground(Color.GRAY);
        billsTab.setForeground(Color.GRAY);

        // Set background
        getContentPane().setBackground(Color.BLACK);
    }

    private void populateWorkersData() {
        List<Worker> workers = workerDao.getWorkersWithActivityOnSite(site.getId());
        DefaultTableModel model = new DefaultTableModel(
                new Object[] { "ID", "Prénom", "Nom", "Fonction", "Total Payé" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        PaymentCheckDAO paymentCheckDAO = new PaymentCheckDAO(conn);

        for (Worker w : workers) {
            double totalPaid = 0;
            try {
                totalPaid = paymentCheckDAO.getTotalPaidForWorkerOnSite(w.getId(), site.getId());
            } catch (SQLException e) {
                e.printStackTrace();
            }

            model.addRow(new Object[] {
                    w.getId(),
                    w.getFirstName(),
                    w.getLastName(),
                    w.getRole(),
                    totalPaid
            });
        }
        workersTable.setModel(model);
        // Hide ID column if desired
        workersTable.getColumnModel().getColumn(0).setMinWidth(0);
        workersTable.getColumnModel().getColumn(0).setMaxWidth(0);
        workersTable.getColumnModel().getColumn(0).setWidth(0);

        updateWorkerStats();
    }

    private void populateCostData() {
        try {
            ConstructionSiteDAO siteDAO = new ConstructionSiteDAO(conn);
            siteDAO.syncSiteTotalCost(site.getId());

            // Get accurate breakdown from DAO
            Map<String, Double> breakdown = siteDAO.getSiteCostBreakdown(site.getId());

            // Re-fetch site to get updated totalCost
            ConstructionSite updatedSite = siteDAO.getConstructionSiteById(site.getId());

            totalCostWorkersLabel.setText(
                    String.format("Coût Travailleurs (Total Payé): %.2f", breakdown.getOrDefault("workers", 0.0)));
            totalCostBillsLabel.setText(String.format("Coût Factures: %.2f", breakdown.getOrDefault("bills", 0.0)));
            totalCostVehiclesLabel.setText(
                    String.format("Coût Véhicules (Maint. + Loc.): %.2f", breakdown.getOrDefault("vehicles", 0.0)));
            grandTotalCostLabel.setText(String.format("COÛT TOTAL DU CHANTIER: %.2f", updatedSite.getTotalCost()));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateBillsData() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[] { "ID", "Numéro de facture", "Fournisseur", "Date", "Coût Total" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        BillDAO billDAO = new BillDAO(conn);
        double totalCost = 0;
        int billCount = 0;

        try (java.sql.ResultSet rs = billDAO.getBillsBySiteId(site.getId())) {
            while (rs.next()) {
                double cost = rs.getDouble("totalCost");
                totalCost += cost;
                billCount++;

                model.addRow(new Object[] {
                        rs.getInt("id"),
                        rs.getString("factureNumber"),
                        rs.getString("supplierName"),
                        rs.getDate("billDate"),
                        cost
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        billsTable.setModel(model);

        // Hide ID column
        billsTable.getColumnModel().getColumn(0).setMinWidth(0);
        billsTable.getColumnModel().getColumn(0).setMaxWidth(0);
        billsTable.getColumnModel().getColumn(0).setWidth(0);

        // Update totals
        totalBillsLabel.setText("Total Bills: " + billCount);
        totalBillsCostLabel.setText(String.format("Total Cost: %.2f", totalCost));
    }

    private void updateWorkerStats() {
        int rowCount = workersTable.getRowCount();
        double totalPaid = 0;

        for (int i = 0; i < rowCount; i++) {
            Object value = workersTable.getValueAt(i, 4); // "Total Payé" column
            if (value instanceof Number) {
                totalPaid += ((Number) value).doubleValue();
            }
        }

        totalWorkersLabel.setText("Total Workers: " + rowCount);
        totalPaidLabel.setText(String.format("Total Paid: %.2f", totalPaid));
    }

    private void populateVehiclesData() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[] { "ID", "Nom", "Numéro de plaque", "Type de Propriété", "Coût de maintenance",
                        "Cout de location" },
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        constructpro.DAO.VehicleDAO vehicleDAO = new constructpro.DAO.VehicleDAO(conn);
        int vehicleCount = 0;
        double totalMaintenance = 0;
        double totalRent = 0;

        try (java.sql.ResultSet rs = vehicleDAO.getVehiclesWithCostsBySiteId(site.getId())) {
            while (rs.next()) {
                double mCost = rs.getDouble("maintenanceCost");
                double rCost = rs.getDouble("rentCost");

                totalMaintenance += mCost;
                totalRent += rCost;
                vehicleCount++;

                model.addRow(new Object[] {
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("plateNumber"),
                        rs.getString("ownershipType"),
                        mCost,
                        rCost
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        vehiclesTable.setModel(model);

        // Hide ID column
        vehiclesTable.getColumnModel().getColumn(0).setMinWidth(0);
        vehiclesTable.getColumnModel().getColumn(0).setMaxWidth(0);
        vehiclesTable.getColumnModel().getColumn(0).setWidth(0);

        // Update totals
        totalVehiclesLabel.setText("Total Vehicles: " + vehicleCount);
        totalMaintenanceCostLabel.setText(String.format("Total Maintenance: %.2f", totalMaintenance));
        totalRentCostLabel.setText(String.format("Total Rent: %.2f", totalRent));
    }

    public void setParentFrame(JFrame parent) {
        this.parentFrame = parent;
    }

    public void refreshWorkersTable() {
        populateWorkersData();
    }
}