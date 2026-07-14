package co.edu.escuelaing.techcup.tournament.dto.request;

import co.edu.escuelaing.techcup.tournament.service.DisqualificationReason;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = """
        Request to disqualify a team from a tournament. The team stays in the records with its \
        past results, but is excluded from any future matchups.""")
public record DisqualifyTeamRequest(
        @Schema(description = "Reason for the disqualification: BRACKET_ELIMINATION, POINTS_STANDING or RULES_VIOLATION.",
                example = "RULES_VIOLATION")
        @NotNull(message = "El motivo de descalificación es obligatorio") DisqualificationReason reason
) {}
