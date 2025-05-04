# UniVAL Faculty Scheduling and Coordination System

A comprehensive JavaFX application for managing faculty evaluation schedules, student feedback, and administrative reporting at the university level.

---

## Table of Contents
- [Project Overview](#project-overview)
- [Features](#features)
- [Architecture](#architecture)
- [OOP Concepts](#oop-concepts)
- [Setup and Configuration](#setup-and-configuration)
- [Building and Running](#building-and-running)
- [Usage](#usage)
- [Offline Admin Mode](#offline-admin-mode)
- [Troubleshooting](#troubleshooting)
- [Security Notes](#security-notes)

---

## Project Overview

**UniVAL** is a role-based scheduling and coordination system for university faculty, students, and administrators. It enforces institutional rules, prevents scheduling conflicts, and provides a transparent interface for all users. The system is built using modern Java OOP principles, a PostgreSQL backend (via Supabase), and a responsive JavaFX GUI.

## Features
- **Role-based login:** Admin, faculty, and student roles with secure authentication
- **Calendar scheduling:** Add/edit evaluations with conflict detection and daily edit limits
- **Multi-cohort/course support:** Schedule for multiple cohorts and courses
- **Student feedback:** Comments and emoji reactions on evaluations
- **Admin dashboard:** Manage users, courses, reports, and settings
- **Offline admin login:** Hardcoded credentials for demonstration/testing
- **Reporting:** Generate and view evaluation and user activity reports

## Architecture
- **Frontend:** JavaFX 20+
- **Backend:** Java 23
- **Database:** Supabase (PostgreSQL, REST API)
- **Build Tool:** Maven
- **Version Control:** Git
- **Key Packages:**
  - `view`: UI components (LoginView, AdminDashboardView, etc.)
  - `model`: Data models (User, Course, Evaluation, etc.)
  - `service`: Backend and business logic (SupabaseClient, AdminService, etc.)
  - `util`: Utilities (WindowStateManager, AppConfig)

## OOP Concepts
- **Encapsulation:** All model fields are private, accessed via getters/setters
- **Inheritance:** `User` is a base class; can be extended for specialized roles
- **Polymorphism:** Service interfaces allow for multiple backend implementations (Supabase, offline)
- **Abstraction:** Abstract service classes define contracts for data access
- **Composition:** Views compose model and service objects for UI logic
- **Collections:** Extensive use of Java Collections (List, Map) for managing users, courses, and evaluations

## Setup and Configuration

### 1. Supabase Credentials

#### Option 1: Environment Variables (Recommended)
Set the following environment variables:
```
SUPABASE_URL=your_supabase_url
SUPABASE_KEY=your_supabase_key
```

#### Option 2: Configuration File
1. Copy `config.properties.template` to `config.properties`
2. Edit `config.properties` and add your Supabase credentials:
```
supabase.url=your_supabase_url
supabase.key=your_supabase_key
```
**Note:** Do not commit `config.properties` to version control (see `.gitignore`).

### 2. Prerequisites
- Java 23 (or higher)
- Maven
- **Supported Platforms:** Windows, macOS, Linux (JavaFX is cross-platform)

## Building and Running

### Build
```
mvn clean package
```

### Run (with Maven)
```
mvn clean javafx:run
```

### Run (with JAR)
```
java -jar target/faculty-scheduling-system-1.0-SNAPSHOT.jar
```

### Running with a Temporary Java Environment (PowerShell)
If you do not want to permanently set JAVA_HOME and PATH, you can use these commands in your PowerShell session:

```
# Set JAVA_HOME for this session
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-23'

# Add Java bin directory to PATH for this session
$env:Path += ";$env:JAVA_HOME\bin"

# Compile the project with Maven
mvn clean package

# Run the application with Maven (recommended for JavaFX apps)
mvn clean javafx:run

# OR, to run the built JAR (after mvn clean package)
java -jar target/faculty-scheduling-system-1.0-SNAPSHOT.jar
```

## Usage
- **Admin Login (Offline):**
  - Email: `admin`
  - Password: `admin123$`
  - No Supabase credentials required for admin demo mode
- **Faculty/Student Login:**
  - Use Supabase credentials
- **Scheduling:**
  - Click on calendar, select slot, enter evaluation details
  - System enforces max 2 evaluations per day, no weekends, and prevents overlaps
- **Feedback:**
  - Students can comment/react on evaluations
- **Admin Dashboard:**
  - Manage users, courses, reports, and settings

## Offline Admin Mode
- If you log in as admin with the hardcoded credentials, the dashboard works with static/sample data and does not require Supabase.
- All other users require Supabase credentials and a working backend.

## Troubleshooting
- **JAVA_HOME not set:** Ensure JAVA_HOME points to your JDK 23 directory and is in your PATH.
- **Supabase errors:** For faculty/student/admin online features, ensure SUPABASE_URL and SUPABASE_KEY are set.
- **UI not updating:** Check for exceptions in the console; ensure you are running with JavaFX 20+.
- **Build issues:** Run `mvn clean` and ensure all dependencies are downloaded.

## Security Notes
- Store sensitive credentials in environment variables or a local configuration file.
- Never commit `config.properties` or credentials to version control.
- For production, always use environment variables for sensitive information.

## Individual Contributions

**Priyanka**
- Creation of the format of the report, with initial reporting of the report.
- Initial Design of the UI flow.

**Sairaj**
- Ideation of the topic
- Designing/implementation of database in Supabase
- Designing the UI flow
- Developing the Java application
- Test and run of the application
- Reporting and Documentation of Project (Complete)
- Version control
- Creation & version control on GitHub Repository

For more details, see the full project report (`OOPs_Final_Submission_Report.md`). 