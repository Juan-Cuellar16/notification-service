package com.distrilink.notification.controller;

import com.distrilink.notification.dto.NotificationRequest;
import com.distrilink.notification.dto.NotificationResponse;
import com.distrilink.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/** Capa de Controllers: recibe HTTP, valida el DTO y delega en el Service. */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notificaciones", description = "CRUD de las notificaciones de DistriLink")
public class NotificationController {

    private final NotificationService service;

    @PostMapping
    @Operation(summary = "Crear una notificación")
    @ApiResponse(responseCode = "201", description = "Notificación creada")
    @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    public ResponseEntity<NotificationResponse> create(@Valid @RequestBody NotificationRequest request) {
        NotificationResponse created = service.create(request);
        return ResponseEntity.created(URI.create("/api/notifications/" + created.id())).body(created);
    }

    @GetMapping
    @Operation(summary = "Listar notificaciones", description = "Si se envía recipientId, devuelve solo las de ese usuario, de la más reciente a la más antigua")
    public List<NotificationResponse> findAll(
            @Parameter(description = "Id del usuario destinatario", example = "15")
            @RequestParam(required = false) Long recipientId) {
        return service.findAll(recipientId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar una notificación por id")
    public NotificationResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una notificación")
    public NotificationResponse update(@PathVariable Long id, @Valid @RequestBody NotificationRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Marcar una notificación como leída")
    public NotificationResponse markAsRead(@PathVariable Long id) {
        return service.markAsRead(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar una notificación")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
