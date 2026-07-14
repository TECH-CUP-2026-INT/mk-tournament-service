package co.edu.escuelaing.techcup.tournament.service;

import co.edu.escuelaing.techcup.tournament.exception.InvalidCourtDataException;

public class Court extends AggregateRoot {

    private static final int MAX_DESCRIPTION_LENGTH = 300;

    private final String tournamentId;
    private final CourtSection section;
    private final String description;
    private String imageId;
    private String matchId;

    private Court(String id, String tournamentId, CourtSection section, String description, String imageId, String matchId) {
        super(id);
        this.tournamentId = tournamentId;
        this.section = section;
        this.description = description;
        this.imageId = imageId;
        this.matchId = matchId;
    }

    public static Court create(String tournamentId, CourtSection section, String description) {
        validateTournamentId(tournamentId);
        validateSection(section);
        validateDescription(description);
        return new Court(null, tournamentId, section, description, null, null);
    }

    public static Court reconstruct(String id, String tournamentId, CourtSection section, String description, String imageId) {
        return new Court(id, tournamentId, section, description, imageId, null);
    }

    public static Court reconstruct(String id, String tournamentId, CourtSection section, String description, String imageId, String matchId) {
        return new Court(id, tournamentId, section, description, imageId, matchId);
    }

    public void attachImage(String imageId) {
        if (imageId == null || imageId.isBlank())
            throw new IllegalArgumentException("El id de la imagen no puede estar vacío");
        this.imageId = imageId;
    }

    public void assignMatch(String matchId) {
        if (matchId == null || matchId.isBlank())
            throw new IllegalArgumentException("El id del partido no puede estar vacío");
        this.matchId = matchId;
    }

    private static void validateTournamentId(String tournamentId) {
        if (tournamentId == null || tournamentId.isBlank())
            throw new InvalidCourtDataException("El torneo es obligatorio para registrar una cancha");
    }

    private static void validateSection(CourtSection section) {
        if (section == null)
            throw new InvalidCourtDataException("Debe seleccionar una de las 4 secciones de la cancha");
    }

    private static void validateDescription(String description) {
        if (description != null && description.length() > MAX_DESCRIPTION_LENGTH)
            throw new InvalidCourtDataException("La descripción no puede superar los " + MAX_DESCRIPTION_LENGTH + " caracteres");
    }

    public String getTournamentId() { return tournamentId; }
    public CourtSection getSection() { return section; }
    public String getDescription() { return description; }
    public String getImageId() { return imageId; }
    public String getMatchId() { return matchId; }
}
