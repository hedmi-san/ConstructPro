package constructpro.UI;
import constructpro.DAO.MaterialDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;

public class MaterialPage  extends JPanel{
    
    private JButton deleteButton;
    private JButton editButton;
    private JButton refreshButton;
    private JTextField searchText;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JTable materialTable;
    private JScrollPane jScrollPane1;
    private MaterialDAO materialDAO;
    public Connection conn;
    public MaterialPage(Connection connection){
        this.conn = connection;
        initDAO();
        initComponents();
        loadDataSet();
    }
    
    private void initDAO(){
    materialDAO = new MaterialDAO(conn);
    }
    
    private void initComponents(){
        editButton = new JButton("Modifier");
        deleteButton = new JButton("Supprimer");
        refreshButton = new JButton("Actualiser");
        searchText = new JTextField(15);
        jLabel1 = new JLabel("Matériel");
        jLabel2 = new JLabel("Rechercher");
        materialTable = new JTable();
        jScrollPane1 = new JScrollPane(materialTable);

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
        materialTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        materialTable.setDefaultEditor(Object.class, null);
        materialTable.setShowVerticalLines(true);
        materialTable.setGridColor(Color.WHITE);
        materialTable.getTableHeader().setReorderingAllowed(false);
        materialTable.addMouseListener(new MouseAdapter() {
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
            ResultSet rs = materialDAO.getToolsInfo();
            DefaultTableModel model = new DefaultTableModel(
                    new Object[]{"ID", "Nom", "Quantité", "Prix", "Chantier", "Date"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Make table non-editable
                }
            };

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("material_id"),
                    rs.getString("material_name"),
                    rs.getString("quantity"),
                    rs.getInt("unit_price"),
                    rs.getString("name"),
                    rs.getString("date_acquired")
                });
            }
            materialTable.setModel(model);

            // Hide ID column if desired
            materialTable.getColumnModel().getColumn(0).setMinWidth(0);
            materialTable.getColumnModel().getColumn(0).setMaxWidth(0);
            materialTable.getColumnModel().getColumn(0).setWidth(0);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des données: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
       
    }
    
    private void loadSearchResults(String searchTerm) {
        try {
            ResultSet rs = materialDAO.searchMaterialByName(searchTerm);
            DefaultTableModel model = new DefaultTableModel(
                    new Object[]{"ID", "Nom", "Quantité", "Prix", "Chantier", "Date"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Make table non-editable
                }
            };

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("material_id"),
                    rs.getString("material_name"),
                    rs.getString("quantity"),
                    rs.getInt("unit_price"),
                    rs.getString("name"),
                    rs.getString("date_acquired")
                });
            }
            materialTable.setModel(model);

            // Hide ID column if desired
            materialTable.getColumnModel().getColumn(0).setMinWidth(0);
            materialTable.getColumnModel().getColumn(0).setMaxWidth(0);
            materialTable.getColumnModel().getColumn(0).setWidth(0);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des données: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
