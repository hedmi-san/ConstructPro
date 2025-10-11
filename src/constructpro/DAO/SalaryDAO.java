package constructpro.DAO;

import constructpro.DTO.Salary;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalaryDAO {
    private Connection conn;

    public SalaryDAO(Connection conn) {
        this.conn = conn;
    }
}
