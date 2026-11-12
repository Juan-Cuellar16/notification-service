package com.distrilink.notification.controller;

import com.distrilink.notification.dto.NotificationRequest;
import com.distrilink.notification.dto.NotificationResponse;
import com.distrilink.notification.exception.NotificationNotFoundException;
import com.distrilink.notification.model.NotificationChannel;
import com.distrilink.notification.model.NotificationStatus;
import com.distrilink.notification.model.NotificationType;
import com.distrilink.notification.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Prueba del Controller: solo levanta la capa web; el Service es un mock. */
@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private NotificationService service;

    private static final String VALID_JSON = """
            {
              "recipientId": 15,
              "orderId": 120,
              "type": "ORDER_CONFIRMED",
              "title": "Pedido aprobado",
              "message": "Tu pedido #120 fue aprobado."
            }
            """;

    private NotificationResponse response() {
        return new NotificationResponse(1L, 15L, 120L, NotificationType.ORDER_CONFIRMED,
                NotificationChannel.IN_APP, "Pedido aprobado", "Tu pedido #120 fue aprobado.",
                NotificationStatus.PENDING, LocalDateTime.now(), null);
    }

    @Test
    void post_valido_devuelve201ConElDto() throws Exception {
        when(service.create(any(NotificationRequest.class))).thenReturn(response());

        mvc.perform(post("/api/notifications").contentType(MediaType.APPLICATION_JSON).content(VALID_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/notifications/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.channel").value("IN_APP"));
    }

    @Test
    void post_sinTitulo_devuelve400ConElCampo() throws Exception {
        String invalid = """
                { "recipientId": 15, "type": "ORDER_CONFIRMED", "title": "", "message": "Hola" }
                """;

        mvc.perform(post("/api/notifications").contentType(MediaType.APPLICATION_JSON).content(invalid))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").exists());
    }

    @Test
    void get_inexistente_devuelve404() throws Exception {
        when(service.findById(99L)).thenThrow(new NotificationNotFoundException(99L));

        mvc.perform(get("/api/notifications/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Notificación no encontrada"));
    }

    @Test
    void delete_devuelve204() throws Exception {
        mvc.perform(delete("/api/notifications/1"))
                .andExpect(status().isNoContent());
    }
}
