package constructpro.UI;

import javax.swing.*;
import java.sql.SQLException;
import constructpro.DAO.WorkerDAO;
import constructpro.DTO.Worker;
import constructpro.Service.WorkerForm;
import constructpro.DAO.WorkerAssignmentDAO;
import java.awt.*;
import java.util.List;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;
import constructpro.Service.WorkerDetailDialog;
import java.sql.Connection;
import java.time.LocalDate;

public class WorkersPage extends JPanel {

    private JButton deleteButton;
    private JButton editButton;
    private JButton addButton;
    private JButton refreshButton;
    private JTextField searchText;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JTable workerstTable;
    private JScrollPane jScrollPane1;
    private WorkerDAO workerDAO;
    private WorkerAssignmentDAO workerAssignmentDAO;
    private JFrame parentFrame;
    public Connection conn;

    public WorkersPage(Connection connection) {
        this.conn = connection;
        initDAO();
        initComponents();
        loadDataSet();
    }

    public WorkersPage(JFrame parent) {
        this.parentFrame = parent;
        initDAO();
        initComponents();
        loadDataSet();
    }

    private void initDAO() {
        try {
            workerDAO = new WorkerDAO(conn);
            workerAssignmentDAO = new WorkerAssignmentDAO(conn);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur de connexion à la base de données: " + e.getMessage());
        }
    }

    private void initComponents() {
        addButton = new JButton("Ajouter");
        editButton = new JButton("Modifier");
        deleteButton = new JButton("Supprimer");
        refreshButton = new JButton("Actualiser");
        searchText = new JTextField(15);
        jLabel1 = new JLabel("Travailleurs");
        jLabel2 = new JLabel("Rechercher");
        workerstTable = new JTable();
        jScrollPane1 = new JScrollPane(workerstTable);

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
        workerstTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        workerstTable.setDefaultEditor(Object.class, null);
        workerstTable.getTableHeader().setReorderingAllowed(false);
        workerstTable.setShowVerticalLines(true);
        workerstTable.setGridColor(Color.WHITE);
        workerstTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    showWorkerDetails();
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

    private void setupButtonActions() {
        // Add button action
        addButton.addActionListener(e -> {
            try {
                WorkerForm dialog = new WorkerForm(parentFrame, "Ajouter un Ouvrier", null, conn);
                dialog.setVisible(true);

                if (dialog.isConfirmed()) {
                    Worker newWorker = dialog.getWorkerFromForm();
                    // insertWorker now returns the generated worker ID
                    int workerId = workerDAO.insertWorker(newWorker);

                    // Create worker assignment if assigned to a site (not site ID 1)
                    if (newWorker.getAssignedSiteID() != 1) {
                        workerAssignmentDAO.insertAssignment(workerId, newWorker.getAssignedSiteID(),
                                LocalDate.now());
                    }

                    loadDataSet();
                    JOptionPane.showMessageDialog(this, "Ouvrier ajouté avec succès!");
                }
            } catch (HeadlessException | SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout: " + ex.getMessage(), "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Edit button action
        editButton.addActionListener(e -> {
            int selectedRow = workerstTable.getSelectedRow();
            if (selectedRow >= 0) {
                try {
                    DefaultTableModel model = (DefaultTableModel) workerstTable.getModel();
                    int workerId = (Integer) model.getValueAt(selectedRow, 0); // Assuming ID is in first column

                    Worker existingWorker = workerDAO.getWorkerById(workerId);
                    if (existingWorker != null) {
                        WorkerForm dialog = new WorkerForm(parentFrame, "Modifier l'Ouvrier", existingWorker, conn);
                        dialog.setVisible(true);

                        if (dialog.isConfirmed()) {
                            Worker updatedWorker = dialog.getWorkerFromForm();
                            updatedWorker.setId(workerId);

                            // Check if site has changed
                            if (existingWorker.getAssignedSiteID() != updatedWorker.getAssignedSiteID()) {
                                // Close the old assignment if worker was assigned to a site (not site ID 1)
                                if (existingWorker.getAssignedSiteID() != 1) {
                                    workerAssignmentDAO.updateWorkerAssignment(workerId,
                                            existingWorker.getAssignedSiteID(), LocalDate.now());
                                }

                                // Create new assignment if worker is being assigned to a site (not site ID 1)
                                if (updatedWorker.getAssignedSiteID() != 1) {
                                    workerAssignmentDAO.insertAssignment(workerId, updatedWorker.getAssignedSiteID(),
                                            LocalDate.now());
                                }
                            }

                            workerDAO.updateWorker(updatedWorker);
                            loadDataSet();
                            JOptionPane.showMessageDialog(this, "Ouvrier modifié avec succès!");
                        }
                    }
                } catch (HeadlessException | SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la modification: " + ex.getMessage(), "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un ouvrier à modifier.");
            }
        });

        // Delete button action
        deleteButton.addActionListener(e -> {
            int selectedRow = workerstTable.getSelectedRow();
            if (selectedRow >= 0) {
                DefaultTableModel model = (DefaultTableModel) workerstTable.getModel();
                String workerName = model.getValueAt(selectedRow, 1) + " " + model.getValueAt(selectedRow, 2);

                int confirm = JOptionPane.showConfirmDialog(this,
                        "Êtes-vous sûr de vouloir supprimer l'ouvrier " + workerName + "?",
                        "Confirmer la suppression",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        int workerId = (Integer) model.getValueAt(selectedRow, 0); // Assuming ID is in first column
                        workerDAO.deleteWorker(workerId);
                        loadDataSet();
                        JOptionPane.showMessageDialog(this, "Ouvrier supprimé avec succès!");
                    } catch (HeadlessException | SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Erreur lors de la suppression: " + ex.getMessage(),
                                "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un ouvrier à supprimer.");
            }
        });
    }

    private void loadDataSet() {
        List<Worker> workers = workerDAO.getWorkersInfo();
        DefaultTableModel model = new DefaultTableModel(
                new Object[] { "ID", "Prénom", "Nom", "Âge", "Fonction", "Téléphone", "Chantier" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        for (Worker w : workers) {
            model.addRow(new Object[] {
                    w.getId(),
                    w.getFirstName(),
                    w.getLastName(),
                    w.getAge(),
                    w.getRole(),
                    w.getPhoneNumber(),
                    w.getSiteName()
            });
        }
        workerstTable.setModel(model);

        // Hide ID column if desired
        workerstTable.getColumnModel().getColumn(0).setMinWidth(0);
        workerstTable.getColumnModel().getColumn(0).setMaxWidth(0);
        workerstTable.getColumnModel().getColumn(0).setWidth(0);
    }

    private void showWorkerDetails() {
        int selectedRow = workerstTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                DefaultTableModel model = (DefaultTableModel) workerstTable.getModel();
                int workerId = (Integer) model.getValueAt(selectedRow, 0); // Get worker ID from hidden column

                Worker worker = workerDAO.getWorkerById(workerId);
                if (worker != null) {
                    WorkerDetailDialog detailDialog = new WorkerDetailDialog(parentFrame, worker, conn);
                    detailDialog.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Employé non trouvé !", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (HeadlessException | SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors du chargement des détails de l'employé : " + ex.getMessage(), "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadSearchResults(String searchTerm) {
        List<Worker> workers = workerDAO.searchWorkersByName(searchTerm);
        DefaultTableModel model = new DefaultTableModel(
                new Object[] { "ID", "Prénom", "Nom", "Âge", "Fonction", "Téléphone", "Chantier" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Worker w : workers) {
            model.addRow(new Object[] {
                    w.getId(),
                    w.getFirstName(),
                    w.getLastName(),
                    w.getAge(),
                    w.getRole(),
                    w.getPhoneNumber(),
                    w.getSiteName()
            });
        }
        workerstTable.setModel(model);
        workerstTable.getColumnModel().getColumn(0).setMinWidth(0);
        workerstTable.getColumnModel().getColumn(0).setMaxWidth(0);
        workerstTable.getColumnModel().getColumn(0).setWidth(0);
    }

    public void setParentFrame(JFrame parent) {
        this.parentFrame = parent;
    }
}
