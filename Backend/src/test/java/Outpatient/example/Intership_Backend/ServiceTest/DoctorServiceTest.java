package Outpatient.example.Intership_Backend.ServiceTest;


import Outpatient.example.Intership_Backend.Advices.ApiError;
import Outpatient.example.Intership_Backend.Controller.NotificationController;
import Outpatient.example.Intership_Backend.DTO.LoginRequest;
import Outpatient.example.Intership_Backend.DTO.RegisterUserDTo;
import Outpatient.example.Intership_Backend.Entity.Appointment;
import Outpatient.example.Intership_Backend.Entity.Doctor;
import Outpatient.example.Intership_Backend.Repository.AppointmentRepository;
import Outpatient.example.Intership_Backend.Repository.DoctorRepository;
import Outpatient.example.Intership_Backend.Repository.NotificationRepository;
import Outpatient.example.Intership_Backend.Service.DoctorService;
import Outpatient.example.Intership_Backend.Service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationService emailService;

    @Mock
    private NotificationController notificationController;

    @InjectMocks
    private DoctorService doctorService;

    private Doctor doctor;
    private LoginRequest loginRequest;
    private RegisterUserDTo registerUserDTo;
    private Appointment appointment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        doctor = new Doctor();
        doctor.setEmail("doctor@example.com");
        doctor.setDoctorName("Dr. John Doe");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("doctor@example.com");

        registerUserDTo = new RegisterUserDTo();
        registerUserDTo.setEmail("doctor@example.com");

        appointment = new Appointment();
        appointment.setId(1L);
        appointment.setDoctorEmail("doctor@example.com");
        appointment.setStatus("PENDING");
    }

    @Test
    void testCreateDoctor() {
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);

        doctorService.createDoctor(registerUserDTo);

        verify(doctorRepository, times(1)).save(any(Doctor.class));
    }

    @Test
    void testUpdateDoctorProfile_DoctorNotFound() {
        when(doctorRepository.findByEmail(doctor.getEmail())).thenReturn(null);
        Doctor updatedDoctor = new Doctor();
        updatedDoctor.setDoctorName("Dr. Updated Name");

        ResponseEntity<ApiError> response = doctorService.updateDoctorProfile(updatedDoctor);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Doctor not found or unauthorized access", response.getBody().getMessage());
    }

    @Test
    void testLoginDoctor() {
        doctorService.loginDoctor(loginRequest);

        assertEquals("doctor@example.com", doctorService.getLoginEmail());
    }



    @Test
    void testCancelAppointment_NotFound() throws Exception {
        when(appointmentRepository.existsById(Math.toIntExact(appointment.getId()))).thenReturn(false);

        boolean result = doctorService.cancelAppointment(Math.toIntExact(appointment.getId()));

        assertFalse(result);
    }

}



