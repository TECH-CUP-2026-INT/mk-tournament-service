package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Standings table for one group of the tournament's group stage.")
public record GroupStandingsResponse(
        @Schema(description = "Group name.", example = "Grupo A") String groupName,
        @Schema(description = "Standings rows, already ordered by position.") List<TeamStandingResponse> standings
) {}
