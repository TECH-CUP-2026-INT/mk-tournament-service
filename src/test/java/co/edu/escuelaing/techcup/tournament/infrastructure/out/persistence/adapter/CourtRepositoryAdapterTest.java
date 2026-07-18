package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.adapter;

import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo.CourtDocument;
import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo.CourtMongoRepository;
import co.edu.escuelaing.techcup.tournament.domain.model.Court;
import co.edu.escuelaing.techcup.tournament.domain.model.CourtSection;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CourtRepositoryAdapterTest {

    private final co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mapper.CourtPersistenceMapper mapper = Mappers.getMapper(co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mapper.CourtPersistenceMapper.class);

    @Test
    void save_delegaAlMongoRepositoryYMapeaDeVuelta() {
        UUID courtId = UUID.randomUUID();
        UUID tournamentId = UUID.randomUUID();
        CourtMongoRepository mongoRepository = mock(CourtMongoRepository.class);
        Court court = Court.create(tournamentId, CourtSection.CANCHA_1, "Descripción");
        CourtDocument saved = new CourtDocument(courtId, tournamentId, "CANCHA_1", "Descripción", null, null);
        when(mongoRepository.save(any())).thenReturn(saved);

        CourtRepositoryAdapter adapter = new CourtRepositoryAdapter(mongoRepository, mapper);
        Court result = adapter.save(court);

        assertEquals(courtId, result.getId());
    }

    @Test
    void findById_cuandoExiste_retornaCourt() {
        UUID courtId = UUID.randomUUID();
        UUID tournamentId = UUID.randomUUID();
        CourtMongoRepository mongoRepository = mock(CourtMongoRepository.class);
        CourtDocument document = new CourtDocument(courtId, tournamentId, "CANCHA_1", "Descripción", "img1", null);
        when(mongoRepository.findById(courtId)).thenReturn(Optional.of(document));

        CourtRepositoryAdapter adapter = new CourtRepositoryAdapter(mongoRepository, mapper);
        Optional<Court> result = adapter.findById(courtId);

        assertTrue(result.isPresent());
        assertEquals(courtId, result.get().getId());
    }

    @Test
    void findById_cuandoNoExiste_retornaVacio() {
        UUID missing = UUID.randomUUID();
        CourtMongoRepository mongoRepository = mock(CourtMongoRepository.class);
        when(mongoRepository.findById(missing)).thenReturn(Optional.empty());

        CourtRepositoryAdapter adapter = new CourtRepositoryAdapter(mongoRepository, mapper);

        assertFalse(adapter.findById(missing).isPresent());
    }

    @Test
    void findByMatchId_cuandoExiste_retornaCourt() {
        UUID courtId = UUID.randomUUID();
        UUID tournamentId = UUID.randomUUID();
        UUID matchId = UUID.randomUUID();
        CourtMongoRepository mongoRepository = mock(CourtMongoRepository.class);
        CourtDocument document = new CourtDocument(courtId, tournamentId, "CANCHA_1", "Descripción", "img1", matchId);
        when(mongoRepository.findByMatchId(matchId)).thenReturn(Optional.of(document));

        CourtRepositoryAdapter adapter = new CourtRepositoryAdapter(mongoRepository, mapper);
        Optional<Court> result = adapter.findByMatchId(matchId);

        assertTrue(result.isPresent());
        assertEquals(matchId, result.get().getMatchId());
    }
}
