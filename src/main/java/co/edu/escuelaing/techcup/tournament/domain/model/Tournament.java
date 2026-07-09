package co.edu.escuelaing.techcup.tournament.domain.model;

public class Tournament {

    private String id;
    private String name;
    private TournamentStatus status;

    public Tournament() {}

    public Tournament(String id, String name, TournamentStatus status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    public boolean isDraft() {
        return TournamentStatus.DRAFT == this.status;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public TournamentStatus getStatus() { return status; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setStatus(TournamentStatus status) { this.status = status; }
}
