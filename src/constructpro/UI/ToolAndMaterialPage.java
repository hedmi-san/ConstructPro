package constructpro.UI;

import constructpro.DAO.ConstructionSiteDAO;
import constructpro.DTO.ConstructionSite;
import constructpro.Service.SiteBillsItemDetailDialog;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import java.sql.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

public class ToolAndMaterialPage extends JPanel{
    
    private JButton refreshButton;
    private JTextField searchText;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JTable sitesTable;
    private JScrollPane jScrollPane1;
    private ConstructionSiteDAO siteDAO;
    public Connection conn;
    private JFrame parentFrame;
    public ToolAndMaterialPage(Connection connection) {
        this.conn = connection;
        initDAO();
        initComponents();
        loadDataSet();
    }
    
    private void initDAO(){
        try {
            siteDAO = new ConstructionSiteDAO(conn);
        } catch (SQLException ex) {
            Logger.getLogger(ToolAndMaterialPage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void initComponents(){
        refreshButton = new JButton("Actualiser");
        searchText = new JTextField(15);
        jLabel1 = new JLabel("Matériel & Outil");
        jLabel2 = new JLabel("Rechercher");
        sitesTable = new JTable();
        jScrollPane1 = new JScrollPane(sitesTable);

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
        sitesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sitesTable.setDefaultEditor(Object.class, null);
        sitesTable.setShowVerticalLines(true);
        sitesTable.setGridColor(Color.WHITE);
        sitesTable.getTableHeader().setReorderingAllowed(false);
        sitesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    showBillsItemsDetails();
                }
            }
        });

        // Buttons panel with white text
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 3));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        buttonPanel.setPreferredSize(new Dimension(0, 30));

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
    }
    
    private void loadDataSet(){
        List<ConstructionSite> sites = siteDAO.getConstructionSiteInfo();
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Nom", "Date de début", "Date de fin", "Lieu"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        for(ConstructionSite site : sites) {
            model.addRow(new Object[]{
                site.getId(),
                site.getName(),
                site.getStartDate() != null ? java.sql.Date.valueOf(site.getStartDate()) : null,
                site.getEndDate() != null ? java.sql.Date.valueOf(site.getEndDate()) : null,
                site.getLocation()
            });
        }
        sitesTable.setModel(model);
        // Hide ID column if desired
        sitesTable.getColumnModel().getColumn(0).setMinWidth(0);
        sitesTable.getColumnModel().getColumn(0).setMaxWidth(0);
        sitesTable.getColumnModel().getColumn(0).setWidth(0);
        
    }

    private void loadSearchResults(String searchTerm) {
        List<ConstructionSite> sites = siteDAO.searchsitesByName(searchTerm);
        DefaultTableModel model = new DefaultTableModel(
                new Object[] {"ID", "Nom", "Date de début", "Date de fin", "Lieu"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (ConstructionSite site : sites) {
            model.addRow(new Object[] {
                site.getId(),
                site.getName(),
                site.getStartDate() != null ? java.sql.Date.valueOf(site.getStartDate()) : null,
                site.getEndDate() != null ? java.sql.Date.valueOf(site.getEndDate()) : null,
                site.getLocation()
            });
        }
        sitesTable.setModel(model);
        sitesTable.getColumnModel().getColumn(0).setMinWidth(0);
        sitesTable.getColumnModel().getColumn(0).setMaxWidth(0);
        sitesTable.getColumnModel().getColumn(0).setWidth(0);
    }
    
    private void showBillsItemsDetails(){
         int selectedRow = sitesTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                DefaultTableModel model = (DefaultTableModel) sitesTable.getModel();
                int siteID = (Integer) model.getValueAt(selectedRow, 0);

                if (Integer.valueOf(siteID) != null) {
                    SiteBillsItemDetailDialog detailDialog = new SiteBillsItemDetailDialog(parentFrame, siteID, conn);
                    detailDialog.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Chantier non trouvé !", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (HeadlessException ex) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors du chargement des détails du chantier : " + ex.getMessage(), "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void setParentFrame(JFrame parent) {
            this.parentFrame = parent;
        }
}
