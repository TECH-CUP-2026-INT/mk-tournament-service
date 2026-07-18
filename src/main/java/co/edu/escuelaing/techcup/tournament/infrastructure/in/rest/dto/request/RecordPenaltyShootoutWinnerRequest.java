package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = """
        Records the penalty shootout winner for a match tied in regulation time. Required \
        before the champion can be assigned for a final match that ended in a tie.""")
public record RecordPenaltyShootoutWinnerRequest(

        @Schema(description = "ID of the team that won the penalty shootout. Must be one of the two teams in the match.",
                example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull(message = "El id del equipo ganador de penales es obligatorio")
        UUID winnerTeamId
) {}
