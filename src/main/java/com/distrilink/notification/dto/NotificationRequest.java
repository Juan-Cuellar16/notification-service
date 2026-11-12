package com.distrilink.notification.dto;

import com.distrilink.notification.model.NotificationChannel;
import com.distrilink.notification.model.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** DTO de entrada: lo que el cliente envía para crear o actualizar una notificación. */
@Schema(description = "Datos para crear o actualizar una notificación")
public record NotificationRequest(

        @Schema(description = "Id del usuario que recibe la notificación", example = "15")
        @NotNull(message = "recipientId es obligatorio")
        @Positive(message = "recipientId debe ser positivo")
        Long recipientId,

        @Schema(description = "Id del pedido relacionado (opcional)", example = "120")
        @Positive(message = "orderId debe ser positivo")
        Long orderId,

        @Schema(description = "Evento que genera la notificación", example = "ORDER_CONFIRMED")
        @NotNull(message = "type es obligatorio")
        NotificationType type,

        @Schema(description = "Canal de entrega. Si no se envía, se usa IN_APP", example = "IN_APP")
        NotificationChannel channel,

        @Schema(description = "Título corto", example = "Pedido aprobado")
        @NotBlank(message = "title es obligatorio")
        @Size(max = 120, message = "title admite máximo 120 caracteres")
        String title,

        @Schema(description = "Texto de la notificación", example = "Tu pedido #120 fue aprobado por Distribuidora El Sol.")
        @NotBlank(message = "message es obligatorio")
        @Size(max = 500, message = "message admite máximo 500 caracteres")
        String message
) {
}
