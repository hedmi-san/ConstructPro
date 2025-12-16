package constructpro.Service;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.*;
import java.util.List;

import constructpro.DAO.SalaryRecordDAO;
import constructpro.DAO.PaymentCheckDAO;
import constructpro.DTO.Worker;
import constructpro.DTO.SalaryRecord;
import constructpro.DTO.PaymentCheck;

public class HistoireDialog extends JDialog {

    private SalaryRecordDAO salaryRecordDAO;
    private PaymentCheckDAO paymentCheckDAO;

    private SalaryRecord salaryRecord;

    private JTable historyTable;
    private DefaultTableModel tableModel;
    private JButton supprimerBtn;
    private JButton modifierBtn;
    private JPanel totalsPanel;
    private static final Color DARK_BACKGROUND = new Color(45, 45, 45);

    public HistoireDialog(JFrame parent, Worker worker, Connection connection) throws SQLException {
        super(parent, "History", true);

        this.salaryRecordDAO = new SalaryRecordDAO(connection);
        this.paymentCheckDAO = new PaymentCheckDAO(connection);

        // Get or create salary record for this worker
        this.salaryRecord = salaryRecordDAO.getOrCreateSalaryRecord(worker.getId());

        initializeComponents();
        setupLayout();
        loadPaymentHistory();

        setSize(1000, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(DARK_BACKGROUND);
    }

    private void initializeComponents() {
        // Create table with columns
        String[] columns = { "Date de Paiement", "Montant du Paiement", "Le montant payé", "Le montant restant" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        historyTable = new JTable(tableModel);
        styleTable();

        // Create totals panel
        totalsPanel = createTotalsPanel();

        // Create buttons
        supprimerBtn = new JButton("Supprimer");
        modifierBtn = new JButton("Modifier");

        styleButton(supprimerBtn);
        styleButton(modifierBtn);

        // Add action listeners
        supprimerBtn.addActionListener(e -> deleteSelectedPayment());
        modifierBtn.addActionListener(e -> modifySelectedPayment());
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

    private void styleTable() {
        historyTable.setBackground(DARK_BACKGROUND);
        historyTable.setForeground(Color.WHITE);
        historyTable.setGridColor(Color.WHITE);
        historyTable.setSelectionBackground(Color.DARK_GRAY);
        historyTable.setSelectionForeground(Color.WHITE);
        historyTable.setFont(new Font("Arial", Font.PLAIN, 14));
        historyTable.setRowHeight(30);
        historyTable.setShowVerticalLines(true);
        historyTable.setGridColor(Color.WHITE);
        historyTable.getTableHeader().setBackground(Color.BLACK);
        historyTable.getTableHeader().setForeground(Color.WHITE);
        historyTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
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

    private void styleButton(JButton button) {
        button.setPreferredSize(new Dimension(120, 40));
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        button.setFont(new Font("Arial", Font.PLAIN, 14));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.DARK_GRAY);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.BLACK);
            }
        });
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));

        // Top panel with title
        JPanel topPanel = new JPanel();
        topPanel.setBackground(DARK_BACKGROUND);
        JLabel titleLabel = new JLabel("Salaire");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        topPanel.add(titleLabel);

        // Center panel with table and totals
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(DARK_BACKGROUND);

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.getViewport().setBackground(DARK_BACKGROUND);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));

        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(totalsPanel, BorderLayout.SOUTH);

        // Bottom panel with buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        bottomPanel.setBackground(DARK_BACKGROUND);
        bottomPanel.add(supprimerBtn);
        bottomPanel.add(modifierBtn);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadPaymentHistory() {
        try {
            tableModel.setRowCount(0); // Clear existing data

            List<PaymentCheck> checks = paymentCheckDAO.getAllWorkerPaymentChecks(salaryRecord.getId());

            double runningRemaining;
            for (PaymentCheck check : checks) {
                // For task-based payments (baseSalary = 0), remaining should be 0
                // For daily payments, calculate actual remaining
                if (check.getBaseSalary() == 0) {
                    runningRemaining = 0;
                } else {
                    runningRemaining = check.getBaseSalary() - check.getPaidAmount();
                }
                Object[] row = {
                        check.getPaymentDay(),
                        String.format("%.0f", check.getBaseSalary()),
                        String.format("%.0f", check.getPaidAmount()),
                        String.format("%.0f", runningRemaining)
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
                    "Erreur lors du chargement de l'historique: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
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

                // Update salary record
                salaryRecord.setAmountPaid(salaryRecord.getAmountPaid() - checkToDelete.getPaidAmount());
                salaryRecord.setTotalEarned(salaryRecord.getTotalEarned() - checkToDelete.getBaseSalary());
                salaryRecordDAO.updateSalaryRecord(salaryRecord);

                // Delete the PC from database
                paymentCheckDAO.deletePaymentCheck(checkToDelete.getId());
                // Reload table
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

        // TODO: Implement modification dialog
        JOptionPane.showMessageDialog(this,
                "Fonctionnalité de modification à implémenter",
                "Information",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
