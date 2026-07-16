package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.controller;

import co.edu.escuelaing.techcup.tournament.domain.exception.RulebookNotAttachedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TeamRemovalNotAllowedException;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleIllegalArgument_devuelve400ConElMensajeDelDominio() {
        ResponseEntity<ErrorResponse> response =
                handler.handleIllegalArgument(new IllegalArgumentException("El id de la imagen no puede estar vacío"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("El id de la imagen no puede estar vacío", response.getBody().message());
        assertTrue(response.getBody().details().isEmpty());
    }

    @Test
    void handleRemovalNotAllowed_devuelve409ConElMensajeDelDominio() {
        ResponseEntity<ErrorResponse> response = handler.handleRemovalNotAllowed(
                new TeamRemovalNotAllowedException("El equipo no está inscrito en este torneo"));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("El equipo no está inscrito en este torneo", response.getBody().message());
    }

    @Test
    void handleRulebookNotAttached_devuelve404ConHintEnDetails() {
        ResponseEntity<ErrorResponse> response =
                handler.handleRulebookNotAttached(new RulebookNotAttachedException(UUID.randomUUID()));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(1, response.getBody().details().size());
        assertTrue(response.getBody().details().get(0).contains("reglamento"));
    }
}
