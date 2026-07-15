package co.edu.escuelaing.techcup.tournament.application.mapper;

import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.HistoricalTournamentResponse;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.TournamentResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TournamentRestMapper {
    TournamentResponse toResponse(Tournament tournament);

    HistoricalTournamentResponse toHistoricalResponse(Tournament tournament);
}