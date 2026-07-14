package co.edu.escuelaing.techcup.tournament.mapper;

import co.edu.escuelaing.techcup.tournament.entity.document.CourtDocument;
import co.edu.escuelaing.techcup.tournament.service.Court;
import co.edu.escuelaing.techcup.tournament.service.CourtSection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CourtPersistenceMapperTest {

    @Test
    void toDomain_convierteTodosLosCampos() {
        CourtDocument document = new CourtDocument("c1", "t1", "CANCHA_1", "Descripción", "img1", "m1");

        Court court = CourtPersistenceMapper.toDomain(document);

        assertEquals("c1", court.getId());
        assertEquals("t1", court.getTournamentId());
        assertEquals(CourtSection.CANCHA_1, court.getSection());
        assertEquals("Descripción", court.getDescription());
        assertEquals("img1", court.getImageId());
        assertEquals("m1", court.getMatchId());
    }

    @Test
    void toDocument_convierteTodosLosCampos() {
        Court court = Court.reconstruct("c1", "t1", CourtSection.CANCHA_2, "Descripción", "img1", "m1");

        CourtDocument document = CourtPersistenceMapper.toDocument(court);

        assertEquals("c1", document.getId());
        assertEquals("t1", document.getTournamentId());
        assertEquals("CANCHA_2", document.getSection());
        assertEquals("Descripción", document.getDescription());
        assertEquals("img1", document.getImageId());
        assertEquals("m1", document.getMatchId());
    }
}
