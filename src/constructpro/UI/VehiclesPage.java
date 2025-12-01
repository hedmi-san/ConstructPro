package constructpro.UI;

import constructpro.DAO.VehicleDAO;
import constructpro.DTO.Vehicle;
import constructpro.Service.VehicleDetailDialog;
import constructpro.Service.VehicleForm;
import constructpro.Service.VehicleOption;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.HeadlessException;
import javax.swing.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;

public class VehiclesPage extends JPanel {

    private JButton addButton;
    private JButton deleteButton;
    private JButton editButton;
    private JButton refreshButton;
    private JTextField searchText;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JTable vehiclesTable;
    private JScrollPane jScrollPane1;
    private VehicleDAO vehicleDAO;
    private JFrame parentFrame;
    public Connection conn;

    public VehiclesPage(Connection connection) {
        this.conn = connection;
        initDAO();
        initComponents();
        loadDataSet();
    }

    private void initDAO() {
        vehicleDAO = new VehicleDAO(conn);
    }

    private void initComponents() {
        addButton = new JButton("Ajouter");
        editButton = new JButton("Modifier");
        deleteButton = new JButton("Supprimer");
        refreshButton = new JButton("Actualiser");
        searchText = new JTextField(15);
        jLabel1 = new JLabel("Véhicules");
        jLabel2 = new JLabel("Rechercher");
        vehiclesTable = new JTable();
        jScrollPane1 = new JScrollPane(vehiclesTable);

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
        vehiclesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        vehiclesTable.setDefaultEditor(Object.class, null);
        vehiclesTable.getTableHeader().setReorderingAllowed(false);
        vehiclesTable.setShowVerticalLines(true);
        vehiclesTable.setGridColor(Color.WHITE);

        // Buttons panel with white text
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        buttonPanel.setPreferredSize(new Dimension(0, 60));

        // Set white foreground color for buttons
        addButton.setForeground(Color.WHITE);
        deleteButton.setForeground(Color.WHITE);
        editButton.setForeground(Color.WHITE);

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

        // Add double-click listener to table
        vehiclesTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    // Vehicle info
                    showVehicleDetails();
                }
            }
        });

        setupButtonActions();
    }

    public void loadDataSet() {
        try {
            ResultSet rs = vehicleDAO.getVehiclesInfo();
            DefaultTableModel model = new DefaultTableModel(
                    new Object[] { "id", "Nom", "Numéro de plaque", "Type de Propriété", "Chantier", "Chauffeur" }, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Make table non-editable
                }
            };

            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getInt("vehicle_id"),
                        rs.getString("vehicle_name"),
                        rs.getString("plateNumber"),
                        rs.getString("ownership_type"),
                        rs.getString("site_name"),
                        rs.getString("driver_name")
                });
            }
            vehiclesTable.setModel(model);

            // Hide ID column if desired
            vehiclesTable.getColumnModel().getColumn(0).setMinWidth(0);
            vehiclesTable.getColumnModel().getColumn(0).setMaxWidth(0);
            vehiclesTable.getColumnModel().getColumn(0).setWidth(0);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des données: " + e.getMessage(), "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSearchResults(String searchTerm) {
        try {
            ResultSet rs = vehicleDAO.searchVehicle(searchTerm);
            DefaultTableModel model = new DefaultTableModel(
                    new Object[] { "id", "Nom", "Numéro de plaque", "Type de Propriété", "Chantier", "Chauffeur" }, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Make table non-editable
                }
            };

            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getInt("vehicle_id"),
                        rs.getString("vehicle_name"),
                        rs.getString("plateNumber"),
                        rs.getString("ownership_type"),
                        rs.getString("site_name"),
                        rs.getString("driver_name")
                });
            }
            vehiclesTable.setModel(model);

            // Hide ID column if desired
            vehiclesTable.getColumnModel().getColumn(0).setMinWidth(0);
            vehiclesTable.getColumnModel().getColumn(0).setMaxWidth(0);
            vehiclesTable.getColumnModel().getColumn(0).setWidth(0);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des données: " + e.getMessage(), "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setupButtonActions() {
        // Add button action
        addButton.addActionListener(e -> {
            try {
                VehicleOption dialog = new VehicleOption(conn, parentFrame, "Ajouter une véhicule", this);
                dialog.setVisible(true);

            } catch (HeadlessException ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout: " + ex.getMessage(), "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Edit button action
        editButton.addActionListener(e -> {
            int selectedRow = vehiclesTable.getSelectedRow();
            if (selectedRow >= 0) {
                try {
                    DefaultTableModel model = (DefaultTableModel) vehiclesTable.getModel();
                    int vehicleId = (Integer) model.getValueAt(selectedRow, 0); // Assuming ID is in first column

                    Vehicle existingVehicle = vehicleDAO.getVehicleById(vehicleId);
                    if (existingVehicle != null) {
                        VehicleForm dialog = new VehicleForm(parentFrame, "Modifier la Véhicule", existingVehicle,
                                conn);
                        dialog.setVisible(true);

                        if (dialog.isConfirmed()) {
                            Vehicle updatedVehicle = dialog.getVehicleFromForm();
                            updatedVehicle.setId(vehicleId);
                            vehicleDAO.updateVehicle(updatedVehicle);
                            loadDataSet();
                            JOptionPane.showMessageDialog(this, "Véhicule modifié avec succès!");
                        }
                    }
                } catch (HeadlessException | SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la modification: " + ex.getMessage(), "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner une véhicules à modifier.");
            }
        });

        // Delete button action
        deleteButton.addActionListener(e -> {
            int selectedRow = vehiclesTable.getSelectedRow();
            if (selectedRow >= 0) {
                DefaultTableModel model = (DefaultTableModel) vehiclesTable.getModel();
                String vehicleName = model.getValueAt(selectedRow, 1).toString();

                int confirm = JOptionPane.showConfirmDialog(this,
                        "Êtes-vous sûr de vouloir supprimer l  " + vehicleName + "?",
                        "Confirmer la suppression",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        int supplierId = (Integer) model.getValueAt(selectedRow, 0); // Assuming ID is in first column
                        vehicleDAO.deleteVehicle(supplierId);
                        loadDataSet();
                        JOptionPane.showMessageDialog(this, "Véhicules supprimé avec succès!");
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

    private void showVehicleDetails() {
        int selectedRow = vehiclesTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                DefaultTableModel model = (DefaultTableModel) vehiclesTable.getModel();
                int vehicleId = (Integer) model.getValueAt(selectedRow, 0); // Get worker ID from hidden column

                Vehicle vehicle = vehicleDAO.getVehicleById(vehicleId);
                if (vehicle != null) {
                    VehicleDetailDialog detailDialog = new VehicleDetailDialog(parentFrame,
                            vehicle, conn);
                    detailDialog.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Véhicule non trouvé !", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (HeadlessException | SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors du chargement des détails de le véhicule : " + ex.getMessage(), "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void setParentFrame(JFrame parent) {
        this.parentFrame = parent;
    }
}
