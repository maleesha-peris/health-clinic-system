# Community Health Clinic Management System

A desktop application designed for community health clinics to manage patient records, doctor allocations, appointment scheduling, treatment logging, searching, sorting, and reporting.

## Features
- **MVC Architecture**: Strict separation of Model, View, and Controller layers.
- **OOP & SOLID Principles**: Inheritance (`Person` -> `Patient`, `Doctor`, `Administrator`), polymorphism, encapsulation, high cohesion, low coupling.
- **Swing GUI Interface**:
  - Main Dashboard / Clinic Overview
  - Patient Registration & Management
  - Doctor Registration & Management
  - Appointment Booking & Scheduling
  - Treatment Entry & History
  - Reports & Doctor Schedules
- **Data Persistence**: File I/O with CSV storage (`patients.csv`, `doctors.csv`, `appointments.csv`, `treatments.csv`) providing full CRUD capabilities.
- **Algorithms**:
  - **Searching**: Binary Search (search patient by ID), Linear Search (multi-criteria).
  - **Sorting**: Quick Sort (sort appointments by date), Bubble Sort (sort patients by ID).

## Requirements
- Java Development Kit (JDK 17 or higher)
- Supported IDEs: IntelliJ IDEA, NetBeans, Eclipse, or standalone JDK command line.

## How to Build and Run
### Using Command Line (Windows)
1. Build the application:
   ```cmd
   build.bat
   ```
2. Run the application:
   ```cmd
   run.bat
   ```
3. Run automated tests:
   ```cmd
   java -cp bin com.healthclinic.test.SystemTest
   ```
