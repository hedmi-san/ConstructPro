package constructpro.DAO;

import constructpro.DTO.Worker;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class WorkerDAO {
    
    private Connection connection;
    Statement st;
    ResultSet rs;
    public WorkerDAO(Connection connection) throws SQLException {
        this.connection = connection;
        st = connection.createStatement();
    }
    
    public void insertWorker(Worker worker) throws SQLException {
        String sql = "INSERT INTO worker (first_name, last_name, birth_place, birth_date, father_name, mother_name, " +
                     "start_date, identity_card_number, identity_card_date, family_situation, account_number, " +
                     "phone_number, job, site_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, worker.getFirstName());
            ps.setString(2, worker.getLastName());
            ps.setString(3, worker.getBirthPlace());
            ps.setDate(4, Date.valueOf(worker.getBirthDate()));
            ps.setString(5, worker.getFatherName());
            ps.setString(6, worker.getMotherName());
            ps.setDate(7, Date.valueOf(worker.getStartDate()));
            ps.setString(8, worker.getIdentityCardNumber());
            ps.setDate(9, Date.valueOf(worker.getIdentityCardDate()));
            ps.setString(10, worker.getFamilySituation());
            ps.setString(11, worker.getAccountNumber());
            ps.setString(12, worker.getPhoneNumber());
            ps.setString(13, worker.getRole());
            ps.setInt(14, worker.getAssignedSiteID());
            ps.executeUpdate();
        }
    }
    
    public void updateWorker(Worker worker) throws SQLException {
        String sql = "UPDATE worker SET first_name=?, last_name=?, birth_place=?, birth_date=?, father_name=?, mother_name=?, start_date=?, identity_card_number=?, identity_card_date=?, family_situation=?, account_number=?, phone_number=?, job=?, site_id=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, worker.getFirstName());
            ps.setString(2, worker.getLastName());
            ps.setString(3, worker.getBirthPlace());
            ps.setDate(4, Date.valueOf(worker.getBirthDate()));
            ps.setString(5, worker.getFatherName());
            ps.setString(6, worker.getMotherName());
            ps.setDate(7, Date.valueOf(worker.getStartDate()));
            ps.setString(8, worker.getIdentityCardNumber());
            ps.setDate(9, Date.valueOf(worker.getIdentityCardDate()));
            ps.setString(10, worker.getFamilySituation());
            ps.setString(11, worker.getAccountNumber());
            ps.setString(12, worker.getPhoneNumber());
            ps.setString(13, worker.getRole());
            ps.setInt(14, worker.getAssignedSiteID());
            ps.setInt(15, worker.getId());
            ps.executeUpdate();
        }
    }

    public void deleteWorker(int id) throws SQLException {
        String sql = "DELETE FROM worker WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
    
    public Worker getWorkerById(int id) throws SQLException {
        String sql = "SELECT * FROM worker WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Worker w = new Worker();
                w.setId(rs.getInt("id"));
                w.setFirstName(rs.getString("first_name"));
                w.setLastName(rs.getString("last_name"));
                w.setBirthPlace(rs.getString("birth_place"));
                w.setBirthDate(rs.getDate("birth_date").toLocalDate());
                w.setFatherName(rs.getString("father_name"));
                w.setMotherName(rs.getString("mother_name"));
                w.setStartDate(rs.getDate("start_date").toLocalDate());
                w.setIdentityCardNumber(rs.getString("identity_card_number"));
                w.setIdentityCardDate(rs.getDate("identity_card_date").toLocalDate());
                w.setFamilySituation(rs.getString("family_situation"));
                w.setAccountNumber(rs.getString("account_number"));
                w.setPhoneNumber(rs.getString("phone_number"));
                w.setRole(rs.getString("job"));
                w.setAssignedSiteID(rs.getInt("site_id"));
                return w;
            }
        }
        return null;
    }
    
    public List<Worker> getAllWorkers() throws SQLException {
        List<Worker> list = new ArrayList<>();
        String sql = "SELECT * FROM worker";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Worker w = new Worker();
                w.setId(rs.getInt("id"));
                w.setFirstName(rs.getString("first_name"));
                w.setLastName(rs.getString("last_name"));
                w.setBirthPlace(rs.getString("birth_place"));
                w.setBirthDate(rs.getDate("birth_date").toLocalDate());
                w.setFatherName(rs.getString("father_name"));
                w.setMotherName(rs.getString("mother_name"));
                w.setStartDate(rs.getDate("start_date").toLocalDate());
                w.setIdentityCardNumber(rs.getString("identity_card_number"));
                w.setIdentityCardDate(rs.getDate("identity_card_date").toLocalDate());
                w.setFamilySituation(rs.getString("family_situation"));
                w.setAccountNumber(rs.getString("account_number"));
                w.setPhoneNumber(rs.getString("phone_number"));
                w.setRole(rs.getString("job"));
                w.setAssignedSiteID(rs.getInt("site_id"));
                list.add(w);
            }
        }
        return list;
    }
    
    public ResultSet getWorkersInfo(){
        try {
            String query = """ 
                           SELECT 
                               w.id,
                               w.first_name,
                               w.last_name,
                               TIMESTAMPDIFF(YEAR, w.birth_date, CURDATE()) AS age,
                               w.job,
                               w.phone_number,
                               s.name AS site_name
                           FROM 
                               worker w
                           LEFT JOIN 
                               ConstructionSite s ON w.site_id = s.id
                           """;
            rs = st.executeQuery(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rs;
    }
    
        public ResultSet getUnassignedWorkers() throws SQLException {
        String sql = "SELECT first_name,last_name,job,phone_number FROM worker WHERE site_id = 1";
        PreparedStatement ps = connection.prepareStatement(sql);
        return ps.executeQuery();
    }

    public void assignWorkerToSite(int workerId, int siteId) throws SQLException {
        String sql = "UPDATE worker SET site_id = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, siteId);
        ps.setInt(2, workerId);
        ps.executeUpdate();
    }

    
    public ResultSet getWorkerRecords(int id){
        String query = "SELECT * FROM worker WHERE id = ?";
        try(PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            rs = ps.executeQuery();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rs;
    }
    
    public void unassignWorker(int workerId) throws SQLException {
    String sql = "UPDATE worker SET site_id = 1 WHERE id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setInt(1, workerId);
        stmt.executeUpdate();
    }
}

    
    public ResultSet getWorkersBySiteId(int siteId) {
    ResultSet rs = null;
    String sql = """
                SELECT 
                    id,
                    first_name,
                    last_name,
                    TIMESTAMPDIFF(YEAR, birth_date, CURDATE()) AS age,
                    job,
                    phone_number
                FROM worker WHERE site_id = ?
                 """;
    try {
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, siteId);
        rs = stmt.executeQuery();
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return rs;
}
    
    public ResultSet searchWorkersByName(String searchTerm) {
    try {
        String query = """
            SELECT 
                w.id,
                w.first_name,
                w.last_name,
                TIMESTAMPDIFF(YEAR, w.birth_date, CURDATE()) AS age,
                w.job,
                w.phone_number,
                s.name AS site_name
            FROM 
                worker w
            LEFT JOIN 
                ConstructionSite s ON w.site_id = s.id
            WHERE 
                w.first_name LIKE ? OR w.last_name LIKE ?
        """;
        PreparedStatement ps = connection.prepareStatement(query);
        String likeTerm = "%" + searchTerm + "%";
        ps.setString(1, likeTerm);
        ps.setString(2, likeTerm);
        return ps.executeQuery();
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
    }
    
    public DefaultTableModel buildTableModel(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        Vector<String> columnNames = new Vector<>();
        int colCount = metaData.getColumnCount();

        for (int col=1; col <= colCount; col++){
            columnNames.add(metaData.getColumnName(col).toUpperCase(Locale.ROOT));
        }

        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        while (resultSet.next()) {
            Vector<Object> vector = new Vector<Object>();
            for (int col=1; col<=colCount; col++) {
                vector.add(resultSet.getObject(col));
            }
            data.add(vector);
        }
        return new DefaultTableModel(data, columnNames);
    }
}