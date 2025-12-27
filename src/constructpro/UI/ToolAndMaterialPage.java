package constructpro.UI;

import constructpro.DAO.BiLLItemDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class ToolAndMaterialPage extends JPanel{
    
    private JButton deleteButton;
    private JButton editButton;
    private JButton refreshButton;
    private JTextField searchText;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JTable toolsTable;
    private JScrollPane jScrollPane1;
    private BiLLItemDAO toolDAO;
    public Connection conn;
    public ToolAndMaterialPage(Connection connection) {
        this.conn = connection;
        initDAO();
        initComponents();
        loadDataSet();
    }
    
    private void initDAO(){
        toolDAO = new BiLLItemDAO(conn);
    }
    
    private void initComponents(){
        editButton = new JButton("Modifier");
        deleteButton = new JButton("Supprimer");
        refreshButton = new JButton("Actualiser");
        searchText = new JTextField(15);
        jLabel1 = new JLabel("Matériel & Outil");
        jLabel2 = new JLabel("Rechercher");
        toolsTable = new JTable();
        jScrollPane1 = new JScrollPane(toolsTable);

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
        toolsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        toolsTable.setDefaultEditor(Object.class, null);
        toolsTable.setShowVerticalLines(true);
        toolsTable.setGridColor(Color.WHITE);
        toolsTable.getTableHeader().setReorderingAllowed(false);
        toolsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    //showWorkerDetails();
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
    }
    
    private void loadDataSet(){
        try {
            ResultSet rs = toolDAO.getItemsInfo();
            DefaultTableModel model = new DefaultTableModel(
                    new Object[]{"ID", "Nom", "Quantité", "Prix", "Type", "Chantier", "Date"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Make table non-editable
                }
            };

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("itemId"),
                    rs.getString("name"),
                    rs.getString("quantity"),
                    rs.getDouble("unitPrice"),
                    rs.getString("itemType"),
                    rs.getString("site_name"),
                    rs.getString("billDate")
                });
            }
            toolsTable.setModel(model);

            // Hide ID column if desired
            toolsTable.getColumnModel().getColumn(0).setMinWidth(0);
            toolsTable.getColumnModel().getColumn(0).setMaxWidth(0);
            toolsTable.getColumnModel().getColumn(0).setWidth(0);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des données: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        
    }

    private void loadSearchResults(String searchTerm) {
        try {
            ResultSet rs = toolDAO.searchItemByName(searchTerm);
            DefaultTableModel model = new DefaultTableModel(
                    new Object[]{"ID", "Nom", "Quantité", "Prix", "Type", "Chantier", "Date"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Make table non-editable
                }
            };

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("itemId"),
                    rs.getString("name"),
                    rs.getString("quantity"),
                    rs.getDouble("unitPrice"),
                    rs.getString("itemType"),
                    rs.getString("site_name"),
                    rs.getString("billDate")
                });
            }
            toolsTable.setModel(model);

            // Hide ID column if desired
            toolsTable.getColumnModel().getColumn(0).setMinWidth(0);
            toolsTable.getColumnModel().getColumn(0).setMaxWidth(0);
            toolsTable.getColumnModel().getColumn(0).setWidth(0);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des données: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
