package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.adapter;

import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo.TournamentParticipantDocument;
import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo.TournamentParticipantMongoRepository;
import co.edu.escuelaing.techcup.tournament.domain.model.ParticipantStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentParticipant;
import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mapper.TournamentParticipantPersistenceMapper;
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

class TournamentParticipantRepositoryAdapterTest {

    private final TournamentParticipantPersistenceMapper mapper =
            Mappers.getMapper(TournamentParticipantPersistenceMapper.class);

    private final UUID participantId = UUID.randomUUID();
    private final UUID tournamentId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    @Test
    void save_delegaAlMongoRepositoryYMapeaDeVuelta() {
        TournamentParticipantMongoRepository mongoRepository = mock(TournamentParticipantMongoRepository.class);
        TournamentParticipant participant = TournamentParticipant.create(tournamentId, userId);
        TournamentParticipantDocument saved = new TournamentParticipantDocument(participantId, tournamentId, userId, "ACTIVE");
        when(mongoRepository.save(any())).thenReturn(saved);

        TournamentParticipantRepositoryAdapter adapter = new TournamentParticipantRepositoryAdapter(mongoRepository, mapper);
        TournamentParticipant result = adapter.save(participant);

        assertEquals(participantId, result.getId());
        assertEquals(ParticipantStatus.ACTIVE, result.getStatus());
    }

    @Test
    void findByTournamentIdAndUserId_cuandoExiste_retornaParticipant() {
        TournamentParticipantMongoRepository mongoRepository = mock(TournamentParticipantMongoRepository.class);
        TournamentParticipantDocument document = new TournamentParticipantDocument(participantId, tournamentId, userId, "INACTIVE");
        when(mongoRepository.findByTournamentIdAndUserId(tournamentId, userId)).thenReturn(Optional.of(document));

        TournamentParticipantRepositoryAdapter adapter = new TournamentParticipantRepositoryAdapter(mongoRepository, mapper);
        Optional<TournamentParticipant> result = adapter.findByTournamentIdAndUserId(tournamentId, userId);

        assertTrue(result.isPresent());
        assertEquals(ParticipantStatus.INACTIVE, result.get().getStatus());
    }

    @Test
    void findByTournamentIdAndUserId_cuandoNoExiste_retornaVacio() {
        UUID missingUser = UUID.randomUUID();
        TournamentParticipantMongoRepository mongoRepository = mock(TournamentParticipantMongoRepository.class);
        when(mongoRepository.findByTournamentIdAndUserId(tournamentId, missingUser)).thenReturn(Optional.empty());

        TournamentParticipantRepositoryAdapter adapter = new TournamentParticipantRepositoryAdapter(mongoRepository, mapper);

        assertFalse(adapter.findByTournamentIdAndUserId(tournamentId, missingUser).isPresent());
    }
}
