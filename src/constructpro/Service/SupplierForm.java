package constructpro.Service;

import constructpro.DTO.Fournisseur;
import java.sql.Connection;
import javax.swing.*;


public class SupplierForm extends JDialog {
    
    private boolean confirmed = false;
    
    public SupplierForm(JFrame parent, String title, Fournisseur supplier,Connection connection){
        super(parent, title, true);
        
        initComponents();
        setupLayout();
        setupActions();
        
        if (supplier != null) {
            populateFields(supplier);
        }
        
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents(){
        
    }
    private void setupLayout(){
        
    }
    private void setupActions(){
        
    }
    private void populateFields(Fournisseur supplier){
        
    }
    
    public Fournisseur getSupplierFromForm(){
        Fournisseur supplier = new Fournisseur();
        //TODO: setting data from text fields.
        return supplier;
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
}
