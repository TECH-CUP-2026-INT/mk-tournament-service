package co.edu.escuelaing.techcup.tournament.dto.response;

import co.edu.escuelaing.techcup.tournament.service.TournamentStatus;

public record PauseTournamentResponse(String tournamentId, TournamentStatus status, boolean paused, String message) {}
