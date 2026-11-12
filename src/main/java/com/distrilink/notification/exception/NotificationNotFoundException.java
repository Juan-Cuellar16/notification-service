package com.distrilink.notification.exception;

public class NotificationNotFoundException extends RuntimeException {

    public NotificationNotFoundException(Long id) {
        super("No existe la notificación con id " + id);
    }
}
