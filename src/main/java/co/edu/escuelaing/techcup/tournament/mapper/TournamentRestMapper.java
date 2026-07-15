package co.edu.escuelaing.techcup.tournament.mapper;

import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.dto.response.HistoricalTournamentResponse;
import co.edu.escuelaing.techcup.tournament.dto.response.TournamentResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TournamentRestMapper {
    TournamentResponse toResponse(Tournament tournament);

    HistoricalTournamentResponse toHistoricalResponse(Tournament tournament);
}