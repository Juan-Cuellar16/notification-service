package com.distrilink.notification.service;

import com.distrilink.notification.dto.NotificationRequest;
import com.distrilink.notification.dto.NotificationResponse;
import com.distrilink.notification.exception.NotificationNotFoundException;
import com.distrilink.notification.model.Notification;
import com.distrilink.notification.model.NotificationChannel;
import com.distrilink.notification.model.NotificationStatus;
import com.distrilink.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/** Capa de Services: aquí viven las reglas de negocio, no en el Controller. */
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;

    @Transactional
    public NotificationResponse create(NotificationRequest request) {
        Notification notification = new Notification();
        apply(request, notification);
        notification.setStatus(NotificationStatus.PENDING);
        return NotificationResponse.from(repository.save(notification));
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> findAll(Long recipientId) {
        List<Notification> result = (recipientId == null)
                ? repository.findAll()
                : repository.findByRecipientIdOrderByCreatedAtDesc(recipientId);
        return result.stream().map(NotificationResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public NotificationResponse findById(Long id) {
        return NotificationResponse.from(getOrThrow(id));
    }

    @Transactional
    public NotificationResponse update(Long id, NotificationRequest request) {
        Notification notification = getOrThrow(id);
        apply(request, notification);
        return NotificationResponse.from(repository.save(notification));
    }

    /** Regla de negocio: marcar como leída guarda la fecha solo la primera vez. */
    @Transactional
    public NotificationResponse markAsRead(Long id) {
        Notification notification = getOrThrow(id);
        if (notification.getStatus() != NotificationStatus.READ) {
            notification.setStatus(NotificationStatus.READ);
            notification.setReadAt(LocalDateTime.now());
        }
        return NotificationResponse.from(repository.save(notification));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(getOrThrow(id));
    }

    private Notification getOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotificationNotFoundException(id));
    }

    private void apply(NotificationRequest request, Notification notification) {
        notification.setRecipientId(request.recipientId());
        notification.setOrderId(request.orderId());
        notification.setType(request.type());
        notification.setChannel(request.channel() != null ? request.channel() : NotificationChannel.IN_APP);
        notification.setTitle(request.title().trim());
        notification.setMessage(request.message().trim());
    }
}
