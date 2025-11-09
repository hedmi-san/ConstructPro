package constructpro.Service;

import constructpro.DAO.ConstructionSiteDAO;
import constructpro.DAO.WorkerAssignmentDAO;
import constructpro.DAO.WorkerDAO;
import constructpro.DTO.ConstructionSite;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class AssignementPanel extends JDialog {

    private ConstructionSite site;
    private ConstructionSiteDAO siteDAO;
    private WorkerDAO workerDAO;
    private WorkerAssignmentDAO workerAssignmentDAO;
    private Connection conn;

    private JTable workersTable;
    private JButton assignBtn, cancelBtn;
    private DefaultTableModel model;

    // Colors
    private static final Color DARK_BG = new Color(45, 45, 45);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color BTN_BG = new Color(70, 130, 180);
    private ShowSitesDetails parentDialog;
    
    public AssignementPanel(JFrame parent, ConstructionSite site, Connection connection,ShowSitesDetails parentDialog) throws SQLException {
        super(parent, "Affecter des Travailleurs", true);
        this.site = site;
        this.conn = connection;
        this.siteDAO = new ConstructionSiteDAO(connection);
        this.workerDAO = new WorkerDAO(connection);
        this.parentDialog= parentDialog;
        initializeComponents();
        setupLayout();
        populateData();

        setSize(600, 400);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void initializeComponents() {
        model = new DefaultTableModel(
                new Object[]{"Prénom", "Nom", "Fonction", "Téléphone"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        workersTable = new JTable(model);
        workersTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        workersTable.setRowHeight(25);
        workersTable.setBackground(DARK_BG);
        workersTable.setForeground(TEXT_COLOR);
        workersTable.getTableHeader().setBackground(new Color(60, 60, 60));
        workersTable.getTableHeader().setForeground(Color.ORANGE);
        workersTable.setDefaultEditor(Object.class, null);
        workersTable.getTableHeader().setReorderingAllowed(false);
        
        assignBtn = new JButton("Affecter");
        cancelBtn = new JButton("Annuler");
        assignBtn.setBackground(BTN_BG);
        assignBtn.setForeground(Color.WHITE);
        cancelBtn.setBackground(Color.GRAY);
        cancelBtn.setForeground(Color.WHITE);

        assignBtn.addActionListener(e -> assignSelectedWorkers());
        cancelBtn.addActionListener(e -> dispose());
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(DARK_BG);
        tablePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        tablePanel.add(new JScrollPane(workersTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottomPanel.setBackground(DARK_BG);
        bottomPanel.add(assignBtn);
        bottomPanel.add(cancelBtn);

        add(tablePanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void populateData() {
        try {
            ResultSet rs = workerDAO.getUnassignedWorkers();
            model.setRowCount(0);

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("job"),
                        rs.getString("phone_number")
                });
            }

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Aucun travailleur disponible à affecter.",
                        "Information", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des travailleurs: " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void assignSelectedWorkers() {
        int[] selectedRows = workersTable.getSelectedRows();

        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner au moins un travailleur.",
                    "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Voulez-vous vraiment affecter les travailleurs sélectionnés au chantier ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                for (int row : selectedRows) {
                    int workerId = (int) model.getValueAt(row, 0);
                    workerDAO.assignWorkerToSite(workerId, site.getId());
                    workerAssignmentDAO.insertAssignment(workerId, site.getId(), LocalDate.now());
                }

                JOptionPane.showMessageDialog(this,
                        "Travailleurs affectés avec succès au chantier : " + site.getName(),
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                if (parentDialog != null) {
                    parentDialog.refreshWorkersTable();
                }
                dispose();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de l'affectation : " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
