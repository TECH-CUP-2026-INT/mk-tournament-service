package co.edu.escuelaing.techcup.tournament.repository.adapter;

import co.edu.escuelaing.techcup.tournament.entity.document.TournamentDocument;
import co.edu.escuelaing.techcup.tournament.mapper.TournamentPersistenceMapper;
import co.edu.escuelaing.techcup.tournament.repository.mongo.TournamentMongoRepository;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.TournamentFormat;
import co.edu.escuelaing.techcup.tournament.service.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.service.TournamentType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TournamentRepositoryAdapterTest {

    private Tournament sampleTournament(String id) {
        return Tournament.reconstruct(id, "TechCup Fútbol 2026", TournamentType.NORMAL, TournamentFormat.BRACKETS,
                8, BigDecimal.valueOf(50000), LocalDate.now().plusDays(10), LocalDate.now().plusDays(20),
                LocalDate.now().plusDays(5), null, null, TournamentStatus.ACTIVE,
                new ArrayList<>(), new ArrayList<>(), null, null, false);
    }

    @Test
    void save_delegaAlMongoRepositoryYMapeaDeVuelta() {
        TournamentMongoRepository mongoRepository = mock(TournamentMongoRepository.class);
        Tournament tournament = sampleTournament("t1");
        TournamentDocument document = TournamentPersistenceMapper.toDocument(tournament);
        when(mongoRepository.save(any())).thenReturn(document);

        TournamentRepositoryAdapter adapter = new TournamentRepositoryAdapter(mongoRepository);
        Tournament result = adapter.save(tournament);

        assertEquals("t1", result.getId());
    }

    @Test
    void findById_cuandoExiste_retornaTournament() {
        TournamentMongoRepository mongoRepository = mock(TournamentMongoRepository.class);
        TournamentDocument document = TournamentPersistenceMapper.toDocument(sampleTournament("t1"));
        when(mongoRepository.findById("t1")).thenReturn(Optional.of(document));

        TournamentRepositoryAdapter adapter = new TournamentRepositoryAdapter(mongoRepository);

        assertTrue(adapter.findById("t1").isPresent());
    }

    @Test
    void findById_cuandoNoExiste_retornaVacio() {
        TournamentMongoRepository mongoRepository = mock(TournamentMongoRepository.class);
        when(mongoRepository.findById("missing")).thenReturn(Optional.empty());

        TournamentRepositoryAdapter adapter = new TournamentRepositoryAdapter(mongoRepository);

        assertFalse(adapter.findById("missing").isPresent());
    }

    @Test
    void deleteById_delegaAlMongoRepository() {
        TournamentMongoRepository mongoRepository = mock(TournamentMongoRepository.class);
        TournamentRepositoryAdapter adapter = new TournamentRepositoryAdapter(mongoRepository);

        adapter.deleteById("t1");

        verify(mongoRepository).deleteById("t1");
    }

    @Test
    void findAllByStatus_retornaTorneosConEseEstado() {
        TournamentMongoRepository mongoRepository = mock(TournamentMongoRepository.class);
        TournamentDocument document = TournamentPersistenceMapper.toDocument(sampleTournament("t1"));
        when(mongoRepository.findAllByStatus("ACTIVE")).thenReturn(List.of(document));

        TournamentRepositoryAdapter adapter = new TournamentRepositoryAdapter(mongoRepository);
        List<Tournament> result = adapter.findAllByStatus(TournamentStatus.ACTIVE);

        assertEquals(1, result.size());
    }

    @Test
    void findByIdAndStatus_cuandoExiste_retornaTournament() {
        TournamentMongoRepository mongoRepository = mock(TournamentMongoRepository.class);
        TournamentDocument document = TournamentPersistenceMapper.toDocument(sampleTournament("t1"));
        when(mongoRepository.findByIdAndStatus("t1", "ACTIVE")).thenReturn(Optional.of(document));

        TournamentRepositoryAdapter adapter = new TournamentRepositoryAdapter(mongoRepository);

        assertTrue(adapter.findByIdAndStatus("t1", TournamentStatus.ACTIVE).isPresent());
    }

    @Test
    void findByMatchId_cuandoExiste_retornaTournament() {
        TournamentMongoRepository mongoRepository = mock(TournamentMongoRepository.class);
        TournamentDocument document = TournamentPersistenceMapper.toDocument(sampleTournament("t1"));
        when(mongoRepository.findByMatchesMatchId("m1")).thenReturn(Optional.of(document));

        TournamentRepositoryAdapter adapter = new TournamentRepositoryAdapter(mongoRepository);

        assertTrue(adapter.findByMatchId("m1").isPresent());
    }

    @Test
    void findAllWithReservedEnrollments_retornaTorneosConReservas() {
        TournamentMongoRepository mongoRepository = mock(TournamentMongoRepository.class);
        TournamentDocument document = TournamentPersistenceMapper.toDocument(sampleTournament("t1"));
        when(mongoRepository.findByEnrollments_Status("RESERVED")).thenReturn(List.of(document));

        TournamentRepositoryAdapter adapter = new TournamentRepositoryAdapter(mongoRepository);
        List<Tournament> result = adapter.findAllWithReservedEnrollments();

        assertEquals(1, result.size());
    }
}
