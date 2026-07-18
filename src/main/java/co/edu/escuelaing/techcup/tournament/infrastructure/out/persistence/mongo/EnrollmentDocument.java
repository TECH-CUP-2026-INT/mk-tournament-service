package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDocument {

    private UUID enrollmentId;
    private UUID teamId;
    private String teamName;
    private String status;
    private int points;
    private LocalDateTime confirmationDate;
    private LocalDateTime reservationExpiresAt;
}
