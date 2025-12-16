package constructpro.Service;

import constructpro.DTO.Worker;
import constructpro.DAO.ConstructionSiteDAO;
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.sql.Connection;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class WorkerForm extends JDialog {
    private JTextField firstNameField, lastNameField, birthPlaceField, fatherNameField, motherNameField,roleField;
    private JTextField identityCardNumberField, accountNumberField, phoneNumberField;
    private JDateChooser birthDateChooser, startDateChooser, identityCardDateChooser;
    private JComboBox<String> familySituationComboBox;
    private JComboBox<String> siteComboBox;
    private JButton saveButton, cancelButton;
    private boolean confirmed = false;
    private ConstructionSiteDAO siteDAO;

    public WorkerForm(JFrame parent, String title, Worker worker,Connection connection) throws SQLException {
        super(parent, title, true);
        this.siteDAO = new ConstructionSiteDAO(connection);
        initComponents();
        setupLayout();
        setupActions();
        
        if (worker != null) {
            populateFields(worker);
        }
        
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        firstNameField = new JTextField(20);
        lastNameField = new JTextField(20);
        birthPlaceField = new JTextField(20);
        fatherNameField = new JTextField(20);
        motherNameField = new JTextField(20);
        identityCardNumberField = new JTextField(20);
        accountNumberField = new JTextField(20);
        phoneNumberField = new JTextField(20);
        
        
        // Initialize JDateChooser components
        birthDateChooser = new JDateChooser();
        startDateChooser = new JDateChooser();
        identityCardDateChooser = new JDateChooser();
        
        // Set date format
        birthDateChooser.setDateFormatString("dd/MM/yyyy");
        startDateChooser.setDateFormatString("dd/MM/yyyy");
        identityCardDateChooser.setDateFormatString("dd/MM/yyyy");
        
        // Set preferred size for date choosers
        Dimension dateChooserSize = new Dimension(200, 25);
        birthDateChooser.setPreferredSize(dateChooserSize);
        startDateChooser.setPreferredSize(dateChooserSize);
        identityCardDateChooser.setPreferredSize(dateChooserSize);
        
        //familly situation combo box 
        familySituationComboBox = new JComboBox<>(new String[]{
            "Célibataire", "Marié(e)", "Divorcé(e)", "Veuf(ve)"
        });
        // Role Text Field
        roleField = new JTextField(20);
        // Site combo box
        siteComboBox = new JComboBox<>();
        loadSites();
        
        saveButton = new JButton("Sauvegarder");
        cancelButton = new JButton("Annuler");
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

    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        int row = 0;
        
        // First Name
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Prénom:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(firstNameField, gbc);
        
        row++;
        // Last Name
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Nom:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(lastNameField, gbc);
        
        row++;
        // Birth Place
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Lieux de Naissance:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(birthPlaceField, gbc);
        
        row++;
        // Birth Date
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Date de Naissance:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(birthDateChooser, gbc);
        
        row++;
        // Father Name
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Nom du père:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(fatherNameField, gbc);
        
        row++;
        // Mother Name
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Nom du mère:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(motherNameField, gbc);
        
        row++;
        // Start Date
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Date de Début:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(startDateChooser, gbc);
        
        row++;
        // Identity Card Number
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Numéro de carte d'identité:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(identityCardNumberField, gbc);
        
        row++;
        // Identity Card Date
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Date de la carte d'identité:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(identityCardDateChooser, gbc);
        
        row++;
        // Family Situation
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Situation familiale:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(familySituationComboBox, gbc);
        
        row++;
        // Account Number
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Numéro de compte:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(accountNumberField, gbc);
        
        row++;
        // Phone Number
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Numéro de téléphone:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(phoneNumberField, gbc);
        
        row++;
        // Job
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Poste:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(roleField, gbc);
        
        row++;
        // Site
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Chantier :"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(siteComboBox, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        add(new JScrollPane(formPanel), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void populateFields(Worker worker) {
        firstNameField.setText(worker.getFirstName());
        lastNameField.setText(worker.getLastName());
        birthPlaceField.setText(worker.getBirthPlace());
        
        // Convert LocalDate to Date for JDateChooser
        if (worker.getBirthDate() != null) {
            birthDateChooser.setDate(Date.from(worker.getBirthDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        
        fatherNameField.setText(worker.getFatherName());
        motherNameField.setText(worker.getMotherName());
        
        if (worker.getStartDate() != null) {
            startDateChooser.setDate(Date.from(worker.getStartDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        
        identityCardNumberField.setText(worker.getIdentityCardNumber());
        
        if (worker.getIdentityCardDate() != null) {
            identityCardDateChooser.setDate(Date.from(worker.getIdentityCardDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        
        familySituationComboBox.setSelectedItem(worker.getFamilySituation());
        accountNumberField.setText(worker.getAccountNumber());
        phoneNumberField.setText(worker.getPhoneNumber());
        roleField.setText(worker.getRole());
        
        if (worker.getAssignedSiteID() > 0) {
            try {
                String siteName = siteDAO.getSiteNameById(worker.getAssignedSiteID());
                if (siteName != null) {
                    siteComboBox.setSelectedItem(siteName);
                }
            } catch (SQLException e) {
            }
        } 
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

    private boolean validateFields() {
    
        if (firstNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le prénom est obligatoire !");
            firstNameField.requestFocus();
            return false;
        }
        if (lastNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le nom est obligatoire !");
            lastNameField.requestFocus();
            return false;
        }


        if (birthDateChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this, "La date de naissance est obligatoire !");
            return false;
        }

        if (startDateChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this, "La date de début est obligatoire !");
            return false;
        }

        if (identityCardDateChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this, "La date de délivrance de la carte d'identité est obligatoire !");
            return false;
        }


        if (birthDateChooser.getDate().after(new Date())) {
            JOptionPane.showMessageDialog(this, "La date de naissance ne peut pas être dans le future !");
            return false;
        }


        if (startDateChooser.getDate().before(birthDateChooser.getDate())) {
            JOptionPane.showMessageDialog(this, "La date de début ne peut pas être antérieure à la date de naissance !");
            return false;
        }

        if (siteComboBox.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un chantier !");
            return false;
        }


        String phone = phoneNumberField.getText().trim();
        if (!phone.isEmpty() && !phone.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Veuillez saisir un numéro de téléphone valide à 10 chiffres !");
            phoneNumberField.requestFocus();
            return false;
        }

        return true;
    }

    
    private LocalDate convertToLocalDate(Date date) {
        if (date == null) return null;
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public Worker getWorkerFromForm() throws SQLException {
        Worker worker = new Worker();
        worker.setFirstName(firstNameField.getText().trim());
        worker.setLastName(lastNameField.getText().trim());
        worker.setBirthPlace(birthPlaceField.getText().trim());
        worker.setBirthDate(convertToLocalDate(birthDateChooser.getDate()));
        worker.setFatherName(fatherNameField.getText().trim());
        worker.setMotherName(motherNameField.getText().trim());
        worker.setStartDate(convertToLocalDate(startDateChooser.getDate()));
        worker.setIdentityCardNumber(identityCardNumberField.getText().trim());
        worker.setIdentityCardDate(convertToLocalDate(identityCardDateChooser.getDate()));
        worker.setFamilySituation((String) familySituationComboBox.getSelectedItem());
        worker.setAccountNumber(accountNumberField.getText().trim());
        worker.setPhoneNumber(phoneNumberField.getText().trim());
        worker.setRole((String) roleField.getText().trim());
        try {
            String selectedSiteName = (String) siteComboBox.getSelectedItem();
            if (selectedSiteName != null && !selectedSiteName.equals("Sélectionner un chantier")) {
                int siteId = siteDAO.getSiteIdByName(selectedSiteName);
                worker.setAssignedSiteID(siteId);
            } else {
                worker.setAssignedSiteID(1);
            }
        } catch (SQLException e) {
            worker.setAssignedSiteID(1); // Default to 0 if error occurs
        }
        return worker;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}