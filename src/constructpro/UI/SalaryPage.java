package constructpro.UI;

import javax.swing.*;
import java.sql.Connection;
import constructpro.DAO.ConstructionSiteDAO;
import constructpro.DTO.ConstructionSite;
import constructpro.Service.PaySlip;
import constructpro.Service.WorkerList;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;

public class SalaryPage extends JPanel{
    private JButton refreshButton,paymentCheckPdfButton;
    private JTextField searchText;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JTable activeSitesTable;
    private JScrollPane jScrollPane1;
    private ConstructionSiteDAO siteDAO;
    private JFrame parentFrame;
    public Connection conn;
    
    public SalaryPage(Connection connection) {
        this.conn = connection;
        initDAO();
        initComponents();
        loadDataSet();
    }
    
    public SalaryPage(JFrame parent) {
        this.parentFrame = parent;
        initDAO();
        initComponents();
        loadDataSet();
    }

    private void initDAO() {
        try {
            siteDAO = new ConstructionSiteDAO(conn);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur de connexion à la base de données: " + e.getMessage());
        }
    }

    private void initComponents() {
        refreshButton = new JButton("Actualiser");
        paymentCheckPdfButton = new JButton("Fiche de Paie");
        searchText = new JTextField(15);
        jLabel1 = new JLabel("Salaire");
        jLabel2 = new JLabel("Rechercher");
        activeSitesTable = new JTable();
        jScrollPane1 = new JScrollPane(activeSitesTable);

        setLayout(new BorderLayout());

        // Header panel with BorderLayout to separate left and right sections
        JPanel headerPanel = new JPanel(new BorderLayout());

        // Left section with "Chantier" label
        JPanel leftHeaderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        jLabel1.setFont(new Font("SansSerif", Font.BOLD, 24));
        leftHeaderPanel.add(jLabel1);

        // Right section with search components
        JPanel rightHeaderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        jLabel2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        rightHeaderPanel.add(jLabel2);
        rightHeaderPanel.add(searchText);
        rightHeaderPanel.add(refreshButton);

        // Add both sections to header panel
        headerPanel.add(leftHeaderPanel, BorderLayout.WEST);
        headerPanel.add(rightHeaderPanel, BorderLayout.EAST);

        // Table setup
        activeSitesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        activeSitesTable.setDefaultEditor(Object.class, null);
        activeSitesTable.setShowVerticalLines(true);
        activeSitesTable.setGridColor(Color.WHITE);
        activeSitesTable.getTableHeader().setReorderingAllowed(false);
        activeSitesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    showSiteAssignedWorkersList();
                }
            }
        });

        // Buttons panel with white text
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        buttonPanel.setPreferredSize(new Dimension(0, 60));
        
        paymentCheckPdfButton.setForeground(Color.WHITE);
        buttonPanel.add(paymentCheckPdfButton);

        refreshButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        refreshButton.addActionListener(e -> {
            String searchTerm = searchText.getText().trim();
            if (searchTerm.isEmpty()) {
                loadDataSet();
            } else {
                loadSearchResults(searchTerm);
            }
        });

        add(headerPanel, BorderLayout.NORTH);
        add(jScrollPane1, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        setupButtonActions();
    }
    
    private void setupButtonActions(){
        paymentCheckPdfButton.addActionListener(e -> {
            try {
                PaySlip dialog = new PaySlip(conn,parentFrame);
                dialog.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    
    }

    private void loadDataSet() {
        try {
            ResultSet rs = siteDAO.getActiveConstructionSiteInfo();
            DefaultTableModel model = new DefaultTableModel(
                    new Object[]{"ID", "Nom", "Lieu","Date de début", "Date de fin"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Make table non-editable
                }
            };

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("Id"),
                        rs.getString("Name"),
                        rs.getString("Location"),
                        rs.getDate("Start_Date"),
                        rs.getDate("End_date")
                });
            }
            activeSitesTable.setModel(model);
            
            // Hide ID column if desired
            activeSitesTable.getColumnModel().getColumn(0).setMinWidth(0);
            activeSitesTable.getColumnModel().getColumn(0).setMaxWidth(0);
            activeSitesTable.getColumnModel().getColumn(0).setWidth(0);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des données: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadSearchResults(String searchterm){
        try {
            ResultSet rs = siteDAO.getSpecificActiveConstructionSiteInfo(searchterm);
            DefaultTableModel model = new DefaultTableModel(
                    new Object[]{"ID", "Nom", "Lieu","Date de début", "Date de fin"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("Id"),
                    rs.getString("Name"),
                    rs.getString("Location"),
                    rs.getDate("Start_Date"),
                    rs.getDate("End_date")
                });
            }
            activeSitesTable.setModel(model);
            activeSitesTable.getColumnModel().getColumn(0).setMinWidth(0);
            activeSitesTable.getColumnModel().getColumn(0).setMaxWidth(0);
            activeSitesTable.getColumnModel().getColumn(0).setWidth(0);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void showSiteAssignedWorkersList(){
        int selectedRow = activeSitesTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                DefaultTableModel model = (DefaultTableModel) activeSitesTable.getModel();
                int siteID = (Integer) model.getValueAt(selectedRow, 0); // Get worker ID from hidden column

                ConstructionSite site = siteDAO.getConstructionSiteById(siteID);
                if (site != null) {
                    WorkerList workerListDialog = new WorkerList(parentFrame, site, conn);
                    workerListDialog.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "La liste des travailleurs est introuvable.!", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (HeadlessException | SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors du chargement la liste des travailleursé : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
}