package constructpro.Service;

import constructpro.Utils.DateChooserConfigurator;

import constructpro.DAO.BillDAO;

import constructpro.DAO.ConstructionSiteDAO;
import constructpro.DAO.WorkerDAO;
import constructpro.DTO.Worker;
import constructpro.DAO.PaymentCheckDAO;
import constructpro.DTO.ConstructionSite;
import java.util.List;
import java.awt.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import com.toedter.calendar.JDateChooser;
import constructpro.DAO.vehicleSystem.MaintenanceDAO;
import constructpro.DAO.vehicleSystem.VehicleRentalDAO;
import javax.swing.*;
import java.awt.Desktop;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.util.function.BiConsumer;

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
    private JPanel workersPanel, costPanel, materialPanel, electricPanel, plomberiePanle, vehiclesPanel;

    // Tab buttons
    private JButton workersTab, costTab, materialTab, electricTab, plomberieTab, vehiclesTab;
    private JTable workersTable, vehiclesTable, materialBillsTable, electricBillsTable, plomberieBillsTable;
    private JLabel totalWorkersLabel, totalPaidLabel;

    private JLabel totalMaterialBillsLabel, totalMaterialBillsCostLabel;
    private JLabel totalElectricBillsLabel, totalElectricBillsCostLabel;
    private JLabel totalPlomberieBillsLabel, totalPlomberieBillsCostLabel;
    private JLabel totalVehiclesLabel, totalMaintenanceCostLabel, totalRentCostLabel;
    private JLabel totalCostWorkersLabel, totalCostBillsLabel, totalCostVehiclesLabel, grandTotalCostLabel;
    private DefaultPieDataset costDataset;
    private ChartPanel chartPanel;
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
        setupLayout();
        setupStyling();
        populateWorkersData();
        populateAllSpecificBillsData();
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
        materialBillsTable = new JTable();
        electricBillsTable = new JTable();
        plomberieBillsTable = new JTable();

        // Create tab panels
        workersPanel = createWorkersPanel();
        costPanel = createCostPanel();
        materialPanel = createMaterialPanel();
        electricPanel = createElectricPanel();
        plomberiePanle = createPlomberiePanel();
        vehiclesPanel = createVehiclesPanel();

        // Create tab buttons
        // Create tab buttons
        workersTab = createTabButton("Travailleurs");
        costTab = createTabButton("Coût");
        materialTab = createTabButton("Matériaux");
        electricTab = createTabButton("Électricité");
        plomberieTab = createTabButton("Plomberie");
        vehiclesTab = createTabButton("Véhicules");

        // Add action listeners to tabs
        // Add action listeners to tabs
        workersTab.addActionListener(e -> switchTab("Travailleurs", workersTab));
        costTab.addActionListener(e -> switchTab("Coût", costTab));
        vehiclesTab.addActionListener(e -> switchTab("Véhicules", vehiclesTab));
        materialTab.addActionListener(e -> switchTab("Matériaux", materialTab));
        electricTab.addActionListener(e -> switchTab("Électricité", electricTab));
        plomberieTab.addActionListener(e -> switchTab("Plomberie", plomberieTab));
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

    private void createReportButton(JPanel panel, String buttonText, BiConsumer<LocalDate, LocalDate> action) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(DARK_BACKGROUND);
        JButton pdfButton = new JButton(buttonText);
        pdfButton.setBackground(new Color(0, 102, 204));
        pdfButton.setForeground(Color.WHITE);
        pdfButton.setFocusPainted(false);
        pdfButton.addActionListener(e -> showDateRangeDialog(action));
        buttonPanel.add(pdfButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void switchTab(String tabName, JButton selectedButton) {
        cardLayout.show(contentPanel, tabName);

        // Reset all tab button styles
        // Reset all tab button styles
        workersTab.setForeground(Color.GRAY);
        costTab.setForeground(Color.GRAY);
        vehiclesTab.setForeground(Color.GRAY);
        materialTab.setForeground(Color.GRAY);
        electricTab.setForeground(Color.GRAY);
        plomberieTab.setForeground(Color.GRAY);
        // Highlight selected tab
        selectedButton.setForeground(Color.ORANGE);

        // Refresh cost data if switching to Cost tab
        if (tabName.equals("Coût")) {
            populateCostData();
        }
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

        totalWorkersLabel = new JLabel("Travailleurs totaux: 0");
        totalWorkersLabel.setForeground(Color.WHITE);
        totalWorkersLabel.setFont(new Font("Arial", Font.BOLD, 14));

        totalPaidLabel = new JLabel("Total payé: 0.0");
        totalPaidLabel.setForeground(Color.WHITE);
        totalPaidLabel.setFont(new Font("Arial", Font.BOLD, 14));

        statsPanel.add(totalWorkersLabel);
        statsPanel.add(totalPaidLabel);

        // Buttons Panel (Right)
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        buttonsPanel.setBackground(DARK_BACKGROUND);
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

        // Combine stats and new button in bottom panel
        JPanel startEndPanel = new JPanel(new BorderLayout());
        startEndPanel.setBackground(DARK_BACKGROUND);
        startEndPanel.add(bottomPanel, BorderLayout.CENTER);
        createReportButton(startEndPanel, "Rapport Travailleurs", (start, end) -> generateWorkerReport(start, end));

        panel.add(startEndPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createCostPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(DARK_BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Create Dataset
        costDataset = new DefaultPieDataset();

        // Create Chart
        JFreeChart chart = ChartFactory.createPieChart(
                null, // title
                costDataset,
                true, // legend - ENABLED
                true, // tooltips
                false // urls
        );

        // Styling the chart
        chart.setBackgroundPaint(DARK_BACKGROUND);
        chart.getLegend().setBackgroundPaint(DARK_BACKGROUND);
        chart.getLegend().setItemPaint(Color.WHITE);
        chart.getLegend().setBorder(0, 0, 0, 0);

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(DARK_BACKGROUND);
        plot.setOutlineVisible(false);
        plot.setLabelBackgroundPaint(DARK_BACKGROUND);
        plot.setLabelOutlinePaint(null);
        plot.setLabelShadowPaint(null);
        plot.setLabelPaint(Color.WHITE);
        plot.setSectionPaint("Travailleurs", new Color(0, 150, 255)); // Blue
        plot.setSectionPaint("Factures", new Color(255, 100, 100)); // Red
        plot.setSectionPaint("Véhicules", new Color(100, 255, 100)); // Green
        plot.setShadowPaint(null);

        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(400, 400));
        chartPanel.setBackground(DARK_BACKGROUND);

        // Right side for labels
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(DARK_BACKGROUND);
        detailsPanel.setBorder(new EmptyBorder(0, 40, 0, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 0, 8, 0);

        totalCostWorkersLabel = createDashboardLabel("Coût Travailleurs: 0.0");
        totalCostWorkersLabel.setFont(new Font("Arial", Font.PLAIN, 15));

        totalCostBillsLabel = createDashboardLabel("Coût Factures: 0.0");
        totalCostBillsLabel.setFont(new Font("Arial", Font.PLAIN, 15));

        totalCostVehiclesLabel = createDashboardLabel("Coût Véhicules: 0.0");
        totalCostVehiclesLabel.setFont(new Font("Arial", Font.PLAIN, 15));

        grandTotalCostLabel = new JLabel("COÛT TOTAL: 0.0");
        grandTotalCostLabel.setForeground(Color.ORANGE);
        grandTotalCostLabel.setFont(new Font("Arial", Font.BOLD, 20));

        detailsPanel.add(totalCostWorkersLabel, gbc);
        detailsPanel.add(totalCostBillsLabel, gbc);
        detailsPanel.add(totalCostVehiclesLabel, gbc);

        gbc.insets = new Insets(25, 0, 10, 0);
        detailsPanel.add(grandTotalCostLabel, gbc);

        // Layout
        JPanel centerPanel = new JPanel(new GridLayout(1, 2));
        centerPanel.setBackground(DARK_BACKGROUND);
        centerPanel.add(chartPanel);
        centerPanel.add(detailsPanel);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom right button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(DARK_BACKGROUND);
        JButton extraBtn = new JButton("Générer Rapport PDF");
        extraBtn.setForeground(Color.WHITE);
        extraBtn.setBackground(new Color(0, 102, 204));
        extraBtn.addActionListener(e -> showDateRangeDialog((start, end) -> generatePDFReport(start, end)));
        bottomPanel.add(extraBtn);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private JLabel createDashboardLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.PLAIN, 18));
        return label;
    }

    private JPanel createMaterialPanel() {
        totalMaterialBillsLabel = new JLabel("Factures totales: 0");
        totalMaterialBillsLabel.setForeground(Color.WHITE);
        totalMaterialBillsLabel.setFont(new Font("Arial", Font.BOLD, 14));

        totalMaterialBillsCostLabel = new JLabel("Coût total: 0.0");
        totalMaterialBillsCostLabel.setForeground(Color.WHITE);
        totalMaterialBillsCostLabel.setFont(new Font("Arial", Font.BOLD, 14));

        return createGenericBillPanel(materialBillsTable, totalMaterialBillsLabel, totalMaterialBillsCostLabel,
                "Rapport Matériaux",
                (start, end) -> generateBillReport(start, end, "Matériaux de construction", "Rapport_Materiaux"));
    }

    private JPanel createElectricPanel() {
        totalElectricBillsLabel = new JLabel("Factures totales: 0");
        totalElectricBillsLabel.setForeground(Color.WHITE);
        totalElectricBillsLabel.setFont(new Font("Arial", Font.BOLD, 14));

        totalElectricBillsCostLabel = new JLabel("Coût total: 0.0");
        totalElectricBillsCostLabel.setForeground(Color.WHITE);
        totalElectricBillsCostLabel.setFont(new Font("Arial", Font.BOLD, 14));

        return createGenericBillPanel(electricBillsTable, totalElectricBillsLabel, totalElectricBillsCostLabel,
                "Rapport Électricité",
                (start, end) -> generateBillReport(start, end, "Électricité", "Rapport_Electricite"));
    }

    private JPanel createPlomberiePanel() {
        totalPlomberieBillsLabel = new JLabel("Factures totales: 0");
        totalPlomberieBillsLabel.setForeground(Color.WHITE);
        totalPlomberieBillsLabel.setFont(new Font("Arial", Font.BOLD, 14));

        totalPlomberieBillsCostLabel = new JLabel("Coût total: 0.0");
        totalPlomberieBillsCostLabel.setForeground(Color.WHITE);
        totalPlomberieBillsCostLabel.setFont(new Font("Arial", Font.BOLD, 14));

        return createGenericBillPanel(plomberieBillsTable, totalPlomberieBillsLabel, totalPlomberieBillsCostLabel,
                "Rapport Plomberie",
                (start, end) -> generateBillReport(start, end, "Plomberie", "Rapport_Plomberie"));
    }

    private JPanel createGenericBillPanel(JTable table, JLabel countLabel, JLabel costLabel, String reportName,
            BiConsumer<LocalDate, LocalDate> reportAction) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(DARK_BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Stats Panel (Left)
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        statsPanel.setBackground(DARK_BACKGROUND);

        statsPanel.add(countLabel);
        statsPanel.add(costLabel);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setDefaultEditor(Object.class, null);
        table.setShowVerticalLines(true);
        table.setGridColor(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(DARK_BACKGROUND);

        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.setBackground(DARK_BACKGROUND);
        bottomContainer.add(statsPanel, BorderLayout.CENTER);

        createReportButton(bottomContainer, reportName, reportAction);

        panel.add(bottomContainer, BorderLayout.SOUTH);

        return panel;
    }

    private void populateAllSpecificBillsData() {
        populateSpecificBillsData(materialBillsTable, totalMaterialBillsLabel, totalMaterialBillsCostLabel,
                "Matériaux de construction");
        populateSpecificBillsData(electricBillsTable, totalElectricBillsLabel, totalElectricBillsCostLabel,
                "Électricité");
        populateSpecificBillsData(plomberieBillsTable, totalPlomberieBillsLabel, totalPlomberieBillsCostLabel,
                "Plomberie");
    }

    private void populateSpecificBillsData(JTable table, JLabel countLabel, JLabel costLabel, String supplierType) {
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

        try (java.sql.ResultSet rs = billDAO.getBillsBySiteAndSupplierType(site.getId(), supplierType)) {
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

        table.setModel(model);

        // Hide ID column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        // Update totals
        countLabel.setText("Factures totales: " + billCount);
        costLabel.setText(String.format("Coût total: %.2f", totalCost));
    }

    private JPanel createVehiclesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(DARK_BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Stats Panel (Left)
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        statsPanel.setBackground(DARK_BACKGROUND);

        totalVehiclesLabel = new JLabel("Véhicules totaux: 0");
        totalVehiclesLabel.setForeground(Color.WHITE);
        totalVehiclesLabel.setFont(new Font("Arial", Font.BOLD, 14));

        totalMaintenanceCostLabel = new JLabel("Maintenance totale: 0.0");
        totalMaintenanceCostLabel.setForeground(Color.WHITE);
        totalMaintenanceCostLabel.setFont(new Font("Arial", Font.BOLD, 14));

        totalRentCostLabel = new JLabel("Loyer total: 0.0");
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

        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.setBackground(DARK_BACKGROUND);
        bottomContainer.add(statsPanel, BorderLayout.CENTER);
        createReportButton(bottomContainer, "Rapport Véhicules", (start, end) -> generateVehicleReport(start, end));

        panel.add(bottomContainer, BorderLayout.SOUTH);

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
        tabPanel.add(materialTab);
        tabPanel.add(electricTab);
        tabPanel.add(plomberieTab);
        tabPanel.add(vehiclesTab);
        tabPanel.add(costTab);

        // Add panels to card layout
        contentPanel.add(workersPanel, "Travailleurs");
        contentPanel.add(materialPanel, "Matériaux");
        contentPanel.add(electricPanel, "Électricité");
        contentPanel.add(plomberiePanle, "Plomberie");
        contentPanel.add(vehiclesPanel, "Véhicules");
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
        materialTab.setForeground(Color.GRAY);
        electricTab.setForeground(Color.GRAY);
        plomberieTab.setForeground(Color.GRAY);

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

            double workers = breakdown.getOrDefault("workers", 0.0);
            double bills = breakdown.getOrDefault("bills", 0.0);
            double vehicles = breakdown.getOrDefault("vehicles", 0.0);

            totalCostWorkersLabel.setText(String.format("Coût Travailleurs: %.2f", workers));
            totalCostBillsLabel.setText(String.format("Coût Factures: %.2f", bills));
            totalCostVehiclesLabel.setText(String.format("Coût Véhicules: %.2f", vehicles));
            grandTotalCostLabel.setText(String.format("COÛT TOTAL: %.2f", updatedSite.getTotalCost()));

            // Update Pie Chart Dataset
            costDataset.setValue("Travailleurs", workers);
            costDataset.setValue("Factures", bills);
            costDataset.setValue("Véhicules", vehicles);

        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    private void showDateRangeDialog(BiConsumer<LocalDate, LocalDate> onConfirm) {
        JDialog dialog = new JDialog(parentFrame, "Sélectionner la période", true);
        dialog.setLayout(new GridBagLayout());
        dialog.getContentPane().setBackground(DARK_BACKGROUND);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel startLabel = new JLabel("Date début:");
        startLabel.setForeground(Color.WHITE);
        JDateChooser startChooser = new JDateChooser();
        DateChooserConfigurator.configure(startChooser);
        startChooser.setPreferredSize(new Dimension(150, 25));

        JLabel endLabel = new JLabel("Date fin:");
        endLabel.setForeground(Color.WHITE);
        JDateChooser endChooser = new JDateChooser();
        DateChooserConfigurator.configure(endChooser);
        endChooser.setPreferredSize(new Dimension(150, 25));

        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(startLabel, gbc);
        gbc.gridx = 1;
        dialog.add(startChooser, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(endLabel, gbc);
        gbc.gridx = 1;
        dialog.add(endChooser, gbc);

        JButton confirmBtn = new JButton("Générer PDF");
        confirmBtn.setForeground(Color.BLACK); // Changed to black for visibility if default look
        confirmBtn.addActionListener(e -> {
            if (startChooser.getDate() == null || endChooser.getDate() == null) {
                JOptionPane.showMessageDialog(dialog, "Veuillez choisir les deux dates.");
                return;
            }
            LocalDate start = startChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate end = endChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            dialog.dispose();
            onConfirm.accept(start, end);
        });

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(confirmBtn, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
    }

    private void generatePDFReport(LocalDate start, LocalDate end) {
        try {
            // Create default file name
            String defaultFileName = "Rapport_" + site.getName().replaceAll("[^a-zA-Z0-9]", "_") + "_"
                    + start.toString() + ".pdf";

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Enregistrer le rapport PDF");
            fileChooser.setSelectedFile(new File(defaultFileName));

            int userSelection = fileChooser.showSaveDialog(parentFrame);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                String outputPath = fileToSave.getAbsolutePath();

                // Ensure .pdf extension
                if (!outputPath.toLowerCase().endsWith(".pdf")) {
                    outputPath += ".pdf";
                }

                PaymentCheckDAO pcDAO = new PaymentCheckDAO(conn);
                BillDAO billDAO = new BillDAO(conn);
                MaintenanceDAO maintDAO = new MaintenanceDAO(conn);
                VehicleRentalDAO rentDAO = new VehicleRentalDAO(conn);

                ResultSet workersRS = pcDAO.getPaymentChecksSummaryReport(site.getId(), start, end);
                ResultSet billsRS = billDAO.getBillsReport(site.getId(), start, end);
                ResultSet maintenanceRS = maintDAO.getMaintenanceReport(site.getId(), start, end);
                ResultSet rentalsRS = rentDAO.getRentalsReport(site.getId(), start, end);

                SiteReportPDFGenerator.generateReport(site, start, end, outputPath, workersRS, billsRS, maintenanceRS,
                        rentalsRS);

                JOptionPane.showMessageDialog(parentFrame, "Rapport PDF généré avec succès :\n" + outputPath);

                // Open the file automatically
                File pdfFile = new File(outputPath);
                if (pdfFile.exists() && Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(pdfFile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parentFrame, "Erreur lors de la génération du PDF: " + e.getMessage());
        }
    }

    private void generateWorkerReport(LocalDate start, LocalDate end) {
        savePdf("Rapport_Travailleurs", (outputPath) -> {
            try {
                PaymentCheckDAO dao = new PaymentCheckDAO(conn);
                ResultSet rs = dao.getDetailedPaymentCheckReport(site.getId(), start, end);
                TabSpecificPDFGenerator.generateWorkerReport(site, start, end, outputPath, rs);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void generateBillReport(LocalDate start, LocalDate end, String supplierType, String reportName) {
        savePdf(reportName, (outputPath) -> {
            try {
                BillDAO dao = new BillDAO(conn);
                ResultSet rs = dao.getBillDetailsBySupplierTypeReport(site.getId(), supplierType, start, end);
                TabSpecificPDFGenerator.generateBillReport(site, reportName, start, end, outputPath, rs);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void generateVehicleReport(LocalDate start, LocalDate end) {
        savePdf("Rapport_Vehicules", (outputPath) -> {
            try {
                MaintenanceDAO mainDao = new MaintenanceDAO(conn);
                VehicleRentalDAO rentDao = new VehicleRentalDAO(conn);
                ResultSet mainRs = mainDao.getMaintenanceReport(site.getId(), start, end);
                ResultSet rentRs = rentDao.getRentalsReport(site.getId(), start, end);
                TabSpecificPDFGenerator.generateVehicleReport(site, start, end, outputPath, mainRs, rentRs);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void savePdf(String defaultName, java.util.function.Consumer<String> generator) {
        try {
            String defaultFileName = defaultName + "_" + site.getName().replaceAll("[^a-zA-Z0-9]", "_") + ".pdf";

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Enregistrer le rapport PDF");
            fileChooser.setSelectedFile(new File(defaultFileName));

            int userSelection = fileChooser.showSaveDialog(parentFrame);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                String outputPath = fileToSave.getAbsolutePath();
                if (!outputPath.toLowerCase().endsWith(".pdf")) {
                    outputPath += ".pdf";
                }

                generator.accept(outputPath);

                JOptionPane.showMessageDialog(parentFrame, "Rapport généré avec succès !");
                File pdfFile = new File(outputPath);
                if (pdfFile.exists() && Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(pdfFile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parentFrame, "Erreur: " + e.getMessage());
        }
    }
}