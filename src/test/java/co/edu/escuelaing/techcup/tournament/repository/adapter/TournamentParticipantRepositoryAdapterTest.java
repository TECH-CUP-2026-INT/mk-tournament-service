package co.edu.escuelaing.techcup.tournament.repository.adapter;

import co.edu.escuelaing.techcup.tournament.entity.document.TournamentParticipantDocument;
import co.edu.escuelaing.techcup.tournament.repository.mongo.TournamentParticipantMongoRepository;
import co.edu.escuelaing.techcup.tournament.service.ParticipantStatus;
import co.edu.escuelaing.techcup.tournament.service.TournamentParticipant;
import co.edu.escuelaing.techcup.tournament.mapper.TournamentParticipantPersistenceMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TournamentParticipantRepositoryAdapterTest {

    private final TournamentParticipantPersistenceMapper mapper =
            Mappers.getMapper(TournamentParticipantPersistenceMapper.class);

    @Test
    void save_delegaAlMongoRepositoryYMapeaDeVuelta() {
        TournamentParticipantMongoRepository mongoRepository = mock(TournamentParticipantMongoRepository.class);
        TournamentParticipant participant = TournamentParticipant.create("t1", "user1");
        TournamentParticipantDocument saved = new TournamentParticipantDocument("p1", "t1", "user1", "ACTIVE");
        when(mongoRepository.save(any())).thenReturn(saved);

        TournamentParticipantRepositoryAdapter adapter = new TournamentParticipantRepositoryAdapter(mongoRepository, mapper);
        TournamentParticipant result = adapter.save(participant);

        assertEquals("p1", result.getId());
        assertEquals(ParticipantStatus.ACTIVE, result.getStatus());
    }

    @Test
    void findByTournamentIdAndUserId_cuandoExiste_retornaParticipant() {
        TournamentParticipantMongoRepository mongoRepository = mock(TournamentParticipantMongoRepository.class);
        TournamentParticipantDocument document = new TournamentParticipantDocument("p1", "t1", "user1", "INACTIVE");
        when(mongoRepository.findByTournamentIdAndUserId("t1", "user1")).thenReturn(Optional.of(document));

        TournamentParticipantRepositoryAdapter adapter = new TournamentParticipantRepositoryAdapter(mongoRepository, mapper);
        Optional<TournamentParticipant> result = adapter.findByTournamentIdAndUserId("t1", "user1");

        assertTrue(result.isPresent());
        assertEquals(ParticipantStatus.INACTIVE, result.get().getStatus());
    }

    @Test
    void findByTournamentIdAndUserId_cuandoNoExiste_retornaVacio() {
        TournamentParticipantMongoRepository mongoRepository = mock(TournamentParticipantMongoRepository.class);
        when(mongoRepository.findByTournamentIdAndUserId("t1", "missing")).thenReturn(Optional.empty());

        TournamentParticipantRepositoryAdapter adapter = new TournamentParticipantRepositoryAdapter(mongoRepository, mapper);

        assertFalse(adapter.findByTournamentIdAndUserId("t1", "missing").isPresent());
    }
}
