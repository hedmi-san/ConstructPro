package constructpro.Service;

import javax.swing.*;
import java.sql.*;
import java.awt.*;
import constructpro.DTO.FinancialTransaction;

public class FinanceTransactionDetail extends JDialog {
    private Connection conn;
    
    public FinanceTransactionDetail(JFrame parent, FinancialTransaction transaction,Connection connection){
        super(parent,"Fiche financial",true);
        this.conn = connection;
    }
}
