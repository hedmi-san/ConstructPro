package constructpro.Service;

import constructpro.DTO.Fournisseur;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import javax.swing.*;


public class SupplierForm extends JDialog {
    
    private JTextField supplierNameField,supplierPhoneNumberField,supplierAddress,totalSpentField,totalPaidField;
    private JButton saveButton, cancelButton;
    private boolean confirmed = false;
    
    public SupplierForm(JFrame parent, String title, Fournisseur supplier,Connection connection){
        super(parent, title, true);
        
        initComponents();
        setupLayout();
        setupActions();
        
        if (supplier != null) {
            populateFields(supplier);
        }
        
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents(){
        supplierNameField = new JTextField(20);
        supplierPhoneNumberField = new JTextField(20);
        supplierAddress = new JTextField(20);
        totalSpentField = new JTextField(20);
        totalSpentField.setText("0.00");
        totalPaidField = new JTextField(20);
        totalPaidField.setText("0.00");
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
    }
    private void setupLayout(){
        setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        int row = 0;
        
        //Supplier Name
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Nom du Fournisseur :"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(supplierNameField, gbc);
        
        row++;
        
        //Supplier Phone Number
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Numéro de téléphone du Fournisseur :"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(supplierPhoneNumberField, gbc);
        
        row++;
        
        //Supplier Address
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Adresse du Fournisseur :"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(supplierAddress, gbc);
        
        row++;
        
        //Supplier Total spent
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Total dépensé avec ce Fournisseur :"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(totalSpentField, gbc);
        
        row++;
        
        //Supplier Total paid
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Total payé avec ce Fournisseur :"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(totalPaidField, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        add(new JScrollPane(formPanel), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH); 
        
    }
    private void populateFields(Fournisseur supplier){
        supplierNameField.setText(supplier.getName());
        supplierPhoneNumberField.setText(supplier.getPhone());
        supplierAddress.setText(supplier.getAddress());
        totalSpentField.setText(String.valueOf(supplier.getTotalSpent()));
        totalPaidField.setText(String.valueOf(supplier.getTotalPaid()));
    }
    
    private boolean validateFields() {
        if (supplierNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le Nom de fournisseur est obligatoire !");
            supplierNameField.requestFocus();
            return false;
        }
        
        String phone = supplierPhoneNumberField.getText().trim();
        if (!phone.isEmpty() && !phone.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Veuillez saisir un numéro de téléphone valide à 10 chiffres !");
            supplierPhoneNumberField.requestFocus();
            return false;
        }
        
        if(Double.parseDouble(totalSpentField.getText().trim()) < Double.parseDouble(totalPaidField.getText().trim()) ){
            JOptionPane.showMessageDialog(this, "Le total des dépenses ne doit pas dépasser le total des paiements.");
            totalSpentField.requestFocus();
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
    
    public Fournisseur getSupplierFromForm(){
        Fournisseur supplier = new Fournisseur();
        //TODO: setting data from text fields.
        supplier.setName(supplierNameField.getText().trim());
        supplier.setPhone(supplierPhoneNumberField.getText().trim());
        supplier.setAddress(supplierAddress.getText().trim());
        supplier.setTotalSpent(Double.parseDouble(totalSpentField.getText().trim()));
        supplier.setTotalPaid(Double.parseDouble(totalPaidField.getText().trim()));
        return supplier;
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
}
