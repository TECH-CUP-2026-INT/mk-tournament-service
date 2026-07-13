package co.edu.escuelaing.techcup.tournament.dto.response;

import co.edu.escuelaing.techcup.tournament.service.RegistrationStatus;

public record DisqualifyTeamResponse(String tournamentId, String teamId, RegistrationStatus status, String message) {}
