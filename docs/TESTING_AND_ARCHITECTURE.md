# Technical Architecture & Quality Assurance Specification

## 1. Architectural Design Pattern (MVC)
The system is built strictly conforming to the **Model-View-Controller (MVC)** architectural design pattern:

```
+-------------------------------------------------------------+
|                           VIEW                              |
|  (MainFrame, DashboardPanel, PatientPanel, DoctorPanel, ...) |
+-------------------------------------------------------------+
          |                                       ^
          | User Input Actions                    | Model Change Events / UI Refresh
          v                                       |
+-------------------------------------------------------------+
|                        CONTROLLER                           |
|  (ClinicController, PatientController, DoctorController,    |
|   AppointmentController, TreatmentController, ReportCtrl)   |
+-------------------------------------------------------------+
          |                                       ^
          | Mutates & Queries State               | Returns Domain Objects
          v                                       |
+-------------------------------------------------------------+
|                           MODEL                             |
|  (Clinic, Patient, Doctor, Appointment, Treatment, Person)  |
+-------------------------------------------------------------+
                               |
                               v
+-------------------------------------------------------------+
|                        PERSISTENCE                          |
|             (DataManager, CSV File Storage)                 |
+-------------------------------------------------------------+
```

### Component Decoupling:
- **Models (`com.healthclinic.model`)**: Pure domain entities encapsulating business attributes and invariants without any Swing or GUI dependencies.
- **Views (`com.healthclinic.view`)**: Java Swing interfaces handling visual rendering, layouts, and dispatching events to controllers.
- **Controllers (`com.healthclinic.controller`)**: Mediate between the View and Model, enforcing input validation, sorting, searching, and exception handling.
- **Data Access (`com.healthclinic.data`)**: Manages CSV I/O, file loading, and serialization.
- **Utilities (`com.healthclinic.util`)**: Contains custom algorithms and validation logic.

---

## 2. Algorithms & Computational Complexity Analysis

| Algorithm | Method Signature | Time Complexity | Space Complexity | Description |
| :--- | :--- | :--- | :--- | :--- |
| **Binary Search** | `SearchAlgorithms.binarySearchPatientById` | **Best:** O(1)<br>**Avg/Worst:** O(log n) | O(1) | Performs logarithmic divide-and-conquer search on sorted patient IDs. |
| **Linear Search** | `SearchAlgorithms.linearSearchPatients` | **Best:** O(1)<br>**Avg/Worst:** O(n) | O(k) | Performs linear scan filtering patient records across names, phones, and IDs. |
| **Quick Sort** | `SortAlgorithms.quickSortAppointmentsByDate` | **Best/Avg:** O(n log n)<br>**Worst:** O(n^2) | O(log n) | In-place recursive partitioning sorting appointments chronologically by date/time. |
| **Bubble Sort** | `SortAlgorithms.bubbleSortPatientsById` | **Best:** O(n)<br>**Avg/Worst:** O(n^2) | O(1) | Stable comparison sort placing patient records in ascending ID order. |

---

## 3. Automated Quality Assurance & Testing Suite

### 3.1. Test Suites Implemented
1. **JUnit 5 Jupiter Suite (`ClinicSystemJUnitTest.java`)**:
   - Uses `@Test`, `@DisplayName`, `@Nested`, `@BeforeEach`, and `org.junit.jupiter.api.Assertions`.
   - Executed via standalone JUnit Platform Console runner (`test-junit.bat`).
2. **Headless Verification Suite (`SystemTest.java`)**:
   - 36 comprehensive checks validating Models, Search/Sort algorithms, CRUD operations, Input Validation, and Reporting (`test-system.bat`).

### 3.2. Test Execution Commands
- Run JUnit 5 tests:
  ```powershell
  .\test-junit.bat
  ```
- Run verification suite:
  ```powershell
  .\test-system.bat
  ```
