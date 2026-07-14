package co.edu.escuelaing.techcup.tournament.entity.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalTime;

@Document(collection = "scheduled_matches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledMatchDocument {

    @Id
    private String id;
    private String matchupId;
    private String courtId;
    private String refereeId;
    private LocalDate matchDate;
    private LocalTime matchTime;
}
