package constructpro.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class SQLiteDateUtils {

    // SQLite often stores dates as "yyyy-MM-dd" string, or long milliseconds
    // We'll prioritize the ISO format
    private static final DateTimeFormatter ISO_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    public static LocalDate getDate(ResultSet rs, String columnName) throws SQLException {
        Object obj = rs.getObject(columnName);
        if (obj == null) {
            return null;
        }

        // Case 1: Standard String "yyyy-MM-dd"
        if (obj instanceof String) {
            String dateStr = (String) obj;
            if (dateStr.trim().isEmpty())
                return null;
            try {
                // Try parsing standard ISO date "2023-01-01"
                return LocalDate.parse(dateStr, ISO_FORMAT);
            } catch (DateTimeParseException e) {
                // Handle potential different formats or log if needed
                // e.g., if it stores full timestamp "yyyy-MM-dd HH:mm:ss"
                try {
                    return LocalDate.parse(dateStr.split(" ")[0]);
                } catch (Exception ex) {
                    System.err.println("Error parsing date: " + dateStr);
                    return null;
                }
            }
        }

        // Case 2: Java SQL Date (if driver converts it automatically)
        if (obj instanceof java.sql.Date) {
            return ((java.sql.Date) obj).toLocalDate();
        }

        // Case 3: Long timestamp
        if (obj instanceof Long) {
            return new java.sql.Date((Long) obj).toLocalDate();
        }

        return null;
    }
}
