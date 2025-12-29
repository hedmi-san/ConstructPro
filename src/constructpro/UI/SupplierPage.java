package constructpro.UI;

import constructpro.DAO.SupplierDAO;
import constructpro.DTO.Supplier;
import constructpro.Service.SupplierDetailDialog;
import constructpro.Service.SupplierForm;
import java.awt.*;
import java.awt.HeadlessException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import javax.swing.table.DefaultTableModel;

public class SupplierPage extends JPanel {

    private JButton addButton;
    private JButton deleteButton;
    private JButton editButton;
    private JButton refreshButton;
    private JTextField searchText;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JTable suppliersTable;
    private JScrollPane jScrollPane1;
    private SupplierDAO supplierDAO;
    private JFrame parentFrame;
    public Connection conn;

    public SupplierPage(Connection connection) {
        this.conn = connection;
        initDAO();
        initComponents();
        loadDataSet();
    }

    private void initDAO() {
        supplierDAO = new SupplierDAO(conn);
    }

    private void initComponents() {
        addButton = new JButton("Ajouter");
        editButton = new JButton("Modifier");
        deleteButton = new JButton("Supprimer");
        refreshButton = new JButton("Actualiser");
        searchText = new JTextField(15);
        jLabel1 = new JLabel("Fournisseur");
        jLabel2 = new JLabel("Rechercher");
        suppliersTable = new JTable();
        jScrollPane1 = new JScrollPane(suppliersTable);

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
        suppliersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        suppliersTable.setDefaultEditor(Object.class, null);
        suppliersTable.getTableHeader().setReorderingAllowed(false);
        suppliersTable.setShowVerticalLines(true);
        suppliersTable.setGridColor(Color.WHITE);
        suppliersTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    showSupplierDetails();
                }
            }
        });

        // Buttons panel with white text
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        buttonPanel.setPreferredSize(new Dimension(0, 60));

        // Set white foreground color for buttons
        addButton.setForeground(Color.WHITE);
        deleteButton.setForeground(Color.WHITE);
        editButton.setForeground(Color.WHITE);

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
    
    private void showSupplierDetails(){
        int selectedRow = suppliersTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                DefaultTableModel model = (DefaultTableModel) suppliersTable.getModel();
                int supplierId = (Integer) model.getValueAt(selectedRow, 0);
                Supplier supplier = supplierDAO.getSupplierById(supplierId);
                if (supplier != null) {
                    SupplierDetailDialog detailDialog = new SupplierDetailDialog(parentFrame, supplier, conn);
                    detailDialog.setVisible(true);
                }else {
                    JOptionPane.showMessageDialog(this, "Facture non trouvé !", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }catch (HeadlessException | SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors du chargement des détails de la facture : " + ex.getMessage(), "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void setupButtonActions() {
        // Add button action
        addButton.addActionListener(e -> {
            try {
                SupplierForm dialog = new SupplierForm(parentFrame, "Ajouter un Fournisseur", null, conn);
                dialog.setVisible(true);

                if (dialog.isConfirmed()) {
                    Supplier newSupplier = dialog.getSupplierFromForm();
                    supplierDAO.insertSupplier(newSupplier);
                    loadDataSet();
                    JOptionPane.showMessageDialog(this, "Fournisseur ajouté avec succès!");
                }
            } catch (HeadlessException | SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout: " + ex.getMessage(), "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Edit button action
        editButton.addActionListener(e -> {
            int selectedRow = suppliersTable.getSelectedRow();
            if (selectedRow >= 0) {
                try {
                    DefaultTableModel model = (DefaultTableModel) suppliersTable.getModel();
                    int supplierId = (Integer) model.getValueAt(selectedRow, 0); // Assuming ID is in first column

                    Supplier existingWorker = supplierDAO.getSupplierById(supplierId);
                    if (existingWorker != null) {
                        SupplierForm dialog = new SupplierForm(parentFrame, "Modifier le Fournisseur", existingWorker,
                                conn);
                        dialog.setVisible(true);

                        if (dialog.isConfirmed()) {
                            Supplier updatedSupplier = dialog.getSupplierFromForm();
                            updatedSupplier.setId(supplierId);
                            supplierDAO.updateSupplier(updatedSupplier);
                            loadDataSet();
                            JOptionPane.showMessageDialog(this, "Fournisseur modifié avec succès!");
                        }
                    }
                } catch (HeadlessException | SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la modification: " + ex.getMessage(), "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un fournisseur à modifier.");
            }
        });

        // Delete button action
        deleteButton.addActionListener(e -> {
            int selectedRow = suppliersTable.getSelectedRow();
            if (selectedRow >= 0) {
                DefaultTableModel model = (DefaultTableModel) suppliersTable.getModel();
                String supplierName = model.getValueAt(selectedRow, 1).toString();

                int confirm = JOptionPane.showConfirmDialog(this,
                        "Êtes-vous sûr de vouloir supprimer le fournisseur " + supplierName + "?",
                        "Confirmer la suppression",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        int supplierId = (Integer) model.getValueAt(selectedRow, 0); // Assuming ID is in first column
                        supplierDAO.deleteWorker(supplierId);
                        loadDataSet();
                        JOptionPane.showMessageDialog(this, "Ouvrier supprimé avec succès!");
                    } catch (HeadlessException | SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Erreur lors de la suppression: " + ex.getMessage(),
                                "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un ouvrier à supprimer.");
            }
        });
    }

    private void loadDataSet() {
        try {
            ResultSet rs = supplierDAO.getSuppliersInfo();
            DefaultTableModel model = new DefaultTableModel(
                    new Object[] { "ID", "Nom", "Numéro de téléphone", "Adresse", "Total dépensé", "Total payé" }, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Make table non-editable
                }
            };

            DecimalFormat df = new DecimalFormat("#,##0.00");
            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getInt("id"),
                        rs.getString("supplierName"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        df.format(rs.getDouble("totalSpent")),
                        df.format(rs.getDouble("totalPaid"))
                });
            }
            suppliersTable.setModel(model);

            // Hide ID column if desired
            suppliersTable.getColumnModel().getColumn(0).setMinWidth(0);
            suppliersTable.getColumnModel().getColumn(0).setMaxWidth(0);
            suppliersTable.getColumnModel().getColumn(0).setWidth(0);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des données: " + e.getMessage(), "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSearchResults(String searchterm) {
        try {
            ResultSet rs = supplierDAO.searchSupplierByName(searchterm);
            DefaultTableModel model = new DefaultTableModel(
                    new Object[] { "ID", "Nom", "Numéro de téléphone", "Adresse", "Total dépensé", "Total payé" }, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            DecimalFormat df = new DecimalFormat("#,##0.00");
            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getInt("id"),
                        rs.getString("supplierName"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        df.format(rs.getDouble("totalSpent")),
                        df.format(rs.getDouble("totalPaid"))
                });
            }
            suppliersTable.setModel(model);
            suppliersTable.getColumnModel().getColumn(0).setMinWidth(0);
            suppliersTable.getColumnModel().getColumn(0).setMaxWidth(0);
            suppliersTable.getColumnModel().getColumn(0).setWidth(0);

        } catch (SQLException e) {
        }
    }

    public void setParentFrame(JFrame parent) {
        this.parentFrame = parent;
    }
}
