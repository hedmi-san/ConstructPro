package constructpro.DAO;

import constructpro.DTO.ConstructionSite;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConstructionSiteDAO {

    final Connection connection;
    // Statement st;
    // ResultSet rs;

    public ConstructionSiteDAO(Connection con) throws SQLException {
        this.connection = con;
    }

    // CREATE - Insert a new construction site
    public void insertConstructionSite(ConstructionSite site) throws SQLException {
        String sql = "INSERT INTO constructionSite (name, location,status,startDate, endDate) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, site.getName());
            ps.setString(2, site.getLocation());
            ps.setString(3, site.getStatus());
            ps.setDate(4, Date.valueOf(site.getStartDate()));

            // Handle null end date for ongoing sites
            if (site.getEndDate() != null) {
                ps.setDate(5, Date.valueOf(site.getEndDate()));
            } else {
                ps.setNull(5, Types.DATE);
            }

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                // Get the generated ID and set it in the site object
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        site.setId(generatedKeys.getInt(1));
                    }
                }
            }
        }
    }

    // READ - Get all construction sites with names only
    public List<String> getAllConstructionSitesNames() throws SQLException {
        List<String> list = new ArrayList<>();
        String sql = "SELECT name FROM constructionSite ORDER BY name";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(rs.getString("name"));
            }
        }
        return list;
    }

    // READ - Get all construction sites with complete information
    public List<ConstructionSite> getAllConstructionSites() throws SQLException {
        List<ConstructionSite> list = new ArrayList<>();
        String sql = "SELECT * FROM constructionSite ORDER BY startDate DESC";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                ConstructionSite site = new ConstructionSite();
                site.setId(rs.getInt("id"));
                site.setName(rs.getString("name"));
                site.setLocation(rs.getString("location"));

                java.sql.Date startDate = rs.getDate("startDate");
                if (startDate != null) {
                    site.setStartDate(startDate.toLocalDate());
                }

                java.sql.Date endDate = rs.getDate("endDate");
                if (endDate != null) {
                    site.setEndDate(endDate.toLocalDate());
                }

                list.add(site);
            }
        }
        return list;
    }

    // READ - Get construction site by ID
    public ConstructionSite getConstructionSiteById(int id) throws SQLException {
        String sql = "SELECT * FROM constructionSite WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ConstructionSite site = new ConstructionSite();
                site.setId(rs.getInt("id"));
                site.setName(rs.getString("name"));
                site.setLocation(rs.getString("location"));
                site.setStatus(rs.getString("status"));

                java.sql.Date startDate = rs.getDate("startDate");
                if (startDate != null) {
                    site.setStartDate(startDate.toLocalDate());
                }

                java.sql.Date endDate = rs.getDate("endDate");
                if (endDate != null) {
                    site.setEndDate(endDate.toLocalDate());
                }

                return site;
            }
        }
        return null;
    }

    // READ - Get construction site by name
    public ConstructionSite getConstructionSiteByName(String name) throws SQLException {
        String sql = "SELECT * FROM constructionSite WHERE name = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ConstructionSite site = new ConstructionSite();
                site.setId(rs.getInt("id"));
                site.setName(rs.getString("name"));
                site.setLocation(rs.getString("location"));

                java.sql.Date startDate = rs.getDate("startDate");
                if (startDate != null) {
                    site.setStartDate(startDate.toLocalDate());
                }

                java.sql.Date endDate = rs.getDate("endDate");
                if (endDate != null) {
                    site.setEndDate(endDate.toLocalDate());
                }

                return site;
            }
        }
        return null;
    }

    public List<ConstructionSite> getConstructionSiteInfo() {
        List<ConstructionSite> list = new ArrayList<>();
        String query = """
                SELECT
                    s.id,
                    s.name,
                    s.location,
                    s.status,
                    s.startDate,
                    s.endDate,
                    s.totalCost
                FROM
                    constructionSite s
                WHERE
                    s.status is not null AND s.id > 1

                """;
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                ConstructionSite site = new ConstructionSite();
                site.setId(rs.getInt("id"));
                site.setName(rs.getString("name"));
                site.setLocation(rs.getString("location"));
                site.setStatus(rs.getString("status"));
                java.sql.Date sDate = rs.getDate("startDate");
                if (sDate != null)
                    site.setStartDate(sDate.toLocalDate());
                java.sql.Date eDate = rs.getDate("endDate");
                if (eDate != null)
                    site.setEndDate(eDate.toLocalDate());
                site.setTotalCost(rs.getDouble("totalCost"));
                list.add(site);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return list;
    }

    public List<ConstructionSite> getActiveConstructionSiteInfo() {
        List<ConstructionSite> list = new ArrayList<>();
        String query = """
                SELECT
                    s.id,
                    s.name,
                    s.location,
                    s.startDate,
                    s.endDate
                FROM
                    constructionSite s
                WHERE
                    s.status = 'Active'
                """;
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                ConstructionSite site = new ConstructionSite();
                site.setId(rs.getInt("id"));
                site.setName(rs.getString("name"));
                site.setLocation(rs.getString("location"));
                java.sql.Date sDate = rs.getDate("startDate");
                if (sDate != null)
                    site.setStartDate(sDate.toLocalDate());
                java.sql.Date eDate = rs.getDate("endDate");
                if (eDate != null)
                    site.setEndDate(eDate.toLocalDate());
                list.add(site);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return list;
    }

    public List<ConstructionSite> getSpecificActiveConstructionSiteInfo(String searchTerm) {
        List<ConstructionSite> list = new ArrayList<>();
        String query = """
                SELECT
                    s.id,
                    s.name,
                    s.location,
                    s.startDate,
                    s.endDate
                FROM
                    constructionSite s
                WHERE
                    s.status = 'Active'
                    AND (s.name LIKE ? OR s.location LIKE ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            String term = "%" + searchTerm + "%";
            ps.setString(1, term);
            ps.setString(2, term);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ConstructionSite site = new ConstructionSite();
                    site.setId(rs.getInt("id"));
                    site.setName(rs.getString("name"));
                    site.setLocation(rs.getString("location"));
                    java.sql.Date sDate = rs.getDate("startDate");
                    if (sDate != null)
                        site.setStartDate(sDate.toLocalDate());
                    java.sql.Date eDate = rs.getDate("endDate");
                    if (eDate != null)
                        site.setEndDate(eDate.toLocalDate());
                    list.add(site);
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return list;
    }

    // UPDATE - Update an existing construction site
    public void updateConstructionSite(ConstructionSite site) throws SQLException {
        String sql = "UPDATE constructionSite SET name=?, location=?, status=?, startDate=?, endDate=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, site.getName());
            ps.setString(2, site.getLocation());
            ps.setString(3, site.getStatus());
            ps.setDate(4, Date.valueOf(site.getStartDate()));

            // Handle null end date for ongoing sites
            if (site.getEndDate() != null) {
                ps.setDate(5, Date.valueOf(site.getEndDate()));
            } else {
                ps.setNull(5, Types.DATE);
            }

            ps.setInt(6, site.getId());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Aucun chantier trouvé avec le nom :" + site.getName());
            }
        }
    }

    // DELETE - Delete construction site by ID
    public void deleteConstructionSite(int id) throws SQLException {
        // First check if there are workers assigned to this site
        String checkWorkersSQL = "SELECT COUNT(*) FROM worker WHERE siteId = ?";
        try (PreparedStatement checkPs = connection.prepareStatement(checkWorkersSQL)) {
            checkPs.setInt(1, id);
            ResultSet rs = checkPs.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                throw new SQLException(
                        "Impossible de supprimer le chantier. Des ouvriers sont affectés à ce site. Veuillez d'abord réaffecter ou retirer les ouvriers.");
            }
        }

        // If no workers are assigned, proceed with deletion
        String sql = "DELETE FROM constructionSite WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Aucun chantier trouvé avec l'id : " + id);
            }
        }
    }

    // Get site ID by name
    public int getSiteIdByName(String siteName) throws SQLException {
        if (siteName == null || siteName.equals("Sélectionner un chantier")) {
            return 0;
        }

        String sql = "SELECT id FROM ConstructionSite WHERE name = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, siteName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return 0;
    }

    public List<ConstructionSite> searchsitesByName(String searchTerm) {
        List<ConstructionSite> list = new ArrayList<>();
        String query = """
                    select
                        s.id,
                        s.name,
                        s.location,
                        s.status,
                        s.startDate,
                        s.endDate,
                        s.totalCost
                    FROM
                        constructionSite s
                    WHERE
                        s.name LIKE ? OR s.location LIKE ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            String likeTerm = "%" + searchTerm + "%";
            ps.setString(1, likeTerm);
            ps.setString(2, likeTerm);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ConstructionSite site = new ConstructionSite();
                    site.setId(rs.getInt("id"));
                    site.setName(rs.getString("name"));
                    site.setLocation(rs.getString("location"));
                    site.setStatus(rs.getString("status"));
                    java.sql.Date sDate = rs.getDate("startDate");
                    if (sDate != null)
                        site.setStartDate(sDate.toLocalDate());
                    java.sql.Date eDate = rs.getDate("endDate");
                    if (eDate != null)
                        site.setEndDate(eDate.toLocalDate());
                    site.setTotalCost(rs.getDouble("totalCost"));
                    list.add(site);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Get site name by ID
    public String getSiteNameById(int siteId) throws SQLException {
        String sql = "SELECT name FROM ConstructionSite WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, siteId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        }
        return null;
    }

    public String getSiteNameByStatus(String status) throws SQLException {
        String sql = "SELECT name FROM ConstructionSite WHERE status = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        }
        return null;
    }

    // Check if site name already exists (for validation)
    public boolean siteNameExists(String name) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ConstructionSite WHERE name = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    // Check if site name exists for update (excluding current site ID)
    public boolean siteNameExistsForUpdate(String name, int excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM constructionSite WHERE name = ? AND id != ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, excludeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    // Get worker count for a specific site
    public int getWorkerCountBySite(int siteId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM worker WHERE siteId = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, siteId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    // Get sites with worker information
    public List<ConstructionSite> getSitesWithWorkerInfo() throws SQLException {
        List<ConstructionSite> list = new ArrayList<>();
        String query = """
                SELECT
                    cs.id,
                    cs.name,
                    cs.location,
                    cs.startDate,
                    cs.endDate,
                    COUNT(w.id) as worker_count,
                    DATEDIFF(cs.endDate, cs.startDate) as duration_days,
                    CASE
                        WHEN cs.endDate < CURDATE() THEN 'Completed'
                        WHEN cs.startDate > CURDATE() THEN 'Not Started'
                        ELSE 'In Progress'
                    END as status
                FROM
                    constructionSite cs
                LEFT JOIN
                    worker w ON cs.id = w.siteId
                GROUP BY
                    cs.id, cs.name, cs.location, cs.startDate, cs.endDate
                ORDER BY
                    cs.startDate DESC
                """;
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                ConstructionSite site = new ConstructionSite();
                site.setId(rs.getInt("id"));
                site.setName(rs.getString("name"));
                site.setLocation(rs.getString("location"));
                java.sql.Date sDate = rs.getDate("startDate");
                if (sDate != null)
                    site.setStartDate(sDate.toLocalDate());
                java.sql.Date eDate = rs.getDate("endDate");
                if (eDate != null)
                    site.setEndDate(eDate.toLocalDate());
                site.setStatus(rs.getString("status"));
                // worker_count and duration_days are currently ignored as they lack DTO fields
                list.add(site);
            }
        }
        return list;
    }

    public Map<String, Double> getSiteCostBreakdown(int siteId) throws SQLException {
        Map<String, Double> breakdown = new HashMap<>();
        String sql = """
                SELECT
                    COALESCE((SELECT SUM(paidAmount) FROM paymentCheck WHERE siteId = ?), 0) as workers,
                    COALESCE((SELECT SUM(totalCost) FROM bills WHERE assignedSiteId = ?), 0) as bills,
                    COALESCE((SELECT SUM(cost) FROM maintenanceTicket WHERE assignedSiteId = ?), 0) as maintenance,
                    COALESCE((SELECT SUM((dailyRate * daysWorked) + transferFee) FROM vehicleRental WHERE assignedSiteId = ?), 0) as rental
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, siteId);
            ps.setInt(2, siteId);
            ps.setInt(3, siteId);
            ps.setInt(4, siteId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    breakdown.put("workers", rs.getDouble("workers"));
                    breakdown.put("bills", rs.getDouble("bills"));
                    breakdown.put("vehicles", rs.getDouble("maintenance") + rs.getDouble("rental"));
                }
            }
        }
        return breakdown;
    }

    public void syncAllSitesTotalCosts() throws SQLException {
        String sql = """
                UPDATE constructionSite s
                SET totalCost = (
                    SELECT
                        COALESCE((SELECT SUM(paidAmount) FROM paymentCheck WHERE siteId = s.id), 0) +
                        COALESCE((SELECT SUM(totalCost) FROM bills WHERE assignedSiteId = s.id), 0) +
                        COALESCE((SELECT SUM(cost) FROM maintenanceTicket WHERE assignedSiteId = s.id), 0) +
                        COALESCE((SELECT SUM((dailyRate * daysWorked) + transferFee) FROM vehicleRental WHERE assignedSiteId = s.id), 0)
                )
                """;
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    public void syncSiteTotalCost(int siteId) throws SQLException {
        String sql = """
                UPDATE constructionSite
                SET totalCost = (
                    SELECT
                        COALESCE((SELECT SUM(paidAmount) FROM paymentCheck WHERE siteId = ?), 0) +
                        COALESCE((SELECT SUM(totalCost) FROM bills WHERE assignedSiteId = ?), 0) +
                        COALESCE((SELECT SUM(cost) FROM maintenanceTicket WHERE assignedSiteId = ?), 0) +
                        COALESCE((SELECT SUM((dailyRate * daysWorked) + transferFee) FROM vehicleRental WHERE assignedSiteId = ?), 0)
                )
                WHERE id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, siteId);
            ps.setInt(2, siteId);
            ps.setInt(3, siteId);
            ps.setInt(4, siteId);
            ps.setInt(5, siteId);
            ps.executeUpdate();
        }
    }
}