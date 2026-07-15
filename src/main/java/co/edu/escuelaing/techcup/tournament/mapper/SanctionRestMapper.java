package co.edu.escuelaing.techcup.tournament.mapper;

import co.edu.escuelaing.techcup.tournament.dto.response.SanctionResponse;
import co.edu.escuelaing.techcup.tournament.service.PlayerSanction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SanctionRestMapper {
    SanctionResponse toResponse(PlayerSanction sanction);
}
