package constructpro.Service;

import constructpro.DAO.ConstructionSiteDAO;
import constructpro.DAO.WorkerDAO;
import constructpro.DTO.ConstructionSite;
import java.sql.Connection;
import java.sql.SQLException;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;

public class ShowSitesDetails extends JDialog {
    
    private Connection conn;
    private ConstructionSiteDAO siteDao;
    private WorkerDAO workerDao;
    private ConstructionSite site;
    
    //Colors
    private static final Color DARK_BACKGROUND = new Color(45, 45, 45);
    // Labels for site info
    private JLabel siteNameLabel, locationLabel, statusLabel, startDateLabel, endDateLabel;
    
    // Panels
    private JPanel mainPanel, headerPanel, tabPanel, contentPanel, bottomPanel;
    private JPanel infoPanel, workersPanel, costPanel, billsPanel;
    
    // Tab buttons
    private JButton infoTab, workersTab, costTab, billsTab,supprimerBtn,ajouterBtn;
    private JTable workersTable;
    private CardLayout cardLayout;
    
    public ShowSitesDetails(JFrame parent, ConstructionSite site, Connection connection) throws SQLException {
        super(parent, "Détails du chantier", true);
        this.site = site;
        this.siteDao = new ConstructionSiteDAO(connection);
        this.workerDao = new WorkerDAO(connection);
        this.conn = connection;
        
        initializeComponents();
        setupLayout();
        setupStyling();
        populateData();
        populateWorkersData();
        populateCostData();
        populateBillsData();
        
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
        
        //Initialize the table
        workersTable = new JTable();
        
        //Create Buttons
        supprimerBtn = new JButton("Supprimer");
        ajouterBtn = new JButton("Affecter");
        
        // Create tab panels
        infoPanel = createInfoPanel();
        workersPanel = createWorkersPanel();
        costPanel = createCostPanel();
        billsPanel = createBillsPanel();
        
        // Create tab buttons
        infoTab = createTabButton("Info");
        workersTab = createTabButton("workers");
        costTab = createTabButton("Cost");
        billsTab = createTabButton("Bills");

        // Add action listeners to tabs
        infoTab.addActionListener(e -> switchTab("Info", infoTab));
        workersTab.addActionListener(e -> switchTab("workers", workersTab));
        costTab.addActionListener(e -> switchTab("Cost", costTab));
        billsTab.addActionListener(e -> switchTab("Bills", billsTab));
        
        //Add action listeners to buttons
        supprimerBtn.addActionListener(e-> unassignWorkers());
        ajouterBtn.addActionListener(e -> assignWorkers());
      
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
        infoTab.setForeground(Color.GRAY);
        workersTab.setForeground(Color.GRAY);
        costTab.setForeground(Color.GRAY);
        billsTab.setForeground(Color.GRAY);
        
        // Highlight selected tab
        selectedButton.setForeground(Color.ORANGE);
    }
    
    private void unassignWorkers(){}
    private void assignWorkers(){}
    
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(DARK_BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create labels
        siteNameLabel = createInfoLabel("Name :");
        locationLabel = createInfoLabel("Location :");
        statusLabel = createInfoLabel("Status :");
        startDateLabel = createInfoLabel("Start Date :");
        endDateLabel = createInfoLabel("End Date :");
        
        panel.add(siteNameLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(locationLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(statusLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(startDateLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(endDateLabel);
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    
    private JPanel createWorkersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(DARK_BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        bottomPanel.setBackground(DARK_BACKGROUND);
        workersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        workersTable.setDefaultEditor(Object.class, null);
        JScrollPane scrollPane = new JScrollPane(workersTable);
        scrollPane.setBackground(DARK_BACKGROUND);
        bottomPanel.add(supprimerBtn);
        bottomPanel.add(ajouterBtn);
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
    
    private JPanel createBillsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(DARK_BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel label = new JLabel("Bills information will be displayed here");
        label.setForeground(Color.WHITE);
        panel.add(label, BorderLayout.NORTH);
        
        return panel;
    }
    
    private void setupLayout() {
        // Header with title and close button
        JLabel titleLabel = new JLabel("Chantier name");
        titleLabel.setForeground(DARK_BACKGROUND);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(new EmptyBorder(10, 20, 10, 10));
        
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.setBackground(DARK_BACKGROUND);
        
        // Tab panel
        tabPanel.setBackground(DARK_BACKGROUND);
        tabPanel.setBorder(new EmptyBorder(0, 10, 0, 0));
        tabPanel.add(infoTab);
        tabPanel.add(workersTab);
        tabPanel.add(costTab);
        tabPanel.add(billsTab);
        
        // Add panels to card layout
        contentPanel.add(infoPanel, "Info");
        contentPanel.add(workersPanel, "workers");
        contentPanel.add(costPanel, "Cost");
        contentPanel.add(billsPanel, "Bills");
        
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
        infoTab.setForeground(Color.WHITE);
        workersTab.setForeground(Color.GRAY);
        costTab.setForeground(Color.GRAY);
        billsTab.setForeground(Color.GRAY);
        
        // Set background
        getContentPane().setBackground(Color.BLACK);
    }
    
    private void populateData() {
        if (site != null) {
            siteNameLabel.setText("Name : " + site.getName());
            locationLabel.setText("Location : " + site.getLocation());
            statusLabel.setText("Status : " + site.getStatus());
            startDateLabel.setText("Start Date : " + (site.getStartDate() != null ? site.getStartDate().toString() : "N/A"));
            endDateLabel.setText("End Date : " + (site.getEndDate() != null ? site.getEndDate().toString() : "N/A"));
        }
    }
    
    private void populateWorkersData() {
        // TODO: Fetch and display workers data from database
        // Example: Create a JTable with worker information
        try {
            ResultSet rs = workerDao.getWorkersBySiteId(site.getId());
            DefaultTableModel model = new DefaultTableModel(
                    new Object[]{"ID", "Prénom", "Nom", "Âge", "Fonction", "Téléphone"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getInt("age"),
                    rs.getString("job"),
                    rs.getString("phone_number")
                });
            }
            workersTable.setModel(model);

            // Hide ID column if desired
            workersTable.getColumnModel().getColumn(0).setMinWidth(0);
            workersTable.getColumnModel().getColumn(0).setMaxWidth(0);
            workersTable.getColumnModel().getColumn(0).setWidth(0);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des données: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        
    }
    
    private void populateCostData() {
        // TODO: Fetch and display cost data from database
    }
    
    private void populateBillsData() {
        // TODO: Fetch and display bills data from database
    }
}