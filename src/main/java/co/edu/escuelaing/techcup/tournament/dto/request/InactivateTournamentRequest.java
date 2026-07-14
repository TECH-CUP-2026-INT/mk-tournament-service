package co.edu.escuelaing.techcup.tournament.dto.request;

import co.edu.escuelaing.techcup.tournament.service.TournamentInactivationAction;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = """
        Request to inactivate or reactivate a tournament. Inactivating blocks ALL functionality of \
        the tournament, including read queries — not just writes (unlike pause).""")
public record InactivateTournamentRequest(
        @Schema(description = "Action to apply: INACTIVATE or REACTIVATE.", example = "INACTIVATE")
        @NotNull TournamentInactivationAction action
) {}
