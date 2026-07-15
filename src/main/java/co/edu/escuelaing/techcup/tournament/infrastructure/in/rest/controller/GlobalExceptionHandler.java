package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.controller;

import co.edu.escuelaing.techcup.tournament.domain.exception.ChampionAssignmentNotAllowedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.ChampionPendingPenaltiesException;
import co.edu.escuelaing.techcup.tournament.domain.exception.CourtNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.exception.FixtureGenerationFailedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.HistoricalTournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.exception.InsufficientApprovedTeamsException;
import co.edu.escuelaing.techcup.tournament.domain.exception.InvalidCourtDataException;
import co.edu.escuelaing.techcup.tournament.domain.exception.InvalidCourtImageException;
import co.edu.escuelaing.techcup.tournament.domain.exception.InvalidRulebookFileException;
import co.edu.escuelaing.techcup.tournament.domain.exception.InvalidSanctionDataException;
import co.edu.escuelaing.techcup.tournament.domain.exception.InvalidScheduledMatchDataException;
import co.edu.escuelaing.techcup.tournament.domain.exception.InvalidTournamentDataException;
import co.edu.escuelaing.techcup.tournament.domain.exception.InvalidTournamentDateRangeException;
import co.edu.escuelaing.techcup.tournament.domain.exception.MatchActivationNotAllowedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.MatchInactiveException;
import co.edu.escuelaing.techcup.tournament.domain.exception.MatchNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.exception.MatchupNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.exception.NoAvailableSlotsException;
import co.edu.escuelaing.techcup.tournament.domain.exception.PaymentOrderCreationFailedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.RulebookNotAttachedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.ScheduleConflictException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TeamDisqualificationNotAllowedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TeamInactivationNotAllowedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TeamRemovalNotAllowedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TeamRosterSizeInvalidException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TeamServiceUnavailableException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentCannotBeDeletedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentCannotBeEditedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentCannotBeFinalizedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentInactivationNotAllowedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentInactiveException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotActiveForEnrollmentException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentPauseNotAllowedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentPreparationNotAllowedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.UserInactivationNotAllowedException;

import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TournamentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTournamentNotFound(TournamentNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(TournamentCannotBeDeletedException.class)
    public ResponseEntity<ErrorResponse> handleCannotBeDeleted(TournamentCannotBeDeletedException ex) {
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

    @ExceptionHandler(HistoricalTournamentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleHistoricalNotFound(HistoricalTournamentNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(RulebookNotAttachedException.class)
    public ResponseEntity<co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.RulebookErrorResponse> handleRulebookNotAttached(RulebookNotAttachedException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.RulebookErrorResponse(
                        "RULEBOOK_NOT_FOUND",
                        ex.getMessage(),
                        "El organizador debe subir el reglamento del torneo antes de que pueda ser consultado"
                )
        );
    }

    @ExceptionHandler({InvalidCourtDataException.class, InvalidCourtImageException.class})
    public ResponseEntity<ErrorResponse> handleInvalidCourtData(RuntimeException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(TournamentCannotBeEditedException.class)
    public ResponseEntity<ErrorResponse> handleTournamentCannotBeEdited(TournamentCannotBeEditedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(TournamentPauseNotAllowedException.class)
    public ResponseEntity<ErrorResponse> handleTournamentPauseNotAllowed(TournamentPauseNotAllowedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(TournamentInactivationNotAllowedException.class)
    public ResponseEntity<ErrorResponse> handleTournamentInactivationNotAllowed(TournamentInactivationNotAllowedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(TournamentInactiveException.class)
    public ResponseEntity<ErrorResponse> handleTournamentInactive(TournamentInactiveException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(TeamDisqualificationNotAllowedException.class)
    public ResponseEntity<ErrorResponse> handleTeamDisqualificationNotAllowed(TeamDisqualificationNotAllowedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(TeamInactivationNotAllowedException.class)
    public ResponseEntity<ErrorResponse> handleTeamInactivationNotAllowed(TeamInactivationNotAllowedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(UserInactivationNotAllowedException.class)
    public ResponseEntity<ErrorResponse> handleUserInactivationNotAllowed(UserInactivationNotAllowedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler({TournamentPreparationNotAllowedException.class, InsufficientApprovedTeamsException.class})
    public ResponseEntity<ErrorResponse> handlePreparationNotAllowed(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(FixtureGenerationFailedException.class)
    public ResponseEntity<ErrorResponse> handleFixtureGenerationFailed(FixtureGenerationFailedException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler({MatchupNotFoundException.class, CourtNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleScheduleMatchNotFound(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(ScheduleConflictException.class)
    public ResponseEntity<ErrorResponse> handleScheduleConflict(ScheduleConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(InvalidScheduledMatchDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidScheduledMatchData(InvalidScheduledMatchDataException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(InvalidSanctionDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidSanctionData(InvalidSanctionDataException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(MatchActivationNotAllowedException.class)
    public ResponseEntity<ErrorResponse> handleMatchActivationNotAllowed(MatchActivationNotAllowedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(MatchInactiveException.class)
    public ResponseEntity<ErrorResponse> handleMatchInactive(MatchInactiveException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler({PaymentOrderCreationFailedException.class, TeamServiceUnavailableException.class})
    public ResponseEntity<ErrorResponse> handleExternalServiceFailure(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(TeamRosterSizeInvalidException.class)
    public ResponseEntity<ErrorResponse> handleTeamRosterSizeInvalid(TeamRosterSizeInvalidException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler({TournamentNotActiveForEnrollmentException.class, NoAvailableSlotsException.class})
    public ResponseEntity<ErrorResponse> handleEnrollmentNotAllowed(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage()));
    }
}
