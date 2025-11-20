package constructpro.Service;

import constructpro.DAO.ConstructionSiteDAO;
import constructpro.DAO.WorkerDAO;
import constructpro.DTO.Vehicle;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;

public class VehicleForm extends JDialog {
    private JTextField vehicleNameField, vehiclePlateNumberField;
    private JButton saveButton, cancelButton;
    private JComboBox<String> siteComboBox, driverComboBox, statusComboBox;
    private boolean confirmed = false;
    private ConstructionSiteDAO siteDAO;
    private WorkerDAO workerDAO;

    public VehicleForm(JFrame parent, String title, Vehicle vehicle, Connection connection) throws SQLException {
        super(parent, title, true);
        this.siteDAO = new ConstructionSiteDAO(connection);
        this.workerDAO = new WorkerDAO(connection);
        initComponents();
        setupLayout();
        setupActions();
        if (vehicle != null) {
            populateFields(vehicle);
        }

        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        vehicleNameField = new JTextField(20);
        vehiclePlateNumberField = new JTextField(20);
        statusComboBox = new JComboBox<>(new String[] {
                "Parking", "Loué", "Travailler"
        });
        siteComboBox = new JComboBox<>();
        loadSites();
        driverComboBox = new JComboBox<>();
        loadDrivers();
        saveButton = new JButton("Sauvegarder");
        cancelButton = new JButton("Annuler");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        int row = 0;

        // Vehicle Name
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Nom:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(vehicleNameField, gbc);

        row++;

        // Vehicle Plate Number
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Numéro de plaque:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(vehiclePlateNumberField, gbc);

        row++;

        // Vehicle Plate Number
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Etat:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(statusComboBox, gbc);

        row++;

        // Vehicle Plate Number
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Chantier:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(siteComboBox, gbc);

        row++;

        // Vehicle Plate Number
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Chauffeur:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(driverComboBox, gbc);

        row++;

        // Add form panel to dialog
        add(formPanel, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private boolean validateFields() {
        if (vehicleNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le Nom est obligatoire !");
            vehicleNameField.requestFocus();
            return false;
        }

        if (vehiclePlateNumberField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le Numéro de plaque est obligatoire !");
            vehiclePlateNumberField.requestFocus();
            return false;
        }

        return true;
    }

    private void setupActions() {
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateFields()) {
                    confirmed = true;
                    dispose();
                }
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmed = false;
                dispose();
            }
        });
    }

    private void populateFields(Vehicle vehicle) {
        vehicleNameField.setText(vehicle.getName());
        vehiclePlateNumberField.setText(vehicle.getPlateNumber());
        statusComboBox.setSelectedItem(vehicle.getStatus());
        if (vehicle.getSiteID() > 0) {
            try {
                String siteName = siteDAO.getSiteNameById(vehicle.getSiteID());
                if (siteName != null) {
                    siteComboBox.setSelectedItem(siteName);
                }
            } catch (SQLException e) {
            }
        }

        if (vehicle.getSiteID() > 0) {
            try {
                String driverName = workerDAO.getDriverNameById(vehicle.getDriverID());
                if (driverName != null) {
                    siteComboBox.setSelectedItem(driverName);
                }
            } catch (SQLException e) {
            }
        }
    }

    private void loadSites() {
        try {
            List<String> siteNames = siteDAO.getAllConstructionSitesNames();
            siteComboBox.removeAllItems();
            siteComboBox.addItem("Sélectionner un chantier");
            for (String siteName : siteNames) {
                siteComboBox.addItem(siteName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des chantiers : " + e.getMessage());
        }
    }

    private void loadDrivers() {
        try {
            List<String> siteNames = workerDAO.getAllDriversNames();
            driverComboBox.removeAllItems();
            driverComboBox.addItem("Sélectionner un Chauffeur");
            for (String siteName : siteNames) {
                driverComboBox.addItem(siteName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading construction sites: " + e.getMessage());
        }
    }

    public Vehicle getVehicleFromForm() {
        Vehicle vehicle = new Vehicle();

        vehicle.setName(vehicleNameField.getText().trim());
        vehicle.setPlateNumber(vehiclePlateNumberField.getText().trim());
        vehicle.setStatus((String) statusComboBox.getSelectedItem());
        try {
            String selectedSiteName = (String) siteComboBox.getSelectedItem();
            if (selectedSiteName != null && !selectedSiteName.equals("Sélectionner un chantier")) {
                int siteId = siteDAO.getSiteIdByName(selectedSiteName);
                vehicle.setSiteID(siteId);
            } else {
                vehicle.setSiteID(1);
            }
        } catch (SQLException e) {
            vehicle.setSiteID(1); // Default to 1 if error occurs
        }
        try {
            String selectedDriverName = (String) driverComboBox.getSelectedItem();
            if (selectedDriverName != null && !selectedDriverName.equals("Sélectionner un Chauffeur")) {
                int workerId = workerDAO.getDriverIdByName(selectedDriverName);
                vehicle.setDriverID(workerId);
            } else {
                vehicle.setDriverID(0);
            }
        } catch (SQLException e) {
            vehicle.setDriverID(0); // Default to 0 if error occurs
        }

        return vehicle;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
