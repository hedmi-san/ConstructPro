package constructpro.Service;

import com.toedter.calendar.JDateChooser;
import constructpro.DTO.FinancialTransaction;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.ZoneId;
import java.util.Date;

public class FinanceTransactionForm extends JDialog {
    private JDateChooser dateChooser;
    private JTextField amountField;
    private JComboBox<String> methodBox;
    private JButton uploadButton;
    private JLabel imageLabel;
    private JButton saveButton;
    private JButton cancelButton;
    private boolean confirmed = false;
    private File selectedImageFile;
    private String uploadedImagePath;

    private FinancialTransaction transaction;

    public FinanceTransactionForm(Window parent, String title) {
        super(parent, title, ModalityType.APPLICATION_MODAL);
        initComponents();
        setupLayout();
        setupActions();

        setSize(400, 350);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        dateChooser = new JDateChooser(new Date());
        dateChooser.setDateFormatString("dd/MM/yyyy");

        amountField = new JTextField(15);

        methodBox = new JComboBox<>(new String[] { "Espèces", "Chèque", "Virement" });

        uploadButton = new JButton("Choisir Image");
        imageLabel = new JLabel("Aucune image sélectionnée");

        saveButton = new JButton("Enregistrer");
        cancelButton = new JButton("Annuler");

        transaction = new FinancialTransaction();
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Date
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Date de paiement:"), gbc);
        gbc.gridx = 1;
        formPanel.add(dateChooser, gbc);
        row++;

        // Amount
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Montant (DA):"), gbc);
        gbc.gridx = 1;
        formPanel.add(amountField, gbc);
        row++;

        // Method
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Méthode:"), gbc);
        gbc.gridx = 1;
        formPanel.add(methodBox, gbc);
        row++;

        // Image
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Preuve (Image):"), gbc);
        gbc.gridx = 1;
        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        imagePanel.add(uploadButton);
        imagePanel.add(Box.createHorizontalStrut(10));
        imagePanel.add(imageLabel);
        formPanel.add(imagePanel, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(new JScrollPane(formPanel), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupActions() {
        uploadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "jpeg"));
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                selectedImageFile = fileChooser.getSelectedFile();
                imageLabel.setText(selectedImageFile.getName());
            }
        });

        saveButton.addActionListener(e -> {
            if (validateFields()) {
                uploadImage();
                populateTransaction();
                confirmed = true;
                dispose();
            }
        });

        cancelButton.addActionListener(e -> dispose());
    }

    private boolean validateFields() {
        if (dateChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this, "La date est obligatoire.");
            return false;
        }
        try {
            Double.parseDouble(amountField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Montant invalide.");
            return false;
        }
        return true;
    }

    private void uploadImage() {
        if (selectedImageFile != null) {
            try {
                File destDir = new File("data/transactions/");
                if (!destDir.exists())
                    destDir.mkdirs();

                String fileName = "trans_" + System.currentTimeMillis() + "_" + selectedImageFile.getName();
                File destFile = new File(destDir, fileName);

                Files.copy(selectedImageFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                uploadedImagePath = destFile.getPath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void populateTransaction() {
        transaction.setPaymentDate(dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        transaction.setAmount(Double.parseDouble(amountField.getText()));
        transaction.setMethod((String) methodBox.getSelectedItem());
        transaction.setImagePath(uploadedImagePath);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public FinancialTransaction getTransaction() {
        return transaction;
    }
}
