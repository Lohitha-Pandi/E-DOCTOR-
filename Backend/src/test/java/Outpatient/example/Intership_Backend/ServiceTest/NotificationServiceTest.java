package Outpatient.example.Intership_Backend.ServiceTest;

import Outpatient.example.Intership_Backend.Entity.Notification;
import Outpatient.example.Intership_Backend.Repository.NotificationRepository;
import Outpatient.example.Intership_Backend.Service.NotificationService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private NotificationRepository notificationRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createDoctorActionNotification_ValidInputs_SavesNotification() {
        String doctorEmail = "doctor@example.com";
        String patientEmail = "patient@example.com";
        String action = "scheduled";

        Notification notification = new Notification();
        notification.setRecipientEmail(patientEmail);
        notification.setSubject("Appointment " + action);
        notification.setMessage("Your appointment has been " + action + " by the doctor.");
        notification.setStatus("PENDING");
        notification.setTimestamp(LocalDateTime.now());

        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        notificationService.createDoctorActionNotification(doctorEmail, patientEmail, action);

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void markAsRead_ValidId_MarksNotificationAsRead() {
        Long id = 1L;

        Notification notification = new Notification();
        notification.setId(id);
        notification.setStatus("PENDING");

        when(notificationRepository.findById(id)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        Notification updatedNotification = notificationService.markAsRead(id);

        assertNotNull(updatedNotification);
        assertEquals("READ", updatedNotification.getStatus());
        verify(notificationRepository, times(1)).findById(id);
        verify(notificationRepository, times(1)).save(notification);
    }

    @Test
    void markAsRead_InvalidId_ThrowsException() {
        Long id = 99L;

        when(notificationRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> notificationService.markAsRead(id));

        assertEquals("Notification not found with id " + id, exception.getMessage());
        verify(notificationRepository, times(1)).findById(id);
        verify(notificationRepository, never()).save(any(Notification.class));
    }
}



