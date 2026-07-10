// infrastructure/rest/GlobalExceptionHandler.java
package co.edu.escuelaing.techcup.tournament.infrastructure.rest;

import co.edu.escuelaing.techcup.tournament.domain.exception.InvalidTournamentDataException;
import co.edu.escuelaing.techcup.tournament.domain.exception.InvalidTournamentDateRangeException;
import co.edu.escuelaing.techcup.tournament.infrastructure.rest.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import co.edu.escuelaing.techcup.tournament.domain.model.TeamRemovalNotAllowedException;
import org.springframework.web.bind.MethodArgumentNotValidException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({InvalidTournamentDataException.class, InvalidTournamentDateRangeException.class})
    public ResponseEntity<ErrorResponse> handleInvalidTournamentData(RuntimeException exception) {
        return ResponseEntity.badRequest().body(new ErrorResponse(exception.getMessage()));
    }
    @ExceptionHandler(TeamRemovalNotAllowedException.class)
    public ResponseEntity<ErrorResponse> handleRemovalNotAllowed(TeamRemovalNotAllowedException exception) {
        return ResponseEntity.status(409).body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .findFirst().orElse("Datos inválidos");
        return ResponseEntity.badRequest().body(new ErrorResponse(message));
    }


}