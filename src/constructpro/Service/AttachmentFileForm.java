package constructpro.Service;

import constructpro.DAO.ConstructionSiteDAO;
import constructpro.DTO.ConstructionSite;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AttachmentFileForm extends JDialog {
    private Connection conn;
    private boolean confirmed = false;

    private JTextField titleField;
    private JComboBox<String> siteComboBox;
    private JTable jobsTable;
    private DefaultTableModel jobsModel;
    private List<String> selectedJobs = new ArrayList<>();

    // Colors (Consistent with other dialogs)
    private static final Color DARK_BG = new Color(45, 45, 45);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color INPUT_BG = new Color(60, 60, 60);
    private static final Color BTN_BG = new Color(70, 130, 180);

    public AttachmentFileForm(JFrame parent, String title, Connection connection) {
        super(parent, title, true);
        this.conn = connection;

        initComponents();
        loadSites();

        setSize(500, 500);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(DARK_BG);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(4, 1, 10, 10)); // Adjusted layout
        formPanel.setBackground(DARK_BG);

        // Title
        JLabel titleLabel = new JLabel("Titre du l'attachement:");
        titleLabel.setForeground(TEXT_COLOR);
        titleField = new JTextField();
        styleComponent(titleField);

        // Site
        JLabel siteLabel = new JLabel("Chantier:");
        siteLabel.setForeground(TEXT_COLOR);
        siteComboBox = new JComboBox<>();
        styleComponent(siteComboBox);

        formPanel.add(titleLabel);
        formPanel.add(titleField);
        formPanel.add(siteLabel);
        formPanel.add(siteComboBox);

        // Jobs/Workers Section
        JPanel jobsPanel = new JPanel(new BorderLayout(5, 5));
        jobsPanel.setBackground(DARK_BG);
        jobsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "Jobs / Rôles Responsables",
                0, 0, new Font("Arial", Font.BOLD, 12), TEXT_COLOR));

        jobsModel = new DefaultTableModel(new Object[] { "Job / Fonction" }, 0);
        jobsTable = new JTable(jobsModel);
        jobsTable.setBackground(INPUT_BG);
        jobsTable.setForeground(TEXT_COLOR);
        jobsTable.setFillsViewportHeight(true);

        JButton selectJobsBtn = new JButton("Sélectionner Jobs");
        styleButton(selectJobsBtn);
        selectJobsBtn.addActionListener(e -> openJobSelection());

        jobsPanel.add(new JScrollPane(jobsTable), BorderLayout.CENTER);
        jobsPanel.add(selectJobsBtn, BorderLayout.SOUTH);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(DARK_BG);

        JButton generateBtn = new JButton("Générer PDF");
        styleButton(generateBtn);
        JButton cancelBtn = new JButton("Annuler");
        styleButton(cancelBtn);
        cancelBtn.setBackground(Color.GRAY);

        generateBtn.addActionListener(e -> onGenerate());
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(generateBtn);
        buttonPanel.add(cancelBtn);

        // Assembly
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(jobsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void styleComponent(JComponent comp) {
        comp.setBackground(INPUT_BG);
        comp.setForeground(TEXT_COLOR);
        if (comp instanceof JTextField) {
            ((JTextField) comp).setCaretColor(TEXT_COLOR);
        }
    }

    private void styleButton(JButton btn) {
        btn.setBackground(BTN_BG);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
    }

    private void loadSites() {
        try {
            ConstructionSiteDAO siteDAO = new ConstructionSiteDAO(conn);
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            for (String siteName : siteDAO.getAllConstructionSitesNames()) {
                model.addElement(siteName);
            }
            siteComboBox.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur chargement sites: " + e.getMessage());
        }
    }

    private void openJobSelection() {
        JobSelectionDialog dialog = new JobSelectionDialog(this, conn);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            List<String> newJobs = dialog.getSelectedJobs();
            for (String job : newJobs) {
                if (!selectedJobs.contains(job)) {
                    selectedJobs.add(job);
                }
            }
            refreshJobsTable();
        }
    }

    private void refreshJobsTable() {
        jobsModel.setRowCount(0);
        for (String job : selectedJobs) {
            jobsModel.addRow(new Object[] { job });
        }
    }

    private void onGenerate() {
        if (titleField.getText().trim().isEmpty() || siteComboBox.getSelectedItem() == null
                || selectedJobs.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs et sélectionner des jobs.");
            return;
        }

        String siteName = (String) siteComboBox.getSelectedItem();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Enregistrer le PDF sous");
        fileChooser.setSelectedFile(new java.io.File(
                "Fiche_Attachement_" + titleField.getText().replaceAll("[^a-zA-Z0-9.-]", "_") + ".pdf"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".pdf")) {
                file = new java.io.File(file.getParentFile(), file.getName() + ".pdf");
            }

            try {
                // Fetch full site object (needed for PDF details like location)
                ConstructionSiteDAO siteDAO = new ConstructionSiteDAO(conn);
                ConstructionSite site = siteDAO.getConstructionSiteByName(siteName);

                AttachmentPDFGenerator.generatePDF(
                        titleField.getText(),
                        site,
                        selectedJobs,
                        file.getAbsolutePath());
                confirmed = true;
                JOptionPane.showMessageDialog(this,
                        "PDF Généré avec succès !\nEnregistré sous : " + file.getAbsolutePath());
                dispose();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors de la génération du PDF: " + e.getMessage());
            }
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
