package constructpro.Service;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import constructpro.DAO.WorkerDAO;
import constructpro.DTO.Worker;
import java.util.logging.Level;
import java.util.logging.Logger;

public class salaryOption extends JDialog{
    private static final Color DARK_BACKGROUND = new Color(45, 45, 45);
    private Connection conn;
    private WorkerDAO workerDAO;
    private Worker worker;
    private JButton histoireBtn;
    private JButton salaireBtn;
    private JFrame parentFrame;
    
    public salaryOption(JFrame parent, Worker worker, Connection connection) throws SQLException {
        super(parent, "Choisissez une action", true);
        this.workerDAO = new WorkerDAO(connection);
        this.worker = worker;
        this.conn = connection;
        initializeComponents();
        setupLayout();
        setSize(455, 200);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    private void initializeComponents(){
        histoireBtn = new JButton("Histoire");
        salaireBtn = new JButton("Salaire");
        
        // Style buttons to match the image
        styleButton(histoireBtn);
        styleButton(salaireBtn);
        
        // Add action listeners
        histoireBtn.addActionListener(e -> {
            dispose();
            try {
                // Open Histoire window
                new HistoireDialog(parentFrame, worker, conn).setVisible(true);
            } catch (SQLException ex) {
                Logger.getLogger(salaryOption.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        salaireBtn.addActionListener(e -> {
            dispose();
            try {
                // Open Salaire window
                new PaymentPanel(parentFrame, worker, conn).setVisible(true);
            } catch (SQLException ex) {
                Logger.getLogger(salaryOption.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    private void styleButton(JButton button) {
        button.setPreferredSize(new Dimension(120, 40));
        button.setBackground(DARK_BACKGROUND);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        button.setFont(new Font("Segeo UI", Font.PLAIN, 14));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.DARK_GRAY);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(DARK_BACKGROUND);
            }
        });
    }
    
    private void setupLayout(){
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Title label
        JLabel titleLabel = new JLabel("Choisissez une action");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segeo UI", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 30, 0);
        add(titleLabel, gbc);
        
        // Histoire button
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 20, 20, 30);
        add(histoireBtn, gbc);
        
        // Salaire button
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 30, 20, 20);
        add(salaireBtn, gbc);
    }
    
    public void setParentFrame(JFrame parent) {
        this.parentFrame = parent;
    }
}
