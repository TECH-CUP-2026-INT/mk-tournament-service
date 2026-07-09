// infrastructure/rest/dto/TournamentResponse.java
package co.edu.escuelaing.techcup.tournament.infrastructure.rest.dto;

import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;

public record TournamentResponse(
        String id,
        String name,
        TournamentStatus status
) {}