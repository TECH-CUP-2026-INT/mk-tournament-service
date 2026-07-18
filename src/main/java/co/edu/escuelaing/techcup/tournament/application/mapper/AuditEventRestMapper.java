package co.edu.escuelaing.techcup.tournament.application.mapper;

import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.AuditEventResponse;
import co.edu.escuelaing.techcup.tournament.domain.model.AuditEvent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
/**
 * Mapper MapStruct: {@link co.edu.escuelaing.techcup.tournament.domain.model.AuditEvent}
 * a su DTO de respuesta REST.
 */
public interface AuditEventRestMapper {
    AuditEventResponse toResponse(AuditEvent event);
}
