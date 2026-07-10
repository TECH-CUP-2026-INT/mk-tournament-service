// infrastructure/rest/dto/RulebookResponse.java
package co.edu.escuelaing.techcup.tournament.infrastructure.rest.dto;

public record RulebookResponse(
        String tournamentId,
        String rulebookFileId,
        String message
) {}