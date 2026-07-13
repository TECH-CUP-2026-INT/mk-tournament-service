package co.edu.escuelaing.techcup.tournament.entity.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "courts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourtDocument {

    @Id
    private String id;
    private String tournamentId;
    private String section;
    private String description;
    private String imageId;
}
