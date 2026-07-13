package co.edu.escuelaing.techcup.tournament.dto.response;

import co.edu.escuelaing.techcup.tournament.service.TournamentStatus;

public record InactivateTournamentResponse(String tournamentId, TournamentStatus status, boolean active, String message) {}
