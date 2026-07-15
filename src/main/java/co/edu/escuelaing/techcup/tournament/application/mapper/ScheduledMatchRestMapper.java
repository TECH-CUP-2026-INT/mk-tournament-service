package co.edu.escuelaing.techcup.tournament.application.mapper;

import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.ScheduledMatchResponse;
import co.edu.escuelaing.techcup.tournament.domain.model.ScheduledMatch;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
/**
 * Mapper MapStruct: {@link co.edu.escuelaing.techcup.tournament.domain.model.ScheduledMatch}
 * a su DTO de respuesta REST.
 */
public interface ScheduledMatchRestMapper {
    ScheduledMatchResponse toResponse(ScheduledMatch scheduledMatch);
}
