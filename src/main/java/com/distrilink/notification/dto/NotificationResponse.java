package com.distrilink.notification.dto;

import com.distrilink.notification.model.Notification;
import com.distrilink.notification.model.NotificationChannel;
import com.distrilink.notification.model.NotificationStatus;
import com.distrilink.notification.model.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/** DTO de salida: lo que devuelve la API. Nunca se devuelve la @Entity directamente. */
@Schema(description = "Notificación devuelta por la API")
public record NotificationResponse(
        Long id,
        Long recipientId,
        Long orderId,
        NotificationType type,
        NotificationChannel channel,
        String title,
        String message,
        NotificationStatus status,
        LocalDateTime createdAt,
        LocalDateTime readAt
) {

    public static NotificationResponse from(Notification n) {
        return new NotificationResponse(
                n.getId(), n.getRecipientId(), n.getOrderId(), n.getType(), n.getChannel(),
                n.getTitle(), n.getMessage(), n.getStatus(), n.getCreatedAt(), n.getReadAt());
    }
}
