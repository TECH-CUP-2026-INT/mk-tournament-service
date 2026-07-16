package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "courts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourtDocument {

    @Id
    private UUID id;
    private UUID tournamentId;
    private String section;
    private String description;
    // GridFS ObjectId (hex string, no es formato UUID).
    private String imageId;
    private UUID matchId;
}
