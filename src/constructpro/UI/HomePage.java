package constructpro.UI;

import javax.swing.*;
import constructpro.DAO.UserDAO;
import constructpro.DTO.User;
import java.awt.Color;
import java.awt.Font;

public class HomePage extends JPanel {

    private JLabel label1;
    private JLabel label2;
    private JLabel welcomeLabel;

    public HomePage(String username) {

        initComponents();
        User user = new User();
        new UserDAO().getFullName(user, username);
        String fullName = user.getFullName();
        if (fullName != null && !fullName.isEmpty()) {
            welcomeLabel.setText("Welcome, " + fullName + ".");
        } else {
            welcomeLabel.setText("Welcome.");
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        welcomeLabel = new JLabel();
        label1 = new JLabel();
        label2 = new JLabel();

        welcomeLabel.setFont(new Font("SansSerif", 0, 36));
        welcomeLabel.setForeground(Color.white);
        
        label1.setFont(new Font("SansSerif", 0, 18));
        label1.setForeground(Color.white);
        label1.setHorizontalAlignment(SwingConstants.CENTER);
        label1.setText("<html>Manage your construction sites, workers and bills, all in one place.<br><br> Click on the Menu button to start. </html>");
        

        label2.setFont(new Font("SansSerif", 0, 18));
        label2.setForeground(Color.white);
        label2.setHorizontalAlignment(SwingConstants.LEFT); 
        label2.setText("<html>"
                + "<ul>"
                + "<li><b>Workers:</b> Liste de tous les travailleurs de l’entreprise, y compris les ouvriers, chauffeurs, comptables, etc</li>"
                + "<li><b>Salaire:</b> Permet de suivre les jours de présence de chaque travailleur pour le calcul des salaires</li>"
                + "<li><b>Facture:</b> Permet de registre les facture de chaque chantier</li>"
                + "<li><b>Chantier:</b> Affiche les différents chantiers sur lesquels l’entreprise travaille</li>"
                + "<li><b>Fournisseur:</b> Liste les fournisseurs de matériaux de construction et permet de suivre leurs factures</li>"
                + "<li><b>Camion:</b> Contient les informations sur les véhicule de l’entreprise, comme les camions et engins</li>"
                + "<li><b>Bureau:</b> Gère les opérations financières du bureau comptable : entrées et sorties d'argent dans le coffre</li>"
                + "<li><b>Material:</b> Suivi des fournisseurs de sol,béton,bric avec les quantités,volumes achetées et les prix</li>"
                + "<li><b>Tools:</b> Gère les achats des equipments avec les quantités achetés et les prix associés</li>"
                + "<li><b>Users:</b> Permet d’ajouter, supprimer ou modifier les comptes utilisateurs du système</li>"
                + "<li><b>User Logs:</b> Sert à suivre les connexions et déconnexions des utilisateurs du système</li>"
                + "</ul></html>");

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(welcomeLabel)
                        .addComponent(label1)
                        .addComponent(label2)
        );

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(welcomeLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(label1)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(label2)
        );
    }
}
