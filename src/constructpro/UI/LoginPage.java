package constructpro.UI;

import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDarkerIJTheme;
import javax.swing.*;
import constructpro.DTO.User;
import constructpro.Database.ConnectionEstablish;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

public class LoginPage extends JFrame {

    User user;
    String userType;
    LocalDateTime inTime;
    private javax.swing.JComboBox<String> comboBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JButton loginButton;
    private javax.swing.JPasswordField passText;
    private javax.swing.JTextField userText;
    public Connection connection;
    public Statement st;
    public LoginPage() {
        
        initComponents();
        user = new User();
        try {
            connection = new ConnectionEstablish().getConn();
            st = connection.createStatement();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        jLabel3 = new JLabel();
        userText = new JTextField();
        passText = new JPasswordField();
        comboBox = new JComboBox();
        loginButton = new JButton();

        this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setTitle("Login");
        this.setBackground(Color.getHSBColor(102, 102, 102));
        this.setBounds(new Rectangle(500, 100, 0, 0));
        this.setName("LoginFrame");
        this.setSize(400, 600);
        this.add(jLabel1);
        jLabel1.setFont(new Font("Segoe UI", 0, 14));
        jLabel1.setText("Username: ");

        this.add(jLabel2);
        jLabel2.setFont(new Font("Segoe UI", 0, 14));
        jLabel2.setText("Password: ");

        this.add(jLabel3);
        jLabel3.setFont(new Font("Poor Richard", 1, 24));
        jLabel3.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel3.setText("Construct Pro");

        this.add(userText);

        this.add(passText);

        this.add(comboBox);
        comboBox.setModel(new DefaultComboBoxModel<>(new String[]{"Admin", "Employee"}));

        this.add(loginButton);
        loginButton.setText("LOGIN");
        loginButton.setForeground(Color.WHITE);
        loginButton.setHorizontalAlignment(SwingConstants.CENTER);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginButtonAction(e);
            }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(47)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(userText))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel2, GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(passText))
                                        .addComponent(comboBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel3, GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE))
                                .addGap(52))
                        .addComponent(loginButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
        );

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGap(44)
                        .addComponent(jLabel3, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
                        .addGap(57)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
                                .addComponent(userText, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel2, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
                                .addComponent(passText, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE))
                        .addGap(18)
                        .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
                        .addGap(46)
                        .addComponent(loginButton, GroupLayout.PREFERRED_SIZE, 37, GroupLayout.PREFERRED_SIZE)
                        .addGap(80)
        );

        pack();
        setVisible(true);
    }

    private void loginButtonAction(ActionEvent event) {
        String username = userText.getText();
        String password = new String(passText.getPassword()).trim();
        userType = (String) comboBox.getSelectedItem();
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.");
            return;
        }
        
        if (new ConnectionEstablish().checkLogin(username, password, userType)) {
            inTime = LocalDateTime.now();
            user.setInTime(String.valueOf(inTime));
            System.out.println("Login successful for: " + username + " as " + userType);
            dispose();
            new Dashboard(connection,username, userType, user);
        } else {
            JOptionPane.showMessageDialog(null,"Invalid username or password.");
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatMaterialDarkerIJTheme());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            new LoginPage().setVisible(true);
        });
    }
}
