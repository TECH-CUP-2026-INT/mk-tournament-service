package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mapper;

import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo.CourtDocument;
import co.edu.escuelaing.techcup.tournament.domain.model.Court;
import co.edu.escuelaing.techcup.tournament.domain.model.CourtSection;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CourtPersistenceMapperTest {

    private final CourtPersistenceMapper mapper = Mappers.getMapper(CourtPersistenceMapper.class);

    @Test
    void toDomain_convierteTodosLosCampos() {
        UUID id = UUID.randomUUID();
        UUID tournamentId = UUID.randomUUID();
        UUID matchId = UUID.randomUUID();
        CourtDocument document = new CourtDocument(id, tournamentId, "CANCHA_1", "Descripción", "img1", matchId);

        Court court = mapper.toDomain(document);

        assertEquals(id, court.getId());
        assertEquals(tournamentId, court.getTournamentId());
        assertEquals(CourtSection.CANCHA_1, court.getSection());
        assertEquals("Descripción", court.getDescription());
        assertEquals("img1", court.getImageId());
        assertEquals(matchId, court.getMatchId());
    }

    @Test
    void toDocument_convierteTodosLosCampos() {
        UUID id = UUID.randomUUID();
        UUID tournamentId = UUID.randomUUID();
        UUID matchId = UUID.randomUUID();
        Court court = Court.reconstruct(id, tournamentId, CourtSection.CANCHA_2, "Descripción", "img1", matchId);

        CourtDocument document = mapper.toDocument(court);

        assertEquals(id, document.getId());
        assertEquals(tournamentId, document.getTournamentId());
        assertEquals("CANCHA_2", document.getSection());
        assertEquals("Descripción", document.getDescription());
        assertEquals("img1", document.getImageId());
        assertEquals(matchId, document.getMatchId());
    }
}
