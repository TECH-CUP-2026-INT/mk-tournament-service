package co.edu.escuelaing.techcup.tournament.exception;

import co.edu.escuelaing.techcup.tournament.exception.ChampionAssignmentNotAllowedException;
import co.edu.escuelaing.techcup.tournament.exception.ChampionPendingPenaltiesException;
import co.edu.escuelaing.techcup.tournament.exception.MatchNotFoundException;
import co.edu.escuelaing.techcup.tournament.exception.InvalidTournamentDateRangeException;
import co.edu.escuelaing.techcup.tournament.exception.TeamRemovalNotAllowedException;
import co.edu.escuelaing.techcup.tournament.exception.TournamentCannotBeFinalizedException;
import co.edu.escuelaing.techcup.tournament.exception.TournamentNotDraftException;
import co.edu.escuelaing.techcup.tournament.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import co.edu.escuelaing.techcup.tournament.exception.InvalidRulebookFileException;
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TournamentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTournamentNotFound(TournamentNotFoundException ex) {
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

    @ExceptionHandler(TeamRemovalNotAllowedException.class)
    public ResponseEntity<Map<String, String>> handleRemovalNotAllowed(TeamRemovalNotAllowedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(TournamentCannotBeFinalizedException.class)
    public ResponseEntity<ErrorResponse> handleTournamentCannotBeFinalized(TournamentCannotBeFinalizedException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(ChampionPendingPenaltiesException.class)
    public ResponseEntity<ErrorResponse> handleChampionPendingPenalties(ChampionPendingPenaltiesException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler({ChampionAssignmentNotAllowedException.class, MatchNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleChampionAssignmentErrors(RuntimeException ex) {
        HttpStatus status = ex instanceof MatchNotFoundException ? HttpStatus.NOT_FOUND : HttpStatus.CONFLICT;
        return ResponseEntity.status(status).body(new ErrorResponse(ex.getMessage()));
    }
    @ExceptionHandler(InvalidRulebookFileException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRulebookFile(InvalidRulebookFileException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
    }
}
