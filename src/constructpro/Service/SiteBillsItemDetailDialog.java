package constructpro.Service;

import javax.swing.*;
import java.sql.*;
import constructpro.DAO.BiLLItemDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.table.DefaultTableModel;

public class SiteBillsItemDetailDialog extends JDialog {
    private JButton deleteButton;
    private JButton editButton;
    private JButton refreshButton;
    private JTextField searchText;
    private JLabel jLabel2;
    private JTable toolsTable;
    private JScrollPane jScrollPane1;
    private BiLLItemDAO toolDAO;
    public Connection conn;
    private int selectedSiteId;

    public SiteBillsItemDetailDialog(JFrame parent, int siteId, Connection connection) {
        super(parent, "Liste des Articles de Factures", true);
        this.toolDAO = new BiLLItemDAO(connection);
        this.selectedSiteId = siteId;

        initializeComponents();

        setSize(900, 650);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initializeComponents() {
        editButton = new JButton("Modifier");
        deleteButton = new JButton("Supprimer");
        refreshButton = new JButton("Actualiser");
        searchText = new JTextField(15);
        jLabel2 = new JLabel("Rechercher");
        toolsTable = new JTable();
        jScrollPane1 = new JScrollPane(toolsTable);

        setLayout(new BorderLayout());
        // Header panel with BorderLayout to separate left and right sections
        JPanel headerPanel = new JPanel(new BorderLayout());

        // Right section with search components
        JPanel rightHeaderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        jLabel2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        rightHeaderPanel.add(jLabel2);
        rightHeaderPanel.add(searchText);
        rightHeaderPanel.add(refreshButton);

        headerPanel.add(rightHeaderPanel, BorderLayout.EAST);

        // Table setup
        toolsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        toolsTable.setDefaultEditor(Object.class, null);
        toolsTable.setShowVerticalLines(true);
        toolsTable.setGridColor(Color.WHITE);
        toolsTable.getTableHeader().setReorderingAllowed(false);
        toolsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    // showWorkerDetails();
                }
            }
        });

        // Buttons panel with white text
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 3));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        buttonPanel.setPreferredSize(new Dimension(0, 30));

        // Set white foreground color for buttons
        deleteButton.setForeground(Color.WHITE);
        editButton.setForeground(Color.WHITE);

        deleteButton.addActionListener(e -> {
            int selectedRow = toolsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un article à supprimer.", "Avertissement",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Êtes-vous sûr de vouloir supprimer cet article ?",
                    "Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    int itemId = (int) toolsTable.getValueAt(selectedRow, 0);
                    toolDAO.deleteBillItem(itemId);
                    loadDataSet(); // Refresh table
                    JOptionPane.showMessageDialog(this, "Article supprimé avec succès.");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la suppression : " + ex.getMessage(), "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);

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
        loadDataSet();
    }

    private void loadDataSet() {
        try {
            ResultSet rs = toolDAO.getBillItemsBySiteId(selectedSiteId);
            DefaultTableModel model = new DefaultTableModel(
                    new Object[] { "ID", "Nom", "Quantité", "Prix", "Type", "Date" }, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Make table non-editable
                }
            };

            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getInt("itemId"),
                        rs.getString("name"),
                        rs.getString("quantity"),
                        rs.getDouble("unitPrice"),
                        rs.getString("itemType"),
                        rs.getString("billDate")
                });
            }
            toolsTable.setModel(model);

            // Hide ID column if desired
            toolsTable.getColumnModel().getColumn(0).setMinWidth(0);
            toolsTable.getColumnModel().getColumn(0).setMaxWidth(0);
            toolsTable.getColumnModel().getColumn(0).setWidth(0);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des données: " + e.getMessage(), "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }

    }

    private void loadSearchResults(String searchTerm) {
        try {
            ResultSet rs = toolDAO.searchBillItemsBySiteId(selectedSiteId, searchTerm);
            DefaultTableModel model = new DefaultTableModel(
                    new Object[] { "ID", "Nom", "Quantité", "Prix", "Type", "Date" }, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Make table non-editable
                }
            };

            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getInt("itemId"),
                        rs.getString("name"),
                        rs.getString("quantity"),
                        rs.getDouble("unitPrice"),
                        rs.getString("itemType"),
                        rs.getString("billDate")
                });
            }
            toolsTable.setModel(model);

            // Hide ID column if desired
            toolsTable.getColumnModel().getColumn(0).setMinWidth(0);
            toolsTable.getColumnModel().getColumn(0).setMaxWidth(0);
            toolsTable.getColumnModel().getColumn(0).setWidth(0);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des données: " + e.getMessage(), "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
