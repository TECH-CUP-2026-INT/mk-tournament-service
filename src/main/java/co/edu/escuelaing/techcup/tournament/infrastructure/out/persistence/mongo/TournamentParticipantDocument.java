package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tournament_participants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TournamentParticipantDocument {

    @Id
    private String id;
    private String tournamentId;
    private String userId;
    private String status;
}
