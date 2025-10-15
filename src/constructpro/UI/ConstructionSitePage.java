package constructpro.UI;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;
import constructpro.DAO.ConstructionSiteDAO;
import constructpro.DTO.ConstructionSite;
import constructpro.Service.ShowSitesDetails;
import constructpro.Service.SiteForm;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import javax.swing.table.DefaultTableModel;

public class ConstructionSitePage extends JPanel {

    private JButton deleteButton;
    private JButton editButton;
    private JButton addButton;
    private JButton refreshButton;
    private JTextField searchText;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JTable sitesTable;
    private JScrollPane jScrollPane1;
    private ConstructionSiteDAO siteDAO;
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
            siteDAO = new ConstructionSiteDAO(conn);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur de connexion à la base de données: " + e.getMessage());
        }
    }

    private void initComponents() {
        addButton = new JButton("Ajouter");
        editButton = new JButton("Modifier");
        deleteButton = new JButton("Supprimer");
        refreshButton = new JButton("Actualiser");
        searchText = new JTextField(15);
        jLabel1 = new JLabel("Chantier");
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
        sitesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    showSiteDetails();
                }
            }
        });

        // Buttons panel with white text
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        buttonPanel.setPreferredSize(new Dimension(0, 60));

        // Set white foreground color for buttons
        deleteButton.setForeground(Color.WHITE);
        editButton.setForeground(Color.WHITE);
        addButton.setForeground(Color.WHITE);

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

    private void setupButtonActions() {
        // Add button action
        addButton.addActionListener(e -> {
            try {
                SiteForm dialog = new SiteForm(parentFrame, "Ajouter un chantier", null, conn);
                dialog.setVisible(true);

                if (dialog.isConfirmed()) {
                    ConstructionSite newSite = dialog.getSiteFromForm();
                    siteDAO.insertConstructionSite(newSite);
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
                    int siteId = (Integer) model.getValueAt(selectedRow, 0);

                    ConstructionSite existingSite = siteDAO.getConstructionSiteById(siteId);
                    if (existingSite != null) {
                        SiteForm dialog = new SiteForm(parentFrame, "Modifier le chantier", existingSite, conn);
                        dialog.setVisible(true);

                        if (dialog.isConfirmed()) {
                            ConstructionSite updatedSite = dialog.getSiteFromForm();
                            updatedSite.setId(siteId);
                            siteDAO.updateConstructionSite(updatedSite);
                            loadDataSet();
                            JOptionPane.showMessageDialog(this, "Chantier modifié avec succès!");
                        }
                    }
                } catch (HeadlessException | SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la modification: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un chantier à modifier.");
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
                        siteDAO.deleteConstructionSite(siteId);
                        loadDataSet();
                        JOptionPane.showMessageDialog(this, "Chantier supprimé avec succès!");
                    } catch (HeadlessException | SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Erreur lors de la suppression: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un chantier à supprimer.");
            }
        });
    }

    private void loadSearchResults(String searchTerm) {
        try {
            ResultSet rs = siteDAO.searchsitesByName(searchTerm);
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
        int selectedRow = sitesTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                DefaultTableModel model = (DefaultTableModel) sitesTable.getModel();
                int siteID = (Integer) model.getValueAt(selectedRow, 0);

                ConstructionSite site = siteDAO.getConstructionSiteById(siteID);
                if (site != null) {
                    ShowSitesDetails detailDialog = new ShowSitesDetails(parentFrame, site, conn);
                    detailDialog.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Chantier non trouvé !", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (HeadlessException | SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors du chargement des détails du chantier : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadDataSet() {
        try {
            ResultSet rs = siteDAO.getConstructionSiteInfo();
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
