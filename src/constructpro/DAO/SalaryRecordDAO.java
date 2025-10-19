package constructpro.DAO;

import constructpro.DTO.SalaryRecord;
import java.sql.*;

public class SalaryRecordDAO {
    private Connection conn;

    public SalaryRecordDAO(Connection conn) {
        this.conn = conn;
    }
}
