package co.edu.escuelaing.techcup.tournament.entity.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Document(collection = "tournaments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TournamentDocument {

    @Id
    private String id;
    private String name;
    private String type;
    private String format;
    private int numberOfTeams;
    private BigDecimal cost;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate registrationDeadline;
    private LocalTime matchStartTime;
    private LocalTime matchEndTime;
    private String status;
    private String rulebookFileId;
    private String championTeamId;
    private String championResolution;
    private boolean paused;
}