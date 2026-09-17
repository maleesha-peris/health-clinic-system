# Community Health Clinic Management System - User Manual

## 1. Introduction
The **Community Health Clinic Management System** is a robust, desktop-based clinical workflow and electronic record management platform built with Java Swing and organized according to strict Model-View-Controller (MVC) architectural patterns.

This user manual provides end-users, clinic receptionists, and medical administrators with complete guidance on operating all modules within the system.

---

## 2. System Launch & Splash Loading Screen
1. Launch the application by executing un.bat or by running java -cp bin com.healthclinic.Main.
2. A high-resolution **Splash Loading Screen** appears with a continuous progress indicator, initializing data stores and checking local CSV persistence.
3. The system enforces **Single-Instance Protection** (port 48567), guaranteeing that multiple simultaneous instances of the clinic database cannot be opened accidentally, preventing file corruption.

---

## 3. Navigation & Dashboard
- **Sidebar Navigation**:
  - Located on the left side with active highlights indicating the current workspace.
  - Sections: **Dashboard**, **Patient Registry**, **Doctor Management**, **Book Appointment**, **Treatment Entry**, and **Reports & Analytics**.
- **Exit Button**:
  - Located at the bottom of the navigation sidebar. Prompts the user with a confirmation dialog before closing the application cleanly.
- **Real-Time Clinic Metrics**:
  - **Total Patients Registered**
  - **Active Medical Staff (Doctors)**
  - **Scheduled & Completed Appointments**
  - **Total Clinical Treatments Administered**
- **Today's Clinic Overview Table**:
  - Displays appointments scheduled for today with color-coded status badges (SCHEDULED, COMPLETED, CANCELLED).

---

## 4. Patient Registry Module
### 4.1. Registering a New Patient
1. Click on **Patient Registry** in the sidebar.
2. The **Patient ID** is automatically generated in sequential format (e.g., P101, P102, P103...) and locked to prevent accidental duplicates.
3. Fill in the required patient fields:
   - **Full Name**: (Alphabetic characters and spaces)
   - **Phone Number**: (Minimum 10 digits)
   - **Email Address**: (Validated against standard email formats)
   - **Date of Birth**: Click the calendar icon to launch the **Interactive Date Picker Dialog**.
   - **Gender**: Select from dropdown (Male, Female, Other).
   - **Blood Group**: Select from dropdown (A+, A-, B+, B-, AB+, AB-, O+, O-).
   - **Emergency Contact**: Phone number of primary kin or contact.
   - **Medical History / Notes**: Known allergies, pre-existing conditions, or surgical history.
4. Click **Register Patient**. A success alert will confirm the addition and immediately refresh the table.

### 4.2. Searching & Sorting Patients
- **Search Bar**: Type any patient name, phone number, or ID in the top search box. The table filters matching records dynamically.
- **Binary Search by ID**: Fast \(\log n)\$ lookup for immediate record retrieval.
- **Bubble Sort by ID**: Sorts records ascending or descending.

---

## 5. Doctor Management Module
1. Navigate to **Doctor Management**.
2. Doctor IDs are auto-generated (e.g., D201, D202...).
3. Enter doctor details:
   - **Doctor Name**
   - **Contact Phone & Email**
   - **Medical Specialization** (e.g., General Medicine, Pediatrics, Cardiology)
   - **Medical License Number**
   - **Consultation Fee ($)**
   - **Weekly Availability** (e.g., Mon, Wed, Fri 09:00-14:00)
4. Click **Add Doctor**. Doctors registered here become instantly selectable in the Appointment Booking module.

---

## 6. Appointment Booking Module
1. Select **Book Appointment** in the sidebar.
2. The **Appointment ID** is auto-generated (e.g., A301, A302...).
3. Select an existing **Patient** and **Doctor** from the dropdown selectors.
4. Set the **Appointment Date & Time**:
   - Click the **Date & Time Picker Dialog**.
   - Navigate months using the interactive calendar grid.
   - Pick the hour and minute using the 24-hour time selector.
5. Set Initial Status (SCHEDULED, COMPLETED, CANCELLED).
6. Enter relevant clinical symptoms or checkup notes.
7. Click **Book Appointment**. The appointment list sorts automatically chronologically via **Quick Sort** (\(n \log n)\$).

---

## 7. Treatment Entry Module
1. Select **Treatment Entry** in the sidebar.
2. The **Treatment ID** is auto-generated (e.g., T401, T402...).
3. Select the associated **Appointment ID**, **Patient**, and **Attending Doctor**.
4. Enter clinical findings:
   - **Diagnosis**: Clinical assessment of patient symptoms.
   - **Prescription / Medication**: Drug names, dosages, and frequency.
   - **Treatment Cost ($)**: Total billed charges.
   - **Doctor Follow-up Notes**: Instructions or review dates.
5. Click **Record Treatment**.

---

## 8. Reports & Analytics Module
The Reports module generates clean summaries without UI clutter:
- **Appointments Summary Report**: Date-filtered report of appointments, patient attendance, and completion rates.
- **Doctor Daily Schedule / Roster**: Workload breakdown per physician.
- **Clinic Financial Overview**: Aggregate treatment revenue and consultation billings.
- **Export to CSV**: Allows downloading clean raw CSV reports directly to the user's computer for external auditing.

---

## 9. Data Persistence & Backup
- All records are saved in comma-separated value format inside the data/ directory:
  - data/patients.csv
  - data/doctors.csv
  - data/appointments.csv
  - data/treatments.csv
- Clicking **Save to Files** triggers a confirmation prompt to prevent accidental overwrites, followed by an immediate write operation.
