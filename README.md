# COIT20258 - Disaster Response System (DRS-Enhanced)

This repository contains a three-tier, client-server application designed for managing disaster response efforts. The system provides functionalities for incident reporting, resource management, and user administration.

**Team Name:** [Enter Your Team Name Here]
**Course:** COIT20258 - Software Development

---

## Table of Contents
1. [System Architecture](#1-system-architecture)
2. [Key Features](#2-key-features)
3. [Technology Stack](#3-technology-stack)
4. [Setup and Installation Guide](#4-setup-and-installation-guide)
5. [How to Run the Application](#5-how-to-run-the-application)
6. [Team Roles and Responsibilities](#6-team-roles-and-responsibilities)
7. [Design Diagrams](#7-design-diagrams)

---

## 1. System Architecture

The DRS-Enhanced application is built upon a robust three-tier architecture to ensure scalability, maintainability, and a clear separation of concerns.

*   **Presentation Tier (Client):** A JavaFX desktop application provides an intuitive graphical user interface (GUI) for all user interactions. The client is designed using the Model-View-Controller (MVC) pattern and communicates with the server via a centralized `ClientController`.

*   **Logic Tier (Application Server):** A multi-threaded Java application serves as the system's core. It listens for client connections, processes all business logic, and orchestrates database operations through a dedicated service layer (`ReportService`, `UserService`, `ResourceService`).

*   **Data Tier (Database):** A MySQL relational database provides persistent storage for all system data, including users, incidents, and resources. Data integrity is enforced through a well-defined schema and foreign key constraints.

## 2. Key Features

*   **Secure User Authentication:** Role-based access control for all users.
*   **Real-time Incident Dashboard:** A central view of all ongoing disasters, with filtering and keyword search capabilities.
*   **Detailed Incident Management:** View and update incident status, priority, communication logs, and assigned resources.
*   **Situation Reporting:** Generate on-demand summary reports of the overall operational status.
*   **(New) Full User Management:** A dedicated administrative panel for creating, viewing, updating, and deactivating user accounts.
*   **(New) Master Resource Dashboard:** A centralized view of all organizational resources and their availability.

## 3. Technology Stack

*   **Language:** Java 17
*   **Framework/UI:** JavaFX 17
*   **Database:** MySQL 8.0
*   **Build Tool:** Apache Maven
*   **Version Control:** Git & GitHub
*   **IDE:** Apache NetBeans

## 4. Setup and Installation Guide

To set up and run this project, you will need Java (JDK 17) and MySQL Server installed on your machine.

### Step 1: Clone the Repository

Clone this repository to your local machine using the following command:
```bash
git clone https://github.com/shubhoc47/Disaster-Response-System.git
```

### Step 2: Set Up the Database

The database can be set up automatically using the provided SQL scripts.

1.  Open MySQL Workbench or your preferred MySQL client.
2.  Create a new database schema named `drs_db`.
    ```sql
    CREATE DATABASE IF NOT EXISTS drs_db;
    ```
3.  Open the `schema.sql` file from the project's root directory and execute it to create all the necessary tables.
4.  Open the `data.sql` file and execute it to populate the tables with sample users, resources, and incidents.

### Step 3: Configure Database Connection

Open the `DatabaseConnector.java` file located at `src/main/java/com/mycompany/drs/database/DatabaseConnector.java` and ensure the username and password match your local MySQL setup.

```java
private static final String USER = "your_mysql_user"; // Replace with your username
private static final String PASSWORD = "your_mysql_password"; // Replace with your password
```

### Step 4: Build the Project

Open the project in Apache NetBeans. Right-click the project and select **Clean and Build** to download all Maven dependencies and compile the source code.

## 5. How to Run the Application

The application consists of two main components: the Server and the Client. **The Server must be started first.**

### Step 5a: Run the Server

1.  In NetBeans, right-click on the `DRS` project in the "Projects" window.
2.  Select **Run Maven > Goals...**
3.  In the "Goals" text field, enter the following command:
    ```
    exec:java -Dexec.mainClass="com.mycompany.drs.server.DRSServer"
    ```
4.  Click **OK**.
5.  The Output window will appear, and you should see the message: `SERVER: DRS Server is running and listening on port 8888`. The process will appear to be "stuck," which is correct as it is now listening for connections.

### Step 5b: Run the Client

1.  **While the server is still running**, right-click on the `DRS` project again.
2.  Select **Run**. (Alternatively, click the main green "Run Project" play button in the toolbar).
3.  The JavaFX client application window will launch, starting with the Welcome screen.

### Sample Login Credentials

You can use the following credentials (defined in `data.sql`) to test different user roles:
*   **Administrator:**
    *   **Username:** `admin`
    *   **Password:** `adminpass`
*   **Dispatcher (Standard User):**
    *   **Username:** `dispatcher1`
    *   **Password:** `disp123`

## 6. Team Roles and Responsibilities

*   **Shubho Chowdhury (Team Leader):** Frontend Development (JavaFX GUI), New Feature Implementation (User Management, Resource Dashboard), UML Design Documentation.
*   **Dishant Shiroya:** Backend Infrastructure (Multi-threaded Server), Database Design and Schema Optimization (`schema.sql`).
*   **Dulguun Battulga:** Middleware and Services (Client-Server Communication, `UserService`, `ReportService`), Testing and Database Validation.

## 7. Design Diagrams

*This section can optionally embed the diagrams for easy viewing.*

### Use Case Diagram
![Use Case Diagram for DRS](docs/use_case_diagram.png)

### System Architecture (Class Diagrams)
**Server-Side**
![Server-Side Class Diagram](docs/server_class_diagram.png)

**Client-Side**
![Client-Side Class Diagram](docs/client_class_diagram.png)

### Database Schema (ERD)
![Entity-Relationship Diagram](docs/database_erd.png)
