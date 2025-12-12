package constructpro.DAO;

import constructpro.DTO.Worker;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Vector;
import constructpro.Database.SQLiteDateUtils;
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
        String sql = "INSERT INTO worker (firstName, lastName, birthPlace, birthDate, fatherName, motherName, " +
                "startDate, identityCardNumber, identityCardDate, familySituation, accountNumber, " +
                "phoneNumber, job, siteId) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
        String sql = "UPDATE worker SET firstName=?, lastName=?, birthPlace=?, birthDate=?, fatherName=?, motherName=?, startDate=?, identityCardNumber=?, identityCardDate=?, familySituation=?, accountNumber=?, phoneNumber=?, job=?, siteId=? WHERE id=?";
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
                w.setFirstName(rs.getString("firstName"));
                w.setLastName(rs.getString("lastName"));
                w.setBirthPlace(rs.getString("birthPlace"));
                w.setBirthDate(SQLiteDateUtils.getDate(rs, "birthDate"));
                w.setFatherName(rs.getString("fatherName"));
                w.setMotherName(rs.getString("motherName"));
                w.setStartDate(SQLiteDateUtils.getDate(rs, "startDate"));
                w.setIdentityCardNumber(rs.getString("identityCardNumber"));
                w.setIdentityCardDate(SQLiteDateUtils.getDate(rs, "identityCardDate"));
                w.setFamilySituation(rs.getString("familySituation"));
                w.setAccountNumber(rs.getString("accountNumber"));
                w.setPhoneNumber(rs.getString("phoneNumber"));
                w.setRole(rs.getString("job"));
                w.setAssignedSiteID(rs.getInt("siteId"));
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
                w.setFirstName(rs.getString("firstName"));
                w.setLastName(rs.getString("lastName"));
                w.setBirthPlace(rs.getString("birthPlace"));
                w.setBirthDate(SQLiteDateUtils.getDate(rs, "birthDate"));
                w.setFatherName(rs.getString("fatherName"));
                w.setMotherName(rs.getString("motherName"));
                w.setStartDate(SQLiteDateUtils.getDate(rs, "startDate"));
                w.setIdentityCardNumber(rs.getString("identityCardNumber"));
                w.setIdentityCardDate(SQLiteDateUtils.getDate(rs, "identityCardDate"));
                w.setFamilySituation(rs.getString("familySituation"));
                w.setAccountNumber(rs.getString("accountNumber"));
                w.setPhoneNumber(rs.getString("phoneNumber"));
                w.setRole(rs.getString("job"));
                w.setAssignedSiteID(rs.getInt("siteId"));
                list.add(w);
            }
        }
        return list;
    }

    public ResultSet getWorkersInfo() {
        try {
            String query = """
                    SELECT
                        w.id,
                        w.firstName,
                        w.lastName,
                        CAST(strftime('%Y.%m%d', 'now') - strftime('%Y.%m%d', w.birthDate) AS INTEGER) AS age,
                        w.job,
                        w.phoneNumber,
                        s.name AS site_name
                    FROM
                        worker w
                    LEFT JOIN
                        constructionSite s ON w.siteId = s.id
                    """;
            rs = st.executeQuery(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rs;
    }

    public ResultSet getWorkers() throws SQLException {
        String sql = "SELECT id,firstName,lastName,job,phoneNumber FROM worker";
        PreparedStatement ps = connection.prepareStatement(sql);
        return ps.executeQuery();
    }

    public void assignWorkerToSite(int workerId, int siteId) throws SQLException {
        String sql = "UPDATE worker SET siteId = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, siteId);
        ps.setInt(2, workerId);
        ps.executeUpdate();
    }

    public ResultSet getWorkerRecords(int id) {
        String query = "SELECT * FROM worker WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            rs = ps.executeQuery();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rs;
    }

    public void unassignWorker(int workerId) throws SQLException {
        String sql = "UPDATE worker SET siteId = 1 WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, workerId);
            stmt.executeUpdate();
        }
    }

    public List<String> getAllDriversNames() throws SQLException {
        List<String> list = new ArrayList<>();
        String sql = """
                SELECT
                CONCAT(firstName, ' ', lastName) AS driver_name
                FROM worker
                WHERE job = 'Chauffeur' or job = 'Grutier'
                """;
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(rs.getString("driver_name"));
            }
        }
        return list;
    }

    public int getDriverIdByName(String driverName) throws SQLException {
        if (driverName == null || driverName.equals("Sélectionner un Chauffeur")) {
            return 0;
        }

        String sql = "SELECT id FROM worker WHERE CONCAT(firstName, ' ', lastName) = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, driverName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return 0;
    }

    public String getDriverNameById(int workerId) throws SQLException {
        String sql = """
                SELECT
                   CONCAT(firstName, ' ', lastName) AS driver_name
                   FROM worker
                   WHERE id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, workerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("driver_name");
            }
        }
        return null;
    }

    public ResultSet getWorkersBySiteId(int siteId) {
        ResultSet rs = null;
        String sql = """
                SELECT
                    id,
                    firstName,
                    lastName,
                    CAST(strftime('%Y.%m%d', 'now') - strftime('%Y.%m%d', birthDate) AS INTEGER) AS age,
                    job,
                    phoneNumber
                FROM worker WHERE siteId = ?
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
                            w.firstName,
                            w.lastName,
                            CAST(strftime('%Y.%m%d', 'now') - strftime('%Y.%m%d', w.birthDate) AS INTEGER) AS age,
                            w.job,
                            w.phoneNumber,
                            s.name AS site_name
                        FROM
                            worker w
                        LEFT JOIN
                            constructionSite s ON w.siteId = s.id
                        WHERE
                            w.firstName LIKE ? OR w.lastName LIKE ?
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

        for (int col = 1; col <= colCount; col++) {
            columnNames.add(metaData.getColumnName(col).toUpperCase(Locale.ROOT));
        }

        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        while (resultSet.next()) {
            Vector<Object> vector = new Vector<Object>();
            for (int col = 1; col <= colCount; col++) {
                vector.add(resultSet.getObject(col));
            }
            data.add(vector);
        }
        return new DefaultTableModel(data, columnNames);
    }
}