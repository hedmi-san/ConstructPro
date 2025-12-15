package constructpro.Service;

import constructpro.DAO.WorkerDAO;
import constructpro.DTO.Worker;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class WorkerSelectionDialog extends JDialog {

    private WorkerDAO workerDAO;
    private Connection conn;
    private List<Worker> selectedWorkers;
    private boolean confirmed = false;

    private JTable workersTable;
    private JButton selectBtn, cancelBtn;
    private DefaultTableModel model;
    private List<Worker> allWorkers;

    // Colors
    private static final Color DARK_BG = new Color(45, 45, 45);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color BTN_BG = new Color(70, 130, 180);

    public WorkerSelectionDialog(Window parent, Connection connection) {
        super(parent, "Sélectionner des Travailleurs", ModalityType.APPLICATION_MODAL);
        this.conn = connection;
        try {
            this.workerDAO = new WorkerDAO(connection);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur initialisation DAO: " + e.getMessage());
        }
        this.selectedWorkers = new ArrayList<>();

        initializeComponents();
        setupLayout();
        if (this.workerDAO != null) {
            populateData();
        }

        setSize(600, 400);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initializeComponents() {
        model = new DefaultTableModel(
                new Object[] { "ID", "Prénom", "Nom", "Fonction" }, 0) {
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

        selectBtn = new JButton("Sélectionner");
        cancelBtn = new JButton("Annuler");
        selectBtn.setBackground(BTN_BG);
        selectBtn.setForeground(Color.WHITE);
        cancelBtn.setBackground(Color.GRAY);
        cancelBtn.setForeground(Color.WHITE);

        selectBtn.addActionListener(e -> confirmSelection());
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
        bottomPanel.add(selectBtn);
        bottomPanel.add(cancelBtn);

        add(tablePanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void populateData() {
        try {
            allWorkers = workerDAO.getWorkers();
            model.setRowCount(0);

            for (Worker w : allWorkers) {
                model.addRow(new Object[] {
                        w.getId(),
                        w.getFirstName(),
                        w.getLastName(),
                        w.getRole()
                });
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des travailleurs: " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void confirmSelection() {
        int[] selectedRows = workersTable.getSelectedRows();

        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner au moins un travailleur.",
                    "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (int row : selectedRows) {
            // Logic to find actual worker from list - ensuring index consistency or using
            // ID
            // Simple approach: ID is in column 0
            int workerId = (int) model.getValueAt(row, 0);
            for (Worker w : allWorkers) {
                if (w.getId() == workerId) {
                    selectedWorkers.add(w);
                    break;
                }
            }
        }
        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public List<Worker> getSelectedWorkers() {
        return selectedWorkers;
    }
}
