package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Enrolled and reserved teams for a tournament, plus slot availability.")
public record RegisteredTeamsResponse(
        @Schema(description = "Teams with a confirmed (paid) enrollment.") List<EnrolledTeamResponse> enrolledTeams,
        @Schema(description = "Teams with a slot reserved while payment is still pending.") List<ReservedTeamResponse> reservedTeams,
        @Schema(description = "Number of confirmed enrollments.", example = "6") int totalEnrolled,
        @Schema(description = "Number of pending reservations.", example = "2") int totalReserved,
        @Schema(description = "Slots still open for new enrollments.", example = "0") int availableSlots
) {}
