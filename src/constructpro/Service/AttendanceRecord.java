package constructpro.Service;

import java.awt.Color;
import javax.swing.*;
import java.sql.*;
import constructpro.DAO.ConstructionSiteDAO;
import constructpro.DAO.WorkerDAO;
import constructpro.DTO.ConstructionSite;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Date;
import java.util.List;

public class AttendanceRecord extends JDialog {
    private static final Color DARK_BACKGROUND = new Color(45, 45, 45);
    private static final Color DARKER_BACKGROUND = new Color(35, 35, 35);
    private static final Color ACCENT_COLOR = new Color(70, 130, 180);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);

    private Connection conn;
    private JComboBox<String> siteComboBox, monthPartComboBox;// First half, second half
    private JButton confirmButton, cancelButton;
    private ConstructionSiteDAO siteDAO;
    private WorkerDAO workerDAO;
    private List<String> siteNames;

    public AttendanceRecord(Connection connection, JFrame owner) throws SQLException {
        super(owner, "Fiche de paie", true);
        this.siteDAO = new ConstructionSiteDAO(connection);
        this.workerDAO = new WorkerDAO(connection);
        this.conn = connection;

        initializeComponents();
        setupLayout();
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(450, 250);
    }

    private void initializeComponents() throws SQLException {
        siteNames = siteDAO.getAllConstructionSitesNames();
        String[] siteNamesArray = siteNames.toArray(new String[0]);

        siteComboBox = new JComboBox<>(siteNamesArray);
        siteComboBox.setBackground(DARKER_BACKGROUND);
        siteComboBox.setForeground(TEXT_COLOR);
        siteComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));

        monthPartComboBox = new JComboBox<>(new String[] { "Première partie du mois", "Deuxieme partie du mois" });
        monthPartComboBox.setBackground(DARKER_BACKGROUND);
        monthPartComboBox.setForeground(TEXT_COLOR);
        monthPartComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));

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
        mainPanel.add(monthPartComboBox, gbc);

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

            // Get selected month part
            int monthPartIndex = monthPartComboBox.getSelectedIndex();
            boolean isFirstHalf = (monthPartIndex == 0); // 0 = first half, 1 = second half

            // Show file chooser
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Enregistrer le PDF");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF Files", "pdf"));

            String periodStr = isFirstHalf ? "1-14" : "15-fin";
            String monthYear = java.time.LocalDate.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("MM-yyyy"));
            fileChooser.setSelectedFile(new java.io.File(
                    "Fiche_Pointage_" + selectedSite.getName() + "_" + monthYear + "_" + periodStr + ".pdf"));

            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                java.io.File fileToSave = fileChooser.getSelectedFile();
                String filePath = fileToSave.getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".pdf")) {
                    filePath += ".pdf";
                }

                // Generate PDF
                AttendancePDFGenerator.generatePDF(
                        conn,
                        selectedSite,
                        isFirstHalf,
                        filePath,
                        workerDAO);

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
