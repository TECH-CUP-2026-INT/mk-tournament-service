package co.edu.escuelaing.techcup.tournament.application.mapper;

import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.EnrollmentResponse;
import co.edu.escuelaing.techcup.tournament.domain.model.Enrollment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
/**
 * Mapper MapStruct: {@link co.edu.escuelaing.techcup.tournament.domain.model.Enrollment}
 * a su DTO de respuesta REST.
 */
public interface EnrollmentRestMapper {
    EnrollmentResponse toResponse(Enrollment enrollment);
}
