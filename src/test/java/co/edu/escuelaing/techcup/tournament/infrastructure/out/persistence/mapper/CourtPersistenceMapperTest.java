package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mapper;

import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo.CourtDocument;
import co.edu.escuelaing.techcup.tournament.domain.model.Court;
import co.edu.escuelaing.techcup.tournament.domain.model.CourtSection;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CourtPersistenceMapperTest {

    private final CourtPersistenceMapper mapper = Mappers.getMapper(CourtPersistenceMapper.class);

    @Test
    void toDomain_convierteTodosLosCampos() {
        CourtDocument document = new CourtDocument("c1", "t1", "CANCHA_1", "Descripción", "img1", "m1");

        Court court = mapper.toDomain(document);

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

        CourtDocument document = mapper.toDocument(court);

        assertEquals("c1", document.getId());
        assertEquals("t1", document.getTournamentId());
        assertEquals("CANCHA_2", document.getSection());
        assertEquals("Descripción", document.getDescription());
        assertEquals("img1", document.getImageId());
        assertEquals("m1", document.getMatchId());
    }
}
