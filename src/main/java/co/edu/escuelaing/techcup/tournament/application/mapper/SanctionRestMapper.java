package co.edu.escuelaing.techcup.tournament.application.mapper;

import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.SanctionResponse;
import co.edu.escuelaing.techcup.tournament.domain.model.PlayerSanction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
/**
 * Mapper MapStruct: {@link co.edu.escuelaing.techcup.tournament.domain.model.PlayerSanction}
 * a su DTO de respuesta REST.
 */
public interface SanctionRestMapper {
    SanctionResponse toResponse(PlayerSanction sanction);
}
