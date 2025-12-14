import java.sql.*;

public class SchemaInspector {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/constructpro";
        String user = "root";
        String password = "root";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = meta.getColumns(null, null, "userLogs", null);

            System.out.println("Columns in userLogs:");
            while (rs.next()) {
                System.out.println(" - " + rs.getString("COLUMN_NAME"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
