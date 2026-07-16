package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.adapter;

import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo.TournamentDocument;
import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mapper.TournamentPersistenceMapper;
import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo.TournamentMongoRepository;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentFormat;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentType;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TournamentRepositoryAdapterTest {

    private final TournamentPersistenceMapper mapper = Mappers.getMapper(TournamentPersistenceMapper.class);

    private Tournament sampleTournament(UUID id) {
        return Tournament.reconstruct(id, "TechCup Fútbol 2026", TournamentType.NORMAL, TournamentFormat.BRACKETS,
                8, BigDecimal.valueOf(50000), LocalDate.now().plusDays(10), LocalDate.now().plusDays(20),
                LocalDate.now().plusDays(5), null, null, TournamentStatus.ACTIVE,
                new ArrayList<>(), new ArrayList<>(), null, null, false);
    }

    @Test
    void save_delegaAlMongoRepositoryYMapeaDeVuelta() {
        UUID id = UUID.randomUUID();
        TournamentMongoRepository mongoRepository = mock(TournamentMongoRepository.class);
        Tournament tournament = sampleTournament(id);
        TournamentDocument document = mapper.toDocument(tournament);
        when(mongoRepository.save(any())).thenReturn(document);

        TournamentRepositoryAdapter adapter = new TournamentRepositoryAdapter(mongoRepository, mapper);
        Tournament result = adapter.save(tournament);

        assertEquals(id, result.getId());
    }

    @Test
    void findById_cuandoExiste_retornaTournament() {
        UUID id = UUID.randomUUID();
        TournamentMongoRepository mongoRepository = mock(TournamentMongoRepository.class);
        TournamentDocument document = mapper.toDocument(sampleTournament(id));
        when(mongoRepository.findById(id)).thenReturn(Optional.of(document));

        TournamentRepositoryAdapter adapter = new TournamentRepositoryAdapter(mongoRepository, mapper);

        assertTrue(adapter.findById(id).isPresent());
    }

    @Test
    void findById_cuandoNoExiste_retornaVacio() {
        UUID missing = UUID.randomUUID();
        TournamentMongoRepository mongoRepository = mock(TournamentMongoRepository.class);
        when(mongoRepository.findById(missing)).thenReturn(Optional.empty());

        TournamentRepositoryAdapter adapter = new TournamentRepositoryAdapter(mongoRepository, mapper);

        assertFalse(adapter.findById(missing).isPresent());
    }

    @Test
    void deleteById_delegaAlMongoRepository() {
        UUID id = UUID.randomUUID();
        TournamentMongoRepository mongoRepository = mock(TournamentMongoRepository.class);
        TournamentRepositoryAdapter adapter = new TournamentRepositoryAdapter(mongoRepository, mapper);

        adapter.deleteById(id);

        verify(mongoRepository).deleteById(id);
    }

    @Test
    void findAllByStatus_retornaTorneosConEseEstado() {
        UUID id = UUID.randomUUID();
        TournamentMongoRepository mongoRepository = mock(TournamentMongoRepository.class);
        TournamentDocument document = mapper.toDocument(sampleTournament(id));
        when(mongoRepository.findAllByStatus("ACTIVE")).thenReturn(List.of(document));

        TournamentRepositoryAdapter adapter = new TournamentRepositoryAdapter(mongoRepository, mapper);
        List<Tournament> result = adapter.findAllByStatus(TournamentStatus.ACTIVE);

        assertEquals(1, result.size());
    }

    @Test
    void findByIdAndStatus_cuandoExiste_retornaTournament() {
        UUID id = UUID.randomUUID();
        TournamentMongoRepository mongoRepository = mock(TournamentMongoRepository.class);
        TournamentDocument document = mapper.toDocument(sampleTournament(id));
        when(mongoRepository.findByIdAndStatus(id, "ACTIVE")).thenReturn(Optional.of(document));

        TournamentRepositoryAdapter adapter = new TournamentRepositoryAdapter(mongoRepository, mapper);

        assertTrue(adapter.findByIdAndStatus(id, TournamentStatus.ACTIVE).isPresent());
    }

    @Test
    void findByMatchId_cuandoExiste_retornaTournament() {
        UUID id = UUID.randomUUID();
        UUID matchId = UUID.randomUUID();
        TournamentMongoRepository mongoRepository = mock(TournamentMongoRepository.class);
        TournamentDocument document = mapper.toDocument(sampleTournament(id));
        when(mongoRepository.findByMatchesMatchId(matchId)).thenReturn(Optional.of(document));

        TournamentRepositoryAdapter adapter = new TournamentRepositoryAdapter(mongoRepository, mapper);

        assertTrue(adapter.findByMatchId(matchId).isPresent());
    }

    @Test
    void findAllWithReservedEnrollments_retornaTorneosConReservas() {
        UUID id = UUID.randomUUID();
        TournamentMongoRepository mongoRepository = mock(TournamentMongoRepository.class);
        TournamentDocument document = mapper.toDocument(sampleTournament(id));
        when(mongoRepository.findByEnrollments_Status("RESERVED")).thenReturn(List.of(document));

        TournamentRepositoryAdapter adapter = new TournamentRepositoryAdapter(mongoRepository, mapper);
        List<Tournament> result = adapter.findAllWithReservedEnrollments();

        assertEquals(1, result.size());
    }
}
