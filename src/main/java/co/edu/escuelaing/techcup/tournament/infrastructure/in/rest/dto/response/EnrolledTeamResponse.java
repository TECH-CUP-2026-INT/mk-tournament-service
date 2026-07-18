package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "A team with a confirmed (paid) enrollment in a tournament.")
public record EnrolledTeamResponse(
        @Schema(description = "Team ID.", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") UUID teamId,
        @Schema(description = "Team name.", example = "Los Compiladores") String teamName,
        @Schema(description = "URL of the team's logo image.", example = "https://placeholder.com/teams/3fa85f64-5717-4562-b3fc-2c963f66afa6/logo") String logoUrl,
        @Schema(description = "Enrollment ID.", example = "6f9619ff-8b86-d011-b42d-00cf4fc964ff") UUID enrollmentId,
        @Schema(description = "Date/time the enrollment payment was confirmed.") LocalDateTime confirmationDate
) {}
