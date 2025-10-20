package constructpro.Service;

import javax.swing.*;
import java.sql.*;
import constructpro.DTO.ConstructionSite;
import constructpro.DAO.WorkerDAO;
import constructpro.DTO.Worker;
import java.awt.HeadlessException;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;

public class WorkerList extends JDialog{
    private Connection conn;
    private final ConstructionSite site;
    private JTable workerstTable;
    private JScrollPane jScrollPane1;
    private WorkerDAO workerDAO;
    private JFrame parentFrame;
    public WorkerList(JFrame parent, ConstructionSite site,Connection connection) throws SQLException {
        super(parent, "Workers List", true);
        this.site= site;
        this.workerDAO = new WorkerDAO(connection);
        this.conn = connection;
        initializeComponents();
        setupLayout();
        populateData();
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initializeComponents() {
        workerstTable = new JTable();
        jScrollPane1 = new JScrollPane(workerstTable);
    }

    private void setupLayout() {
        setLayout(new java.awt.BorderLayout());
        add(jScrollPane1, java.awt.BorderLayout.CENTER);

        workerstTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        workerstTable.setDefaultEditor(Object.class, null);
        workerstTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    showOptions();
                }
            }
        });
    }

    private void populateData() {
        try {
            ResultSet rs = workerDAO.getWorkersBySiteId(site.getId());
            DefaultTableModel model = new DefaultTableModel(
                    new Object[]{"ID", "Prénom", "Nom", "Âge", "Fonction", "Téléphone"}, 0
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
                    rs.getInt("age"),
                    rs.getString("job"),
                    rs.getString("phone_number")
                });
            }
            workerstTable.setModel(model);

            // Hide ID column if desired
            workerstTable.getColumnModel().getColumn(0).setMinWidth(0);
            workerstTable.getColumnModel().getColumn(0).setMaxWidth(0);
            workerstTable.getColumnModel().getColumn(0).setWidth(0);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des données: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showOptions(){
        int selectedRow = workerstTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                DefaultTableModel model = (DefaultTableModel) workerstTable.getModel();
                int workerID = (Integer) model.getValueAt(selectedRow, 0);

                Worker worker = workerDAO.getWorkerById(workerID);
                if (worker != null) {
                    salaryOption options = new salaryOption(parentFrame, worker, conn);
                    options.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Chantier non trouvé !", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (HeadlessException | SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors du chargement des détails du chantier : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    public void setParentFrame(JFrame parent) {
        this.parentFrame = parent;
    }
}
