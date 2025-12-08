package constructpro.Service;

import constructpro.DTO.BiLLItem;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ItemForm extends JDialog {
    private JTextField itemNameField, quantityField, unitPriceField;
    private JComboBox<String> itemTypeComboBox;
    private JButton saveButton, cancelButton;
    private boolean confirmed = false;

    public ItemForm(JDialog parent, String title) {
        super(parent, title, true);
        initComponents();
        setupLayout();
        setupActions();

        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        itemNameField = new JTextField(20);
        quantityField = new JTextField(20);
        unitPriceField = new JTextField(20);

        // Item type combo box with "Outil" and "Matériel"
        itemTypeComboBox = new JComboBox<>(new String[] {
                "Outil", "Matériel"
        });

        saveButton = new JButton("Ajouter");
        cancelButton = new JButton("Annuler");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        int row = 0;

        // Item Name
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Nom de l'article:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(itemNameField, gbc);

        row++;
        // Item Type
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(itemTypeComboBox, gbc);

        row++;
        // Quantity
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Quantité:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(quantityField, gbc);

        row++;
        // Unit Price
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Prix unitaire (DA):"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(unitPriceField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
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
        // Validate item name
        if (itemNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le nom de l'article est obligatoire !");
            itemNameField.requestFocus();
            return false;
        }

        // Validate quantity
        try {
            double quantity = Double.parseDouble(quantityField.getText().trim());
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "La quantité doit être supérieure à zéro !");
                quantityField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Veuillez saisir une quantité valide !");
            quantityField.requestFocus();
            return false;
        }

        // Validate unit price
        try {
            double unitPrice = Double.parseDouble(unitPriceField.getText().trim());
            if (unitPrice <= 0) {
                JOptionPane.showMessageDialog(this, "Le prix unitaire doit être supérieur à zéro !");
                unitPriceField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Veuillez saisir un prix unitaire valide !");
            unitPriceField.requestFocus();
            return false;
        }

        return true;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public BiLLItem getBillItemFromForm() {
        BiLLItem item = new BiLLItem();
        item.setItemName(itemNameField.getText().trim());
        item.setBillType((String) itemTypeComboBox.getSelectedItem());
        item.setQuantity(Double.parseDouble(quantityField.getText().trim()));
        item.setUnitPrice(Double.parseDouble(unitPriceField.getText().trim()));
        item.setTotalPrice(item.getQuantity() * item.getUnitPrice());
        return item;
    }
}
