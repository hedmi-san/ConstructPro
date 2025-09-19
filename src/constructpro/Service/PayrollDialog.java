package constructpro.Service;

import constructpro.DTO.PayrollPeriod;
import constructpro.DTO.Salary;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

public class PayrollDialog extends JDialog {
    private PayrollService payrollService;
    private Connection conn;

    private JTextField dateField;
    private JList<Integer> workerList;
    private DefaultListModel<Integer> workerListModel;
    private JTextArea resultArea;
    private JButton processButton;

    public PayrollDialog(Frame parent, Connection conn) {
        super(parent, "Payroll Processing", true);
        this.conn = conn;
        try {
            this.payrollService = new PayrollService(conn);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error initializing PayrollService: " + e.getMessage());
            return;
        }

        initComponents();
        setSize(600, 400);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Date (yyyy-mm-dd):"));
        dateField = new JTextField(LocalDate.now().toString(), 10);
        topPanel.add(dateField);

        workerListModel = new DefaultListModel<>();
        // For demo, just populate worker IDs (replace with DAO fetch)
        workerListModel.addElement(1);
        workerListModel.addElement(2);
        workerListModel.addElement(3);
        workerList = new JList<>(workerListModel);
        workerList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JScrollPane workerScroll = new JScrollPane(workerList);
        workerScroll.setPreferredSize(new Dimension(150, 200));

        processButton = new JButton("Process Payroll");
        processButton.addActionListener(this::processPayroll);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane resultScroll = new JScrollPane(resultArea);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(new JLabel("Select Workers:"), BorderLayout.NORTH);
        centerPanel.add(workerScroll, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(processButton, BorderLayout.NORTH);
        bottomPanel.add(resultScroll, BorderLayout.CENTER);

        getContentPane().setLayout(new BorderLayout(5, 5));
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(centerPanel, BorderLayout.WEST);
        getContentPane().add(bottomPanel, BorderLayout.CENTER);
    }

    private void processPayroll(ActionEvent e) {
        try {
            LocalDate date = LocalDate.parse(dateField.getText());
            PayrollPeriod period = payrollService.generatePayrollPeriod(date);

            List<Integer> selectedIds = workerList.getSelectedValuesList();
            if (selectedIds.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select at least one worker.");
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("Payroll for period: ").append(period.getStartDate())
              .append(" to ").append(period.getEndDate()).append("\n");

            for (int workerId : selectedIds) {
                Salary salary = payrollService.calculateWorkerSalary(workerId, period);
                sb.append("Worker ").append(workerId)
                  .append(" -> Days: ").append(salary.getDaysWorked())
                  .append(", Earned: ").append(salary.getTotalEarned())
                  .append(", Paid: ").append(salary.getAmountPaid())
                  .append(", Retained: ").append(salary.getRetainedAmount())
                  .append("\n");
            }

            period.setProcessed(true);
            resultArea.setText(sb.toString());

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error processing payroll: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
