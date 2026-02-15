package constructpro.Service;

import javax.swing.*;
import java.awt.*;
import constructpro.DTO.Worker;
import constructpro.DAO.ConstructionSiteDAO;
import constructpro.DAO.InsuranceDAO;
import constructpro.DTO.Insurance;
import constructpro.DAO.SalaryRecordDAO;
import constructpro.DAO.PaymentCheckDAO;
import constructpro.DTO.SalaryRecord;
import constructpro.DTO.PaymentCheck;
import constructpro.DAO.WorkerAssignmentDAO;
import constructpro.DTO.WorkerAssignment;
import java.sql.*;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;

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
    // Insurance
    private static final Color STATUS_ACTIVE = new Color(46, 125, 50);
    private static final Color STATUS_INACTIVE = new Color(183, 28, 28);
    private static final Color STATUS_PENDING = new Color(255, 152, 0);
    private static final Color DOCUMENT_SUBMITTED = new Color(76, 175, 80);
    private static final Color DOCUMENT_MISSING = new Color(244, 67, 54);
    // Components
    // Profile
    private JLabel nameLabel;
    private JPanel profilePanel;
    private JTabbedPane tabbedPane;
    // Insurrance
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

    // Payment History Components
    private JPanel paymentHistoryPanel;
    private JTable historyTable;
    private DefaultTableModel tableModel;
    private SalaryRecordDAO salaryRecordDAO;
    private PaymentCheckDAO paymentCheckDAO;
    private SalaryRecord salaryRecord;
    private JPanel totalsPanel;
    private JButton addPaymentBtn;
    private JButton modifyPaymentBtn;
    private JButton deletePaymentBtn;
    private JButton printReceiptBtn;
    private JFrame parentFrame;
    // Assignment History Components
    private JPanel assignmentHistoryPanel;
    private JTable assignmentsTable;
    private DefaultTableModel tableModel2;
    private WorkerAssignmentDAO workerAssignmentDAO;

    private Connection conn;

    public WorkerDetailDialog(JFrame parent, Worker worker, Connection connection) throws SQLException {
        super(parent, "Détails de Travailleur", true);
        this.currentWorker = worker;
        this.siteDAO = new ConstructionSiteDAO(connection);
        this.insurance = new Insurance();
        this.conn = connection;
        this.insuranceDAO = new InsuranceDAO(connection);
        this.salaryRecordDAO = new SalaryRecordDAO(connection);
        this.paymentCheckDAO = new PaymentCheckDAO(connection);
        this.workerAssignmentDAO = new WorkerAssignmentDAO(connection);

        this.salaryRecord = salaryRecordDAO.getOrCreateSalaryRecord(worker.getId());

        initializeComponents();
        setupLayout();
        setupStyling();
        populateData();
        populateInsuranceData(insurance);
        loadPaymentHistory(); // Load payment history data
        loadAssignmentHistory(); // Load assignment history data

        setSize(900, 650);
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

        // Initialize payment history components
        initializePaymentHistoryComponents();
        // Initialize Assignment history components
        initializeAssignmentHistoryComponents();
    }

    private void initializePaymentHistoryComponents() {
        // Create table with columns
        String[] columns = { "Date de Paiement", "Montant du Paiement", "Le montant payé", "Le montant restant" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        historyTable = new JTable(tableModel);
        stylePaymentHistoryTable();

        // Create totals panel
        totalsPanel = createTotalsPanel();

        // Create payment history panel
        paymentHistoryPanel = new JPanel(new BorderLayout(10, 10));
        paymentHistoryPanel.setBackground(DARK_BACKGROUND);
    }

    private void initializeAssignmentHistoryComponents() {
        // table
        String[] columns = { "Chantier", "Date d'affectation", "Date de désaffectation" };
        tableModel2 = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        assignmentsTable = new JTable(tableModel2);
        styleAssignmentHistoryTable();

        // Create payment history panel
        assignmentHistoryPanel = new JPanel(new BorderLayout(10, 10));
        assignmentHistoryPanel.setBackground(DARK_BACKGROUND);
    }

    private JPanel createTotalsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4));
        panel.setBackground(DARK_BACKGROUND);
        panel.setBorder(BorderFactory.createMatteBorder(2, 1, 1, 1, Color.WHITE));
        panel.setPreferredSize(new Dimension(0, 40));

        return panel;
    }

    private void updateTotalsPanel(double totalEarned, double totalPaid, double totalRemaining) {
        totalsPanel.removeAll();

        JLabel totalLabel = createTotalLabel("Total");
        JLabel earnedLabel = createTotalLabel(String.format("%.0f", totalEarned));
        JLabel paidLabel = createTotalLabel(String.format("%.0f", totalPaid));
        JLabel remainingLabel = createTotalLabel(String.format("%.0f", totalRemaining));

        totalsPanel.add(totalLabel);
        totalsPanel.add(earnedLabel);
        totalsPanel.add(paidLabel);
        totalsPanel.add(remainingLabel);

        totalsPanel.revalidate();
        totalsPanel.repaint();
    }

    private JLabel createTotalLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setBackground(DARK_BACKGROUND);
        label.setOpaque(true);
        label.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.WHITE));
        return label;
    }

    private void stylePaymentHistoryTable() {
        historyTable.setBackground(DARK_BACKGROUND);
        historyTable.setForeground(Color.WHITE);
        historyTable.setGridColor(Color.WHITE);
        historyTable.setSelectionBackground(Color.DARK_GRAY);
        historyTable.setSelectionForeground(Color.WHITE);
        historyTable.setFont(new Font("Arial", Font.PLAIN, 12));
        historyTable.setRowHeight(25);
        historyTable.setShowVerticalLines(true);
        historyTable.setGridColor(Color.WHITE);
        historyTable.getTableHeader().setBackground(Color.BLACK);
        historyTable.getTableHeader().setForeground(Color.WHITE);
        historyTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        historyTable.getTableHeader().setReorderingAllowed(false);

        // Center align all cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setBackground(DARK_BACKGROUND);
        centerRenderer.setForeground(Color.WHITE);

        for (int i = 0; i < historyTable.getColumnCount(); i++) {
            historyTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private void styleAssignmentHistoryTable() {
        assignmentsTable.setBackground(DARK_BACKGROUND);
        assignmentsTable.setForeground(Color.WHITE);
        assignmentsTable.setGridColor(Color.WHITE);
        assignmentsTable.setSelectionBackground(Color.DARK_GRAY);
        assignmentsTable.setSelectionForeground(Color.WHITE);
        assignmentsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        assignmentsTable.setRowHeight(25);
        assignmentsTable.setShowVerticalLines(true);
        assignmentsTable.setGridColor(Color.WHITE);
        assignmentsTable.getTableHeader().setBackground(Color.BLACK);
        assignmentsTable.getTableHeader().setForeground(Color.WHITE);
        assignmentsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        assignmentsTable.getTableHeader().setReorderingAllowed(false);

        // Center align all cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setBackground(DARK_BACKGROUND);
        centerRenderer.setForeground(Color.WHITE);

        for (int i = 0; i < assignmentsTable.getColumnCount(); i++) {
            assignmentsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private void setupPaymentHistoryPanel() {
        paymentHistoryPanel.removeAll();

        // Title panel with buttons
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(DARK_BACKGROUND);

        JLabel titleLabel = new JLabel("Historique des Paiements");
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titlePanel.add(titleLabel, BorderLayout.WEST);

        // Create buttons
        addPaymentBtn = new JButton("Ajouter");
        modifyPaymentBtn = new JButton("Modifier");
        deletePaymentBtn = new JButton("Supprimer");
        printReceiptBtn = new JButton("Reçu de paiement");

        styleButton(addPaymentBtn);
        styleButton(modifyPaymentBtn);
        styleButton(deletePaymentBtn);
        styleButton(printReceiptBtn);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.setBackground(DARK_BACKGROUND);
        buttonPanel.add(addPaymentBtn);
        buttonPanel.add(modifyPaymentBtn);
        buttonPanel.add(deletePaymentBtn);
        buttonPanel.add(printReceiptBtn);

        titlePanel.add(buttonPanel, BorderLayout.EAST);

        // Add action listeners
        addPaymentBtn.addActionListener(e -> {
            try {
                QuickPayDialog dialog = new QuickPayDialog(parentFrame, currentWorker, conn);
                dialog.setVisible(true);

                if (dialog.isSaved()) {
                    // Refresh salary record and payment history
                    salaryRecord = salaryRecordDAO.getSalaryRecordById(salaryRecord.getId());
                    loadPaymentHistory();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de l'ouverture du dialogue de paiement: " + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        modifyPaymentBtn.addActionListener(e -> modifySelectedPayment());
        deletePaymentBtn.addActionListener(e -> deleteSelectedPayment());
        printReceiptBtn.addActionListener(e -> {
            int selectedRow = historyTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                        "Veuillez sélectionner un paiement pour générer un reçu",
                        "Aucune sélection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                // Get the payment check from the selected row
                List<PaymentCheck> checks = paymentCheckDAO.getAllWorkerPaymentChecks(salaryRecord.getId());
                PaymentCheck selectedPaymentCheck = checks.get(selectedRow);

                PaymentReceiptForm dialog = new PaymentReceiptForm(parentFrame,
                        currentWorker,
                        selectedPaymentCheck,
                        conn);
                dialog.setVisible(true);

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de l'ouverture du formulaire de reçu: " + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Center panel with table and totals
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(DARK_BACKGROUND);

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.getViewport().setBackground(DARK_BACKGROUND);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));

        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(totalsPanel, BorderLayout.SOUTH);

        paymentHistoryPanel.add(titlePanel, BorderLayout.NORTH);
        paymentHistoryPanel.add(centerPanel, BorderLayout.CENTER);
    }

    private void setupAssignmentHistoryPanel() {
        assignmentHistoryPanel.removeAll();

        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(DARK_BACKGROUND);
        JLabel titleLabel = new JLabel("Historique des Affectations (Lecture Seule)");
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titlePanel.add(titleLabel);

        // Center panel with table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(DARK_BACKGROUND);

        JScrollPane scrollPane = new JScrollPane(assignmentsTable);
        scrollPane.getViewport().setBackground(DARK_BACKGROUND);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));

        centerPanel.add(scrollPane, BorderLayout.CENTER);

        assignmentHistoryPanel.add(titlePanel, BorderLayout.NORTH);
        assignmentHistoryPanel.add(centerPanel, BorderLayout.CENTER);
    }

    private void loadAssignmentHistory() {
        try {
            tableModel2.setRowCount(0); // Clear existing data

            List<WorkerAssignment> assignments = workerAssignmentDAO.getAllWorkerAssignments(currentWorker.getId());

            for (WorkerAssignment assignment : assignments) {
                // Get site name from siteDAO
                String siteName = siteDAO.getSiteNameById(assignment.getSiteId());
                if (siteName == null) {
                    siteName = "N/A";
                }

                // Format dates
                String assignmentDate = assignment.getAssignmentDate() != null
                        ? assignment.getAssignmentDate().toString()
                        : "N/A";
                String unassignmentDate = assignment.getUnAssignmentDate() != null
                        ? assignment.getUnAssignmentDate().toString()
                        : "En cours";

                Object[] row = {
                        siteName,
                        assignmentDate,
                        unassignmentDate
                };
                tableModel2.addRow(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors du chargement de l'historique des affectations: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadPaymentHistory() {
        try {
            tableModel.setRowCount(0); // Clear existing data

            List<PaymentCheck> checks = paymentCheckDAO.getAllWorkerPaymentChecks(salaryRecord.getId());

            double runningRemaining;

            for (PaymentCheck check : checks) {
                if (check.getBaseSalary() == 0) {
                    runningRemaining = 0;
                } else {
                    runningRemaining = check.getBaseSalary() - check.getPaidAmount();
                }

                Object[] row = {
                        check.getPaymentDay(),
                        String.format("%.0f", check.getBaseSalary()),
                        String.format("%.0f", check.getPaidAmount()),
                        String.format("%.0f", Math.abs(runningRemaining))
                };
                tableModel.addRow(row);
            }

            // Update the fixed totals panel
            double totalEarned = salaryRecord.getTotalEarned();
            double totalPaid = salaryRecord.getAmountPaid();
            double totalRemaining = totalPaid - totalEarned;
            updateTotalsPanel(totalEarned, totalPaid, Math.abs(totalRemaining));

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors du chargement de l'historique des paiements: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
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
                "Documents requis",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                TEXT_COLOR));

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
                "Informations sur l'assurance",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                TEXT_COLOR));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.anchor = GridBagConstraints.WEST;

        // Add insurance fields
        addInsuranceField(infoPanel, gbc, 0, 0, "Numéro d'assurance :", insuranceNumberValue);
        addInsuranceField(infoPanel, gbc, 0, 1, "Agence:", agencyNameValue);
        addInsuranceField(infoPanel, gbc, 0, 2, "Etat:", statusValue);
        addInsuranceField(infoPanel, gbc, 0, 3, "Date de début :", insuranceStartDateValue);
        addInsuranceField(infoPanel, gbc, 0, 4, "Date de fin :", endDateValue);

        // Documents section
        JPanel documentsSection = new JPanel(new BorderLayout(0, 10));
        documentsSection.setBackground(DARK_BACKGROUND);

        // Progress bar
        JPanel progressPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        progressPanel.setBackground(DARK_BACKGROUND);
        JLabel docLabel = new JLabel("Avancement du document :");
        docLabel.setForeground(LABEL_COLOR);
        progressPanel.add(docLabel);
        progressPanel.add(documentProgressBar);

        documentsSection.add(progressPanel, BorderLayout.NORTH);
        documentsSection.add(documentsPanel, BorderLayout.CENTER);

        // Buttons panel (Add / Edit)
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.setBackground(DARK_BACKGROUND);

        JButton addInsuranceBtn = new JButton("Ajouter");
        JButton editInsuranceBtn = new JButton("Modifier");

        // Style buttons if needed
        addInsuranceBtn.setBackground(new Color(0, 123, 255));
        addInsuranceBtn.setForeground(Color.WHITE);
        editInsuranceBtn.setBackground(new Color(255, 193, 7));
        editInsuranceBtn.setForeground(Color.BLACK);

        addInsuranceBtn.addActionListener(e -> {
            InsuranceFormDialog dialog = new InsuranceFormDialog(
                    SwingUtilities.getWindowAncestor(insurancePanel),
                    null, // no existing insurance
                    currentWorker.getId() // link to current worker
            );
            dialog.setVisible(true);
            Insurance newInsurance = null;
            if (dialog.isSaved()) {
                newInsurance = dialog.getInsurance();
                try {
                    insuranceDAO.addInsurance(newInsurance);
                } catch (SQLException ex) {
                }
            }
            populateInsuranceData(newInsurance);
        });
        editInsuranceBtn.addActionListener(e -> {
            try {
                Insurance existingInsurance = insuranceDAO.getInsuranceByWorkerId(currentWorker.getId());

                if (existingInsurance == null) {
                    JOptionPane.showMessageDialog(insurancePanel,
                            "Aucun dossier d'assurance trouvé pour ce travailleur.",
                            "Pas d'assurance",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                InsuranceFormDialog dialog = new InsuranceFormDialog(
                        SwingUtilities.getWindowAncestor(insurancePanel),
                        existingInsurance,
                        currentWorker.getId());
                dialog.setVisible(true);

                if (dialog.isSaved()) {
                    Insurance updatedInsurance = dialog.getInsurance();
                    insuranceDAO.updateInsurance(updatedInsurance);
                    populateInsuranceData(updatedInsurance);
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(insurancePanel,
                        "Erreur lors de la récupération de l'assurance : " + ex.getMessage(),
                        "Erreur de base de données",
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

        // Setup payment history panel
        setupPaymentHistoryPanel();

        // Setup assignment history panel
        setupAssignmentHistoryPanel();

        // Add tabs
        JScrollPane profileScroll = new JScrollPane(profilePanel);
        profileScroll.setBorder(null);
        profileScroll.getViewport().setBackground(DARK_BACKGROUND);
        tabbedPane.addTab("Profile", profileScroll);

        // Add insurance tab
        tabbedPane.addTab("Assurance", insurancePanel);

        // Add payment history tab
        JScrollPane paymentHistoryScroll = new JScrollPane(paymentHistoryPanel);
        paymentHistoryScroll.setBorder(null);
        paymentHistoryScroll.getViewport().setBackground(DARK_BACKGROUND);
        tabbedPane.addTab("Historique des Paiements", paymentHistoryScroll);

        // Add assignment history tab
        JScrollPane assignmentHistoryScroll = new JScrollPane(assignmentHistoryPanel);
        assignmentHistoryScroll.setBorder(null);
        assignmentHistoryScroll.getViewport().setBackground(DARK_BACKGROUND);
        tabbedPane.addTab("Historique des Affectations", assignmentHistoryScroll);

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
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        // Document name
        JLabel nameLabel = new JLabel(documentName);
        nameLabel.setForeground(TEXT_COLOR);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // Status indicator
        JLabel statusLabel = new JLabel(isSubmitted ? "Déposé" : "Absent");
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
            case "active":
                return STATUS_ACTIVE;
            case "non active":
            case "inactive":
                return STATUS_INACTIVE;
            case "pending":
                return STATUS_PENDING;
            default:
                return TEXT_COLOR;
        }
    }

    private void setupProfilePanel() {
        profilePanel.setBackground(DARK_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 20, 12, 20);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Two-column layout
        addFieldToPanel(gbc, 0, row, "Nom du père:", fatherNameValue, "Chantier :", chantierValue);
        row++;
        addFieldToPanel(gbc, 0, row, "Nom du mère:", motherNameValue, "", null);
        row++;
        addFieldToPanel(gbc, 0, row, "Date de Naissance:", birthDateValue, "", null);
        row++;
        addFieldToPanel(gbc, 0, row, "Lieux de Naissance:", birthPlaceValue, "", null);
        row++;
        addFieldToPanel(gbc, 0, row, "Date de Début:", startDateValue, "", null);
        row++;
        addFieldToPanel(gbc, 0, row, "Situation familiale:", familySituationValue, "", null);
        row++;
        addFieldToPanel(gbc, 0, row, "Numéro de carte d'identité:", identityCardNumberValue, "", null);
        row++;
        addFieldToPanel(gbc, 0, row, "Date de la carte d'identité:", idCardDateValue, "", null);
        row++;
        addFieldToPanel(gbc, 0, row, "Numéro de téléphone:", phoneNumberValue, "", null);
        row++;
        addFieldToPanel(gbc, 0, row, "Poste:", roleValue, "", null);
    }

    private void addFieldToPanel(GridBagConstraints gbc, int startX, int y, String label1, JLabel value1, String label2,
            JLabel value2) {
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
        insuranceNumberValue.setText(insurance.getInsuranceNumber() != null ? insurance.getInsuranceNumber() : "N/A");
        agencyNameValue.setText(insurance.getAgencyName() != null ? insurance.getAgencyName() : "N/A");

        // Set status with color coding
        String status = insurance.getStatus() != null ? insurance.getStatus() : "Inconnu";
        statusValue.setText(status);
        statusValue.setForeground(getStatusColor(status));

        insuranceStartDateValue.setText(insurance.getStartDate() != null ? insurance.getStartDate().toString() : "N/A");
        endDateValue.setText(insurance.getEndDate() != null ? insurance.getEndDate().toString() : "N/A");

        // Populate documents
        populateDocuments(insurance.getInsuranceDocuments());
    }

    private void styleButton(JButton button) {
        button.setPreferredSize(new Dimension(120, 30));
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        button.setFont(new Font("Arial", Font.PLAIN, 12));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.DARK_GRAY);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.BLACK);
            }
        });
    }

    private void deleteSelectedPayment() {
        int selectedRow = historyTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un paiement à supprimer",
                    "Aucune sélection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Voulez-vous vraiment supprimer ce paiement?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                List<PaymentCheck> checks = paymentCheckDAO.getAllWorkerPaymentChecks(salaryRecord.getId());
                PaymentCheck checkToDelete = checks.get(selectedRow);

                // Delete the payment check from database
                paymentCheckDAO.deletePaymentCheck(checkToDelete.getId());

                // Update salary record totals
                salaryRecordDAO.updateSalaryRecordTotals(salaryRecord.getId());

                // Refresh salary record and reload table
                salaryRecord = salaryRecordDAO.getSalaryRecordById(salaryRecord.getId());
                loadPaymentHistory();

                JOptionPane.showMessageDialog(this,
                        "Paiement supprimé avec succès",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de la suppression: " + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void modifySelectedPayment() {
        int selectedRow = historyTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un paiement à modifier",
                    "Aucune sélection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Get the payment check from the selected row
            List<PaymentCheck> checks = paymentCheckDAO.getAllWorkerPaymentChecks(salaryRecord.getId());
            PaymentCheck selectedPaymentCheck = checks.get(selectedRow);

            ModifyPaymentCheck dialog = new ModifyPaymentCheck(
                    this,
                    selectedPaymentCheck,
                    conn);
            dialog.setVisible(true);

            if (dialog.isSaved()) {
                // Refresh the salary record from database
                salaryRecord = salaryRecordDAO.getSalaryRecordById(salaryRecord.getId());
                // Refresh table
                loadPaymentHistory();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de la modification: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateData() throws SQLException {
        nameLabel.setText(currentWorker.getFirstName() + " " + currentWorker.getLastName());
        fatherNameValue.setText(currentWorker.getFatherName() != null ? currentWorker.getFatherName() : "N/A");
        motherNameValue.setText(currentWorker.getMotherName() != null ? currentWorker.getMotherName() : "N/A");
        birthDateValue.setText(currentWorker.getBirthDate() != null ? currentWorker.getBirthDate().toString() : "N/A");
        birthPlaceValue.setText(currentWorker.getBirthPlace() != null ? currentWorker.getBirthPlace() : "N/A");
        startDateValue.setText(currentWorker.getStartDate() != null ? currentWorker.getStartDate().toString() : "N/A");
        familySituationValue
                .setText(currentWorker.getFamilySituation() != null ? currentWorker.getFamilySituation() : "N/A");
        identityCardNumberValue
                .setText(currentWorker.getIdentityCardNumber() != null ? currentWorker.getIdentityCardNumber() : "N/A");
        idCardDateValue.setText(
                currentWorker.getIdentityCardDate() != null ? currentWorker.getIdentityCardDate().toString() : "N/A");
        phoneNumberValue.setText(currentWorker.getPhoneNumber() != null ? currentWorker.getPhoneNumber() : "N/A");
        accountNumberValue.setText(currentWorker.getAccountNumber() != null ? currentWorker.getAccountNumber() : "N/A");
        roleValue.setText(currentWorker.getRole() != null ? currentWorker.getRole() : "N/A");

        String siteName = "N/A";
        if (currentWorker.getAssignedSiteID() > 0) {
            siteName = siteDAO.getSiteNameById(currentWorker.getAssignedSiteID());
            if (siteName == null)
                siteName = "N/A";
        }
        chantierValue.setText(siteName);
    }

    public void setParentFrame(JFrame parent) {
        this.parentFrame = parent;
    }
}
