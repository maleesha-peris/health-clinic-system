package com.healthclinic.test;

import com.healthclinic.data.DataManager;
import com.healthclinic.model.Clinic;
import com.healthclinic.model.Patient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Task 7: Formal JUnit 5 Tests for Data Persistence & CSV File I/O CRUD.
 */
@DisplayName("Task 7: Data Management CSV Persistence Tests")
public class DataManagerJUnitTest {

    @Test
    @DisplayName("DataManager seeds, serializes, and loads clinic entities without data loss")
    void testCsvSeedAndLoad() throws Exception {
        DataManager dm = new DataManager();
        Clinic testClinic = new Clinic("Test Clinic", "123 Main Street", "555-0000");

        dm.seedSampleData(testClinic);
        dm.saveAll(testClinic);

        Clinic loadedClinic = new Clinic();
        dm.loadAll(loadedClinic);

        assertTrue(loadedClinic.getPatients().size() >= 4, "Must load at least 4 seed patients");
        assertTrue(loadedClinic.getDoctors().size() >= 3, "Must load at least 3 seed doctors");
        assertTrue(loadedClinic.getAppointments().size() >= 4, "Must load at least 4 seed appointments");
        assertTrue(loadedClinic.getTreatments().size() >= 1, "Must load at least 1 seed treatment");
    }

    @Test
    @DisplayName("DataManager CRUD Update operation persists modifications across disk reloads")
    void testUpdatePersistence() throws Exception {
        DataManager dm = new DataManager();
        Clinic loaded = new Clinic();
        dm.loadAll(loaded);

        Patient patient = loaded.findPatientById("P101");
        assertNotNull(patient, "Seed patient P101 must exist");
        String originalName = patient.getName();

        // Perform Update
        patient.setName(originalName + "-JUnitVerified");
        dm.savePatients(loaded.getPatients());

        // Reload from disk to verify persistence
        Clinic reloaded = new Clinic();
        reloaded.setPatients(dm.loadPatients());
        Patient verifiedPatient = reloaded.findPatientById("P101");

        assertNotNull(verifiedPatient);
        assertEquals(originalName + "-JUnitVerified", verifiedPatient.getName());

        // Roll back change
        patient.setName(originalName);
        dm.savePatients(loaded.getPatients());
    }
}
