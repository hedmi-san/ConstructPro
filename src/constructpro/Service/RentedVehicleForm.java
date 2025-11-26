package constructpro.Service;

import java.awt.Color;
import constructpro.DAO.ConstructionSiteDAO;
import constructpro.DAO.WorkerDAO;
import constructpro.DAO.VehicleDAO;
import constructpro.DAO.vehicleSystem.VehicleRentalDAO;
import javax.swing.*;
import java.sql.*;
import com.toedter.calendar.JDateChooser;
import constructpro.DTO.Vehicle;
import constructpro.DTO.vehicleSystem.VehicleRental;
import constructpro.UI.VehiclesPage;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RentedVehicleForm extends JDialog {
    private static final Color DARK_BACKGROUND = new Color(45, 45, 45);
    private JTextField vehicleNameField, vehiclePlateNumberField, ownerNameField, ownerPhoneField, dailyRateField,
            depositAmountFielf, TransferFeeField;
    private JDateChooser startDateChooser, endDateChooser;
    private JButton saveButton, cancelButton;
    private JComboBox<String> siteComboBox;
    private ConstructionSiteDAO siteDAO;
    private WorkerDAO workerDAO;
    private VehicleDAO vehicleDAO;
    private VehiclesPage parentframe;
    private VehicleRentalDAO vehicleRentalDAO;

    public RentedVehicleForm(JFrame parent, Connection connection, VehiclesPage parentframe) throws SQLException {
        super(parent, "Loué", true);
        this.siteDAO = new ConstructionSiteDAO(connection);
        this.workerDAO = new WorkerDAO(connection);
        this.vehicleDAO = new VehicleDAO(connection);
        this.vehicleRentalDAO = new VehicleRentalDAO(connection);
        this.parentframe = parentframe;

        // Initialize components and setup the form
        initComponents();
        setupLayout();
        setupActions();

        // Set dialog properties
        setSize(500, 600);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initComponents() {
        vehicleNameField = new JTextField(20);
        vehiclePlateNumberField = new JTextField(20);
        siteComboBox = new JComboBox<>();
        loadSites();
        saveButton = new JButton("Sauvegarder");
        cancelButton = new JButton("Annuler");
        ownerNameField = new JTextField(20);
        ownerPhoneField = new JTextField(20);
        dailyRateField = new JTextField(20);
        depositAmountFielf = new JTextField(20);
        TransferFeeField = new JTextField(20);
        startDateChooser = new JDateChooser();
        endDateChooser = new JDateChooser();
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

        // Vehicle Owner company
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Propriétaire de véhicule:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(ownerNameField, gbc);

        row++;

        // Vehicle Owner Phone Number
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Numéro de Téléphone du propriétaire:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(ownerPhoneField, gbc);

        row++;

        // Vehicle daily rate
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Taux journalier:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(dailyRateField, gbc);

        row++;

        // Vehicle deposite amount
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Montant du dépôt:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(depositAmountFielf, gbc);

        row++;

        // Vehicle transfer Fee
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Chauffeur:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(TransferFeeField, gbc);

        row++;

        // Start Date
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Date de Début:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(startDateChooser, gbc);
        row++;

        // End Date
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Date de Fin:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(endDateChooser, gbc);
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
                    Vehicle newVehicle = createNewVehicle();
                    try {
                        vehicleDAO.insertVehicle(newVehicle);
                        int vehicleId = vehicleDAO.getVehicleId(newVehicle);
                        VehicleRental rented = createRentedVehicle(vehicleId);
                        vehicleRentalDAO.insertNewRentedVehicle(rented);
                    } catch (SQLException ex) {
                        Logger.getLogger(OwnedVehicleForm.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    dispose();
                    loadDataSet(parentframe);
                    JOptionPane.showMessageDialog(parentframe, "Véhicule ajouté avec succès!");
                }
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void loadSites() {
        try {
            java.util.List<String> siteNames = siteDAO.getAllConstructionSitesNames();
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

    private void loadDataSet(VehiclesPage parentframe) {
        if (parentframe != null) {
            parentframe.loadDataSet();
        }
    }

    private Vehicle createNewVehicle() {
        Vehicle vehicle = new Vehicle();

        vehicle.setName(vehicleNameField.getText().trim());
        vehicle.setPlateNumber(vehiclePlateNumberField.getText().trim());
        vehicle.setOwnershipType("Loué");
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
        return vehicle;
    }

    private LocalDate convertToLocalDate(java.util.Date date) {
        if (date == null)
            return null;
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private VehicleRental createRentedVehicle(int vehicleId) {
        VehicleRental rented = new VehicleRental();

        rented.setVehicle_id(vehicleId);
        rented.setOwnerName(ownerNameField.getText().trim());
        rented.setOwnerPhone(ownerPhoneField.getText().trim());
        rented.setDailyRate(Double.parseDouble(dailyRateField.getText().trim()));
        rented.setStartDate((convertToLocalDate(startDateChooser.getDate())));
        rented.setEndDate(
                (convertToLocalDate(endDateChooser.getDate())) != null ? (convertToLocalDate(endDateChooser.getDate()))
                        : null);
        rented.setDepositAmount(Double.parseDouble(depositAmountFielf.getText().trim()));
        rented.setTransferFee(Double.parseDouble(TransferFeeField.getText().trim()));

        return rented;
    }
}
