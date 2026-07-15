package co.edu.escuelaing.techcup.tournament.mapper;

import co.edu.escuelaing.techcup.tournament.dto.response.MatchupResponse;
import co.edu.escuelaing.techcup.tournament.service.Match;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MatchupRestMapper {
    MatchupResponse toResponse(Match match);
}
