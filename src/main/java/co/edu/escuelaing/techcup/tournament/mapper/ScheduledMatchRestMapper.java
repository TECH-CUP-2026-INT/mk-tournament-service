package co.edu.escuelaing.techcup.tournament.mapper;

import co.edu.escuelaing.techcup.tournament.dto.response.ScheduledMatchResponse;
import co.edu.escuelaing.techcup.tournament.service.ScheduledMatch;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ScheduledMatchRestMapper {
    ScheduledMatchResponse toResponse(ScheduledMatch scheduledMatch);
}
