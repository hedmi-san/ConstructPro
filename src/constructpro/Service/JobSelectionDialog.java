package constructpro.Service;

import constructpro.DAO.WorkerDAO;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class JobSelectionDialog extends JDialog {

    private WorkerDAO workerDAO;
    private Connection conn;
    private List<String> selectedJobs;
    private boolean confirmed = false;

    private JTable jobsTable;
    private JButton selectBtn, cancelBtn;
    private DefaultTableModel model;
    private List<String> allJobs;

    // Colors
    private static final Color DARK_BG = new Color(45, 45, 45);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color BTN_BG = new Color(70, 130, 180);

    public JobSelectionDialog(Window parent, Connection connection) {
        super(parent, "Sélectionner des Appellations (Jobs)", ModalityType.APPLICATION_MODAL);
        this.conn = connection;
        try {
            this.workerDAO = new WorkerDAO(connection);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur initialisation DAO: " + e.getMessage());
        }
        this.selectedJobs = new ArrayList<>();

        initializeComponents();
        setupLayout();
        if (this.workerDAO != null) {
            populateData();
        }

        setSize(400, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initializeComponents() {
        model = new DefaultTableModel(new Object[] { "Fonction / Job" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        jobsTable = new JTable(model);
        jobsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jobsTable.setRowHeight(25);
        jobsTable.setBackground(DARK_BG);
        jobsTable.setForeground(TEXT_COLOR);
        jobsTable.getTableHeader().setBackground(new Color(60, 60, 60));
        jobsTable.getTableHeader().setForeground(Color.ORANGE);
        jobsTable.setDefaultEditor(Object.class, null);
        jobsTable.getTableHeader().setReorderingAllowed(false);

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
        tablePanel.add(new JScrollPane(jobsTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottomPanel.setBackground(DARK_BG);
        bottomPanel.add(selectBtn);
        bottomPanel.add(cancelBtn);

        add(tablePanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void populateData() {
        try {
            allJobs = workerDAO.getDistinctRoles();
            model.setRowCount(0);

            for (String job : allJobs) {
                model.addRow(new Object[] { job });
            }

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Aucun rôle trouvé dans la base de données.",
                        "Information", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des roles: " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void confirmSelection() {
        int[] selectedRows = jobsTable.getSelectedRows();

        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner au moins un rôle.",
                    "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (int row : selectedRows) {
            String job = (String) model.getValueAt(row, 0);
            selectedJobs.add(job);
        }
        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public List<String> getSelectedJobs() {
        return selectedJobs;
    }
}
