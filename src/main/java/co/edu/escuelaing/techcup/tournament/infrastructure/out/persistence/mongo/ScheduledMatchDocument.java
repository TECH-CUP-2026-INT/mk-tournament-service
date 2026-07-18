package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Document(collection = "scheduled_matches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledMatchDocument {

    @Id
    private UUID id;
    private UUID matchupId;
    private UUID courtId;
    private UUID refereeId;
    private LocalDate matchDate;
    private LocalTime matchTime;
}
