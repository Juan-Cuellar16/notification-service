package com.distrilink.notification.service;

import com.distrilink.notification.dto.NotificationRequest;
import com.distrilink.notification.dto.NotificationResponse;
import com.distrilink.notification.exception.NotificationNotFoundException;
import com.distrilink.notification.model.Notification;
import com.distrilink.notification.model.NotificationChannel;
import com.distrilink.notification.model.NotificationStatus;
import com.distrilink.notification.model.NotificationType;
import com.distrilink.notification.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Prueba unitaria del Service: el repositorio es un mock, no se usa base de datos. */
@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository repository;

    @InjectMocks
    private NotificationService service;

    private NotificationRequest request(NotificationChannel channel) {
        return new NotificationRequest(15L, 120L, NotificationType.ORDER_CONFIRMED, channel,
                "  Pedido aprobado  ", "Tu pedido #120 fue aprobado.");
    }

    private Notification saved(Long id, NotificationStatus status) {
        Notification n = new Notification();
        n.setId(id);
        n.setRecipientId(15L);
        n.setOrderId(120L);
        n.setType(NotificationType.ORDER_CONFIRMED);
        n.setChannel(NotificationChannel.IN_APP);
        n.setTitle("Pedido aprobado");
        n.setMessage("Tu pedido #120 fue aprobado.");
        n.setStatus(status);
        n.setCreatedAt(LocalDateTime.now());
        return n;
    }

    @Test
    void create_guardaComoPendienteConCanalPorDefectoYTextoLimpio() {
        when(repository.save(any(Notification.class))).thenAnswer(inv -> {
            Notification n = inv.getArgument(0);
            n.setId(1L);
            return n;
        });

        NotificationResponse response = service.create(request(null));

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.status()).isEqualTo(NotificationStatus.PENDING);
        assertThat(response.channel()).isEqualTo(NotificationChannel.IN_APP);
        assertThat(response.title()).isEqualTo("Pedido aprobado");
    }

    @Test
    void findAll_conRecipientIdFiltraPorDestinatario() {
        when(repository.findByRecipientIdOrderByCreatedAtDesc(15L))
                .thenReturn(List.of(saved(1L, NotificationStatus.SENT)));

        List<NotificationResponse> result = service.findAll(15L);

        assertThat(result).hasSize(1);
        verify(repository, never()).findAll();
    }

    @Test
    void findById_inexistenteLanzaExcepcion() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(NotificationNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void markAsRead_cambiaEstadoYGuardaFecha() {
        when(repository.findById(1L)).thenReturn(Optional.of(saved(1L, NotificationStatus.SENT)));
        when(repository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

        NotificationResponse response = service.markAsRead(1L);

        assertThat(response.status()).isEqualTo(NotificationStatus.READ);
        assertThat(response.readAt()).isNotNull();
    }

    @Test
    void markAsRead_yaLeidaNoCambiaLaFecha() {
        Notification alreadyRead = saved(1L, NotificationStatus.READ);
        LocalDateTime firstRead = LocalDateTime.of(2026, 11, 10, 8, 30);
        alreadyRead.setReadAt(firstRead);
        when(repository.findById(1L)).thenReturn(Optional.of(alreadyRead));
        when(repository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

        NotificationResponse response = service.markAsRead(1L);

        assertThat(response.readAt()).isEqualTo(firstRead);
    }

    @Test
    void delete_inexistenteNoBorraNada() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99L)).isInstanceOf(NotificationNotFoundException.class);
        verify(repository, never()).delete(any());
    }
}
