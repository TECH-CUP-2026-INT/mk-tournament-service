package co.edu.escuelaing.techcup.tournament.infrastructure.rest.dto;

import co.edu.escuelaing.techcup.tournament.domain.model.EliminationType;
import co.edu.escuelaing.techcup.tournament.domain.model.RegistrationStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;

import java.time.LocalDate;
import java.util.List;

public record TournamentResponse(
        String id,
        String name,
        LocalDate startDate,
        LocalDate endDate,
        int durationDays,
        TournamentStatus status,
        EliminationType eliminationType,
        List<TeamResponse> teams
) {
    public record TeamResponse(String teamId, String teamName, RegistrationStatus registrationStatus, int points) {}
}
