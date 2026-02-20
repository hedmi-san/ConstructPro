package constructpro.UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import constructpro.DAO.UserDAO;
import constructpro.DTO.User;
import java.awt.*;
import java.sql.*;

public class HomePage extends JPanel {

    private Connection connection;
    private String username;
    private JLabel welcomeLabel;
    private JPanel cardsPanel;

    public HomePage(Connection con, String username) {
        this.connection = con;
        this.username = username;
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(30, 40, 30, 40));
        setOpaque(false);

        // --- Header Section ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        User user = new User();
        new UserDAO().getFullName(user, username);
        String fullName = user.getFullName();

        welcomeLabel = new JLabel(
                "Bon retour," + (fullName != null && !fullName.isEmpty() ? fullName : username) + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        welcomeLabel.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Voici ce qui se passe aujourd'hui avec les ressources de votre entreprise.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(new Color(180, 180, 180));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 5));
        titlePanel.setOpaque(false);
        titlePanel.add(welcomeLabel);
        titlePanel.add(subtitle);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // --- Center Content: Stats & Modules ---
        JPanel centerPanel = new JPanel(new BorderLayout(0, 40));
        centerPanel.setOpaque(false);

        // 1. Resource Overshow (Cards)
        cardsPanel = new JPanel(new GridLayout(1, 4, 25, 0));
        cardsPanel.setOpaque(false);
        centerPanel.add(cardsPanel, BorderLayout.NORTH);

        // 2. Modules Guide (Optional but helpful for transition)
        JPanel guidePanel = new JPanel(new BorderLayout(0, 15));
        guidePanel.setOpaque(false);

        JLabel guideTitle = new JLabel("GUIDE DE NAVIGATION RAPIDE");
        guideTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        guideTitle.setForeground(new Color(120, 120, 120));
        guidePanel.add(guideTitle, BorderLayout.NORTH);

        JPanel modulesGrid = new JPanel(new GridLayout(0, 2, 30, 15));
        modulesGrid.setOpaque(false);

        addModuleGuide(modulesGrid, "Travailleurs", "Gérez les employés et leurs affectations.");
        addModuleGuide(modulesGrid, "Chantier", "Suivez les projets de construction en cours.");
        addModuleGuide(modulesGrid, "Salaire", "Gérer les salaires et les paiements.");
        addModuleGuide(modulesGrid, "Matériel & Outil", "Gérer les matériaux et l'équipement.");

        guidePanel.add(modulesGrid, BorderLayout.CENTER);
        centerPanel.add(guidePanel, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
    }

    public void loadData() {
        cardsPanel.removeAll();

        // Fetching counts from DB with filters
        int workersCount = getCount("worker", null);
        int sitesCount = getCount("constructionSite", "status = 'Active'");
        int vehicleCount = getCount("vehicle", "assignedSiteId != 1");
        int supplierCount = getCount("suppliers", null);

        // Adding cards with specific accent colors
        cardsPanel.add(new StatCard("TOTAL DES TRAVAILLEURS", workersCount, new Color(41, 128, 185))); // Blue
        cardsPanel.add(new StatCard("CHANTIER ACTIVE", sitesCount, new Color(39, 174, 96))); // Green
        cardsPanel.add(new StatCard("PARC AUTOMOBILE", vehicleCount, new Color(211, 84, 0))); // Orange
        cardsPanel.add(new StatCard("FOURNISSEURS", supplierCount, new Color(142, 68, 173))); // Purple

        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private void addModuleGuide(JPanel container, String title, String desc) {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setOpaque(false);

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 14));
        t.setForeground(new Color(220, 220, 220));

        JLabel d = new JLabel(desc);
        d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        d.setForeground(new Color(150, 150, 150));

        p.add(t, BorderLayout.NORTH);
        p.add(d, BorderLayout.CENTER);

        container.add(p);
    }

    private int getCount(String tableName, String condition) {
        if (connection == null)
            return 0;
        String query = "SELECT COUNT(*) FROM " + tableName;
        if (condition != null && !condition.isEmpty()) {
            query += " WHERE " + condition;
        }
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error fetching count for " + tableName + ": " + e.getMessage());
        }
        return 0;
    }

    /**
     * Inner class for a modern dashboard statistic card.
     */
    private static class StatCard extends JPanel {
        public StatCard(String title, int value, Color accentColor) {
            setLayout(new BorderLayout());
            setBackground(new Color(40, 40, 40)); // Slightly lighter than background

            // Modern rounded border effect using compound border
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(60, 60, 60), 1),
                    new EmptyBorder(25, 25, 25, 25)));

            // Accent strip at the top
            JPanel accentStrip = new JPanel();
            accentStrip.setPreferredSize(new Dimension(0, 4));
            accentStrip.setBackground(accentColor);
            add(accentStrip, BorderLayout.NORTH);

            JPanel content = new JPanel(new GridLayout(2, 1, 0, 5));
            content.setOpaque(false);

            JLabel valueLabel = new JLabel(String.valueOf(value));
            valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
            valueLabel.setForeground(Color.WHITE);

            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            titleLabel.setForeground(new Color(140, 140, 140));

            content.add(valueLabel);
            content.add(titleLabel);

            add(content, BorderLayout.CENTER);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
