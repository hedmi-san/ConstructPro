package constructpro.Service;

import javax.swing.*;
import java.sql.*;
import constructpro.DTO.SalaryRecord;
import constructpro.DTO.PaymentCheck;
import constructpro.DAO.PaymentCheckDAO;
import constructpro.DAO.SalaryRecordDAO;
import constructpro.DAO.WorkerDAO;
import constructpro.DTO.Worker;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
        
public class PaymentPanel extends JDialog {
    private WorkerDAO workerDAO;
    private PaymentCheckDAO paymentCheckDAO;
    private SalaryRecordDAO salaryRecordDAO;
    
    public PaymentPanel(JFrame parent,Worker worker,Connection connection) throws SQLException {
        super(parent, "Histoire", true);
        this.workerDAO = new WorkerDAO(connection);
        this.paymentCheckDAO = new PaymentCheckDAO(connection);
        this.salaryRecordDAO = new SalaryRecordDAO(connection);
        initializeComponents();
        setupLayout();
        setSize(600, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    private void initializeComponents(){} 
    private void setupLayout(){}
}
