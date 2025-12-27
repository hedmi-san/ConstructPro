package constructpro.Service;

import constructpro.DTO.Bill;
import constructpro.DTO.BiLLItem;
import constructpro.DAO.BillDAO;
import constructpro.DAO.BiLLItemDAO;
import java.sql.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class BillDetailDialog extends JDialog {

    // Color scheme for dark theme
    private static final Color DARK_BACKGROUND = new Color(45, 45, 45);
    private static final Color DARKER_BACKGROUND = new Color(35, 35, 35);
    private static final Color ACCENT_COLOR = new Color(0, 120, 215);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);
    private static final Color LABEL_COLOR = new Color(180, 180, 180);

    private final Bill currentBill;
    private JTabbedPane tabbedPane;
    private JPanel infoPanel;
    private JPanel imagePanel;
    private JTable billItemsTable;
    private DefaultTableModel tableModel;
    private JPanel totalsPanel;
    private BillDAO billDAO;
    private BiLLItemDAO itemDAO;

    private Connection conn;

    public BillDetailDialog(JFrame parent, Bill bill, Connection connection) {
        super(parent, "Détails de Facture", true);
        this.currentBill = bill;
        this.conn = connection;
        this.billDAO = new BillDAO(connection);
        this.itemDAO = new BiLLItemDAO(connection);

        initializeComponents();
        setupLayout();
        setupStyling();
        populateData();

        setSize(900, 650);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initializeComponents() {
        // Initialize tabbed pane
        tabbedPane = new JTabbedPane();

        // Initialize info panel
        infoPanel = new JPanel(new BorderLayout(10, 10));
        infoPanel.setBackground(DARK_BACKGROUND);

        // Initialize image panel (for later)
        imagePanel = new JPanel();
        imagePanel.setBackground(DARK_BACKGROUND);

        // Initialize bill items table
        String[] columns = { "Type d'article", "Nom de l'article", "Quantité", "Prix unitaire", "Prix total" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        billItemsTable = new JTable(tableModel);
        styleBillItemsTable();

        // Initialize totals panel
        totalsPanel = createTotalsPanel();
    }

    private void styleBillItemsTable() {
        billItemsTable.setBackground(DARK_BACKGROUND);
        billItemsTable.setForeground(Color.WHITE);
        billItemsTable.setGridColor(Color.WHITE);
        billItemsTable.setSelectionBackground(Color.DARK_GRAY);
        billItemsTable.setSelectionForeground(Color.WHITE);
        billItemsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        billItemsTable.setRowHeight(25);
        billItemsTable.setShowVerticalLines(true);
        billItemsTable.setGridColor(Color.WHITE);
        billItemsTable.getTableHeader().setBackground(Color.BLACK);
        billItemsTable.getTableHeader().setForeground(Color.WHITE);
        billItemsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        billItemsTable.getTableHeader().setReorderingAllowed(false);

        // Center align all cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setBackground(DARK_BACKGROUND);
        centerRenderer.setForeground(Color.WHITE);

        for (int i = 0; i < billItemsTable.getColumnCount(); i++) {
            billItemsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private JPanel createTotalsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBackground(DARK_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, Color.WHITE),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));
        panel.setPreferredSize(new Dimension(0, 80));

        return panel;
    }

    private void updateTotalsPanel(double transferFee, double billTotal) {
        totalsPanel.removeAll();

        // Transfer Fee row
        JLabel transferFeeLabel = createTotalLabel("Frais de transfert:", true);
        JLabel transferFeeValue = createTotalLabel(String.format("%.2f DA", transferFee), false);

        // Bill Total row
        JLabel billTotalLabel = createTotalLabel("Total de la facture:", true);
        JLabel billTotalValue = createTotalLabel(String.format("%.2f DA", billTotal), false);

        totalsPanel.add(transferFeeLabel);
        totalsPanel.add(transferFeeValue);
        totalsPanel.add(billTotalLabel);
        totalsPanel.add(billTotalValue);

        totalsPanel.revalidate();
        totalsPanel.repaint();
    }

    private JLabel createTotalLabel(String text, boolean isLabel) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", isLabel ? Font.BOLD : Font.PLAIN, 14));
        label.setBackground(DARK_BACKGROUND);
        label.setOpaque(true);

        if (isLabel) {
            label.setHorizontalAlignment(SwingConstants.RIGHT);
        } else {
            label.setHorizontalAlignment(SwingConstants.LEFT);
        }

        return label;
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Setup info panel
        setupInfoPanel();

        // Add tabs
        tabbedPane.addTab("Informations", infoPanel);
        tabbedPane.addTab("Images", imagePanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void setupInfoPanel() {
        infoPanel.removeAll();

        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(DARK_BACKGROUND);
        JLabel titleLabel = new JLabel("Articles de la facture");
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titlePanel.add(titleLabel);

        // Center panel with table and totals
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(DARK_BACKGROUND);

        JScrollPane scrollPane = new JScrollPane(billItemsTable);
        scrollPane.getViewport().setBackground(DARK_BACKGROUND);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));

        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(totalsPanel, BorderLayout.SOUTH);

        infoPanel.add(titlePanel, BorderLayout.NORTH);
        infoPanel.add(centerPanel, BorderLayout.CENTER);
    }

    private void setupStyling() {
        getContentPane().setBackground(DARK_BACKGROUND);

        // Style the tabbed pane
        tabbedPane.setBackground(DARK_BACKGROUND);
        tabbedPane.setForeground(TEXT_COLOR);
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Custom tab styling
        UIManager.put("TabbedPane.selected", ACCENT_COLOR);
        UIManager.put("TabbedPane.background", DARKER_BACKGROUND);
        UIManager.put("TabbedPane.foreground", TEXT_COLOR);

        SwingUtilities.updateComponentTreeUI(tabbedPane);
    }

    private void populateData() {
        try {
            // Clear existing data
            tableModel.setRowCount(0);

            // Load bill items
            List<BiLLItem> items = itemDAO.getBillItems(currentBill.getId());

            double itemsTotal = 0.0;

            for (BiLLItem item : items) {
                Object[] row = {
                        item.getBillType(),
                        item.getItemName(),
                        String.format("%.2f", item.getQuantity()),
                        String.format("%.2f", item.getUnitPrice()),
                        String.format("%.2f", item.getTotalPrice())
                };
                tableModel.addRow(row);
                itemsTotal += item.getTotalPrice();
            }

            // Calculate bill total (items total + transfer fee)
            double transferFee = currentBill.getTransferFee();
            double billTotal = itemsTotal + transferFee;

            // Update totals panel
            updateTotalsPanel(transferFee, billTotal);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors du chargement des articles de la facture: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
