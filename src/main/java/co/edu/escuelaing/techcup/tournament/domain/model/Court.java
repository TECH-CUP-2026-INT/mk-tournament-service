package co.edu.escuelaing.techcup.tournament.domain.model;

import co.edu.escuelaing.techcup.tournament.domain.exception.InvalidCourtDataException;

import java.util.UUID;

/**
 * Cancha física del campus donde se juegan los partidos, con su sección del mapa,
 * imagen y el partido que tenga asignado.
 */
@SuppressWarnings("java:S2160") // identidad por id (heredada de AggregateRoot), no por campos: patron DDD intencional
public class Court extends AggregateRoot {

    private static final int MAX_DESCRIPTION_LENGTH = 300;

    private final UUID tournamentId;
    private final CourtSection section;
    private final String description;
    // GridFS ObjectId (hex string, no es formato UUID) — ver GridFsCourtImageStorageAdapter.
    private String imageId;
    private UUID matchId;

    private Court(UUID id, UUID tournamentId, CourtSection section, String description, String imageId, UUID matchId) {
        super(id);
        this.tournamentId = tournamentId;
        this.section = section;
        this.description = description;
        this.imageId = imageId;
        this.matchId = matchId;
    }

    public static Court create(UUID tournamentId, CourtSection section, String description) {
        validateTournamentId(tournamentId);
        validateSection(section);
        validateDescription(description);
        return new Court(UUID.randomUUID(), tournamentId, section, description, null, null);
    }

    public static Court reconstruct(UUID id, UUID tournamentId, CourtSection section, String description, String imageId) {
        return new Court(id, tournamentId, section, description, imageId, null);
    }

    public static Court reconstruct(UUID id, UUID tournamentId, CourtSection section, String description, String imageId, UUID matchId) {
        return new Court(id, tournamentId, section, description, imageId, matchId);
    }

    public void attachImage(String imageId) {
        if (imageId == null || imageId.isBlank())
            throw new IllegalArgumentException("El id de la imagen no puede estar vacío");
        this.imageId = imageId;
    }

    public void assignMatch(UUID matchId) {
        if (matchId == null)
            throw new IllegalArgumentException("El id del partido no puede estar vacío");
        this.matchId = matchId;
    }

    private static void validateTournamentId(UUID tournamentId) {
        if (tournamentId == null)
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

    public UUID getTournamentId() { return tournamentId; }
    public CourtSection getSection() { return section; }
    public String getDescription() { return description; }
    public String getImageId() { return imageId; }
    public UUID getMatchId() { return matchId; }
}
