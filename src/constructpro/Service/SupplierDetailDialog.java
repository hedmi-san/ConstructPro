package constructpro.Service;

import java.awt.*;
import constructpro.DTO.Supplier;
import constructpro.DAO.BillDAO;
import constructpro.DTO.FinancialTransaction;
import constructpro.DAO.FinancialTransactionDAO;
import javax.swing.*;
import java.sql.*;
import java.io.File;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class SupplierDetailDialog extends JDialog {
    private static final Color DARK_BACKGROUND = new Color(45, 45, 45);
    private static final Color DARKER_BACKGROUND = new Color(35, 35, 35);
    private static final Color ACCENT_COLOR = new Color(0, 120, 215);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);

    private Supplier currentSupplier;
    private BillDAO billDAO;
    private FinancialTransactionDAO transactionDAO;

    private JTabbedPane tabbedPane;
    private JPanel infoPanel;
    private JPanel financeOperationPanel;

    // Tab 1 Components
    private JTable billsTable;
    private DefaultTableModel tableModel1;
    private JLabel totalSpentLabel, totalPaidLabel, debtLabel;

    // Tab 2 Components
    private JTable financeOperationTable;
    private DefaultTableModel tableModel2;
    private JButton addTransactionButton, deleteTransactionButton, printTransactionReceiptButton,
            printHistoryRecordButton;
    private JFrame parentFrame;

    private Connection conn;

    public SupplierDetailDialog(JFrame parent, Supplier supplier, Connection connection) {
        super(parent, "Détails de Fournisseur", true);
        this.conn = connection;
        this.currentSupplier = supplier;
        this.billDAO = new BillDAO(connection);
        this.transactionDAO = new FinancialTransactionDAO(connection);

        initializeComponents();
        setupLayout();
        setupStyling();
        populateData();
        populateTransactions();

        setSize(900, 650);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initializeComponents() {
        tabbedPane = new JTabbedPane();

        // --- Info Panel Components ---
        infoPanel = new JPanel(new BorderLayout());

        String[] columns = { "N° Facture", "Chantier", "Date", "Coût Total", "Montant Payé" };
        tableModel1 = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        billsTable = new JTable(tableModel1);

        totalSpentLabel = new JLabel("Total Dépensé: 0.00");
        totalPaidLabel = new JLabel("Total Payé: 0.00");
        debtLabel = new JLabel("Dette (Reste): 0.00");
        printHistoryRecordButton = new JButton("Historique PDF");
        // --- Finance/Operations Panel Components ---
        financeOperationPanel = new JPanel(new BorderLayout());

        String[] columns2 = { "ID", "Date", "Montant", "Méthode" };
        tableModel2 = new DefaultTableModel(columns2, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        financeOperationTable = new JTable(tableModel2);
        financeOperationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        financeOperationTable.setDefaultEditor(Object.class, null);
        financeOperationTable.getTableHeader().setReorderingAllowed(false);
        financeOperationTable.setShowVerticalLines(true);
        financeOperationTable.setGridColor(Color.WHITE);
        addTransactionButton = new JButton("Ajouter Transaction");
        deleteTransactionButton = new JButton("Supprimer Transaction");
        printTransactionReceiptButton = new JButton("Reçu de Paiement");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(DARK_BACKGROUND);

        // --- Setup Info Panel Layout ---
        JScrollPane tableScroll = new JScrollPane(billsTable);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());
        tableScroll.getViewport().setBackground(DARK_BACKGROUND);

        JPanel totalsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        totalsPanel.setBackground(DARKER_BACKGROUND);
        totalsPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(60, 60, 60)));
        totalsPanel.add(totalSpentLabel);
        totalsPanel.add(totalPaidLabel);
        totalsPanel.add(debtLabel);
        totalsPanel.add(printHistoryRecordButton);

        infoPanel.add(tableScroll, BorderLayout.CENTER);
        infoPanel.add(totalsPanel, BorderLayout.SOUTH);

        // --- Setup Finance Panel Layout ---
        JScrollPane transactionScroll = new JScrollPane(financeOperationTable);
        transactionScroll.setBorder(BorderFactory.createEmptyBorder());
        transactionScroll.getViewport().setBackground(DARK_BACKGROUND);

        JPanel transactionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        transactionButtonsPanel.setBackground(DARK_BACKGROUND);
        transactionButtonsPanel.add(addTransactionButton);
        transactionButtonsPanel.add(deleteTransactionButton);
        transactionButtonsPanel.add(printTransactionReceiptButton);

        financeOperationPanel.add(transactionScroll, BorderLayout.CENTER);
        financeOperationPanel.add(transactionButtonsPanel, BorderLayout.SOUTH);

        // Add tabs
        tabbedPane.addTab("Informations", infoPanel);
        tabbedPane.addTab("Opérations Financières", financeOperationPanel);

        add(tabbedPane, BorderLayout.CENTER);

        setupTransactionActions();
    }

    private void setupTransactionActions() {
        addTransactionButton.addActionListener(e -> {
            FinanceTransactionForm form = new FinanceTransactionForm(this, "Ajouter une Transaction");
            form.setVisible(true);

            if (form.isConfirmed()) {
                FinancialTransaction ft = form.getTransaction();
                ft.setSupplierId(currentSupplier.getId());
                try {
                    transactionDAO.insertTransaction(ft);
                    populateTransactions();
                    JOptionPane.showMessageDialog(this, "Transaction ajoutée avec succès!");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout: " + ex.getMessage());
                }
            }
        });

        financeOperationTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    uploadImage();
                }
            }
        });

        deleteTransactionButton.addActionListener(e -> {
            int selectedRow = financeOperationTable.getSelectedRow();
            if (selectedRow >= 0) {
                int id = (int) tableModel2.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer cette transaction ?",
                        "Confirmer", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        transactionDAO.deleteTransaction(id);
                        populateTransactions();
                        JOptionPane.showMessageDialog(this, "Transaction supprimée.");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner une transaction.");
            }
        });

        printTransactionReceiptButton.addActionListener(e -> {
            int selectedRow = financeOperationTable.getSelectedRow();
            if (selectedRow >= 0) {
                try {
                    int transactionId = (int) tableModel2.getValueAt(selectedRow, 0);
                    FinancialTransaction ft = transactionDAO.getTransactionById(transactionId);

                    if (ft != null) {
                        JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setDialogTitle("Enregistrer le reçu de paiement");
                        fileChooser.setSelectedFile(new File(
                                "Recu_Paiement_" + currentSupplier.getName() + "_" + ft.getId() + ".pdf"));

                        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                            String path = fileChooser.getSelectedFile().getAbsolutePath();
                            if (!path.toLowerCase().endsWith(".pdf"))
                                path += ".pdf";

                            SupplierTransactionReceiptPDFGenerator.generatePDF(currentSupplier, ft, path);
                            JOptionPane.showMessageDialog(this, "Reçu généré avec succès !");
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Erreur lors de la génération du PDF: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner une transaction.");
            }
        });

        printHistoryRecordButton.addActionListener(e -> {
            try {
                ResultSet rs = billDAO.getBillsBySupplierId(currentSupplier.getId());

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Enregistrer l'historique des factures");
                fileChooser
                        .setSelectedFile(new File("Historique_Factures_" + currentSupplier.getName() + ".pdf"));

                if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    String path = fileChooser.getSelectedFile().getAbsolutePath();
                    if (!path.toLowerCase().endsWith(".pdf"))
                        path += ".pdf";

                    SupplierBillHistoryPDFGenerator.generatePDF(conn, currentSupplier, rs, path);
                    JOptionPane.showMessageDialog(this, "Historique généré avec succès !");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors de la génération du PDF: " + ex.getMessage());
            }
        });
    }

    private void uploadImage() {
        int selectedRow = financeOperationTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                DefaultTableModel model = (DefaultTableModel) financeOperationTable.getModel();
                int transactionId = (Integer) model.getValueAt(selectedRow, 0); // Get worker ID from hidden column
                FinancialTransaction transaction = transactionDAO.getTransactionById(transactionId);
                if (transaction != null) {
                    FinanceTransactionDetail detailDialog = new FinanceTransactionDetail(parentFrame, transaction,
                            conn);
                    detailDialog.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Transaction non trouvé !", "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (HeadlessException | SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors du chargement des détails de transaction : " + ex.getMessage(), "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void setupStyling() {
        // TabbedPane Styling
        tabbedPane.setBackground(DARK_BACKGROUND);
        tabbedPane.setForeground(TEXT_COLOR);

        // Panels
        infoPanel.setBackground(DARK_BACKGROUND);
        financeOperationPanel.setBackground(DARK_BACKGROUND);

        // Tables
        styleTable(billsTable);
        styleTable(financeOperationTable);

        // Labels
        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        totalSpentLabel.setFont(labelFont);
        totalSpentLabel.setForeground(TEXT_COLOR);
        totalPaidLabel.setFont(labelFont);
        totalPaidLabel.setForeground(new Color(100, 255, 100));
        debtLabel.setFont(labelFont);
        debtLabel.setForeground(new Color(255, 100, 100));

        // Buttons
        styleButton(addTransactionButton, ACCENT_COLOR);
        styleButton(deleteTransactionButton, new Color(200, 50, 50));
        styleButton(printHistoryRecordButton, ACCENT_COLOR);
        styleButton(printTransactionReceiptButton, new Color(47, 230, 41));
    }

    private void styleButton(JButton btn, Color bgColor) {
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
    }

    private void styleTable(JTable table) {
        table.setBackground(DARK_BACKGROUND);
        table.setForeground(TEXT_COLOR);
        table.setGridColor(new Color(60, 60, 60));
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(ACCENT_COLOR);
        table.setSelectionForeground(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setBackground(DARKER_BACKGROUND);
        header.setForeground(TEXT_COLOR);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(60, 60, 60)));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private void populateData() {
        tableModel1.setRowCount(0);
        double totalSpent = 0;
        double totalPaid = 0;
        DecimalFormat currencyFormat = new DecimalFormat("#,##0.00");

        try {
            ResultSet rs = billDAO.getBillsBySupplierId(currentSupplier.getId());
            while (rs.next()) {
                String factureNum = rs.getString("factureNumber");
                String siteName = rs.getString("site_name");
                Date date = rs.getDate("billDate");
                double cost = rs.getDouble("totalCost");
                double paid = rs.getDouble("paidAmount");

                totalSpent += cost;
                totalPaid += paid;

                tableModel1.addRow(new Object[] {
                        factureNum,
                        siteName,
                        date,
                        currencyFormat.format(cost),
                        currencyFormat.format(paid)
                });
            }

            totalSpentLabel.setText("Total Dépensé: " + currencyFormat.format(totalSpent) + " DA");
            totalPaidLabel.setText("Total Payé: " + currencyFormat.format(totalPaid) + " DA");
            double debt = totalSpent - totalPaid;
            debtLabel.setText("Dette (Reste): " + currencyFormat.format(debt) + " DA");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateTransactions() {
        tableModel2.setRowCount(0);
        DecimalFormat df = new DecimalFormat("#,##0.00");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try {
            List<FinancialTransaction> transactions = transactionDAO
                    .getTransactionsBySupplierId(currentSupplier.getId());
            for (FinancialTransaction ft : transactions) {
                tableModel2.addRow(new Object[] {
                        ft.getId(),
                        ft.getPaymentDate().format(formatter),
                        df.format(ft.getAmount()) + " DA",
                        ft.getMethod()
                });
            }

            // Hide ID column
            financeOperationTable.getColumnModel().getColumn(0).setMinWidth(0);
            financeOperationTable.getColumnModel().getColumn(0).setMaxWidth(0);
            financeOperationTable.getColumnModel().getColumn(0).setWidth(0);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setParentFrame(JFrame parent) {
        this.parentFrame = parent;
    }
}
