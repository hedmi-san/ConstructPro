package constructpro.DAO;

import constructpro.DTO.ConstructionSite;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConstructionSiteDAO {

    final Connection connection;
    Statement st;
    ResultSet rs;
    public ConstructionSiteDAO(Connection con) throws SQLException {
        this.connection = con;
        st = connection.createStatement();
    }
    
    // CREATE - Insert a new construction site
    public void insertConstructionSite(ConstructionSite site) throws SQLException {
        String sql = "INSERT INTO ConstructionSite (name, location,status,start_date, end_date) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, site.getName());
            ps.setString(2, site.getLocation());
            ps.setString(3, site.getStatus());
            ps.setDate(4, Date.valueOf(site.getStartDate()));
            ps.setDate(5, Date.valueOf(site.getEndDate()));
            
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
        String sql = "SELECT name FROM ConstructionSite ORDER BY name";
        try(Statement stmt = connection.createStatement()){
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                list.add(rs.getString("name"));
            }
        }
        return list;
    }
    
    // READ - Get all construction sites with complete information
    public List<ConstructionSite> getAllConstructionSites() throws SQLException {
        List<ConstructionSite> list = new ArrayList<>();
        String sql = "SELECT * FROM ConstructionSite ORDER BY start_date DESC";
        try(Statement stmt = connection.createStatement()){
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                ConstructionSite site = new ConstructionSite();
                site.setId(rs.getInt("id"));
                site.setName(rs.getString("name"));
                site.setLocation(rs.getString("location"));
                site.setStartDate(rs.getDate("start_date").toLocalDate());
                site.setEndDate(rs.getDate("end_date").toLocalDate());
                list.add(site);
            }
        }
        return list;
    }
    
    // READ - Get construction site by ID
    public ConstructionSite getConstructionSiteById(int id) throws SQLException {
        String sql = "SELECT * FROM ConstructionSite WHERE id = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                ConstructionSite site = new ConstructionSite();
                site.setId(rs.getInt("id"));
                site.setName(rs.getString("name"));
                site.setLocation(rs.getString("location"));
                site.setStatus(rs.getString("status"));
                site.setStartDate(rs.getDate("start_date").toLocalDate());
                site.setEndDate(rs.getDate("end_date").toLocalDate());
                return site;
            }
        }
        return null;
    }
    
    // READ - Get construction site by name
    public ConstructionSite getConstructionSiteByName(String name) throws SQLException {
        String sql = "SELECT * FROM ConstructionSite WHERE name = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                ConstructionSite site = new ConstructionSite();
                site.setId(rs.getInt("id"));
                site.setName(rs.getString("name"));
                site.setLocation(rs.getString("location"));
                site.setStartDate(rs.getDate("start_date").toLocalDate());
                site.setEndDate(rs.getDate("end_date").toLocalDate());
                return site;
            }
        }
        return null;
    }
    
    public ResultSet getConstructionSiteInfo(){
        try {
            String query = """ 
                           SELECT 
                               s.id,
                               s.name,
                               s.location,
                               s.status,
                               s.start_date,
                               s.end_date,
                               s.total_cost
                           FROM 
                               ConstructionSite s
                           WHERE
                               s.status is not null
                           """;
            rs = st.executeQuery(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rs;
    }
    
    public ResultSet getActiveConstructionSiteInfo(){
        try {
            String query = """ 
                           SELECT 
                               s.id,
                               s.name,
                               s.location,
                               s.start_date,
                               s.end_date
                           FROM 
                               ConstructionSite s
                           WHERE 
                               s.status = 'Active'
                           """;
            rs = st.executeQuery(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rs;
    }
    
    public ResultSet getSpecificActiveConstructionSiteInfo(String searchTerm) {
        ResultSet rs = null;
        try {
            String query = """
                       SELECT 
                           s.id,
                           s.name,
                           s.location,
                           s.start_date,
                           s.end_date
                       FROM 
                           ConstructionSite s
                       WHERE 
                           s.status = 'Active' 
                           AND (s.name LIKE ? OR s.location LIKE ?)
                       """;

            PreparedStatement ps = connection.prepareStatement(query);
            String term = "%" + searchTerm + "%";
            ps.setString(1, term);
            ps.setString(2, term);

            rs = ps.executeQuery();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
    }
    return rs;
}
    
    // UPDATE - Update an existing construction site
    public void updateConstructionSite(ConstructionSite site) throws SQLException {
        String sql = "UPDATE ConstructionSite SET name=?, location=?, status=?, start_date=?, end_date=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, site.getName());
            ps.setString(2, site.getLocation());
            ps.setString(3, site.getStatus());
            ps.setDate(4, Date.valueOf(site.getStartDate()));
            ps.setDate(5, Date.valueOf(site.getEndDate()));
            ps.setInt(6, site.getId());
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No construction site found with ID: " + site.getId());
            }
        }
    }
    
    // DELETE - Delete construction site by ID
    public void deleteConstructionSite(int id) throws SQLException {
        // First check if there are workers assigned to this site
        String checkWorkersSQL = "SELECT COUNT(*) FROM worker WHERE site_id = ?";
        try (PreparedStatement checkPs = connection.prepareStatement(checkWorkersSQL)) {
            checkPs.setInt(1, id);
            ResultSet rs = checkPs.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                throw new SQLException("Cannot delete construction site. There are workers assigned to this site. Please reassign or remove workers first.");
            }
        }
        
        // If no workers are assigned, proceed with deletion
        String sql = "DELETE FROM ConstructionSite WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No construction site found with ID: " + id);
            }
        }
    }
    
    // Get site ID by name
    public int getSiteIdByName(String siteName) throws SQLException {
        if (siteName == null || siteName.equals("Sélectionner un chantier")) {
            return 0;
        }
        
        String sql = "SELECT id FROM ConstructionSite WHERE name = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, siteName);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return rs.getInt("id");
            }
        }
        return 0;
    }
    
    public ResultSet searchsitesByName(String searchTerm) {
    try {
        String query = """
            SELECT 
                s.id,
                s.name,
                s.location,
                s.status,
                s.start_date,
                s.end_date,
                s.total_cost
                FROM 
                ConstructionSite s
            WHERE 
                s.name LIKE ? OR s.location LIKE ?
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
    
    // Get site name by ID
    public String getSiteNameById(int siteId) throws SQLException {
        String sql = "SELECT name FROM ConstructionSite WHERE id = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, siteId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return rs.getString("name");
            }
        }
        return null;
    }
    
    public String getSiteNameByStatus(String status) throws SQLException {
        String sql = "SELECT name FROM ConstructionSite WHERE status = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return rs.getString("name");
            }
        }
        return null;
    }
    
    // Check if site name already exists (for validation)
    public boolean siteNameExists(String name) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ConstructionSite WHERE name = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
    
    // Check if site name exists for update (excluding current site ID)
    public boolean siteNameExistsForUpdate(String name, int excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ConstructionSite WHERE name = ? AND id != ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, excludeId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
    
    // Get worker count for a specific site
    public int getWorkerCountBySite(int siteId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM worker WHERE site_id = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, siteId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    
    // Get sites with worker information
    public ResultSet getSitesWithWorkerInfo() throws SQLException {
        String query = """
                       SELECT 
                           cs.id,
                           cs.name,
                           cs.location,
                           cs.start_date,
                           cs.end_date,
                           COUNT(w.id) as worker_count,
                           DATEDIFF(cs.end_date, cs.start_date) as duration_days,
                           CASE 
                               WHEN cs.end_date < CURDATE() THEN 'Completed'
                               WHEN cs.start_date > CURDATE() THEN 'Not Started'
                               ELSE 'In Progress'
                           END as status
                       FROM 
                           ConstructionSite cs
                       LEFT JOIN 
                           worker w ON cs.id = w.site_id
                       GROUP BY 
                           cs.id, cs.name, cs.location, cs.start_date, cs.end_date
                       ORDER BY 
                           cs.start_date DESC
                       """;
        return st.executeQuery(query);
    }
}