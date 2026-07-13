package co.edu.escuelaing.techcup.tournament.dto.request;

import co.edu.escuelaing.techcup.tournament.service.TournamentInactivationAction;
import jakarta.validation.constraints.NotNull;

public record InactivateTournamentRequest(@NotNull TournamentInactivationAction action) {}
