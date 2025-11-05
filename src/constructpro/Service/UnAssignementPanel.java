package constructpro.Service;

import constructpro.DAO.WorkerDAO;
import constructpro.DAO.ConstructionSiteDAO;
import constructpro.DTO.ConstructionSite;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class UnAssignementPanel extends JDialog {
    
    private ConstructionSite site;
    private Connection conn;
    private WorkerDAO workerDAO;
    private ConstructionSiteDAO siteDAO;
    private JTable assignedWorkersTable;
    private JButton unassignButton, cancelButton;
    private ShowSitesDetails parentDialog;
    
    public UnAssignementPanel(JFrame parent, ConstructionSite site, Connection connection, ShowSitesDetails parentDialog) throws SQLException {
        super(parent, "Désaffecter des Travailleurs", true);
        this.site = site;
        this.conn = connection;
        this.workerDAO = new WorkerDAO(connection);
        this.siteDAO = new ConstructionSiteDAO(connection);
        this.parentDialog = parentDialog;
        
        initializeComponents();
        setupLayout();
        loadAssignedWorkers();
        
        setSize(600, 400);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }
    
    private void initializeComponents() {
        assignedWorkersTable = new JTable();
        assignedWorkersTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        assignedWorkersTable.setDefaultEditor(Object.class, null);
        assignedWorkersTable.getTableHeader().setReorderingAllowed(false);
        
        unassignButton = new JButton("Désaffecter");
        unassignButton.setBackground(Color.BLACK);
        unassignButton.setForeground(Color.WHITE);
        
        cancelButton = new JButton("Annuler");
        cancelButton.setBackground(Color.BLACK);
        cancelButton.setForeground(Color.WHITE);
        
        unassignButton.addActionListener(e -> unassignSelectedWorkers());
        cancelButton.addActionListener(e -> dispose());
    }
    
    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.BLACK);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JScrollPane scrollPane = new JScrollPane(assignedWorkersTable);
        scrollPane.setBackground(Color.BLACK);
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.BLACK);
        bottomPanel.add(unassignButton);
        bottomPanel.add(cancelButton);
        
        JLabel titleLabel = new JLabel("Travailleurs affectés au chantier : " + site.getName());
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void loadAssignedWorkers() {
        try {
            ResultSet rs = workerDAO.getWorkersBySiteId(site.getId());
            DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Prénom", "Nom", "Fonction", "Téléphone"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("job"),
                    rs.getString("phone_number")
                });
            }
            assignedWorkersTable.setModel(model);
            
            // Hide ID column
            assignedWorkersTable.getColumnModel().getColumn(0).setMinWidth(0);
            assignedWorkersTable.getColumnModel().getColumn(0).setMaxWidth(0);
            assignedWorkersTable.getColumnModel().getColumn(0).setWidth(0);
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des travailleurs : " + ex.getMessage(),
                                          "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void unassignSelectedWorkers() {
        int[] selectedRows = assignedWorkersTable.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner au moins un travailleur à désaffecter.",
                                          "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
                        "Voulez-vous vraiment désaffecter les travailleurs sélectionnés ?",
                        "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        
        try {
            for (int row : selectedRows) {
                int workerId = (int) assignedWorkersTable.getValueAt(row, 0);
                workerDAO.unassignWorker(workerId); // We'll add this method next
            }
            
            JOptionPane.showMessageDialog(this, "Travailleurs désaffectés avec succès.",
                                          "Succès", JOptionPane.INFORMATION_MESSAGE);
            
            // 🔄 Refresh parent dialog
            if (parentDialog != null) {
                parentDialog.refreshWorkersTable();
            }
            
            dispose();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la désaffectation : " + ex.getMessage(),
                                          "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
