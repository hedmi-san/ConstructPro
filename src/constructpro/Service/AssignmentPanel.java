package constructpro.Service;

import constructpro.DAO.WorkerAssignmentDAO;
import constructpro.DAO.WorkerDAO;
import constructpro.DTO.ConstructionSite;
import constructpro.DTO.Worker;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AssignmentPanel extends JDialog {

    private JTabbedPane tabbedPane;
    private JPanel assignPanel, unassignPanel;
    private JTable assignTable, unassignTable;
    private DefaultTableModel assignModel, unassignModel;
    private JButton saveAssignButton, cancelAssignButton;
    private JButton saveUnassignButton, cancelUnassignButton;

    private Connection conn;
    private ConstructionSite site;
    private WorkerDAO workerDAO;
    private WorkerAssignmentDAO workerAssignmentDAO;
    private boolean confirmed = false;

    public AssignmentPanel(Window parent, ConstructionSite site, Connection connection) {
        super(parent, "Attribuer les travailleurs : " + site.getName(), ModalityType.APPLICATION_MODAL);
        this.conn = connection;
        this.site = site;

        try {
            this.workerDAO = new WorkerDAO(conn);
            this.workerAssignmentDAO = new WorkerAssignmentDAO(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        initComponents();
        loadData();
        setupActions();

        setSize(800, 600);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();

        // Assign Panel
        assignPanel = new JPanel(new BorderLayout());
        assignModel = createWorkerTableModel();
        assignTable = new JTable(assignModel);
        setupTable(assignTable);
        assignPanel.add(new JScrollPane(assignTable), BorderLayout.CENTER);

        JPanel assignButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveAssignButton = new JButton("Affecter");
        cancelAssignButton = new JButton("Annuler");
        assignButtonPanel.add(saveAssignButton);
        assignButtonPanel.add(cancelAssignButton);
        assignPanel.add(assignButtonPanel, BorderLayout.SOUTH);

        // Unassign Panel
        unassignPanel = new JPanel(new BorderLayout());
        unassignModel = createWorkerTableModel();
        unassignTable = new JTable(unassignModel);
        setupTable(unassignTable);
        unassignPanel.add(new JScrollPane(unassignTable), BorderLayout.CENTER);

        JPanel unassignButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveUnassignButton = new JButton("Désaffecter");
        cancelUnassignButton = new JButton("Annuler");
        unassignButtonPanel.add(saveUnassignButton);
        unassignButtonPanel.add(cancelUnassignButton);
        unassignPanel.add(unassignButtonPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("Assigner des travailleurs", assignPanel);
        tabbedPane.addTab("Désassigner des travailleurs", unassignPanel);

        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
    }

    private DefaultTableModel createWorkerTableModel() {
        return new DefaultTableModel(
                new Object[] { "ID", "Prénom", "Nom", "Âge", "Fonction", "Téléphone", "Site Actuel" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void setupTable(JTable table) {
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        // Hide ID column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);
    }

    private void loadData() {
        // Load data for assigning
        assignModel.setRowCount(0);
        List<Worker> notAtSite = workerDAO.getWorkersNotAtSite(site.getId());
        for (Worker w : notAtSite) {
            assignModel.addRow(new Object[] {
                    w.getId(), w.getFirstName(), w.getLastName(), w.getAge(),
                    w.getRole(), w.getPhoneNumber(), w.getSiteName()
            });
        }

        // Load data for unassigning
        unassignModel.setRowCount(0);
        List<Worker> atSite = workerDAO.getWorkersBySiteId(site.getId());
        for (Worker w : atSite) {
            unassignModel.addRow(new Object[] {
                    w.getId(), w.getFirstName(), w.getLastName(), w.getAge(),
                    w.getRole(), w.getPhoneNumber(), site.getName()
            });
        }
    }

    private void setupActions() {
        saveAssignButton.addActionListener(e -> {
            int[] selectedRows = assignTable.getSelectedRows();
            if (selectedRows.length == 0) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner au moins un travailleur.");
                return;
            }

            try {
                for (int row : selectedRows) {
                    int workerId = (int) assignModel.getValueAt(row, 0);
                    Worker w = workerDAO.getWorkerById(workerId);
                    int oldSiteId = w.getAssignedSiteID();

                    // 1. Close old assignment if any (not site 1)
                    if (oldSiteId != 1) {
                        workerAssignmentDAO.updateWorkerAssignment(workerId, oldSiteId, LocalDate.now());
                    }

                    // 2. Assign to new site
                    workerDAO.assignWorkerToSite(workerId, site.getId());

                    // 3. Create new assignment record
                    workerAssignmentDAO.insertAssignment(workerId, site.getId(), LocalDate.now());
                }
                confirmed = true;
                JOptionPane.showMessageDialog(this, "Travailleurs affectés avec succès.");
                loadData();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors de l'affectation: " + ex.getMessage());
            }
        });

        saveUnassignButton.addActionListener(e -> {
            int[] selectedRows = unassignTable.getSelectedRows();
            if (selectedRows.length == 0) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner au moins un travailleur.");
                return;
            }

            try {
                for (int row : selectedRows) {
                    int workerId = (int) unassignModel.getValueAt(row, 0);

                    // 1. Close current assignment
                    workerAssignmentDAO.updateWorkerAssignment(workerId, site.getId(), LocalDate.now());

                    // 2. Unassign worker (set siteId to 1)
                    workerDAO.unassignWorker(workerId);
                }
                confirmed = true;
                JOptionPane.showMessageDialog(this, "Travailleurs désaffectés avec succès.");
                loadData();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors de la désaffectation: " + ex.getMessage());
            }
        });

        cancelAssignButton.addActionListener(e -> dispose());
        cancelUnassignButton.addActionListener(e -> dispose());
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
