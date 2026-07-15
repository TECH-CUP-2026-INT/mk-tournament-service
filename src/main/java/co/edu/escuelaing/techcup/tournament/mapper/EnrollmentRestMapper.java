package co.edu.escuelaing.techcup.tournament.mapper;

import co.edu.escuelaing.techcup.tournament.dto.response.EnrollmentResponse;
import co.edu.escuelaing.techcup.tournament.service.Enrollment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EnrollmentRestMapper {
    EnrollmentResponse toResponse(Enrollment enrollment);
}
