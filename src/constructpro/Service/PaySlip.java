package constructpro.Service;

import com.toedter.calendar.JDateChooser;
import java.awt.Color;
import javax.swing.*;
import java.sql.*;
import constructpro.DAO.ConstructionSiteDAO;
import constructpro.DAO.PaymentCheckDAO;
import constructpro.DAO.WorkerDAO;
import constructpro.DTO.ConstructionSite;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import javax.swing.filechooser.FileNameExtensionFilter;

public class PaySlip extends JDialog {
    private static final Color DARK_BACKGROUND = new Color(45, 45, 45);
    private static final Color DARKER_BACKGROUND = new Color(35, 35, 35);
    private static final Color ACCENT_COLOR = new Color(70, 130, 180);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);

    private Connection conn;
    private JComboBox<String> siteComboBox;
    private JDateChooser paymentDate;
    private JButton confirmButton, cancelButton;
    private ConstructionSiteDAO siteDAO;
    private PaymentCheckDAO checkDAO;
    private WorkerDAO workerDAO;
    private List<String> siteNames;

    public PaySlip(Connection connection, JFrame owner) throws SQLException {
        super(owner, "Fiche de paie", true);
        this.siteDAO = new ConstructionSiteDAO(connection);
        this.checkDAO = new PaymentCheckDAO(connection);
        this.workerDAO = new WorkerDAO(connection);
        this.conn = connection;

        initializeComponents();
        setupLayout();
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(450, 250);
    }

    private void initializeComponents() throws SQLException {
        // Load site names
        siteNames = siteDAO.getAllConstructionSitesNames();
        String[] siteNamesArray = siteNames.toArray(new String[0]);

        siteComboBox = new JComboBox<>(siteNamesArray);
        siteComboBox.setBackground(DARKER_BACKGROUND);
        siteComboBox.setForeground(TEXT_COLOR);
        siteComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));

        paymentDate = new JDateChooser();
        paymentDate.setDate(new Date());
        paymentDate.setFont(new Font("SansSerif", Font.PLAIN, 14));
        paymentDate.setBackground(DARKER_BACKGROUND);
        paymentDate.setForeground(TEXT_COLOR);

        confirmButton = new JButton("Enregistrer");
        styleButton(confirmButton, ACCENT_COLOR);
        confirmButton.addActionListener(e -> generatePDF());

        cancelButton = new JButton("Annuler");
        styleButton(cancelButton, new Color(180, 70, 70));
        cancelButton.addActionListener(e -> dispose());
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
    }

    private void setupLayout() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(DARK_BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Site label
        JLabel siteLabel = new JLabel("Chantier:");
        siteLabel.setForeground(TEXT_COLOR);
        siteLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        mainPanel.add(siteLabel, gbc);

        // Site combo box
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        mainPanel.add(siteComboBox, gbc);

        // Date label
        JLabel dateLabel = new JLabel("Date de paiement:");
        dateLabel.setForeground(TEXT_COLOR);
        dateLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        mainPanel.add(dateLabel, gbc);

        // Date picker
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        mainPanel.add(paymentDate, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(DARK_BACKGROUND);
        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        mainPanel.add(buttonPanel, gbc);

        setContentPane(mainPanel);
    }

    private void generatePDF() {
        try {
            // Get selected site
            int selectedIndex = siteComboBox.getSelectedIndex();
            if (selectedIndex < 0) {
                JOptionPane.showMessageDialog(this,
                        "Veuillez sélectionner un chantier",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            String selectedSiteName = siteNames.get(selectedIndex);
            ConstructionSite selectedSite = siteDAO.getConstructionSiteByName(selectedSiteName);

            if (selectedSite == null) {
                JOptionPane.showMessageDialog(this,
                        "Erreur: Chantier introuvable",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get selected date
            Date selectedDate = paymentDate.getDate();
            if (selectedDate == null) {
                JOptionPane.showMessageDialog(this,
                        "Veuillez sélectionner une date",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            LocalDate payDate = selectedDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            // Show file chooser
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Enregistrer le PDF");
            fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
            fileChooser.setSelectedFile(new java.io.File(
                    "Chèques_de_Paiement_" + selectedSite.getName() + "_" + payDate + ".pdf"));

            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                java.io.File fileToSave = fileChooser.getSelectedFile();
                String filePath = fileToSave.getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".pdf")) {
                    filePath += ".pdf";
                }

                // Generate PDF
                PaymentCheckPDFGenerator.generatePDF(
                        conn,
                        selectedSite,
                        payDate,
                        filePath,
                        checkDAO);

                JOptionPane.showMessageDialog(this,
                        "PDF généré avec succès!\n" + filePath,
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de la génération du PDF: " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

}
