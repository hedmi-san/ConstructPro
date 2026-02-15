package constructpro.Service;

import constructpro.DTO.PaymentCheck;
import constructpro.DTO.Worker;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import java.sql.*;
import java.io.File;

public class PaymentReceiptForm extends JDialog {
    private static final Color DARK_BACKGROUND = new Color(45, 45, 45);
    private Worker worker;
    private PaymentCheck paymentCheck;
    private JLabel titleLabel;
    private JTextField NINField, destinorField;
    private JButton saveButton;
    private boolean saved = false;

    public PaymentReceiptForm(JFrame parent, Worker worker, PaymentCheck paymentCheck, Connection connection) {
        super(parent, "Reçu de Paiement", true);

        this.worker = worker;
        this.paymentCheck = paymentCheck;
        initComponents(worker.getLastName() + " " + worker.getFirstName());
        setupLayout();
        setupActions();
        pack();
        setSize(500, 300);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initComponents(String workerFullName) {
        // Title
        titleLabel = new JLabel(workerFullName, SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        // Text fields
        NINField = createTextField();
        destinorField = createTextField();

        // Button
        saveButton = createButton("Enregistrer");
    }

    private JTextField createTextField() {
        JTextField field = new JTextField(20);
        field.setBackground(DARK_BACKGROUND);
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        return field;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(DARK_BACKGROUND);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        button.setPreferredSize(new Dimension(120, 30));
        return button;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return label;
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(DARK_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Title
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(titleLabel, gbc);
        gbc.gridwidth = 1;
        row++;

        // NIN field
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(createLabel("Numéro d'Identification Nationale"), gbc);
        gbc.gridx = 1;
        formPanel.add(NINField, gbc);
        row++;

        // Destinor name field
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(createLabel("Nom du Destinataire"), gbc);
        gbc.gridx = 1;
        formPanel.add(destinorField, gbc);
        row++;

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(DARK_BACKGROUND);
        buttonPanel.add(saveButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupActions() {
        saveButton.addActionListener(e -> {
            try {
                // Validate fields
                if (NINField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Veuillez saisir le numéro d'identification nationale !");
                    return;
                }

                if (destinorField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Veuillez saisir le nom du destinataire !");
                    return;
                }

                String nin = NINField.getText().trim();
                String destinorName = destinorField.getText().trim();

                // Create file chooser
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Enregistrer le reçu de paiement");

                // Set default filename
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String defaultFilename = String.format("Recu_%s_%s_%s.pdf",
                        worker.getLastName().replaceAll("\\s+", "_"),
                        worker.getFirstName().replaceAll("\\s+", "_"),
                        paymentCheck.getPaymentDay().format(formatter));
                fileChooser.setSelectedFile(new File(defaultFilename));

                // Show save dialog
                int result = fileChooser.showSaveDialog(this);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    String outputPath = selectedFile.getAbsolutePath();

                    // Ensure .pdf extension
                    if (!outputPath.toLowerCase().endsWith(".pdf")) {
                        outputPath += ".pdf";
                    }

                    // Generate PDF
                    PaymentReceiptPDFGenerator.generatePDF(
                            worker,
                            paymentCheck,
                            nin,
                            destinorName,
                            outputPath);

                    saved = true;
                    dispose();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de la génération du reçu : " + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public boolean isSaved() {
        return saved;
    }
}
