package constructpro.Service;

import constructpro.DAO.ConstructionSiteDAO;
import constructpro.DAO.WorkerDAO;
import constructpro.DTO.Vehicle;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;

public class VehicleForm extends JDialog{
    private JTextField vehicleNameField,vehiclePlateNumberField;
    private JButton saveButton, cancelButton;
    private JComboBox<String> siteComboBox,driverComboBox,statusComboBox;
    private boolean confirmed = false;
    private ConstructionSiteDAO siteDAO;
    private WorkerDAO workerDAO;
    
    public VehicleForm(JFrame parent, String title, Vehicle vehicle,Connection connection) throws SQLException{
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
    
    private void initComponents(){
        vehicleNameField = new JTextField(20);
        vehiclePlateNumberField = new JTextField(20);
        statusComboBox =  new JComboBox<>(new String[]{
            "Parking", "Loué", "Travailler"
        }); 
        siteComboBox = new JComboBox<>();
        loadSites();
        driverComboBox = new JComboBox<>();
        loadDrivers();
        saveButton = new JButton("Sauvegarder");
        cancelButton = new JButton("Annuler");
    }
    
    private void setupLayout(){
        setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        int row = 0;
        
        // Vehicle Name
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Nom:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(vehicleNameField, gbc);
        
        row++;
        
        // Vehicle Plate Number
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Numéro de plaque:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(vehiclePlateNumberField, gbc);
        
        row++;
        
        // Vehicle Plate Number
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Etat:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(statusComboBox, gbc);
        
        row++;
        
        // Vehicle Plate Number
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Chantier:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(siteComboBox, gbc);
        
        row++;
        
        // Vehicle Plate Number
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Chauffeur:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(driverComboBox, gbc);
        
        row++;
    }
    
    private boolean validateFields(){
        
    }
    
    private void setupActions(){
        
    }
    private void populateFields(Vehicle vehicle){}
    
    private void loadSites(){
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
    
    private void loadDrivers(){
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
}
