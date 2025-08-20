package constructpro.Service;

import javax.swing.*;
import java.awt.*;
import constructpro.DTO.Worker;
import constructpro.DAO.ConstructionSiteDAO;
import constructpro.DAO.InsuranceDAO;
import constructpro.DTO.Insurance;
import java.sql.*;
import java.util.List;

public class WorkerDetailDialog extends JDialog {
    private final Worker currentWorker;
    private final ConstructionSiteDAO siteDAO;
    private final Insurance insurance;
    private final InsuranceDAO insuranceDAO;
    
    // Color scheme for dark theme
    private static final Color DARK_BACKGROUND = new Color(45, 45, 45);
    private static final Color DARKER_BACKGROUND = new Color(35, 35, 35);
    private static final Color ACCENT_COLOR = new Color(0, 120, 215);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);
    private static final Color LABEL_COLOR = new Color(180, 180, 180);
    //Insurance
    private static final Color STATUS_ACTIVE = new Color(46, 125, 50);    
    private static final Color STATUS_INACTIVE = new Color(183, 28, 28);  
    private static final Color STATUS_PENDING = new Color(255, 152, 0);   
    private static final Color DOCUMENT_SUBMITTED = new Color(76, 175, 80); 
    private static final Color DOCUMENT_MISSING = new Color(244, 67, 54);
    // Components
    //Profile
    private JLabel nameLabel;
    private JPanel profilePanel;
    private JTabbedPane tabbedPane;
    //Insurrance 
    private JPanel insurancePanel;
    private JLabel insuranceNumberValue, agencyNameValue, statusValue;
    private JLabel insuranceStartDateValue, endDateValue;
    private JPanel documentsPanel;
    private JProgressBar documentProgressBar;
    // Profile fields
    private JLabel fatherNameValue, motherNameValue, birthDateValue, birthPlaceValue;
    private JLabel familySituationValue, identityCardNumberValue, phoneNumberValue;
    private JLabel roleValue, chantierValue, startDateValue, accountNumberValue, idCardDateValue;
    private static final String[] REQUIRED_DOCUMENTS = {
    "Acte de Naissance",
    "Fiche familiale de l'état civil", 
    "Photocopie de la carte identité",
    "Photocopie de chèque"
    };

    private Connection conn;
    
    public WorkerDetailDialog(JFrame parent, Worker worker,Connection connection) throws SQLException {
        super(parent, "Worker Details", true);
        this.currentWorker = worker;
        this.siteDAO = new ConstructionSiteDAO(connection);
        this.insurance = new Insurance();
        this.conn = connection;
        this.insuranceDAO = new InsuranceDAO(connection);
        initializeComponents();
        setupLayout();
        setupStyling();
        populateData();
        populateInsuranceData(insurance);
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initializeComponents() {
        // Header
        nameLabel = new JLabel();
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        
        // Tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Profile panel
        profilePanel = new JPanel();
        profilePanel.setLayout(new GridBagLayout());
        
        // Initialize value labels
        fatherNameValue = createValueLabel();
        motherNameValue = createValueLabel();
        birthDateValue = createValueLabel();
        birthPlaceValue = createValueLabel();
        familySituationValue = createValueLabel();
        identityCardNumberValue = createValueLabel();
        phoneNumberValue = createValueLabel();
        roleValue = createValueLabel();
        chantierValue = createValueLabel();
        startDateValue = createValueLabel();
        accountNumberValue = createValueLabel();
        idCardDateValue = createValueLabel();
        
    }
    
    private void initializeInsuranceComponents() {
    // Insurance panel
    insurancePanel = new JPanel();
    insurancePanel.setLayout(new BorderLayout());
    insurancePanel.setBackground(DARK_BACKGROUND);
    
    // Insurance info labels
    insuranceNumberValue = createValueLabel();
    agencyNameValue = createValueLabel();
    statusValue = createValueLabel();
    insuranceStartDateValue = createValueLabel();
    endDateValue = createValueLabel();
    
    // Documents panel
    documentsPanel = new JPanel();
    documentsPanel.setLayout(new BoxLayout(documentsPanel, BoxLayout.Y_AXIS));
    documentsPanel.setBackground(DARK_BACKGROUND);
    documentsPanel.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(LABEL_COLOR), 
        "Required Documents", 
        0, 0, 
        new Font("Segoe UI", Font.BOLD, 14), 
        TEXT_COLOR
    ));
    
    // Progress bar for documents
    documentProgressBar = new JProgressBar(0, REQUIRED_DOCUMENTS.length);
    documentProgressBar.setStringPainted(true);
    documentProgressBar.setString("0/" + REQUIRED_DOCUMENTS.length + " Documents");
    documentProgressBar.setBackground(DARKER_BACKGROUND);
    documentProgressBar.setForeground(ACCENT_COLOR);
    }
    
    private void addInsuranceField(JPanel panel, GridBagConstraints gbc, int x, int y, String label, JLabel value) {
    gbc.gridx = x;
    gbc.gridy = y;
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0;
    panel.add(createFieldLabel(label), gbc);
    
    gbc.gridx = x + 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;
    panel.add(value, gbc);
}
    
    private void setupInsurancePanel() {
        insurancePanel.removeAll();

        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(DARK_BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Insurance info panel
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(DARK_BACKGROUND);
        infoPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(LABEL_COLOR), 
            "Insurance Information", 
            0, 0, 
            new Font("Segoe UI", Font.BOLD, 14), 
            TEXT_COLOR
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.anchor = GridBagConstraints.WEST;

        // Add insurance fields
        addInsuranceField(infoPanel, gbc, 0, 0, "Insurance Number:", insuranceNumberValue);
        addInsuranceField(infoPanel, gbc, 0, 1, "Agency:", agencyNameValue);
        addInsuranceField(infoPanel, gbc, 0, 2, "Status:", statusValue);
        addInsuranceField(infoPanel, gbc, 0, 3, "Start Date:", insuranceStartDateValue);
        addInsuranceField(infoPanel, gbc, 0, 4, "End Date:", endDateValue);

        // Documents section
        JPanel documentsSection = new JPanel(new BorderLayout(0, 10));
        documentsSection.setBackground(DARK_BACKGROUND);

        // Progress bar
        JPanel progressPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        progressPanel.setBackground(DARK_BACKGROUND);
        JLabel docLabel = new JLabel("Document Progress:");
        docLabel.setForeground(LABEL_COLOR);
        progressPanel.add(docLabel);
        progressPanel.add(documentProgressBar);

        documentsSection.add(progressPanel, BorderLayout.NORTH);
        documentsSection.add(documentsPanel, BorderLayout.CENTER);

        // Buttons panel (Add / Edit)
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.setBackground(DARK_BACKGROUND);

        JButton addInsuranceBtn = new JButton("Add");
        JButton editInsuranceBtn = new JButton("Edit");

        // Style buttons if needed
        addInsuranceBtn.setBackground(new Color(0, 123, 255));
        addInsuranceBtn.setForeground(Color.WHITE);
        editInsuranceBtn.setBackground(new Color(255, 193, 7));
        editInsuranceBtn.setForeground(Color.BLACK);

        // Add listeners (to open dialog later)

        addInsuranceBtn.addActionListener(e -> {
            InsuranceFormDialog dialog = new InsuranceFormDialog(
                SwingUtilities.getWindowAncestor(insurancePanel),
                null, // no existing insurance
                currentWorker.getId() // link to current worker
            );
            dialog.setVisible(true);

            if (dialog.isSaved()) {
                Insurance newInsurance = dialog.getInsurance();
                try {
                    insuranceDAO.addInsurance(newInsurance);
                } catch (SQLException ex) {}
                populateInsuranceData(newInsurance);
            }
        });
        editInsuranceBtn.addActionListener(e -> {
            try {
                Insurance existingInsurance = insuranceDAO.getInsuranceByWorkerId(currentWorker.getId());

                if (existingInsurance == null) {
                    JOptionPane.showMessageDialog(insurancePanel, 
                        "No insurance record found for this worker.", 
                        "No Insurance", 
                        JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                InsuranceFormDialog dialog = new InsuranceFormDialog(
                    SwingUtilities.getWindowAncestor(insurancePanel),
                    existingInsurance,
                    currentWorker.getId()
                );
                dialog.setVisible(true);

                if (dialog.isSaved()) {
                    Insurance updatedInsurance = dialog.getInsurance();
                    insuranceDAO.updateInsurance(updatedInsurance);
                    populateInsuranceData(updatedInsurance);
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(insurancePanel, 
                    "Error fetching insurance: " + ex.getMessage(), 
                    "Database Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonsPanel.add(addInsuranceBtn);
        buttonsPanel.add(editInsuranceBtn);

        // Add components to main panel
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.add(documentsSection, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(DARK_BACKGROUND);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        insurancePanel.add(scrollPane, BorderLayout.CENTER);
}
    
    private JLabel createValueLabel() {
        JLabel label = new JLabel();
        label.setForeground(TEXT_COLOR);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return label;
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(LABEL_COLOR);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return label;
    }

    private void setupLayout() {
    setLayout(new BorderLayout());
    
    // Header panel with name and close button
    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setBackground(DARKER_BACKGROUND);
    headerPanel.add(nameLabel, BorderLayout.WEST);
    add(headerPanel, BorderLayout.NORTH);
    
    // Setup profile panel layout
    setupProfilePanel();
    
    // Initialize and setup insurance components
    initializeInsuranceComponents();
    setupInsurancePanel();
    
    // Add tabs
    JScrollPane profileScroll = new JScrollPane(profilePanel);
    profileScroll.setBorder(null);
    profileScroll.getViewport().setBackground(DARK_BACKGROUND);
    tabbedPane.addTab("Profile", profileScroll);
    
    // Add insurance tab
    tabbedPane.addTab("Insurance", insurancePanel);
    
    add(tabbedPane, BorderLayout.CENTER);
}
    
    private void populateDocuments(List<String> submittedDocuments) {
    documentsPanel.removeAll();
    
    int submittedCount = 0;
    
    for (String requiredDoc : REQUIRED_DOCUMENTS) {
        JPanel docPanel = createDocumentPanel(requiredDoc, 
            submittedDocuments != null && submittedDocuments.contains(requiredDoc));
        documentsPanel.add(docPanel);
        documentsPanel.add(Box.createVerticalStrut(8)); // Spacing
        
        if (submittedDocuments != null && submittedDocuments.contains(requiredDoc)) {
            submittedCount++;
        }
    }
    
    // Update progress bar
    documentProgressBar.setValue(submittedCount);
    documentProgressBar.setString(submittedCount + "/" + REQUIRED_DOCUMENTS.length + " Documents");
    
    // Color code progress bar
    if (submittedCount == REQUIRED_DOCUMENTS.length) {
        documentProgressBar.setForeground(STATUS_ACTIVE);
    } else if (submittedCount > 0) {
        documentProgressBar.setForeground(STATUS_PENDING);
    } else {
        documentProgressBar.setForeground(STATUS_INACTIVE);
    }
    
    documentsPanel.revalidate();
    documentsPanel.repaint();
}
    
    private JPanel createDocumentPanel(String documentName, boolean isSubmitted) {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(DARKER_BACKGROUND);
    panel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(isSubmitted ? DOCUMENT_SUBMITTED : DOCUMENT_MISSING, 1),
        BorderFactory.createEmptyBorder(8, 12, 8, 12)
    ));
    
    // Document name
    JLabel nameLabel = new JLabel(documentName);
    nameLabel.setForeground(TEXT_COLOR);
    nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    
    // Status indicator
    JLabel statusLabel = new JLabel(isSubmitted ? "✓ Submitted" : "✗ Missing");
    statusLabel.setForeground(isSubmitted ? DOCUMENT_SUBMITTED : DOCUMENT_MISSING);
    statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
    
    // Status icon
    JLabel iconLabel = new JLabel(isSubmitted ? "📄" : "❌");
    
    panel.add(nameLabel, BorderLayout.WEST);
    panel.add(statusLabel, BorderLayout.CENTER);
    panel.add(iconLabel, BorderLayout.EAST);
    
    return panel;
}
    
    private Color getStatusColor(String status) {
    switch (status.toLowerCase()) {
        case "active": return STATUS_ACTIVE;
        case "non active": 
        case "inactive": return STATUS_INACTIVE;
        case "pending": return STATUS_PENDING;
        default: return TEXT_COLOR;
    }
}

    private void setupProfilePanel() {
        profilePanel.setBackground(DARK_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 20, 12, 20);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        // Two-column layout
        addFieldToPanel(gbc, 0, row, "Father Name :", fatherNameValue, "Chantier :", chantierValue);
        row++;
        addFieldToPanel(gbc, 0, row, "Mother Name :", motherNameValue, "", null);
        row++;
        addFieldToPanel(gbc, 0, row, "Birth Date :", birthDateValue, "", null);
        row++;
        addFieldToPanel(gbc, 0, row, "Birth Place :", birthPlaceValue, "", null);
        row++;
        addFieldToPanel(gbc, 0, row, "Start Date :", startDateValue, "", null);
        row++;
        addFieldToPanel(gbc, 0, row, "Family Situation :", familySituationValue, "", null);
        row++;
        addFieldToPanel(gbc, 0, row, "Identity Card Number :", identityCardNumberValue, "", null);
        row++;
        addFieldToPanel(gbc, 0, row, "ID Card Date :", idCardDateValue, "", null);
        row++;
        addFieldToPanel(gbc, 0, row, "Phone Number :", phoneNumberValue, "", null);
        row++;
        addFieldToPanel(gbc, 0, row, "Fonction :", roleValue, "", null);
    }
    
    private void addFieldToPanel(GridBagConstraints gbc, int startX, int y, String label1, JLabel value1, String label2, JLabel value2) {
        gbc.gridy = y;
        
        // Left side
        gbc.gridx = startX;
        gbc.gridwidth = 1;
        profilePanel.add(createFieldLabel(label1), gbc);
        
        gbc.gridx = startX + 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        profilePanel.add(value1, gbc);
        
        // Right side (if provided)
        if (!label2.isEmpty() && value2 != null) {
            gbc.gridx = startX + 2;
            gbc.weightx = 0;
            gbc.fill = GridBagConstraints.NONE;
            gbc.insets = new Insets(12, 40, 12, 20);
            profilePanel.add(createFieldLabel(label2), gbc);
            
            gbc.gridx = startX + 3;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            gbc.insets = new Insets(12, 20, 12, 20);
            profilePanel.add(value2, gbc);
        }
        
        gbc.insets = new Insets(12, 20, 12, 20); // Reset insets
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
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

    private void populateInsuranceData(Insurance insurance) {
        
    try {
        InsuranceDAO insuranceDAO = new InsuranceDAO(conn);
        insurance = insuranceDAO.getInsuranceByWorkerId(currentWorker.getId());
    } catch (SQLException e) {
        e.printStackTrace();
    }
    if (insurance == null) {
        return;
    }
    
    // Populate insurance info
    insuranceNumberValue.setText(insurance.getInsuranceNumber() != null ? 
        insurance.getInsuranceNumber() : "N/A");
    agencyNameValue.setText(insurance.getAgencyName() != null ? 
        insurance.getAgencyName() : "N/A");
    
    // Set status with color coding
    String status = insurance.getStatus() != null ? insurance.getStatus() : "Unknown";
    statusValue.setText(status);
    statusValue.setForeground(getStatusColor(status));
    
    insuranceStartDateValue.setText(insurance.getStartDate() != null ? 
        insurance.getStartDate().toString() : "N/A");
    endDateValue.setText(insurance.getEndDate() != null ? 
        insurance.getEndDate().toString() : "N/A");
    
    // Populate documents
    populateDocuments(insurance.getInsuranceDocuments());
}
    
    private void populateData() throws SQLException {
        nameLabel.setText(currentWorker.getFirstName() + " " + currentWorker.getLastName());
        fatherNameValue.setText(currentWorker.getFatherName() != null ? currentWorker.getFatherName() : "N/A");
        motherNameValue.setText(currentWorker.getMotherName() != null ? currentWorker.getMotherName() : "N/A");
        birthDateValue.setText(currentWorker.getBirthDate() != null ? currentWorker.getBirthDate().toString() : "N/A");
        birthPlaceValue.setText(currentWorker.getBirthPlace() != null ? currentWorker.getBirthPlace() : "N/A");
        startDateValue.setText(currentWorker.getStartDate() != null ? currentWorker.getStartDate().toString() : "N/A");
        familySituationValue.setText(currentWorker.getFamilySituation() != null ? currentWorker.getFamilySituation() : "N/A");
        identityCardNumberValue.setText(currentWorker.getIdentityCardNumber() != null ? currentWorker.getIdentityCardNumber() : "N/A");
        idCardDateValue.setText(currentWorker.getIdentityCardDate() != null ? currentWorker.getIdentityCardDate().toString() : "N/A");
        phoneNumberValue.setText(currentWorker.getPhoneNumber() != null ? currentWorker.getPhoneNumber() : "N/A");
        accountNumberValue.setText(currentWorker.getAccountNumber() != null ? currentWorker.getAccountNumber() : "N/A");
        roleValue.setText(currentWorker.getRole() != null ? currentWorker.getRole() : "N/A");
        
        String siteName = "N/A";
        if (currentWorker.getAssignedSiteID() > 0) {
            siteName = siteDAO.getSiteNameById(currentWorker.getAssignedSiteID());
            if (siteName == null) siteName = "N/A";
        }
        chantierValue.setText(siteName);
    }

}