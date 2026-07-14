package co.edu.escuelaing.techcup.tournament.dto.response;

import java.util.List;

public record RegisteredTeamsResponse(
        List<EnrolledTeamResponse> enrolledTeams,
        List<ReservedTeamResponse> reservedTeams,
        int totalEnrolled,
        int totalReserved,
        int availableSlots
) {}
