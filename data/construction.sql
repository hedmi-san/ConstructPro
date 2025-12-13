-- Cleaned & Consistent SQLite Schema (camelCase naming, cascades kept)
PRAGMA foreign_keys = ON;

-- =================================================
-- USERS
-- =================================================
DROP TABLE IF EXISTS users;
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    fullName TEXT NOT NULL,
    location TEXT NOT NULL,
    phone TEXT NOT NULL,
    userName TEXT NOT NULL UNIQUE,
    passWord TEXT NOT NULL,
    usertype TEXT NOT NULL
);

INSERT INTO users (id, fullName, location, phone, userName, passWord, usertype) VALUES
(1,'Ihab Mazouzi','El Eulma','0796708999','ihab','ihab1998','Admin'),
(2,'Nourdin Benaris','Bni Aziz','0796657316','Nori','norinori','Employee'),
(3,'Admin System','El Eulma','0796708999','admin','admin','Admin');

-- =================================================
-- constructionSite
-- =================================================
DROP TABLE IF EXISTS constructionSite;
CREATE TABLE constructionSite (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    location TEXT,
    status TEXT CHECK(status IN ('Active', 'Terminé', 'Non Spécifié')),
    startDate TEXT,
    endDate TEXT,
    totalCost REAL DEFAULT 0.0
);

INSERT INTO constructionSite (id, name, location, status, startDate, endDate, totalCost) VALUES
(1,'Non Assigné', NULL, 'Non Spécifié', NULL, NULL, 0.00),
(2,'Pont Oued Bousselam', 'Sétif', 'Active', '2024-03-01', '2025-09-30', 45000000.00),
(3,'Résidence Les Palmiers', 'El Eulma', 'Active', '2024-06-15', '2025-12-31', 28000000.00),
(4,'Extension Autoroute Est-Ouest', 'Constantine', 'Active', '2023-01-10', '2024-11-30', 120000000.00),
(5,'Bureau Mazoca', 'El Eulma', 'Non Spécifié', '2020-01-01', NULL, 0.00);

-- =================================================
-- suppliers
-- =================================================
DROP TABLE IF EXISTS suppliers;
CREATE TABLE suppliers (
    supplierId INTEGER PRIMARY KEY AUTOINCREMENT,
    supplierName TEXT NOT NULL,
    phone TEXT,
    address TEXT,
    totalSpent REAL DEFAULT 0.0,
    totalPaid REAL DEFAULT 0.0
);

INSERT INTO suppliers (supplierId, supplierName, phone, address, totalSpent, totalPaid) VALUES
(1,'Cimenterie de Ain El Kebira', '036-123-456', 'Zone Industrielle, Ain El Kebira, Sétif', 8500000.00, 7200000.00),
(2,'SARL Agrégats du Tell', '036-234-567', 'Route de Constantine, Sétif', 4200000.00, 4200000.00),
(3,'Ferronnerie Sétifienne', '036-345-678', '45 Rue Larbi Ben M''hidi, Sétif', 6800000.00, 5500000.00),
(4,'Bois et Menuiserie Boumaza', '036-456-789', 'Zone Industrielle El Eulma', 1200000.00, 1000000.00),
(5,'Électricité Générale Algérie', '036-567-890', '12 Boulevard de la République, Sétif', 2800000.00, 2800000.00),
(6,'Plomberie Pro SARL', '036-678-901', '78 Rue des Martyrs, El Eulma', 1500000.00, 1200000.00),
(7,'Lafarge Algérie', '021-789-012', 'Zone Industrielle Rouiba, Alger', 12000000.00, 9500000.00),
(8,'Quincaillerie El Baraka', '036-890-123', 'Marché de Gros, Sétif', 850000.00, 850000.00);

-- =================================================
-- worker
-- =================================================
DROP TABLE IF EXISTS worker;
CREATE TABLE worker (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    firstName TEXT,
    lastName TEXT,
    birthPlace TEXT,
    birthDate TEXT,
    fatherName TEXT,
    motherName TEXT,
    startDate TEXT,
    identityCardNumber TEXT UNIQUE,
    identityCardDate TEXT,
    familySituation TEXT,
    accountNumber TEXT,
    phoneNumber TEXT,
    job TEXT,
    siteId INTEGER,
    FOREIGN KEY (siteId) REFERENCES constructionSite(id) ON DELETE CASCADE
);

INSERT INTO worker (id, firstName, lastName, birthPlace, birthDate, fatherName, motherName, startDate, identityCardNumber, identityCardDate, familySituation, accountNumber, phoneNumber, job, siteId) VALUES
(1,'Rachid','Mammeri','Sétif','1980-03-15','Mohand','Tassadit','2024-03-01','ID-2024-001','2015-02-14','Married','ACC-001-2024','0550123001','Chef de Chantier',2),
(2,'Ali','Bendjebbour','Sétif','1990-05-10','Salah','Zohra','2024-03-05','ID-2024-002','2010-03-01','Married','ACC-002-2024','0550123002','Maçon',2),
(3,'Karim','Belkacem','Annaba','1985-12-15','Mohamed','Aicha','2024-03-10','ID-2024-003','2011-05-20','Married','ACC-003-2024','0550123003','Chauffeur',2),
(4,'Youcef','Bouguerra','Sétif','1995-11-08','Rachid','Samira','2024-04-01','ID-2024-004','2015-09-14','Single','ACC-004-2024','0550123004','Chauffeur',2),
(5,'Bilal','Mesbah','Constantine','1989-06-14','Mustapha','Latifa','2024-04-15','ID-2024-005','2009-04-22','Single','ACC-005-2024','0550123005','Soudeur',2),
(6,'Nourdin','Zerouki','Sétif','1988-07-22','Omar','Khadija','2024-03-01','ID-2024-006','2012-12-03','Married','ACC-006-2024','0550123006','Chauffeur',2),
(7,'Samir','Touati','El Eulma','1987-09-23','Ahmed','Fatima','2024-06-15','ID-2024-007','2009-07-12','Married','ACC-007-2024','0660123007','Chef d''Équipe',3),
(8,'Omar','Cherif','El Eulma','1992-03-08','Youssef','Khadija','2024-06-20','ID-2024-008','2012-11-08','Single','ACC-008-2024','0660123008','Électricien',3),
(9,'Farid','Hamidi','Sétif','1991-08-27','Laid','Djamila','2024-07-01','ID-2024-009','2011-11-07','Married','ACC-009-2024','0660123009','Plombier',3),
(10,'Nassim','Brahimi','El Eulma','1993-01-30','Hocine','Malika','2024-07-15','ID-2024-010','2013-06-18','Single','ACC-010-2024','0660123010','Peintre',3),
(11,'Amine','Larbi','Sétif','1994-04-18','Tahar','Farida','2024-08-01','ID-2024-011','2014-02-10','Single','ACC-011-2024','0660123011','Chauffeur',3),
(12,'Sofiane','Kadri','Constantine','1986-04-18','Larbi','Malika','2023-01-15','ID-2023-001','2008-12-05','Married','ACC-012-2024','0770123012','Chauffeur',4),
(13,'Yacine','Boudjelal','Constantine','1991-11-05','Abdelkader','Yamina','2023-02-01','ID-2023-002','2014-08-30','Divorced','ACC-013-2024','0770123013','Grutier',4),
(14,'Karim','Mansouri','El Eulma','1985-06-20','Mohamed','Aicha','2020-01-15','ID-2020-001','2012-05-20','Married','ACC-014-2024','0551234567','Comptable',5),
(15,'Redouane','Benali','El Eulma','1990-12-05','Kamel','Nadia','2021-03-01','ID-2021-001','2010-08-25','Single','ACC-015-2024','0551234568','Comptable',1);

-- =================================================
-- insurance
-- =================================================
DROP TABLE IF EXISTS insurance;
CREATE TABLE insurance (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    workerId INTEGER NOT NULL,
    insuranceNumber TEXT,
    agencyName TEXT,
    status TEXT DEFAULT 'Non Active' CHECK(status IN ('Active', 'Non Active', 'Bureau')),
    startDate TEXT,
    endDate TEXT,
    insuranceDocuments TEXT,
    createdAt TEXT DEFAULT CURRENT_TIMESTAMP,
    updatedAt TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (workerId) REFERENCES worker(id) ON DELETE CASCADE,
    UNIQUE(workerId)
);

INSERT INTO insurance (workerId, insuranceNumber, agencyName, status, startDate, endDate, insuranceDocuments) VALUES
(1, 'CNAS-2024-0001', 'CNAS Sétif', 'Active', '2024-03-01', '2025-02-28', 'Acte de Naissance,Fiche familiale de l''état civil,Photocopie de la carte identité,Photocopie de chèque'),
(2, 'CNAS-2024-0002', 'CNAS Sétif', 'Active', '2024-03-05', '2025-03-04', 'Acte de Naissance,Fiche familiale de l''état civil,Photocopie de la carte identité,Photocopie de chèque'),
(3, 'CNAS-2024-0003', 'CNAS Sétif', 'Active', '2024-03-10', '2025-03-09', 'Acte de Naissance,Fiche familiale de l''état civil,Photocopie de la carte identité,Photocopie de chèque'),
(4, 'CNAS-2024-0004', 'CNAS Sétif', 'Active', '2024-04-01', '2025-03-31', 'Acte de Naissance,Fiche familiale de l''état civil,Photocopie de la carte identité,Photocopie de chèque'),
(5, 'CNAS-2024-0005', 'CNAS Sétif', 'Active', '2024-04-15', '2025-04-14', 'Acte de Naissance,Fiche familiale de l''état civil,Photocopie de la carte identité,Photocopie de chèque'),
(6, 'CNAS-2024-0006', 'CNAS Sétif', 'Active', '2024-03-01', '2025-02-28', 'Acte de Naissance,Fiche familiale de l''état civil,Photocopie de la carte identité,Photocopie de chèque'),
(7, 'CNAS-2024-0007', 'CNAS El Eulma', 'Bureau', '2024-06-15', '2025-06-14', 'Acte de Naissance,Fiche familiale de l''état civil,Photocopie de la carte identité,Photocopie de chèque'),
(8, 'CNAS-2024-0008', 'CNAS El Eulma', 'Active', '2024-06-20', '2025-06-19', 'Acte de Naissance,Fiche familiale de l''état civil,Photocopie de la carte identité,Photocopie de chèque'),
(9, 'CNAS-2024-0009', 'CNAS El Eulma', 'Non Active', '2024-07-01', '2025-06-30', 'Acte de Naissance,Fiche familiale de l''état civil,Photocopie de la carte identité,Photocopie de chèque'),
(10, 'CNAS-2024-0010', 'CNAS El Eulma', 'Bureau', '2024-07-15', '2025-07-14', 'Acte de Naissance,Fiche familiale de l''état civil,Photocopie de la carte identité,Photocopie de chèque'),
(11, 'CNAS-2024-0011', 'CNAS El Eulma', 'Active', '2024-08-01', '2025-07-31', 'Acte de Naissance,Fiche familiale de l''état civil,Photocopie de la carte identité,Photocopie de chèque'),
(12, 'CNAS-2023-0001', 'CNAS Constantine', 'Active', '2023-01-15', '2025-01-14', 'Acte de Naissance,Fiche familiale de l''état civil,Photocopie de la carte identité,Photocopie de chèque'),
(13, 'CNAS-2023-0002', 'CNAS Constantine', 'Active', '2023-02-01', '2025-01-31', 'Acte de Naissance,Fiche familiale de l''état civil,Photocopie de la carte identité,Photocopie de chèque'),
(14, 'CNAS-2020-0001', 'CNAS El Eulma', 'Bureau', '2020-01-15', '2025-01-14', 'Acte de Naissance'),
(15, 'CNAS-2021-0001', 'CNAS El Eulma', 'Bureau', '2021-03-01', '2025-02-28', 'Acte de Naissance,Photocopie de la carte identité,Photocopie de chèque');

-- =================================================
-- vehicle
-- =================================================
DROP TABLE IF EXISTS vehicle;
CREATE TABLE vehicle (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT,
    plateNumber TEXT NOT NULL UNIQUE,
    ownershipType TEXT CHECK(ownershipType IN ('Possédé','Loué')),
    assignedSiteId INTEGER,
    driverId INTEGER,
    FOREIGN KEY (driverId) REFERENCES worker(id) ON DELETE CASCADE,
    FOREIGN KEY (assignedSiteId) REFERENCES constructionSite(id) ON DELETE CASCADE
);

INSERT INTO vehicle (id, name, plateNumber, ownershipType, assignedSiteId, driverId) VALUES
(1,'Grue Mobile 50T','19-123-456','Possédé',2,NULL),
(2,'Camion Benne 20T','19-234-567','Possédé',2,6),
(3,'Bétonnière','19-345-678','Possédé',2,NULL),
(4,'Excavatrice CAT 320','19-456-789','Possédé',2,NULL),
(5,'Compresseur','19-567-890','Possédé',2,NULL),
(6,'Camionnette Iveco','19-678-901','Possédé',3,NULL),
(7,'Mini-Pelle','19-789-012','Possédé',3,NULL),
(8,'Élévateur Télescopique','19-890-123','Possédé',3,NULL),
(9,'Groupe Électrogène','19-901-234','Loué',3,NULL),
(10,'Bulldozer D6','25-123-456','Loué',4,12),
(11,'Grue à Tour','25-234-567','Possédé',4,13),
(12,'Camion Plateau','19-012-345','Loué',NULL,NULL),
(13,'Rouleau Compacteur','19-111-222','Possédé',NULL,NULL),
(14,'Chargeuse sur Pneus','19-222-333','Possédé',NULL,NULL),
(15,'Nacelle Élévatrice','19-333-444','Loué',NULL,NULL),
(16,'Excavatrice Komatsu','19-444-555','Loué',NULL,NULL),
(17,'Niveleuse','19-555-666','Loué',NULL,NULL);

-- =================================================
-- vehicleRental
-- =================================================
DROP TABLE IF EXISTS vehicleRental;
CREATE TABLE vehicleRental (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    vehicleId INTEGER,
    assignedSiteId INTEGER,
    ownerCompany TEXT,
    ownerPhone TEXT,
    dailyRate REAL,
    startDate TEXT NOT NULL,
    endDate TEXT,
    daysWorked INTEGER,
    depositAmount REAL DEFAULT 0.0,
    transferFee REAL DEFAULT 0.0,
    FOREIGN KEY (vehicleId) REFERENCES vehicle(id) ON DELETE CASCADE,
    FOREIGN KEY (assignedSiteId) REFERENCES constructionSite(id) ON DELETE CASCADE
);

INSERT INTO vehicleRental (vehicleId, assignedSiteId, ownerCompany, ownerPhone, dailyRate, startDate, endDate, daysWorked, depositAmount, transferFee) VALUES
(9, 3,'Location Matériel BTP Sétif','0550103040',8500.00, '2024-07-01', '2025-01-31',30, 50000.00, 5000.00),
(10, 4, 'Engins TP Constantine','0750902040', 25000.00, '2023-01-15', '2025-01-15',100, 0.00, 0.00),
(12, 3, 'Trans-Location Algérie','0655103088', 12000.00, '2024-03-01', '2024-08-31',60, 75000.00, 8000.00),
(15, 4,'Hauteur Services SARL','0666103090', 15000.00, '2024-06-15', '2024-10-15',30, 0.00, 0.0),
(16, 2,'Machines Lourdes Algérie','0750773018', 28000.00, '2024-09-01', '2024-11-30',50, 200000.00, 12000.00),
(17, 2,'Location Pro BTP','0771865743', 22000.00, '2024-05-01', '2024-09-30',40, 120000.00, 10000.00);

-- =================================================
-- vehicleAssignment
-- =================================================
DROP TABLE IF EXISTS vehicleAssignment;
CREATE TABLE vehicleAssignment (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    vehicleId INTEGER,
    assignedSiteId INTEGER,
    assignmentDate TEXT NOT NULL,
    unassignmentDate TEXT,
    FOREIGN KEY (vehicleId) REFERENCES vehicle(id) ON DELETE CASCADE,
    FOREIGN KEY (assignedSiteId) REFERENCES constructionSite(id) ON DELETE CASCADE
);

INSERT INTO vehicleAssignment (vehicleId, assignedSiteId, assignmentDate, unassignmentDate) VALUES
(1, 2, '2024-03-01', NULL),
(2, 2, '2024-03-01', NULL),
(3, 2, '2024-03-05', NULL),
(4, 2, '2024-03-10', NULL),
(5, 2, '2024-04-01', NULL),
(6, 3, '2024-06-15', NULL),
(7, 3, '2024-06-20', NULL),
(8, 3, '2024-07-01', NULL),
(9, 3, '2024-07-01', NULL),
(10, 4, '2023-01-15', NULL),
(11, 4, '2023-02-01', NULL),
(12, 2, '2024-=3-11',NULL);

INSERT INTO workerAssignment (workerId, siteId, assignmentDate, unassignmentDate) VALUES
(1, 2, '2024-03-01', NULL),
(2, 2, '2024-03-05', NULL),
(3, 2, '2024-03-10', NULL),
(4, 2, '2024-04-01', NULL),
(5, 2, '2024-04-15', NULL),
(6, 2, '2024-03-01', NULL),
(7, 3, '2024-06-15', NULL),
(8, 3, '2024-06-20', NULL),
(9, 3, '2024-07-01', NULL),
(10, 3, '2024-07-15', NULL),
(11, 3, '2024-08-01', NULL),
(12, 4, '2023-01-15', NULL),
(13, 4, '2023-02-01', NULL),
(2, 4, '2023-06-01', '2024-02-28'),
(7, 4, '2023-03-01', '2024-06-10');

-- =================================================
-- salaryRecord
-- =================================================
DROP TABLE IF EXISTS salaryRecord;
CREATE TABLE salaryRecord (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    workerId INTEGER NOT NULL,
    totalEarned REAL NOT NULL DEFAULT 0.00,
    totalPaid REAL NOT NULL DEFAULT 0.00,
    FOREIGN KEY (workerId) REFERENCES worker(id) ON DELETE CASCADE,
    UNIQUE(workerId)
);

INSERT INTO salaryRecord (workerId, totalEarned, totalPaid) VALUES
(1, 640000.00, 600000.00),
(2, 400000.00, 380000.00),
(3, 420000.00, 400000.00),
(4, 350000.00, 350000.00),
(5, 320000.00, 300000.00),
(6, 480000.00, 450000.00),
(7, 375000.00, 360000.00),
(8, 275000.00, 275000.00),
(9, 250000.00, 240000.00),
(10, 200000.00, 180000.00),
(11, 160000.00, 150000.00),
(12, 1150000.00, 1100000.00),
(13, 1050000.00, 1000000.00),
(14, 2400000.00, 2400000.00),
(15, 1440000.00, 1440000.00);

-- =================================================
-- paymentCheck
-- (references salaryRecord and constructionSite)
-- =================================================
DROP TABLE IF EXISTS paymentCheck;
CREATE TABLE paymentCheck (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    salaryRecordId INTEGER NOT NULL,
    siteId INTEGER NOT NULL,
    paymentDate TEXT NOT NULL,
    baseSalary REAL NOT NULL,
    paidAmount REAL NOT NULL,
    FOREIGN KEY (salaryRecordId) REFERENCES salaryRecord(id) ON DELETE CASCADE,
    FOREIGN KEY (siteId) REFERENCES constructionSite(id) ON DELETE CASCADE
);

INSERT INTO paymentCheck (salaryRecordId, siteId, paymentDate, baseSalary, paidAmount) VALUES
(1, 2, '2024-03-31', 80000.00, 75000.00),
(1, 2, '2024-04-30', 80000.00, 75000.00),
(1, 2, '2024-05-31', 80000.00, 75000.00),
(1, 2, '2024-06-30', 80000.00, 75000.00),
(1, 2, '2024-07-31', 80000.00, 75000.00),
(1, 2, '2024-08-31', 80000.00, 75000.00),
(1, 2, '2024-09-30', 80000.00, 75000.00),
(1, 2, '2024-10-31', 80000.00, 75000.00),
(2, 2, '2024-03-31', 50000.00, 47500.00),
(2, 2, '2024-04-30', 50000.00, 47500.00),
(2, 2, '2024-05-31', 50000.00, 47500.00),
(2, 2, '2024-06-30', 50000.00, 47500.00),
(2, 2, '2024-07-31', 50000.00, 47500.00),
(2, 2, '2024-08-31', 50000.00, 47500.00),
(2, 2, '2024-09-30', 50000.00, 47500.00),
(2, 2, '2024-10-31', 50000.00, 47500.00),
(7, 3, '2024-06-30', 75000.00, 72000.00),
(7, 3, '2024-07-31', 75000.00, 72000.00),
(7, 3, '2024-08-31', 75000.00, 72000.00),
(7, 3, '2024-09-30', 75000.00, 72000.00),
(7, 3, '2024-10-31', 75000.00, 72000.00),
(11, 3, '2024-07-31', 50000.00, 47500.00),
(11, 3, '2024-08-31', 50000.00, 50000.00),
(11, 3, '2024-09-30', 50000.00, 47500.00),
(11, 3, '2024-10-31', 50000.00, 50000.00),
(13, 4, '2024-07-31', 40000.00, 40000.00),
(13, 4, '2024-08-31', 40000.00, 40000.00),
(13, 4, '2024-09-30', 40000.00, 40000.00),
(13, 4, '2024-10-31', 40000.00, 40000.00);

-- =================================================
-- bills
-- =================================================
DROP TABLE IF EXISTS bills;
CREATE TABLE bills (
    billId INTEGER PRIMARY KEY AUTOINCREMENT,
    billDate TEXT NOT NULL,
    factureNumber TEXT,
    supplierId INTEGER NOT NULL,
    assignedSiteId INTEGER NOT NULL,
    transferFee REAL,
    totalCost REAL,
    paidAmount REAL,
    FOREIGN KEY (supplierId) REFERENCES suppliers(supplierId) ON DELETE CASCADE,
    FOREIGN KEY (assignedSiteId) REFERENCES constructionSite(id) ON DELETE CASCADE
);

INSERT INTO bills (billDate, factureNumber, supplierId, assignedSiteId, transferFee, totalCost, paidAmount) VALUES
('2024-03-05', '7040', 1, 2, 15000.00, 2500000.00, 1300000.00),
('2024-03-10', '9041', 3, 2, 25000.00, 3500000.00, 3500000.00),
('2024-03-15', '5042', 2, 2, 20000.00, 1800000.00, 1800000.00),
('2024-04-01', '2043', 8, 2, 5000.00, 450000.00, 450000.00),
('2024-06-20', '7041', 1, 3, 12000.00, 1500000.00, 1500000.00),
('2024-07-01', '1948', 4, 3, 8000.00, 650000.00, 650000.00),
('2024-07-15', '2949', 5, 3, 5000.00, 1200000.00, 1200000.00),
('2024-08-01', '4450', 6, 3, 4000.00, 750000.00, 300000.00),
('2024-08-15', '2044', 8, 3, 3000.00, 280000.00, 280000.00);

-- =================================================
-- billItems
-- =================================================
DROP TABLE IF EXISTS billItems;
CREATE TABLE billItems (
    itemId INTEGER PRIMARY KEY AUTOINCREMENT,
    billId INTEGER NOT NULL,
    itemType TEXT NOT NULL CHECK(itemType IN ('Outil','Matériel')),
    itemName TEXT NOT NULL,
    quantity REAL NOT NULL,
    unitPrice REAL NOT NULL,
    FOREIGN KEY (billId) REFERENCES bills(billId) ON DELETE CASCADE
);

INSERT INTO billItems (billId, itemType, itemName, quantity, unitPrice) VALUES
(1, 'Matériel', 'Ciment CPJ 42.5 (Tonne)', 80.00, 28000.00),
(1, 'Matériel', 'Ciment Blanc (Sac 50kg)', 100.00, 2600.00),
(2, 'Matériel', 'Fer à Béton Ø12 (Tonne)', 15.00, 180000.00),
(2, 'Matériel', 'Fer à Béton Ø16 (Tonne)', 8.00, 185000.00),
(2, 'Matériel', 'Treillis Soudé (Panneau)', 50.00, 8500.00),
(3, 'Matériel', 'Gravier 15/25 (m³)', 150.00, 8000.00),
(3, 'Matériel', 'Sable de Carrière (m³)', 100.00, 5000.00),
(4, 'Outil', 'Perceuse Bosch GBH 2-28', 5.00, 45000.00),
(4, 'Outil', 'Meuleuse Makita 230mm', 4.00, 28000.00),
(5, 'Matériel', 'Ciment CPJ 42.5 (Tonne)', 50.00, 28000.00),
(6, 'Matériel', 'Madrier Pin 4m', 100.00, 3500.00);

-- =================================================
-- userLogs
-- =================================================
DROP TABLE IF EXISTS userLogs;
CREATE TABLE userLogs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    userName TEXT NOT NULL,
    inTime TEXT NOT NULL,
    outTime TEXT
);

INSERT INTO userLogs (userName, inTime, outTime) VALUES
('ihab', '2024-11-18 08:15:00', '2024-11-18 17:30:00'),
('admin', '2024-11-18 09:00:00', '2024-11-18 18:00:00'),
('Nori', '2024-11-18 07:45:00', '2024-11-18 16:15:00'),
('ihab', '2024-11-19 08:10:00', '2024-11-19 17:20:00'),
('admin', '2024-11-19 09:05:00', '2024-11-19 18:30:00'),
('Nori', '2024-11-19 07:50:00', '2024-11-19 16:00:00'),
('ihab', '2024-11-20 08:00:00', '2024-11-20 17:45:00'),
('admin', '2024-11-20 08:55:00', '2024-11-20 18:15:00'),
('ihab', '2024-11-21 08:20:00', '2024-11-21 17:30:00'),
('Nori', '2024-11-21 07:40:00', '2024-11-21 16:10:00'),
('ihab', '2024-11-22 08:05:00', NULL);

-- =================================================
-- Indexes (helpful for performance)
-- =================================================
CREATE INDEX IF NOT EXISTS idx_worker_siteId ON worker(siteId);
CREATE INDEX IF NOT EXISTS idx_vehicle_plateNumber ON vehicle(plateNumber);
CREATE INDEX IF NOT EXISTS idx_bills_assignedSiteId ON bills(assignedSiteId);
CREATE INDEX IF NOT EXISTS idx_billItems_billId ON billItems(billId);
CREATE INDEX IF NOT EXISTS idx_workerAssignment_workerId ON workerAssignment(workerId);
CREATE INDEX IF NOT EXISTS idx_vehicleRental_vehicleId ON vehicleRental(vehicleId);

-- =================================================
-- Notes:
-- 1) All table & column names use camelCase as requested.
-- 2) ON DELETE CASCADE is applied across FKs (per your 'deletes: A' choice).
-- 3) Dates are stored as TEXT (ISO format YYYY-MM-DD) — SQLite best practice for portability.
-- 4) Numeric/monetary columns use REAL. If you need integer cents, we can convert to INTEGER cents.
-- 5) If you maintain Java DAOs, update table/column names to match this camelCase scheme.
-- =================================================
