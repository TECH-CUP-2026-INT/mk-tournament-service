package co.edu.escuelaing.techcup.tournament.dto.request;

import co.edu.escuelaing.techcup.tournament.service.MatchActivationAction;
import jakarta.validation.constraints.NotNull;

public record MatchActivationRequest(@NotNull MatchActivationAction action) {}
