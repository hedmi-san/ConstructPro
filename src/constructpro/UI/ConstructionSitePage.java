package constructpro.UI;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;
import constructpro.DAO.ConstructionSiteDAO;
import constructpro.DTO.ConstructionSite;
import constructpro.Service.SiteForm;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import javax.swing.table.DefaultTableModel;


public class ConstructionSitePage extends JPanel{
    
    private JButton deleteButton;
    private JButton editButton;
    private JButton addButton;
    private JButton refreshButton;
    private JTextField searchText;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JTable sitesTable;
    private JScrollPane jScrollPane1;
    private ConstructionSiteDAO SiteDAO;
    private JFrame parentFrame;
    public Connection conn;
    public ConstructionSitePage(Connection connection) {
        this.conn = connection;
        initDAO();
        initComponents();
        loadDataSet();
    }
    
    public ConstructionSitePage(JFrame parent) {
        this.parentFrame = parent;
        initDAO();
        initComponents();
        loadDataSet();
    }
    
    private void initDAO() {
        try {
            SiteDAO = new ConstructionSiteDAO(conn);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur de connexion à la base de données: " + e.getMessage());
        }
    }
    
    private void initComponents() {
        addButton = new JButton("Add");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");
        refreshButton = new JButton("Refresh");
        searchText = new JTextField(15);
        jLabel1 = new JLabel("Chantier");
        jLabel2 = new JLabel("Search");
        sitesTable = new JTable();
        jScrollPane1 = new JScrollPane(sitesTable);
        
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
        sitesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sitesTable.setDefaultEditor(Object.class, null);

        sitesTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    //TODO 
                    //showSitesDetails();
                }
            }
        });

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);
        buttonPanel.add(addButton);

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
        // Add button action
        addButton.addActionListener(e -> {
            try {
                SiteForm dialog = new SiteForm(parentFrame,"Ajouter un chantier",null,conn);
                dialog.setVisible(true);
        
                if (dialog.isConfirmed()) {
                    ConstructionSite newSite = dialog.getSiteFromForm();
                    SiteDAO.insertConstructionSite(newSite);
                    loadDataSet();
                    JOptionPane.showMessageDialog(this, "Chantier ajouté avec succès!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Edit button action
        editButton.addActionListener(e -> {
            int selectedRow = sitesTable.getSelectedRow();
            if (selectedRow >= 0) {
                try {
                    DefaultTableModel model = (DefaultTableModel) sitesTable.getModel();
                    int siteId = (Integer) model.getValueAt(selectedRow, 0); // Assuming ID is in first column
                    
                    ConstructionSite existingSite = SiteDAO.getConstructionSiteById(siteId);
                    if (existingSite != null) {
                        SiteForm dialog = new SiteForm(parentFrame, "Modifier le chantier", existingSite,conn);
                        dialog.setVisible(true);
                        
                        if (dialog.isConfirmed()) {
                            ConstructionSite updatedSite = dialog.getSiteFromForm();
                            updatedSite.setId(siteId);
                            SiteDAO.updateConstructionSite(updatedSite);
                            loadDataSet();
                            JOptionPane.showMessageDialog(this, "Ouvrier modifié avec succès!");
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Erreur lors de la modification: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un ouvrier à modifier.");
            }
        });
        
        // Delete button action
        deleteButton.addActionListener(e -> {
            int selectedRow = sitesTable.getSelectedRow();
            if (selectedRow >= 0) {
                DefaultTableModel model = (DefaultTableModel) sitesTable.getModel();
                String chantierName = model.getValueAt(selectedRow, 1) + " " + model.getValueAt(selectedRow, 2);
                
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Êtes-vous sûr de vouloir supprimer le chantier " + chantierName + "?",
                    "Confirmer la suppression", 
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        int siteId = (Integer) model.getValueAt(selectedRow, 0); 
                        SiteDAO.deleteConstructionSite(siteId);
                        loadDataSet();
                        JOptionPane.showMessageDialog(this, "Chantier supprimé avec succès!");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Erreur lors de la suppression: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un chantier à supprimer.");
            }
        });
    }
    
    private void loadSearchResults(String searchTerm){
        try {
        ResultSet rs = SiteDAO.searchsitesByName(searchTerm);
        DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Nom", "Lieu", "Etat", "Date de début", "Date de fin", "Coût Total"}, 0
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
                        rs.getString("Status"),
                        rs.getDate("Start_Date"),
                        rs.getDate("End_date"),
                        rs.getString("Total_Cost")
            });
        }
        sitesTable.setModel(model);
        sitesTable.getColumnModel().getColumn(0).setMinWidth(0);
        sitesTable.getColumnModel().getColumn(0).setMaxWidth(0);
        sitesTable.getColumnModel().getColumn(0).setWidth(0);

        } catch (SQLException e) {
        e.printStackTrace();
        }
    }
    
    private void showSiteDetails() {
        
    }
    
    private void loadDataSet() {
        try {
            ResultSet rs = SiteDAO.getConstructionSiteInfo();
            DefaultTableModel model = new DefaultTableModel(
                    new Object[]{"ID", "Nom", "Lieu", "Etat", "Date de début", "Date de fin", "Coût Total"}, 0
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
                        rs.getString("Status"),
                        rs.getDate("Start_Date"),
                        rs.getDate("End_date"),
                        rs.getString("Total_Cost")
                });
            }
            sitesTable.setModel(model);
            
            // Hide ID column if desired
            sitesTable.getColumnModel().getColumn(0).setMinWidth(0);
            sitesTable.getColumnModel().getColumn(0).setMaxWidth(0);
            sitesTable.getColumnModel().getColumn(0).setWidth(0);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des données: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
}
