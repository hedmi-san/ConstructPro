package constructpro.Service;

import constructpro.DAO.FinancialTransactionDAO;
import constructpro.DTO.FinancialTransaction;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;

public class FinanceTransactionDetail extends JDialog {

    private static final Color DARK_BACKGROUND = new Color(45, 45, 45);
    private static final Color ACCENT_COLOR = new Color(0, 120, 215);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);
    private static final Color LABEL_COLOR = new Color(180, 180, 180);

    private final FinancialTransaction currentTransaction;
    private final FinancialTransactionDAO transactionDAO;
    private JPanel imagePanel;

    public FinanceTransactionDetail(JFrame parent, FinancialTransaction transaction, Connection connection) {
        super(parent, "Détails de la Transaction", true);
        this.currentTransaction = transaction;
        this.transactionDAO = new FinancialTransactionDAO(connection);

        initializeComponents();
        setupLayout();

        setSize(800, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initializeComponents() {
        imagePanel = new JPanel(new BorderLayout(10, 10));
        imagePanel.setBackground(DARK_BACKGROUND);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(DARK_BACKGROUND);

        setupImagePanel();
        add(imagePanel, BorderLayout.CENTER);
    }

    private void setupImagePanel() {
        imagePanel.removeAll();

        // Title panel with upload button
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(DARK_BACKGROUND);

        JLabel titleLabel = new JLabel("Preuve de Transaction");
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

        if (currentTransaction.getImagePath() == null || currentTransaction.getImagePath().isEmpty()) {
            // Show placeholder message
            JLabel noImageLabel = new JLabel("Aucune image n'a été sélectionnée pour le moment");
            noImageLabel.setForeground(LABEL_COLOR);
            noImageLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            noImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            displayPanel.add(noImageLabel, BorderLayout.CENTER);
        } else {
            // Display the image
            try {
                File imageFile = new File(currentTransaction.getImagePath());
                if (imageFile.exists()) {
                    ImageIcon originalIcon = new ImageIcon(currentTransaction.getImagePath());

                    // Scale image to fit panel while maintaining aspect ratio
                    int maxWidth = 700;
                    int maxHeight = 450;
                    Image scaledImage = scaleImage(originalIcon.getImage(), maxWidth, maxHeight);

                    JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
                    imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

                    JScrollPane scrollPane = new JScrollPane(imageLabel);
                    scrollPane.setBackground(DARK_BACKGROUND);
                    scrollPane.getViewport().setBackground(DARK_BACKGROUND);
                    scrollPane.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));

                    displayPanel.add(scrollPane, BorderLayout.CENTER);
                } else {
                    JLabel errorLabel = new JLabel("Image introuvable: " + currentTransaction.getImagePath());
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
        fileChooser.setDialogTitle("Sélectionner une preuve de transaction");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Images (*.jpg, *.jpeg, *.png)", "jpg", "jpeg", "png"));

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            try {
                // Create data/transactions directory if it doesn't exist
                File transactionsDir = new File("data/transactions");
                if (!transactionsDir.exists()) {
                    transactionsDir.mkdirs();
                }

                // Create unique filename using transaction ID and timestamp
                String fileExtension = getFileExtension(selectedFile.getName());
                String newFileName = "trans_" + currentTransaction.getId() + "_" + System.currentTimeMillis()
                        + fileExtension;
                File destFile = new File(transactionsDir, newFileName);

                // Copy file to destination
                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Update transaction with image path
                String imagePath = destFile.getPath();
                currentTransaction.setImagePath(imagePath);

                // Update database (Requires update method in DAO, if not exists need to add it
                // or use raw query here if needed,
                // but assuming DAO handles it or we directly update)
                // Since FinancialTransactionDAO doesn't explicitly have update, we might need
                // to add it.
                // For now, let's assume we can update it directly or modify DAO.
                // Wait, FinancialTransactionDAO usually has insert/delete. Let's check or add
                // update.
                transactionDAO.updateTransactionImageInDB(currentTransaction);

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
}
