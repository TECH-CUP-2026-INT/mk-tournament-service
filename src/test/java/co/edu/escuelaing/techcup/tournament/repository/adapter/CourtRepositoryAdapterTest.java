package co.edu.escuelaing.techcup.tournament.repository.adapter;

import co.edu.escuelaing.techcup.tournament.entity.document.CourtDocument;
import co.edu.escuelaing.techcup.tournament.repository.mongo.CourtMongoRepository;
import co.edu.escuelaing.techcup.tournament.service.Court;
import co.edu.escuelaing.techcup.tournament.service.CourtSection;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CourtRepositoryAdapterTest {

    private final co.edu.escuelaing.techcup.tournament.mapper.CourtPersistenceMapper mapper = Mappers.getMapper(co.edu.escuelaing.techcup.tournament.mapper.CourtPersistenceMapper.class);

    @Test
    void save_delegaAlMongoRepositoryYMapeaDeVuelta() {
        CourtMongoRepository mongoRepository = mock(CourtMongoRepository.class);
        Court court = Court.create("t1", CourtSection.CANCHA_1, "Descripción");
        CourtDocument saved = new CourtDocument("c1", "t1", "CANCHA_1", "Descripción", null, null);
        when(mongoRepository.save(any())).thenReturn(saved);

        CourtRepositoryAdapter adapter = new CourtRepositoryAdapter(mongoRepository, mapper);
        Court result = adapter.save(court);

        assertEquals("c1", result.getId());
    }

    @Test
    void findById_cuandoExiste_retornaCourt() {
        CourtMongoRepository mongoRepository = mock(CourtMongoRepository.class);
        CourtDocument document = new CourtDocument("c1", "t1", "CANCHA_1", "Descripción", "img1", null);
        when(mongoRepository.findById("c1")).thenReturn(Optional.of(document));

        CourtRepositoryAdapter adapter = new CourtRepositoryAdapter(mongoRepository, mapper);
        Optional<Court> result = adapter.findById("c1");

        assertTrue(result.isPresent());
        assertEquals("c1", result.get().getId());
    }

    @Test
    void findById_cuandoNoExiste_retornaVacio() {
        CourtMongoRepository mongoRepository = mock(CourtMongoRepository.class);
        when(mongoRepository.findById("missing")).thenReturn(Optional.empty());

        CourtRepositoryAdapter adapter = new CourtRepositoryAdapter(mongoRepository, mapper);

        assertFalse(adapter.findById("missing").isPresent());
    }

    @Test
    void findByMatchId_cuandoExiste_retornaCourt() {
        CourtMongoRepository mongoRepository = mock(CourtMongoRepository.class);
        CourtDocument document = new CourtDocument("c1", "t1", "CANCHA_1", "Descripción", "img1", "m1");
        when(mongoRepository.findByMatchId("m1")).thenReturn(Optional.of(document));

        CourtRepositoryAdapter adapter = new CourtRepositoryAdapter(mongoRepository, mapper);
        Optional<Court> result = adapter.findByMatchId("m1");

        assertTrue(result.isPresent());
        assertEquals("m1", result.get().getMatchId());
    }
}
