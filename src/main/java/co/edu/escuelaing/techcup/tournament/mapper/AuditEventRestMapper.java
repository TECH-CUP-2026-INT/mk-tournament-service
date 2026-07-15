package co.edu.escuelaing.techcup.tournament.mapper;

import co.edu.escuelaing.techcup.tournament.dto.response.AuditEventResponse;
import co.edu.escuelaing.techcup.tournament.service.AuditEvent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuditEventRestMapper {
    AuditEventResponse toResponse(AuditEvent event);
}
