package co.edu.escuelaing.techcup.tournament.application.mapper;

import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.MatchupResponse;
import co.edu.escuelaing.techcup.tournament.domain.model.Match;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MatchupRestMapper {
    MatchupResponse toResponse(Match match);
}
