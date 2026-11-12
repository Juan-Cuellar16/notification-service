package com.distrilink.notification.controller;

import com.distrilink.notification.config.CorsConfig;
import com.distrilink.notification.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Checks that the browser of the web client is allowed and any other site is blocked. */
@WebMvcTest(NotificationController.class)
@Import(CorsConfig.class)
@TestPropertySource(properties = "app.cors.allowed-origins=http://localhost:5500,http://127.0.0.1:5500")
class CorsConfigTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private NotificationService service;

    @Test
    void preflight_desdeLiveServer_estaPermitido() throws Exception {
        mvc.perform(options("/api/notifications")
                        .header(HttpHeaders.ORIGIN, "http://127.0.0.1:5500")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "Authorization, Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://127.0.0.1:5500"));
    }

    @Test
    void get_desdeLiveServer_devuelveCabeceraCors() throws Exception {
        when(service.findAll(null)).thenReturn(List.of());

        mvc.perform(get("/api/notifications").header(HttpHeaders.ORIGIN, "http://localhost:5500"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5500"));
    }

    @Test
    void preflight_desdeOtroSitio_esRechazado() throws Exception {
        mvc.perform(options("/api/notifications")
                        .header(HttpHeaders.ORIGIN, "https://sitio-malicioso.com")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "DELETE"))
                .andExpect(status().isForbidden());
    }
}
