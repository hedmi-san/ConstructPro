package constructpro.Service;

import constructpro.DAO.WorkerDAO;
import constructpro.DTO.Worker;
import constructpro.DAO.PaymentCheckDAO;
import constructpro.DAO.BillDAO;
import constructpro.DAO.vehicleSystem.MaintenanceDAO;
import constructpro.DAO.vehicleSystem.VehicleRentalDAO;
import constructpro.DTO.ConstructionSite;
import java.sql.Connection;
import java.util.List;

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
        populateCostData();
        populateBillsData();
        populateVehiclesData();
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
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(DARK_BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel label = new JLabel("Cost information will be displayed here");
        label.setForeground(Color.WHITE);
        panel.add(label, BorderLayout.NORTH);

        return panel;
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

        JLabel label = new JLabel("Véhicules information will be displayed here");
        label.setForeground(Color.WHITE);
        panel.add(label, BorderLayout.NORTH);

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
        List<Worker> workers = workerDao.getWorkersBySiteId(site.getId());
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
        // TODO: Fetch and display cost data from database
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
        // TODO: Fetch and display vehicles data from database
    }

    public void setParentFrame(JFrame parent) {
        this.parentFrame = parent;
    }

    public void refreshWorkersTable() {
        populateWorkersData();
    }
}