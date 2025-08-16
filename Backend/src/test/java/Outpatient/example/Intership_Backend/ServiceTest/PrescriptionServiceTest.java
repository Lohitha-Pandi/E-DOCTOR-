package Outpatient.example.Intership_Backend.ServiceTest;

import Outpatient.example.Intership_Backend.DTO.PrescriptionDTO;
import Outpatient.example.Intership_Backend.Entity.Appointment;
import Outpatient.example.Intership_Backend.Entity.Doctor;
import Outpatient.example.Intership_Backend.Entity.Patient;
import Outpatient.example.Intership_Backend.Entity.Prescription;
import Outpatient.example.Intership_Backend.Repository.PrescriptionRepository;
import Outpatient.example.Intership_Backend.Service.AppointmentService;
import Outpatient.example.Intership_Backend.Service.DoctorService;
import Outpatient.example.Intership_Backend.Service.PatientService;
import Outpatient.example.Intership_Backend.Service.PrescriptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PrescriptionServiceTest {

    @InjectMocks
    private PrescriptionService prescriptionService;

    @Mock
    private DoctorService doctorService;

    @Mock
    private PatientService patientService;

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getPrescriptionByAppointmentId_Found_ReturnsPrescription() {
        Long appointmentId = 1L;
        Prescription prescription = new Prescription();

        when(prescriptionRepository.findByAppointmentId(appointmentId)).thenReturn(Optional.of(prescription));

        Prescription result = prescriptionService.getPrescriptionByAppointmentId(appointmentId);

        assertNotNull(result);
        assertEquals(prescription, result);
        verify(prescriptionRepository, times(1)).findByAppointmentId(appointmentId);
    }

    @Test
    void getPrescriptionByAppointmentId_NotFound_ReturnsNull() {
        Long appointmentId = 1L;

        when(prescriptionRepository.findByAppointmentId(appointmentId)).thenReturn(Optional.empty());

        Prescription result = prescriptionService.getPrescriptionByAppointmentId(appointmentId);

        assertNull(result);
        verify(prescriptionRepository, times(1)).findByAppointmentId(appointmentId);
    }


    @Test
    void getPrescriptionForLoggedInUser_InvalidPrescription_ReturnsNull() {
        Long appointmentId = 1L;

        when(prescriptionRepository.findByAppointmentId(appointmentId)).thenReturn(Optional.empty());

        PrescriptionDTO dto = prescriptionService.getPrescriptionForLoggedInUser(appointmentId);

        assertNull(dto);
        verify(prescriptionRepository, times(1)).findByAppointmentId(appointmentId);
    }
}

