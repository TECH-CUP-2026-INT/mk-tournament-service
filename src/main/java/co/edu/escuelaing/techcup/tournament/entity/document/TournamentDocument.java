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
import java.util.List;

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
    private List<TeamRegistrationDocument> teams;
    private List<MatchDocument> matches;
    private boolean paused;
    // Boolean (no boolean primitivo): así los torneos guardados antes de TCF-154,
    // que no tienen este campo en Mongo, se leen como null y se tratan como activos
    // en vez de caer por defecto en false (inactivos).
    private Boolean active;
}