package co.edu.escuelaing.techcup.tournament.dto.request;

import co.edu.escuelaing.techcup.tournament.service.TournamentPauseAction;
import jakarta.validation.constraints.NotNull;

public record PauseTournamentRequest(@NotNull TournamentPauseAction action) {}
