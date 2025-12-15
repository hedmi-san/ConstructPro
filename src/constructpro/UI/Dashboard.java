package constructpro.UI;

import constructpro.DAO.UserDAO;
import constructpro.DTO.User;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.time.LocalDateTime;
import javax.swing.*;

public class Dashboard extends JFrame {
    
    private JPanel displayPanel;
    private JMenu jMenu1;
    private JMenu jMenu2;
    private JMenuBar jMenuBar1;
    private JPanel mainPanel;
    private JPanel menuPanel;
    private JLabel nameLabel;
    private JPanel navPanel;
    private JPanel userPanel;
    private JButton homeButton;
    private JButton logoutButton;
    private JButton menuButton;
    private JButton WorkersButton;
    private JButton SalaryButton;
    private JButton BillButton;
    private JButton ConstructionSiteButton;
    private JButton SuppliersButton;
    private JButton truckButton;
    private JButton AccountButton;
    private JButton AttachmentButton;
    private JButton ToolsButton;
    private JButton usersButton;
    private JButton logsButton;
    Connection connection;
    CardLayout layout;
    String username;
    String userSelect;
    User userDTO;
    LocalDateTime outTime;
    
    public Dashboard(Connection con,String username, String usertype, User userDTO) {
        initComponents();
        navPanel.setVisible(true);
        menuPanel.setVisible(true);
        layout = new CardLayout();
        userSelect = usertype;
        this.username = username;
        this.userDTO = userDTO;
        this.connection = con;
        if("Employee".equalsIgnoreCase(usertype)){
            notForEmployee();
        }
        currentUserSession();
        
        // Panel Layout set to Card Layout to allow switching between different sections
        displayPanel.setLayout(layout);
        displayPanel.add("Home", new HomePage(username));
        displayPanel.add("Workers", new WorkersPage(connection));
        displayPanel.add("Salaire", new SalaryPage(connection));
        displayPanel.add("Facture", new BillPage(connection));
        displayPanel.add("Construction Sites", new ConstructionSitePage(connection));
        displayPanel.add("Suppliers", new SupplierPage(connection));
        displayPanel.add("Trucks", new VehiclesPage(connection));
        displayPanel.add("Accounting office", new AccountingOffice(connection));
        displayPanel.add("Attachment", new AttachmentPage(connection));
        displayPanel.add("Tools", new ToolAndMaterialPage(connection));
        displayPanel.add("Users", new UsersPage(connection));
        displayPanel.add("Logs", new UserLogPage(connection));
        
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                outTime = LocalDateTime.now();
                userDTO.setOutTime(String.valueOf(outTime));
                userDTO.setUserName(username);
                new UserDAO().addUserLogin(userDTO);
                super.windowClosing(e);
            }
        });
        setTitle("Construct Pro");
        setVisible(true);
    }
    
    // Methods to display different sections in the mainframe
    public void addHomePage(){
        layout.show(displayPanel, "Home");
    }
    public void addWorkersPage(){
        layout.show(displayPanel, "Workers");
    }
    public void addSalairePage(){
        layout.show(displayPanel, "Salaire");
    }
    public void addBillPage(){
        layout.show(displayPanel, "Facture");
    }
    public void addSitesPage(){
        layout.show(displayPanel, "Construction Sites");
    }
    public void addSuppPage(){
        layout.show(displayPanel, "Suppliers");
    }
    public void addTrucksPage(){
        layout.show(displayPanel, "Trucks");
    }
    public void addAccountPage(){
        layout.show(displayPanel, "Accounting office");
    }
    public void addAttachmentPage(){
        layout.show(displayPanel, "Attachment");
    }
    public void addToolPage(){
        layout.show(displayPanel, "Tools");
    }
    public void addUsersPage(){
        layout.show(displayPanel, "Users");
    }
    public void addLogsPage(){
        layout.show(displayPanel, "Logs");
    }
    
    
    @SuppressWarnings("unchecked")
    private void initComponents() {
        displayPanel = new JPanel();
        mainPanel = new JPanel();
        menuPanel = new JPanel();
        navPanel = new JPanel();
        userPanel = new JPanel();
        nameLabel = new JLabel();
        jMenu1 = new JMenu();
        jMenu2 = new JMenu();
        jMenuBar1 = new JMenuBar();
        homeButton = new JButton();
        logoutButton = new JButton();
        menuButton = new JButton();
        WorkersButton = new JButton();
        SalaryButton = new JButton();
        BillButton = new JButton();
        ConstructionSiteButton = new JButton();
        SuppliersButton = new JButton();
        truckButton = new JButton();
        AccountButton = new JButton();
        AttachmentButton = new JButton();
        ToolsButton = new JButton();
        usersButton = new JButton();
        logsButton = new JButton();
        
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        this.setTitle("Construct Pro");
        this.setForeground(Color.white);
        this.setBounds(new Rectangle(400, 100, 0, 0));
        
        menuPanel.setPreferredSize(new Dimension(120,26));
        menuButton.setFont(new Font("Segeo UI",1,14));
        menuButton.setIcon(new ImageIcon(getClass().getResource("/constructpro/UI/Icons/menu_icon.png")));
        menuButton.setText("MENU");
        menuButton.setForeground(Color.white);
        menuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MenuButtonActionPerformed(e);
            }
        });
        
        GroupLayout menuPanelLayout = new GroupLayout(menuPanel);
        menuPanel.setLayout(menuPanelLayout);
        menuPanel.setLayout(menuPanelLayout);
        menuPanelLayout.setHorizontalGroup(
            menuPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(menuButton,GroupLayout.DEFAULT_SIZE,GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        menuPanelLayout.setVerticalGroup(
            menuPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(menuPanelLayout.createSequentialGroup()
                .addComponent(menuButton,GroupLayout.PREFERRED_SIZE, 52,GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        navPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        
        homeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/constructpro/UI/Icons/home_icon.png")));
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HomeButtonActionPerformed(e);
            }
        });
        
        WorkersButton.setText("Travailleurs");
        WorkersButton.setForeground(Color.white);
        WorkersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                WorkersButtonActionPerformed(e);
            }
        });
        
        SalaryButton.setText("Salaire");
        SalaryButton.setForeground(Color.white);
        SalaryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SalaireButtonActionPerformed(e);
            }
        });
        
        BillButton.setText("Facture");
        BillButton.setForeground(Color.white);
        BillButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BillButtonActionPerformed(e);
            }
        });
        
        ConstructionSiteButton.setText("Chantier");
        ConstructionSiteButton.setForeground(Color.white);
        ConstructionSiteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SitesButtonActionPerformed(e);
            }
        });
        
        SuppliersButton.setText("Fournisseur");
        SuppliersButton.setForeground(Color.white);
        SuppliersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SuppliersButtonActionPerformed(e);
            }
        });
        
        truckButton.setText("Véhicules");
        truckButton.setForeground(Color.white);
        truckButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TrucksButtonActionPerformed(e);
            }
        });
        
        AccountButton.setText("Bureau");
        AccountButton.setForeground(Color.white);
        AccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AccountButtonActionPerformed(e);
            }
        });
        
        AttachmentButton.setText("Attachement");
        AttachmentButton.setForeground(Color.white);
        AttachmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AttachmentButtonActionPerformed(e);
            }
        });
        
        ToolsButton.setText("Matériel & Outils");
        ToolsButton.setForeground(Color.white);
        ToolsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ToolsButtonActionPerformed(e);
            }
        });
        
        usersButton.setText("Users");
        usersButton.setForeground(Color.white);
        usersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                usersButtonActionPerformed(e);
            }
        });
        
        logsButton.setText("User Logs");
        logsButton.setForeground(Color.white);
        logsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LogsButtonActionPerformed(e);
            }
        });
        
        GroupLayout navPanelLayout = new GroupLayout(navPanel);
        navPanel.setLayout(navPanelLayout);
        navPanelLayout.setHorizontalGroup(
            navPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(navPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(navPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(homeButton, GroupLayout.DEFAULT_SIZE,GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(WorkersButton,GroupLayout.DEFAULT_SIZE,GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(SalaryButton,GroupLayout.DEFAULT_SIZE,GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(BillButton,GroupLayout.DEFAULT_SIZE,GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ConstructionSiteButton,GroupLayout.DEFAULT_SIZE,GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(SuppliersButton,GroupLayout.DEFAULT_SIZE,GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(truckButton,GroupLayout.DEFAULT_SIZE,GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(AccountButton,GroupLayout.DEFAULT_SIZE,GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(AttachmentButton,GroupLayout.DEFAULT_SIZE,GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ToolsButton,GroupLayout.DEFAULT_SIZE,GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(usersButton,GroupLayout.DEFAULT_SIZE,GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(logsButton,GroupLayout.DEFAULT_SIZE,GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        
        navPanelLayout.setVerticalGroup(
            navPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(navPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(homeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(WorkersButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(SalaryButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(BillButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(ConstructionSiteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(SuppliersButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(truckButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(AccountButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(AttachmentButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(ToolsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(usersButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(logsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );
        displayPanel.setLayout(new CardLayout());
        
        nameLabel.setFont(new Font("Segoe UI Black", 0, 12));
        nameLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/constructpro/UI/Icons/user_icon.png"))); 
        nameLabel.setText("User: ");
        nameLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        
        logoutButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/constructpro/UI/Icons/log-out_icon.png"))); 
        logoutButton.setText("Sign out");
        logoutButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                logoutButtonActionPerformed(evt);
            }
        });
        
        GroupLayout userPanelLayout = new GroupLayout(userPanel);
        userPanel.setLayout(userPanelLayout);
        userPanelLayout.setHorizontalGroup(
            userPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(userPanelLayout.createSequentialGroup()
                .addContainerGap(401, Short.MAX_VALUE)
                .addComponent(nameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(logoutButton))
        );
        userPanelLayout.setVerticalGroup(
            userPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(userPanelLayout.createSequentialGroup()
                .addGroup(userPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, userPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(nameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(logoutButton, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        
        GroupLayout mainPanelLayout = new GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(navPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(menuPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(displayPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(userPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(userPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(menuPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(displayPanel,GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(navPanel, GroupLayout.DEFAULT_SIZE,GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        
        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel,GroupLayout.DEFAULT_SIZE,GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel,GroupLayout.DEFAULT_SIZE,GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        
    }
    
    //Logout method
    private void logoutButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_logoutButtonActionPerformed
        int opt = JOptionPane.showConfirmDialog(
                null,
                "<html>Are you sure you want to logout?<br>You will have to login again.<html>",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);
        if (opt==JOptionPane.YES_OPTION){
            outTime = LocalDateTime.now();
            userDTO.setOutTime(String.valueOf(outTime));
            userDTO.setFullName(username);
            new UserDAO().addUserLogin(userDTO);
            dispose();
            LoginPage logPage = new LoginPage();
            logPage.setVisible(true);
        }
    }
    
    private void usersButtonActionPerformed(ActionEvent evt) {
        addUsersPage();
    }
    private void HomeButtonActionPerformed(ActionEvent evt) {
        addHomePage();
    }
    private void WorkersButtonActionPerformed(ActionEvent evt) {
        addWorkersPage();
    }
    private void SalaireButtonActionPerformed(ActionEvent evt) {
        addSalairePage();
    }
    private void BillButtonActionPerformed(ActionEvent evt){
        addBillPage();
    }
    private void SitesButtonActionPerformed(ActionEvent evt) {
        addSitesPage();
    }
    private void SuppliersButtonActionPerformed(ActionEvent evt) {
        addSuppPage();
    }
    private void TrucksButtonActionPerformed(ActionEvent evt) {
        addTrucksPage();
    }
    private void AccountButtonActionPerformed(ActionEvent evt) {
        addAccountPage();
    }
    private void AttachmentButtonActionPerformed(ActionEvent evt) {
        addAttachmentPage();
    }
    private void ToolsButtonActionPerformed(ActionEvent evt) {
        addToolPage();
    }
    private void MenuButtonActionPerformed(ActionEvent evt) {
        navPanel.setVisible(!navPanel.isVisible());
    }
    private void LogsButtonActionPerformed(ActionEvent evt) {
        addLogsPage();
    }
    
    // Method to display the user currently logged in
    private void currentUserSession() {
        User user = new User();
        new UserDAO().getFullName(user, username);
        nameLabel.setText("User: " + user.getFullName() + " ("+userSelect+")");
    }
    
    // Allows only the ADMINISTRATOR type user to view and manipulate 'Users' and 'User Logs'
    private void notForEmployee(){
        navPanel.remove(usersButton);
        navPanel.remove(logsButton);
    }
}
