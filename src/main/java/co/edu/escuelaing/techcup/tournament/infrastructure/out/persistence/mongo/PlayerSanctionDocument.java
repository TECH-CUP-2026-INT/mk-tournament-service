package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "player_sanctions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerSanctionDocument {

    @Id
    private String id;
    private String playerId;
    private String type;
    private int matchesRemaining;
}
