package com.distrilink.notification.model;

/** Qué evento del negocio generó la notificación. */
public enum NotificationType {
    ORDER_CREATED,     // El Distribuidor recibe un pedido nuevo
    ORDER_CONFIRMED,   // El Cliente: su pedido fue aprobado (RF4.4, RF4.6)
    ORDER_REJECTED,    // El Cliente: su pedido fue rechazado (RF4.5, RF4.6)
    ORDER_DISPATCHED,  // El Cliente: su pedido fue despachado (RF4.7, RF4.6)
    ORDER_CANCELLED,   // El Distribuidor: el Cliente canceló el pedido (RF3.8)
    LOW_STOCK,         // El Distribuidor: un producto quedó con pocas existencias (RF2.7)
    ACCOUNT_LOCKED     // El usuario: su cuenta se bloqueó por intentos fallidos (RF1.7)
}
