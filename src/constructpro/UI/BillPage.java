package constructpro.UI;

import javax.swing.*;
import java.sql.*;
import constructpro.DAO.BillDAO;
import constructpro.DTO.Bill;

import constructpro.Service.BillForm;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;

public class BillPage extends JPanel {
    private JButton refreshButton;
    private JButton deleteButton;
    private JButton editButton;
    private JButton addButton;
    private JTextField searchText;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JTable billsTable;
    private JScrollPane jScrollPane1;
    private JFrame parentFrame;
    private BillDAO billDAO;

    private Connection conn;

    public BillPage(Connection connection) {
        this.conn = connection;
        initDAO();
        initComponents();
        loadDataSet();

    }

    private void initDAO() {
        billDAO = new BillDAO(conn);
    }

    private void initComponents() {
        addButton = new JButton("Ajouter");
        editButton = new JButton("Modifier");
        deleteButton = new JButton("Supprimer");
        refreshButton = new JButton("Actualiser");
        searchText = new JTextField(15);
        jLabel1 = new JLabel("Factures");
        jLabel2 = new JLabel("Rechercher");
        billsTable = new JTable();
        jScrollPane1 = new JScrollPane(billsTable);

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
        billsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        billsTable.setDefaultEditor(Object.class, null);
        billsTable.getTableHeader().setReorderingAllowed(false);
        billsTable.setShowVerticalLines(true);
        billsTable.setGridColor(Color.WHITE);
        billsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    showBillDetails();
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

    private void showBillDetails() {
        // TODO: Implement bill details dialog
    }

    private void loadSearchResults(String searchTerm) {
        try {
            ResultSet rs = billDAO.searchBillsByFactureNumber(searchTerm);
            DefaultTableModel model = new DefaultTableModel(
                    new Object[] { "ID", "Numéro de facture", "Fournisseur", "Chantier", "Date", "Coût Total",
                            "Montant Payé" },
                    0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getInt("id"),
                        rs.getString("factureNumber"),
                        rs.getString("supplierName"),
                        rs.getString("site_name"),
                        rs.getDate("billDate"),
                        String.format("%.2f", rs.getDouble("totalCost")),
                        String.format("%.2f", rs.getDouble("paidAmount"))
                });
            }
            billsTable.setModel(model);

            // Hide ID column
            billsTable.getColumnModel().getColumn(0).setMinWidth(0);
            billsTable.getColumnModel().getColumn(0).setMaxWidth(0);
            billsTable.getColumnModel().getColumn(0).setWidth(0);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la recherche: " + e.getMessage(), "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setupButtonActions() {
        // add button action
        addButton.addActionListener(e -> {
            try {
                BillForm dialog = new BillForm(parentFrame, "Ajouter une Facture", null, conn);
                dialog.setVisible(true);

                if (dialog.isConfirmed()) {
                    Bill newBill = dialog.getBillFromForm();

                    // Insert the bill and get the generated bill ID
                    int billId = billDAO.insertBill(newBill);

                    if (billId > 0) {
                        // Insert all bill items
                        constructpro.DAO.BiLLItemDAO billItemDAO = new constructpro.DAO.BiLLItemDAO(conn);
                        for (constructpro.DTO.BiLLItem item : dialog.getBillItems()) {
                            billItemDAO.insertBillItem(billId, item.getBillType(), item.getItemName(),
                                    item.getQuantity(), item.getUnitPrice());
                        }

                        loadDataSet();
                        JOptionPane.showMessageDialog(this, "Facture ajoutée avec succès!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout de la facture.", "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout: " + ex.getMessage(), "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Edit button action
        editButton.addActionListener(e -> {
            int selectedRow = billsTable.getSelectedRow();
            if (selectedRow >= 0) {
                try {
                    DefaultTableModel model = (DefaultTableModel) billsTable.getModel();
                    int billId = (Integer) model.getValueAt(selectedRow, 0);

                    Bill existingBill = billDAO.getBillById(billId);
                    if (existingBill != null) {
                        BillForm dialog = new BillForm(parentFrame, "Modifier la facture", existingBill, conn);
                        dialog.setVisible(true);

                        if (dialog.isConfirmed()) {
                            Bill updatedBill = dialog.getBillFromForm();
                            updatedBill.setId(billId);
                            billDAO.updateBill(updatedBill);

                            // Update items: Delete old items and insert new ones
                            constructpro.DAO.BiLLItemDAO billItemDAO = new constructpro.DAO.BiLLItemDAO(conn);
                            billItemDAO.deleteBillItems(billId);
                            for (constructpro.DTO.BiLLItem item : dialog.getBillItems()) {
                                billItemDAO.insertBillItem(billId, item.getBillType(), item.getItemName(),
                                        item.getQuantity(), item.getUnitPrice());
                            }

                            loadDataSet();
                            JOptionPane.showMessageDialog(this, "Facture modifié avec succès!");
                        }
                    }
                } catch (HeadlessException | SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la modification: "
                            + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner une facture à modifier.");
            }
        });

        // Delete button action
        // deleteButton.addActionListener(e -> {
        // int selectedRow = sitesTable.getSelectedRow();
        // if (selectedRow >= 0) {
        // DefaultTableModel model = (DefaultTableModel) sitesTable.getModel();
        // String chantierName = model.getValueAt(selectedRow, 1) + " " +
        // model.getValueAt(selectedRow, 2);
        //
        // int confirm = JOptionPane.showConfirmDialog(this,
        // "Êtes-vous sûr de vouloir supprimer le chantier " + chantierName + "?",
        // "Confirmer la suppression",
        // JOptionPane.YES_NO_OPTION);
        //
        // if (confirm == JOptionPane.YES_OPTION) {
        // try {
        // int siteId = (Integer) model.getValueAt(selectedRow, 0);
        // siteDAO.deleteConstructionSite(siteId);
        // loadDataSet();
        // JOptionPane.showMessageDialog(this, "Chantier supprimé avec succès!");
        // } catch (HeadlessException | SQLException ex) {
        // JOptionPane.showMessageDialog(this, "Erreur lors de la suppression: " +
        // ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        // }
        // }
        // } else {
        // JOptionPane.showMessageDialog(this, "Veuillez sélectionner un chantier à
        // supprimer.");
        // }
        // });
    }

    private void loadDataSet() {
        try {
            ResultSet rs = billDAO.getBillsInfo();
            DefaultTableModel model = new DefaultTableModel(
                    new Object[] { "ID", "Numéro de facture", "Fournisseur", "Chantier", "Date", "Coût Total",
                            "Montant Payé" },
                    0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Make table non-editable
                }
            };

            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getInt("id"),
                        rs.getString("factureNumber"),
                        rs.getString("supplierName"),
                        rs.getString("site_name"),
                        rs.getDate("billDate"),
                        String.format("%.2f", rs.getDouble("totalCost")),
                        String.format("%.2f", rs.getDouble("paidAmount"))
                });
            }
            billsTable.setModel(model);

            // Hide ID column
            billsTable.getColumnModel().getColumn(0).setMinWidth(0);
            billsTable.getColumnModel().getColumn(0).setMaxWidth(0);
            billsTable.getColumnModel().getColumn(0).setWidth(0);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des données: " + e.getMessage(), "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
