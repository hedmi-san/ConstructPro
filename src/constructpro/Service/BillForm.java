package constructpro.Service;

import constructpro.Utils.DateChooserConfigurator;

import constructpro.DTO.Bill;
import constructpro.DTO.BiLLItem;

import constructpro.DAO.SupplierDAO;
import constructpro.DAO.ConstructionSiteDAO;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class BillForm extends JDialog {
    private JTextField factureNumberTextField, transferFeeTextField, paidAmountTextField;
    private JComboBox<String> supplierComboBox, siteComboBox;
    private JDateChooser billDateChooser;
    private JButton addItemButton, deleteItemButton, saveButton, cancelButton;
    private JPanel billItemPanel, totalPanel;
    private JTable billItemsTable;
    private DefaultTableModel tableModel;
    private JLabel itemsTotalLabel, grandTotalLabel;
    private SupplierDAO supplierDAO;
    private ConstructionSiteDAO siteDAO;

    private Connection conn;
    private boolean confirmed = false;
    private List<BiLLItem> billItems;

    public BillForm(JFrame parent, String title, Bill bill, Connection connection) throws SQLException {
        super(parent, title, true);
        this.conn = connection;
        this.siteDAO = new ConstructionSiteDAO(connection);
        this.supplierDAO = new SupplierDAO(connection);

        this.billItems = new ArrayList<>();

        initComponents();
        setupLayout();
        setupActions();
        loadSites();
        loadSuppliers();

        if (bill != null) {
            populateFields(bill);
        }

        setSize(800, 700);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        factureNumberTextField = new JTextField(20);
        transferFeeTextField = new JTextField(20);
        transferFeeTextField.setText("0");
        paidAmountTextField = new JTextField(20);
        paidAmountTextField.setText("0");

        supplierComboBox = new JComboBox<>();
        siteComboBox = new JComboBox<>();

        // Initialize JDateChooser
        billDateChooser = new JDateChooser();
        DateChooserConfigurator.configure(billDateChooser);
        billDateChooser.setDateFormatString("dd/MM/yyyy");
        billDateChooser.setPreferredSize(new Dimension(200, 25));
        billDateChooser.setDate(new Date()); // Set to current date by default

        addItemButton = new JButton("Ajouter Article");
        deleteItemButton = new JButton("Supprimer Article");
        saveButton = new JButton("Sauvegarder");
        cancelButton = new JButton("Annuler");

        // Initialize bill items table
        String[] columnNames = { "Nom", "Type", "Quantité", "Prix Unitaire (DA)", "Prix Total (DA)" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        billItemsTable = new JTable(tableModel);
        billItemsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        billItemPanel = new JPanel();
        totalPanel = new JPanel();

        itemsTotalLabel = new JLabel("Total Articles: 0.00 DA");
        grandTotalLabel = new JLabel("Total Général: 0.00 DA");
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));

        // Main form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Facture Number
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Numéro de Facture:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(factureNumberTextField, gbc);

        row++;
        // Bill Date
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Date de Facture:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(billDateChooser, gbc);

        row++;
        // Supplier
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Fournisseur:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(supplierComboBox, gbc);

        row++;
        // Site
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Chantier:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(siteComboBox, gbc);

        row++;
        // Transfer Fee
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Frais de Transport (DA):"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(transferFeeTextField, gbc);

        row++;
        // Paid Amount
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Montant Payé (DA):"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(paidAmountTextField, gbc);

        // Bill Items Panel
        billItemPanel.setLayout(new BorderLayout(5, 5));
        billItemPanel.setBorder(BorderFactory.createTitledBorder("Articles de la Facture"));

        JScrollPane tableScrollPane = new JScrollPane(billItemsTable);
        tableScrollPane.setPreferredSize(new Dimension(700, 200));
        billItemPanel.add(tableScrollPane, BorderLayout.CENTER);

        JPanel itemButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        itemButtonPanel.add(addItemButton);
        itemButtonPanel.add(deleteItemButton);
        billItemPanel.add(itemButtonPanel, BorderLayout.SOUTH);

        // Total Panel
        totalPanel.setLayout(new GridLayout(2, 1, 5, 5));
        totalPanel.setBorder(BorderFactory.createTitledBorder("Totaux"));
        itemsTotalLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        grandTotalLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        totalPanel.add(itemsTotalLabel);
        totalPanel.add(grandTotalLabel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add all panels to main layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.NORTH);
        topPanel.add(billItemPanel, BorderLayout.CENTER);
        topPanel.add(totalPanel, BorderLayout.SOUTH);

        add(new JScrollPane(topPanel), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupActions() {
        // Add Item button
        addItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ItemForm itemDialog = new ItemForm(BillForm.this, "Ajouter un Article");
                itemDialog.setVisible(true);

                if (itemDialog.isConfirmed()) {
                    BiLLItem item = itemDialog.getBillItemFromForm();
                    billItems.add(item);
                    addItemToTable(item);
                    updateTotals();
                }
            }
        });

        // Delete Item button
        deleteItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = billItemsTable.getSelectedRow();
                if (selectedRow >= 0) {
                    billItems.remove(selectedRow);
                    tableModel.removeRow(selectedRow);
                    updateTotals();
                } else {
                    JOptionPane.showMessageDialog(BillForm.this, "Veuillez sélectionner un article à supprimer.");
                }
            }
        });

        // Save button
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateFields()) {
                    confirmed = true;
                    dispose();
                }
            }
        });

        // Cancel button
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmed = false;
                dispose();
            }
        });

        // Add listener to transfer fee field to update totals
        transferFeeTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTotals();
            }
        });
    }

    private void addItemToTable(BiLLItem item) {
        Object[] rowData = {
                item.getItemName(),
                item.getBillType(),
                item.getQuantity(),
                String.format("%.2f", item.getUnitPrice()),
                String.format("%.2f", item.getTotalPrice())
        };
        tableModel.addRow(rowData);
    }

    private void updateTotals() {
        double itemsTotal = 0.0;
        for (BiLLItem item : billItems) {
            itemsTotal += item.getTotalPrice();
        }

        double transferFee = 0.0;
        try {
            transferFee = Double.parseDouble(transferFeeTextField.getText().trim());
        } catch (NumberFormatException e) {
            transferFee = 0.0;
        }

        double grandTotal = itemsTotal + transferFee;

        itemsTotalLabel.setText(String.format("Total Articles: %.2f DA", itemsTotal));
        grandTotalLabel.setText(String.format("Total Général: %.2f DA", grandTotal));
    }

    private void populateFields(Bill bill) {
        factureNumberTextField.setText(bill.getFactureNumber());

        if (bill.getBillDate() != null) {
            billDateChooser.setDate(Date.from(bill.getBillDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }

        transferFeeTextField.setText(String.valueOf(bill.getTransferFee()));
        paidAmountTextField.setText(String.valueOf(bill.getPaidAmount()));

        // Set supplier
        try {
            String supplierName = supplierDAO.getSupplierById(bill.getSupplierID()).getName();
            supplierComboBox.setSelectedItem(supplierName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set site
        try {
            String siteName = siteDAO.getSiteNameById(bill.getSiteID());
            siteComboBox.setSelectedItem(siteName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Load bill items
        try {
            constructpro.DAO.BiLLItemDAO billItemDAO = new constructpro.DAO.BiLLItemDAO(conn);
            List<BiLLItem> items = billItemDAO.getBillItems(bill.getId());
            for (BiLLItem item : items) {
                billItems.add(item);
                addItemToTable(item);
            }
            updateTotals();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des articles : " + e.getMessage());
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

    private void loadSuppliers() {
        try {
            List<String> supplierNames = supplierDAO.getAllSuppliersNames();
            supplierComboBox.removeAllItems();
            supplierComboBox.addItem("Sélectionner un fournisseure");
            for (String supplierName : supplierNames) {
                supplierComboBox.addItem(supplierName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement les Fournisseurs : " + e.getMessage());
        }
    }

    private boolean validateFields() {
        // Validate facture number
        if (factureNumberTextField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le numéro de facture est obligatoire !");
            factureNumberTextField.requestFocus();
            return false;
        }

        // Validate bill date
        if (billDateChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this, "La date de facture est obligatoire !");
            return false;
        }

        // Validate supplier selection
        if (supplierComboBox.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un fournisseur !");
            return false;
        }

        // Validate site selection
        if (siteComboBox.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un chantier !");
            return false;
        }

        // Validate transfer fee
        try {
            double transferFee = Double.parseDouble(transferFeeTextField.getText().trim());
            if (transferFee < 0) {
                JOptionPane.showMessageDialog(this, "Les frais de transport ne peuvent pas être négatifs !");
                transferFeeTextField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Veuillez saisir des frais de transport valides !");
            transferFeeTextField.requestFocus();
            return false;
        }

        // Validate paid amount
        try {
            double paidAmount = Double.parseDouble(paidAmountTextField.getText().trim());
            if (paidAmount < 0) {
                JOptionPane.showMessageDialog(this, "Le montant payé ne peut pas être négatif !");
                paidAmountTextField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Veuillez saisir un montant payé valide !");
            paidAmountTextField.requestFocus();
            return false;
        }

        // Validate that at least one item exists
        if (billItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez ajouter au moins un article à la facture !");
            return false;
        }

        return true;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    private LocalDate convertToLocalDate(Date date) {
        if (date == null)
            return null;
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public Bill getBillFromForm() throws SQLException {
        Bill bill = new Bill();
        bill.setFactureNumber(factureNumberTextField.getText().trim());
        bill.setBillDate(convertToLocalDate(billDateChooser.getDate()));

        // Get supplier ID
        String selectedSupplier = (String) supplierComboBox.getSelectedItem();
        int supplierId = supplierDAO.getSupplierIdByName(selectedSupplier);
        bill.setSupplierID(supplierId);

        // Get site ID
        String selectedSite = (String) siteComboBox.getSelectedItem();
        int siteId = siteDAO.getSiteIdByName(selectedSite);
        bill.setSiteID(siteId);

        bill.setTransferFee(Double.parseDouble(transferFeeTextField.getText().trim()));
        bill.setPaidAmount(Double.parseDouble(paidAmountTextField.getText().trim()));

        // Calculate total cost
        double itemsTotal = 0.0;
        for (BiLLItem item : billItems) {
            itemsTotal += item.getTotalPrice();
        }
        bill.setCost(itemsTotal + bill.getTransferFee());

        return bill;
    }

    public List<BiLLItem> getBillItems() {
        return billItems;
    }
}
