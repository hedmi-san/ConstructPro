package constructpro.Service;

import constructpro.DAO.ConstructionSiteDAO;
import constructpro.DAO.WorkerDAO;
import constructpro.DAO.vehicleSystem.VehicleRentalDAO;
import constructpro.DTO.Vehicle;
import constructpro.DTO.vehicleSystem.VehicleRental;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import javax.swing.*;

public class VehicleForm extends JDialog {
    private JTextField vehicleNameField, vehiclePlateNumberField;
    private JTextField ownerNameField, ownerPhoneField;
    private JButton saveButton, cancelButton;
    private JComboBox<String> siteComboBox, driverComboBox;
    private boolean confirmed = false;
    private ConstructionSiteDAO siteDAO;
    private WorkerDAO workerDAO;
    private VehicleRentalDAO rentalDAO;
    private Connection conn;

    private Vehicle originalVehicle; // Track original vehicle for comparison
    private int originalSiteId = 0;

    public VehicleForm(JFrame parent, String title, Vehicle vehicle, Connection connection) throws SQLException {
        super(parent, title, true);
        this.conn = connection;
        this.siteDAO = new ConstructionSiteDAO(connection);
        this.workerDAO = new WorkerDAO(connection);
        this.rentalDAO = new VehicleRentalDAO(connection);
        this.originalVehicle = vehicle;

        initComponents();
        setupLayout();
        setupActions();
        if (vehicle != null) {
            populateFields(vehicle);
            originalSiteId = vehicle.getSiteID();
        }

        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        vehicleNameField = new JTextField(20);
        vehiclePlateNumberField = new JTextField(20);
        ownerNameField = new JTextField(20);
        ownerPhoneField = new JTextField(20);
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

        // Vehicle Assigned Site
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Chantier:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(siteComboBox, gbc);

        row++;

        // Vehicle Driver
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Chauffeur:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(driverComboBox, gbc);

        row++;

        // Owner fields (only visible for rented vehicles)
        if (originalVehicle != null && "Loué".equalsIgnoreCase(originalVehicle.getOwnershipType())) {
            // Owner Name
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.anchor = GridBagConstraints.WEST;
            formPanel.add(new JLabel("Nom du propriétaire:"), gbc);
            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            formPanel.add(ownerNameField, gbc);

            row++;

            // Owner Phone
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.anchor = GridBagConstraints.WEST;
            formPanel.add(new JLabel("Téléphone du propriétaire:"), gbc);
            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            formPanel.add(ownerPhoneField, gbc);

            row++;
        }

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
                    // Handle rental record creation if site changed for rented vehicle
                    if (originalVehicle != null && "Loué".equalsIgnoreCase(originalVehicle.getOwnershipType())) {
                        try {
                            handleRentalSiteChange();
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(VehicleForm.this,
                                    "Erreur lors de la mise à jour des enregistrements de location: " + ex.getMessage(),
                                    "Erreur",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }

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

    private void handleRentalSiteChange() throws SQLException {
        // Get new site ID
        int newSiteId = 0;
        try {
            String selectedSiteName = (String) siteComboBox.getSelectedItem();
            if (selectedSiteName != null && !selectedSiteName.equals("Sélectionner un chantier")) {
                newSiteId = siteDAO.getSiteIdByName(selectedSiteName);
            }
        } catch (SQLException e) {
            throw e;
        }

        // Check if site changed
        if (newSiteId != originalSiteId && newSiteId > 0) {
            // Get current rental info
            VehicleRental currentRental = rentalDAO.getCurrentRentalInfo(originalVehicle.getId());

            if (currentRental != null) {
                // If current rental has no end date, set it to today
                if (currentRental.getEndDate() == null) {
                    currentRental.setEndDate(LocalDate.now());
                    rentalDAO.updateRentalRecord(currentRental);
                }

                // Create new rental record
                VehicleRental newRental = new VehicleRental();
                newRental.setVehicle_id(originalVehicle.getId());
                newRental.setStartDate(LocalDate.now());
                newRental.setEndDate(null); // Ongoing
                newRental.setDaysWorked(0);
                newRental.setTransferFee(0.0);
                newRental.setAssignedSiteId(newSiteId);

                // Copy owner info and daily rate from current rental
                newRental.setOwnerName(ownerNameField.getText().trim());
                newRental.setOwnerPhone(ownerPhoneField.getText().trim());
                newRental.setDailyRate(currentRental.getDailyRate());
                newRental.setDepositAmount(0.0); // New rental, no deposit yet

                // Save new rental record
                rentalDAO.addRentalRecord(newRental);
            }
        } else if (originalVehicle != null) {
            // Site didn't change, but owner info might have - update current rental
            VehicleRental currentRental = rentalDAO.getCurrentRentalInfo(originalVehicle.getId());
            if (currentRental != null) {
                currentRental.setOwnerName(ownerNameField.getText().trim());
                currentRental.setOwnerPhone(ownerPhoneField.getText().trim());
                rentalDAO.updateRentalRecord(currentRental);
            }
        }
    }

    private void populateFields(Vehicle vehicle) {
        vehicleNameField.setText(vehicle.getName());
        vehiclePlateNumberField.setText(vehicle.getPlateNumber());

        if (vehicle.getSiteID() > 0) {
            try {
                String siteName = siteDAO.getSiteNameById(vehicle.getSiteID());
                if (siteName != null) {
                    siteComboBox.setSelectedItem(siteName);
                }
            } catch (SQLException e) {
            }
        }

        if (vehicle.getDriverID() > 0) {
            try {
                String driverName = workerDAO.getDriverNameById(vehicle.getDriverID());
                if (driverName != null) {
                    driverComboBox.setSelectedItem(driverName);
                }
            } catch (SQLException e) {
            }
        }

        // Populate owner fields if rented vehicle
        if ("Loué".equalsIgnoreCase(vehicle.getOwnershipType())) {
            try {
                VehicleRental currentRental = rentalDAO.getCurrentRentalInfo(vehicle.getId());
                if (currentRental != null) {
                    ownerNameField.setText(currentRental.getOwnerName());
                    ownerPhoneField.setText(currentRental.getOwnerPhone());
                }
            } catch (SQLException e) {
                e.printStackTrace();
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
