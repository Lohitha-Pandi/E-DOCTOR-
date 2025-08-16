package Outpatient.example.Intership_Backend.ServiceTest;

import Outpatient.example.Intership_Backend.Entity.Patient;
import Outpatient.example.Intership_Backend.Entity.Payment;
import Outpatient.example.Intership_Backend.Repository.AppointmentRepository;
import Outpatient.example.Intership_Backend.Repository.PatientRepository;
import Outpatient.example.Intership_Backend.Repository.PaymentRepository;
import Outpatient.example.Intership_Backend.Service.PaymentService;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    private Patient patient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        patient = new Patient();
        patient.setEmail("patient@example.com");
    }

    @Test
    void createCheckoutSession_ShouldReturnSessionUrl() throws Exception {
        // Mock Stripe Session
        Session session = mock(Session.class);
        when(session.getUrl()).thenReturn("http://mocked-stripe-session-url");

        try (var mockedSession = mockStatic(Session.class)) {
            // Specify the parameter type explicitly
            when(Session.create(any(SessionCreateParams.class))).thenReturn(session);

            String sessionUrl = paymentService.createCheckoutSession("1", 500.0);

            assertNotNull(sessionUrl);
            assertEquals("http://mocked-stripe-session-url", sessionUrl);

            mockedSession.verify(() -> Session.create(any(SessionCreateParams.class)), times(1));
        }
    }

    @Test
    void verifyPayment_ShouldReturnTrueForPaidSession() throws Exception {
        // Mock Stripe Session
        Session session = mock(Session.class);
        when(session.getPaymentStatus()).thenReturn("paid");

        try (var mockedSession = mockStatic(Session.class)) {
            when(Session.retrieve(anyString())).thenReturn(session);

            boolean isPaid = paymentService.verifyPayment("mocked-session-id");

            assertTrue(isPaid);

            mockedSession.verify(() -> Session.retrieve("mocked-session-id"), times(1));
        }
    }

    @Test
    void verifyPayment_ShouldReturnFalseForUnpaidSession() throws Exception {
        // Mock Stripe Session
        Session session = mock(Session.class);
        when(session.getPaymentStatus()).thenReturn("unpaid");

        try (var mockedSession = mockStatic(Session.class)) {
            when(Session.retrieve(anyString())).thenReturn(session);

            boolean isPaid = paymentService.verifyPayment("mocked-session-id");

            assertFalse(isPaid);

            mockedSession.verify(() -> Session.retrieve("mocked-session-id"), times(1));
        }
    }

    @Test
    void savePaymentDetails_ShouldSavePaymentRecord() {
        when(patientRepository.findByEmail("patient@example.com")).thenReturn(patient);

        paymentService.savePaymentDetails(500.0, "1", "doctor@example.com", "patient@example.com");

        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void savePaymentDetails_ShouldThrowExceptionWhenPatientNotFound() {
        when(patientRepository.findByEmail("patient@example.com")).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                paymentService.savePaymentDetails(500.0, "1", "doctor@example.com", "patient@example.com"));

        assertEquals("Patient not found", exception.getMessage());
        verify(paymentRepository, never()).save(any(Payment.class));
    }
}
