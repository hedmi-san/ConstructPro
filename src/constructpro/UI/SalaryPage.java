package constructpro.UI;

import javax.swing.*;
import java.sql.Connection;
import constructpro.DAO.ConstructionSiteDAO;
import constructpro.DAO.WorkerDAO;
import constructpro.DAO.SalaryDAO;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;

public class SalaryPage extends JPanel{
    private JButton refreshButton;
    private JTextField searchText;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JTable activeSitesTable;
    private JScrollPane jScrollPane1;
    private ConstructionSiteDAO siteDAO;
    private WorkerDAO workerDAO;
    private SalaryDAO salaryDAO;
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
            workerDAO = new WorkerDAO(conn);
            salaryDAO = new SalaryDAO(conn);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur de connexion à la base de données: " + e.getMessage());
        }
    }

    private void initComponents() {
        refreshButton = new JButton("Actualiser");
        searchText = new JTextField(15);
        jLabel1 = new JLabel("Salaire");
        jLabel2 = new JLabel("Rechercher");
        activeSitesTable = new JTable();
        jScrollPane1 = new JScrollPane(activeSitesTable);
        
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        jLabel1.setFont(new Font("SansSerif", Font.BOLD, 24));
        headerPanel.add(jLabel1);
        headerPanel.add(Box.createHorizontalStrut(50));
        jLabel2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        headerPanel.add(jLabel2);
        headerPanel.add(searchText);
        headerPanel.add(refreshButton);
        
        // Table setup
        activeSitesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        activeSitesTable.setDefaultEditor(Object.class, null);
        activeSitesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    //TODO:
                    showSiteAssignedWorkersList();
                }
            }
        });

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
        
    }
    
    private void showSiteAssignedWorkersList(){
        
    }
    
}