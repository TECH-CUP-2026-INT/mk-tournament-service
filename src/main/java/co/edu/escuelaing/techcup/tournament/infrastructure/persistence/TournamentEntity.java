package co.edu.escuelaing.techcup.tournament.infrastructure.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tournaments")
public class TournamentEntity {

    @Id
    private String id;
    private String name;
    private String status;

    public TournamentEntity() {}

    public TournamentEntity(String id, String name, String status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getStatus() { return status; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setStatus(String status) { this.status = status; }
}
