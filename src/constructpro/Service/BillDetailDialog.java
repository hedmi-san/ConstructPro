package constructpro.Service;

import constructpro.DTO.Bill;
import constructpro.DTO.BiLLItem;
import constructpro.DAO.SupplierDAO;
import constructpro.DAO.ConstructionSiteDAO;
import constructpro.DAO.BillDAO;
import constructpro.DAO.BiLLItemDAO;
import java.sql.*;
import java.io.File;
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
    private JButton printBillButton;
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

        // Initialize image panel
        imagePanel = new JPanel(new BorderLayout(10, 10));
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
        printBillButton = new JButton("Imprimer PDF");
        styleButton(printBillButton, ACCENT_COLOR);
        totalsPanel = createTotalsPanel();
    }

    private void styleButton(JButton btn, Color bgColor) {
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
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
        JPanel panel = new JPanel(new BorderLayout(20, 0));
        panel.setBackground(DARK_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, Color.WHITE),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));
        panel.setPreferredSize(new Dimension(0, 80));

        return panel;
    }

    private void updateTotalsPanel(double transferFee, double billTotal) {
        totalsPanel.removeAll();

        JPanel labelsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        labelsPanel.setBackground(DARK_BACKGROUND);

        JLabel transferFeeLabel = createTotalLabel("Frais de transfert:", true);
        JLabel transferFeeValue = createTotalLabel(String.format("%.2f DA", transferFee), false);
        JLabel billTotalLabel = createTotalLabel("Total de la facture:", true);
        JLabel billTotalValue = createTotalLabel(String.format("%.2f DA", billTotal), false);

        labelsPanel.add(transferFeeLabel);
        labelsPanel.add(transferFeeValue);
        labelsPanel.add(new JLabel("  |  ") {
            {
                setForeground(Color.GRAY);
            }
        });
        labelsPanel.add(billTotalLabel);
        labelsPanel.add(billTotalValue);

        totalsPanel.add(labelsPanel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setBackground(DARK_BACKGROUND);
        buttonPanel.add(printBillButton);
        totalsPanel.add(buttonPanel, BorderLayout.EAST);

        setupPDFAction();

        totalsPanel.revalidate();
        totalsPanel.repaint();
    }

    private void setupPDFAction() {
        // Remove existing listeners to avoid multiple calls
        for (java.awt.event.ActionListener al : printBillButton.getActionListeners()) {
            printBillButton.removeActionListener(al);
        }

        printBillButton.addActionListener(e -> {
            try {
                // Fetch extra info needed for PDF
                SupplierDAO sDAO = new SupplierDAO(conn);
                ConstructionSiteDAO csDAO = new ConstructionSiteDAO(conn);

                String sName = sDAO.getSupplierById(currentBill.getSupplierID()).getName();
                String csName = csDAO.getConstructionSiteById(currentBill.getSiteID()).getName();
                List<BiLLItem> items = itemDAO.getBillItems(currentBill.getId());

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Enregistrer la facture PDF");
                fileChooser
                        .setSelectedFile(new File("Facture_" + currentBill.getFactureNumber() + "_" + sName + ".pdf"));

                if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    String path = fileChooser.getSelectedFile().getAbsolutePath();
                    if (!path.toLowerCase().endsWith(".pdf"))
                        path += ".pdf";

                    SingleBillPDFGenerator.generatePDF(currentBill, items, sName, csName, path);
                    JOptionPane.showMessageDialog(this, "Facture générée avec succès !");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors de la génération du PDF: " + ex.getMessage());
            }
        });
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
        // Setup image panel
        setupImagePanel();

        // Add tabs
        tabbedPane.addTab("Informations", infoPanel);
        tabbedPane.addTab("Images", imagePanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void setupImagePanel() {
        imagePanel.removeAll();

        // Title panel with upload button
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(DARK_BACKGROUND);

        JLabel titleLabel = new JLabel("Image de la facture");
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton uploadButton = new JButton("Choisir l'image");
        uploadButton.setBackground(ACCENT_COLOR);
        uploadButton.setForeground(Color.WHITE);
        uploadButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        uploadButton.setFocusPainted(false);
        uploadButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        uploadButton.addActionListener(e -> uploadImage());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(DARK_BACKGROUND);
        buttonPanel.add(uploadButton);

        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(buttonPanel, BorderLayout.EAST);

        // Image display panel
        JPanel imageDisplayPanel = new JPanel(new BorderLayout());
        imageDisplayPanel.setBackground(DARK_BACKGROUND);
        imageDisplayPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        imagePanel.add(titlePanel, BorderLayout.NORTH);
        imagePanel.add(imageDisplayPanel, BorderLayout.CENTER);

        // Load and display image if exists
        displayImage(imageDisplayPanel);
    }

    private void displayImage(JPanel displayPanel) {
        displayPanel.removeAll();

        if (currentBill.getImagePath() == null || currentBill.getImagePath().isEmpty()) {
            // Show placeholder message
            JLabel noImageLabel = new JLabel("Aucune image n'a été sélectionnée pour le moment");
            noImageLabel.setForeground(LABEL_COLOR);
            noImageLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            noImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            displayPanel.add(noImageLabel, BorderLayout.CENTER);
        } else {
            // Display the image
            try {
                java.io.File imageFile = new java.io.File(currentBill.getImagePath());
                if (imageFile.exists()) {
                    ImageIcon originalIcon = new ImageIcon(currentBill.getImagePath());

                    // Scale image to fit panel while maintaining aspect ratio
                    int maxWidth = 700;
                    int maxHeight = 500;
                    Image scaledImage = scaleImage(originalIcon.getImage(), maxWidth, maxHeight);

                    JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
                    imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

                    JScrollPane scrollPane = new JScrollPane(imageLabel);
                    scrollPane.setBackground(DARK_BACKGROUND);
                    scrollPane.getViewport().setBackground(DARK_BACKGROUND);
                    scrollPane.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));

                    displayPanel.add(scrollPane, BorderLayout.CENTER);
                } else {
                    JLabel errorLabel = new JLabel("Image introuvable: " + currentBill.getImagePath());
                    errorLabel.setForeground(Color.RED);
                    errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    displayPanel.add(errorLabel, BorderLayout.CENTER);
                }
            } catch (Exception e) {
                JLabel errorLabel = new JLabel("Erreur lors du chargement de l'image");
                errorLabel.setForeground(Color.RED);
                errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
                displayPanel.add(errorLabel, BorderLayout.CENTER);
            }
        }

        displayPanel.revalidate();
        displayPanel.repaint();
    }

    private Image scaleImage(Image originalImage, int maxWidth, int maxHeight) {
        int originalWidth = originalImage.getWidth(null);
        int originalHeight = originalImage.getHeight(null);

        double widthRatio = (double) maxWidth / originalWidth;
        double heightRatio = (double) maxHeight / originalHeight;
        double ratio = Math.min(widthRatio, heightRatio);

        int scaledWidth = (int) (originalWidth * ratio);
        int scaledHeight = (int) (originalHeight * ratio);

        return originalImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
    }

    private void uploadImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Sélectionner une image de facture");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Images (*.jpg, *.jpeg, *.png)", "jpg", "jpeg", "png"));

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File selectedFile = fileChooser.getSelectedFile();

            try {
                // Create data/bills directory if it doesn't exist
                java.io.File billsDir = new java.io.File("data/bills");
                if (!billsDir.exists()) {
                    billsDir.mkdirs();
                }

                // Create unique filename using bill ID and timestamp
                String fileExtension = getFileExtension(selectedFile.getName());
                String newFileName = "bill_" + currentBill.getId() + "_" + System.currentTimeMillis() + fileExtension;
                java.io.File destFile = new java.io.File(billsDir, newFileName);

                // Copy file to destination
                java.nio.file.Files.copy(selectedFile.toPath(), destFile.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                // Update bill with image path
                String imagePath = destFile.getPath();
                currentBill.setImagePath(imagePath);

                // Update database
                billDAO.updateBill(currentBill);

                // Refresh image display
                JPanel imageDisplayPanel = (JPanel) imagePanel.getComponent(1);
                displayImage(imageDisplayPanel);

                JOptionPane.showMessageDialog(this,
                        "Image téléchargée avec succès!",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors du téléchargement de l'image: " + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0) {
            return fileName.substring(lastDot);
        }
        return "";
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
