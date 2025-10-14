package constructpro.DAO;

import constructpro.DTO.Salary;
import java.sql.*;

public class SalaryDAO {
    private Connection conn;

    public SalaryDAO(Connection conn) {
        this.conn = conn;
    }
}
