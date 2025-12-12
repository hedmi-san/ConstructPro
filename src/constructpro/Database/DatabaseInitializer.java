package constructpro.Database;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseInitializer {

    public static void initializeDatabase() {
        ConnectionEstablish ce = new ConnectionEstablish();
        try (Connection conn = ce.getConn();
                Statement stmt = conn.createStatement()) {

            if (conn != null) {
                // Enable foreign keys
                stmt.execute("PRAGMA foreign_keys = ON;");

                // 1. constructionSite
                stmt.execute("""
                            CREATE TABLE IF NOT EXISTS constructionSite (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                name TEXT NOT NULL,
                                location TEXT,
                                status TEXT,
                                startDate TEXT,
                                endDate TEXT
                            );
                        """);

                // 2. worker
                stmt.execute("""
                            CREATE TABLE IF NOT EXISTS worker (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                firstName TEXT,
                                lastName TEXT,
                                birthPlace TEXT,
                                birthDate TEXT,
                                fatherName TEXT,
                                motherName TEXT,
                                startDate TEXT,
                                identityCardNumber TEXT,
                                identityCardDate TEXT,
                                familySituation TEXT,
                                accountNumber TEXT,
                                phoneNumber TEXT,
                                job TEXT,
                                siteId INTEGER,
                                FOREIGN KEY (siteId) REFERENCES constructionSite(id)
                            );
                        """);

                // 3. users
                stmt.execute("""
                            CREATE TABLE IF NOT EXISTS users (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                fullName TEXT,
                                location TEXT,
                                phone TEXT,
                                userName TEXT UNIQUE,
                                passWord TEXT,
                                usertype TEXT
                            );
                        """);

                // 4. userLogs
                stmt.execute("""
                            CREATE TABLE IF NOT EXISTS userLogs (
                                logId INTEGER PRIMARY KEY AUTOINCREMENT,
                                userName TEXT,
                                usertype TEXT,
                                inTime TEXT,
                                outTime TEXT
                            );
                        """);

                // 5. vehicle
                stmt.execute("""
                            CREATE TABLE IF NOT EXISTS vehicle (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                name TEXT,
                                plateNumber TEXT UNIQUE,
                                ownershipType TEXT,
                                assignedSiteId INTEGER,
                                driverId INTEGER,
                                FOREIGN KEY (assignedSiteId) REFERENCES constructionSite(id),
                                FOREIGN KEY (driverId) REFERENCES worker(id)
                            );
                        """);

                // 6. vehicleRental
                stmt.execute("""
                            CREATE TABLE IF NOT EXISTS vehicleRental (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                vehicleId INTEGER,
                                assignedSiteId INTEGER,
                                ownerCompany TEXT,
                                ownerPhone TEXT,
                                dailyRate REAL,
                                startDate TEXT,
                                endDate TEXT,
                                daysWorked INTEGER,
                                depositAmount REAL,
                                transferFee REAL,
                                FOREIGN KEY (vehicleId) REFERENCES vehicle(id),
                                FOREIGN KEY (assignedSiteId) REFERENCES constructionSite(id)
                            );
                        """);

                // 7. vehicleAssignment
                stmt.execute("""
                            CREATE TABLE IF NOT EXISTS vehicleAssignment (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                vehicleId INTEGER,
                                assignedSiteId INTEGER,
                                assignmentDate TEXT,
                                unassignmentDate TEXT,
                                FOREIGN KEY (vehicleId) REFERENCES vehicle(id),
                                FOREIGN KEY (assignedSiteId) REFERENCES constructionSite(id)
                            );
                        """);

                // 8. maintenanceTicket
                stmt.execute("""
                            CREATE TABLE IF NOT EXISTS maintenanceTicket (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                vehicleId INTEGER,
                                maintenanceType TEXT,
                                description TEXT,
                                cost REAL,
                                repairDate TEXT,
                                FOREIGN KEY (vehicleId) REFERENCES vehicle(id)
                            );
                        """);

                // 9. insurance
                stmt.execute("""
                            CREATE TABLE IF NOT EXISTS insurance (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                workerId INTEGER,
                                insuranceNumber TEXT,
                                agencyName TEXT,
                                status TEXT,
                                startDate TEXT,
                                endDate TEXT,
                                insuranceDocuments TEXT,
                                FOREIGN KEY (workerId) REFERENCES worker(id)
                            );
                        """);

                // 10. salaryRecord
                stmt.execute("""
                            CREATE TABLE IF NOT EXISTS salaryRecord (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                workerId INTEGER UNIQUE,
                                totalEarned REAL DEFAULT 0,
                                totalPaid REAL DEFAULT 0,
                                FOREIGN KEY (workerId) REFERENCES worker(id)
                            );
                        """);

                // 11. paymentCheck
                stmt.execute("""
                            CREATE TABLE IF NOT EXISTS paymentCheck (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                salaryRecordId INTEGER,
                                siteId INTEGER,
                                paymentDate TEXT,
                                baseSalary REAL,
                                paidAmount REAL,
                                FOREIGN KEY (salaryRecordId) REFERENCES salaryRecord(id),
                                FOREIGN KEY (siteId) REFERENCES constructionSite(id)
                            );
                        """);

                // 12. workerAssignment
                stmt.execute("""
                            CREATE TABLE IF NOT EXISTS workerAssignment (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                workerId INTEGER,
                                siteId INTEGER,
                                assignmentDate TEXT,
                                unassignmentDate TEXT,
                                FOREIGN KEY (workerId) REFERENCES worker(id),
                                FOREIGN KEY (siteId) REFERENCES constructionSite(id)
                            );
                        """);

                // 13. suppliers
                stmt.execute("""
                            CREATE TABLE IF NOT EXISTS suppliers (
                                supplierId INTEGER PRIMARY KEY AUTOINCREMENT,
                                supplierName TEXT UNIQUE,
                                phone TEXT,
                                address TEXT,
                                totalSpent REAL DEFAULT 0,
                                totalPaid REAL DEFAULT 0
                            );
                        """);

                // 14. bills
                stmt.execute("""
                            CREATE TABLE IF NOT EXISTS bills (
                                billId INTEGER PRIMARY KEY AUTOINCREMENT,
                                billDate TEXT,
                                factureNumber TEXT,
                                supplierId INTEGER,
                                assignedSiteId INTEGER,
                                transferFee REAL DEFAULT 0,
                                totalCost REAL DEFAULT 0,
                                paidAmount REAL DEFAULT 0,
                                FOREIGN KEY (supplierId) REFERENCES suppliers(supplierId),
                                FOREIGN KEY (assignedSiteId) REFERENCES constructionSite(id)
                            );
                        """);

                // 15. billItems
                stmt.execute("""
                            CREATE TABLE IF NOT EXISTS billItems (
                                itemId INTEGER PRIMARY KEY AUTOINCREMENT,
                                billId INTEGER,
                                itemType TEXT,
                                itemName TEXT,
                                quantity REAL,
                                unitPrice REAL,
                                FOREIGN KEY (billId) REFERENCES bills(billId) ON DELETE CASCADE
                            );
                        """);

                System.out.println("All SQLite tables initialized successfully.");
            }
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
