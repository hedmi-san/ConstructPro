🏗️ ConstructPro ERP

Comprehensive Construction Company Management System

📘 Overview

ConstructPro is a full-featured ERP (Enterprise Resource Planning) desktop application designed to streamline the operations of construction companies.
Built with Java Swing, MySQL, and a clean modular architecture, it manages all key business aspects — from workers and attendance to equipment, materials, sites, suppliers, insurance, and payroll.

The system is developed to provide data consistency, easy scalability, and real-time operational visibility across departments.

🧭 Core Objectives

Automate worker management, attendance, and salary tracking.

Manage construction sites, assigned workers, and equipment.

Track insurance, suppliers, purchases, and expenses.

Centralize all company operations into one database-backed system.

Enable clear financial reporting (salaries, rentals, site budgets, etc.).

🏗️ System Modules
Module Description
Authentication Secure login system for authorized users.
Dashboard Central control panel summarizing key stats and navigation.
Workers Management Add, edit, delete, and view worker profiles with detailed info (name, ID, phone, birth date, assigned site, etc.).
Insurance Management Manage individual worker insurance records through a dedicated dialog (add/edit functionality).
Attendance Tracking Record worker attendance per site and track workdays for payroll calculations.
Payroll System Core salary and payment management engine supporting both daily-rate and task-based payments, with salary records, payment checks, and retained salary handling.
Construction Site Management Manage active projects/sites, assigned workers, materials, and on-site equipment.
Equipment Management Manage owned and rented vehicles/equipment, including rental periods, costs, and site assignments.
Supplier & Purchase Management Track materials, suppliers, purchase orders, and related costs.
Finance & Bureau Ledger Centralized ledger for tracking all financial entries (salaries, purchases, rentals, etc.) linked to their source entities.

## 🧱 System Architecture

The project follows a clean, modular **MVC-like architecture**:

```
constructpro/
│
├── DAO/             → Data Access Objects (SQL operations)
│
├── DTO/             → Data Transfer Objects (data models)
│
├── Service/         → Application logic and UI dialogs
│
└── resources/       → Icons, themes, configuration (optional)
```

Each layer has a **single responsibility**:

- **DTO**: Represents database entities as Java objects.
- **DAO**: Encapsulates all SQL operations (insert, update, fetch).
- **Service/UI**: Provides the user interface and orchestrates DAO logic.

---

## ⚙️ Tech Stack

- **Language:** Java (JDK 17+)
- **UI:** Swing
- **Database:** MySQL
- **Architecture:** MVC-inspired layered design
- **IDE:** NetBeans / IntelliJ IDEA
- **Build Tool:** Maven or manual compilation

---

## 👨‍💻 Author

**Mister Abdo**
Network Engineer & Computer Science Student
Focused on intelligent automation and robust system design for real-world enterprise software.

---

> _“Precision in logic, clarity in design — that’s how great software is built.”_

```

---
```

```

```
