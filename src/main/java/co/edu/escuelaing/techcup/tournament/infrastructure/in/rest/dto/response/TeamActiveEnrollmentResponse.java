package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Whether a team currently has a confirmed enrollment in an ACTIVE or IN_PROGRESS tournament.")
public record TeamActiveEnrollmentResponse(
        @Schema(description = "Team ID.", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID teamId,
        @Schema(description = "True if the team has a confirmed (ENROLLED) enrollment in a tournament that is "
                + "currently ACTIVE or IN_PROGRESS.") boolean hasActiveEnrollment
) {}
