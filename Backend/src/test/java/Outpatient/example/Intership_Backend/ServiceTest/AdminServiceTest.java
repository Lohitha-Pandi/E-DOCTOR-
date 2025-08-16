package Outpatient.example.Intership_Backend.ServiceTest;

import Outpatient.example.Intership_Backend.DTO.LoginRequest;
import Outpatient.example.Intership_Backend.Entity.Doctor;
import Outpatient.example.Intership_Backend.Entity.User;
import Outpatient.example.Intership_Backend.Repository.*;
import Outpatient.example.Intership_Backend.Service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    @InjectMocks
    private AdminService adminService;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private UserRepo userRepo;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @Mock
    private PatientRepository patientRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deleteDoctorByEmail_DoctorExists_ReturnsTrue() {
        String email = "doctor@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));
        when(doctorRepository.existsByEmail(email)).thenReturn(true);

        boolean result = adminService.deleteDoctorByEmail(email);

        assertTrue(result);
        verify(userRepo, times(1)).deleteByEmail(email);
        verify(appointmentRepository, times(1)).deleteByDoctorEmail(email);
        verify(doctorRepository, times(1)).deleteById(email);
    }

    @Test
    void deleteDoctorByEmail_DoctorDoesNotExist_ReturnsFalse() {
        String email = "nonexistent@example.com";

        when(userRepo.findByEmail(email)).thenReturn(Optional.empty());

        boolean result = adminService.deleteDoctorByEmail(email);

        assertFalse(result);
        verify(userRepo, never()).deleteByEmail(email);
    }

    @Test
    void deleteDoctorByEmail_ExceptionOccurs_ReturnsFalse() {
        String email = "doctor@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));
        when(doctorRepository.existsByEmail(email)).thenThrow(new RuntimeException("Database error"));

        boolean result = adminService.deleteDoctorByEmail(email);

        assertFalse(result);
    }

    @Test
    void loginAdmin_SetsLoginEmail() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("admin@example.com");

        adminService.loginAdmin(loginRequest);

        assertEquals("admin@example.com", adminService.getLoginEmail());
    }

    @Test
    void deletePatientByEmail_PatientExists_ReturnsTrue() {
        String email = "patient@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));
        when(patientRepository.existsByEmail(email)).thenReturn(true);

        boolean result = adminService.deletePatientByEmail(email);

        assertTrue(result);
        verify(userRepo, times(1)).deleteByEmail(email);
        verify(appointmentRepository, times(1)).deleteByPatientEmail(email);
        verify(patientRepository, times(1)).deleteById(email);
    }

    @Test
    void deletePatientByEmail_PatientDoesNotExist_ReturnsFalse() {
        String email = "nonexistent@example.com";

        when(userRepo.findByEmail(email)).thenReturn(Optional.empty());

        boolean result = adminService.deletePatientByEmail(email);

        assertFalse(result);
        verify(userRepo, never()).deleteByEmail(email);
    }

    @Test
    void deletePatientByEmail_ExceptionOccurs_ReturnsFalse() {
        String email = "patient@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));
        when(patientRepository.existsByEmail(email)).thenThrow(new RuntimeException("Database error"));

        boolean result = adminService.deletePatientByEmail(email);

        assertFalse(result);
    }

    @Test
    void getDoctorsCount_ReturnsCorrectCount() {
        when(doctorRepository.count()).thenReturn(10L);

        long count = adminService.getDoctorsCount();

        assertEquals(10L, count);
    }

    @Test
    void getPatientsCount_ReturnsCorrectCount() {
        when(patientRepository.count()).thenReturn(15L);

        long count = adminService.getPatientsCount();

        assertEquals(15L, count);
    }
}

