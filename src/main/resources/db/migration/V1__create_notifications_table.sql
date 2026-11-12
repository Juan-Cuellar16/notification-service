-- notification-service: tabla principal.
-- Hibernate NO crea esta tabla (ddl-auto=validate); la crea Flyway con este script.
-- Una migracion ya aplicada NUNCA se edita: para cambios se crea V2__..., V3__...

CREATE TABLE notifications (
    id            BIGINT        NOT NULL AUTO_INCREMENT,
    recipient_id  BIGINT        NOT NULL,              -- id del usuario en access-control-service (sin FK: otra base de datos)
    order_id      BIGINT        NULL,                  -- id del pedido en order-service, si la notificacion es de un pedido
    type          VARCHAR(40)   NOT NULL,              -- ORDER_CREATED, ORDER_CONFIRMED, ORDER_REJECTED, ...
    channel       VARCHAR(20)   NOT NULL,              -- IN_APP, EMAIL
    title         VARCHAR(120)  NOT NULL,
    message       VARCHAR(500)  NOT NULL,
    status        VARCHAR(20)   NOT NULL,              -- PENDING, SENT, READ
    created_at    DATETIME(6)   NOT NULL,
    read_at       DATETIME(6)   NULL,
    PRIMARY KEY (id),
    INDEX idx_notifications_recipient (recipient_id, created_at)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;
