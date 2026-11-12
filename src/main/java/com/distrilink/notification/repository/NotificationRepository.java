package com.distrilink.notification.repository;

import com.distrilink.notification.model.Notification;
import com.distrilink.notification.model.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Capa de Repositories: Spring Data genera el SQL a partir del nombre del método. */
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId);

    long countByRecipientIdAndStatusNot(Long recipientId, NotificationStatus status);
}
