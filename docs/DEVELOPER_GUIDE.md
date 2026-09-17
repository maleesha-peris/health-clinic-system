# Community Health Clinic Management System - Developer & Contributor Guide

## 1. Developer Setup & Prerequisites
- **JDK Requirement**: Java SE Development Kit 17 (LTS) or higher.
- **IDE Support**: Compatible with IntelliJ IDEA, Eclipse, NetBeans, or VS Code with Java Extension Pack.
- **Source Encoding**: UTF-8.
- **Build Scripts**:
  - `build.bat` - Compiles all source files into the `bin/` directory.
  - `run.bat` - Starts the desktop Swing application.
  - `test-junit.bat` - Executes the JUnit 5 test suite.
  - `test-system.bat` - Runs the comprehensive headless verification suite.

---

## 2. Codebase Organization & Package Hierarchy

```
src/com/healthclinic/
|-- Main.java                    # Entry point & Single-Instance socket guard
|-- controller/
|   |-- ClinicController.java     # Aggregate coordinator
|   |-- PatientController.java    # Patient business logic & validation
|   |-- DoctorController.java     # Doctor scheduling & consultation fees
|   |-- AppointmentController.java# Appointment booking & Quick Sort ordering
|   |-- TreatmentController.java  # Clinical notes & billing calculations
|   `-- ReportController.java     # Aggregated reports & analytics
|-- data/
|   `-- DataManager.java          # CSV File I/O (CRUD operations)
|-- model/
|   |-- Person.java               # Abstract base entity
|   |-- Patient.java              # Inherits Person, encapsulates clinical profile
|   |-- Doctor.java               # Inherits Person, encapsulates medical license & fees
|   |-- Administrator.java        # Inherits Person, system admin role
|   |-- Appointment.java          # Clinical appointment domain entity
|   |-- Treatment.java            # Diagnosis, prescription, and billing record
|   `-- Clinic.java               # Aggregate root entity
|-- util/
|   |-- AuditLogger.java          # Healthcare regulatory audit trailing
|   |-- ReportExporter.java       # Formatted report export utility
|   |-- SearchAlgorithms.java     # Binary search & Linear search implementations
|   |-- SortAlgorithms.java       # Quick sort & Bubble sort implementations
|   |-- ValidationUtil.java       # Regex validators for email, dates, and IDs
|   |-- ValidationException.java  # Custom checked exception
|   `-- DataPersistenceException.java # Custom checked persistence exception
|-- view/
|   |-- MainFrame.java            # Primary Swing desktop window & layout
|   |-- DashboardPanel.java       # Metric cards & daily clinic schedule
|   |-- PatientManagementPanel.java# Top-table & bottom registration form
|   |-- DoctorManagementPanel.java # Doctor directory & fee setup
|   |-- AppointmentBookingPanel.java # Interactive calendar & booking form
|   |-- TreatmentEntryPanel.java  # Clinical diagnosis & prescription entry
|   |-- ReportsPanel.java         # Roster & analytics view
|   `-- components/               # Custom UI controls (DatePickerDialog, ModernButton)
`-- test/
    |-- ClinicSystemJUnitTest.java# JUnit 5 Jupiter automated test suite
    `-- SystemTest.java           # 36-point headless verification suite
```

---

## 3. Regulatory Audit Logging (`AuditLogger`)
All sensitive data modifications must record an entry through `AuditLogger.log(category, action)`.
- Log file destination: `data/audit.log`
- Format: `[YYYY-MM-DD HH:MM:SS] [CATEGORY   ] Action description`
- Categories:
  - `PATIENT` - Creation, updates, or deletions of patient records.
  - `APPOINTMENT` - Booking, status transitions (`SCHEDULED` -> `COMPLETED`/`CANCELLED`).
  - `TREATMENT` - Clinical diagnosis entries and billing charges.
  - `SECURITY` - Authentication, access violations, or single-instance collisions.
  - `EXPORT` - Data export operations to disk.

---

## 4. Report & Data Exporter (`ReportExporter`)
Provides unified file-handling for exporting reports to the `exports/` folder:
```java
File target = new File(ReportExporter.getDefaultExportDirectory(), 
                       ReportExporter.generateExportFilename("appointments", "txt"));
ReportExporter.exportTextReport(reportContent, target);
```

---

## 5. Team Git Workflow
- **Main Branch**: Always deployable and tested (`origin/main`).
- **Atomic Commits**: Group related changes with conventional commit prefixes:
  - `feat:` for new capabilities.
  - `fix:` for bug resolutions.
  - `test:` for test additions or updates.
  - `docs:` for documentation updates.
  - `style:` for UI design changes.
