package co.edu.escuelaing.techcup.tournament.infrastructure.rest;

import co.edu.escuelaing.techcup.tournament.domain.exception.InvalidTournamentDataException;
import co.edu.escuelaing.techcup.tournament.domain.exception.InvalidTournamentDateRangeException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotDraftException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.TeamRemovalNotAllowedException;
import co.edu.escuelaing.techcup.tournament.infrastructure.rest.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TournamentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(TournamentNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(TournamentNotDraftException.class)
    public ResponseEntity<ErrorResponse> handleNotDraft(TournamentNotDraftException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler({InvalidTournamentDataException.class, InvalidTournamentDateRangeException.class})
    public ResponseEntity<ErrorResponse> handleInvalidTournamentData(RuntimeException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(TeamRemovalNotAllowedException.class)
    public ResponseEntity<Map<String, String>> handleRemovalNotAllowed(TeamRemovalNotAllowedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .findFirst().orElse("Datos inválidos");
        return ResponseEntity.badRequest().body(Map.of("error", message));
    }
}