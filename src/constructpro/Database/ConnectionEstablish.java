package constructpro.Database;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionEstablish {

    static String driver = "com.mysql.cj.jdbc.Driver";
    static String url = "jdbc:mysql://localhost:3306/construction";
    static String username = "root";
    static String password = "root";
    Properties prop;
    Connection conn;
    Statement statement;
    ResultSet result;

    public ConnectionEstablish() {
        try {
            // MySQL credentials
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
            statement = conn.createStatement();
            System.out.println("Connected to DB successfully.");
        } catch (SQLException | ClassNotFoundException ex) {
            System.err.println("Database connection error: " + ex.getMessage());
        }
    }

    public Connection getConn() {
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error in getConn: " + e.getMessage());
            e.printStackTrace();
        }
        return conn;
    }

    public boolean checkLogin(String username, String password, String userType) {
        String query = "SELECT * FROM users WHERE userName=? AND passWord=? AND usertype=? LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, userType);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Login query failed: " + e.getMessage());
        }

        return false;
    }

}
