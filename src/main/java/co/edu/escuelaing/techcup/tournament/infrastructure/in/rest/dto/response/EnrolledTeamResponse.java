package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response;

import co.edu.escuelaing.techcup.tournament.domain.model.Enrollment;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "A team with a confirmed (paid) enrollment in a tournament.")
public record EnrolledTeamResponse(
        @Schema(description = "Team ID.", example = "team_xyz789") String teamId,
        @Schema(description = "Team name.", example = "Los Compiladores") String teamName,
        @Schema(description = "URL of the team's logo image.", example = "https://placeholder.com/teams/team_xyz789/logo") String logoUrl,
        @Schema(description = "Enrollment ID.", example = "enr_abc123") String enrollmentId,
        @Schema(description = "Date/time the enrollment payment was confirmed.") LocalDateTime confirmationDate
) {}
