// infrastructure/rest/mapper/TournamentRestMapper.java
package co.edu.escuelaing.techcup.tournament.infrastructure.rest.mapper;

import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.infrastructure.rest.dto.TournamentResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TournamentRestMapper {
    TournamentResponse toResponse(Tournament tournament);
}