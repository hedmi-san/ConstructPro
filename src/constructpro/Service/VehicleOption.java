package constructpro.Service;

import constructpro.UI.VehiclesPage;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class VehicleOption extends JDialog {

    private JFrame parentFrame;
    private static final Color DARK_BACKGROUND = new Color(45, 45, 45);
    private Connection conn;
    private JButton ownedBtn;
    private JButton rentedBtn;
    VehiclesPage parentframe;

    public VehicleOption(Connection conn, JFrame owner, String title, VehiclesPage parentframe) {
        super(owner, "Choisissez une action", true);
        this.conn = conn;
        this.parentframe = parentframe;
        initializeComponents();
        setupLayout();
        setSize(455, 200);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initializeComponents() {
        ownedBtn = new JButton("Possédé");
        rentedBtn = new JButton("Loué");

        // Style buttons to match the image
        styleButton(ownedBtn);
        styleButton(rentedBtn);

        // Add action listeners
        ownedBtn.addActionListener(e -> {
            dispose();
            try {
                // Open Owned Vehicle window
                new OwnedVehicleForm(parentFrame, conn, parentframe).setVisible(true);
            } catch (SQLException ex) {
                Logger.getLogger(VehicleOption.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        rentedBtn.addActionListener(e -> {
            dispose();
            try {
                // Open Rented Vehicle window
                new RentedVehicleForm(parentFrame, conn, parentframe).setVisible(true);
            } catch (SQLException ex) {
                Logger.getLogger(VehicleOption.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    private void setupLayout() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(DARK_BACKGROUND);

        // Add vertical spacing and center the buttons
        buttonPanel.add(Box.createVerticalGlue());

        ownedBtn.setAlignmentX(CENTER_ALIGNMENT);
        buttonPanel.add(ownedBtn);

        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20))); // 20px spacing

        rentedBtn.setAlignmentX(CENTER_ALIGNMENT);
        buttonPanel.add(rentedBtn);

        buttonPanel.add(Box.createVerticalGlue());

        add(buttonPanel);
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

    public void setParentFrame(JFrame parent) {
        this.parentFrame = parent;
    }
}
