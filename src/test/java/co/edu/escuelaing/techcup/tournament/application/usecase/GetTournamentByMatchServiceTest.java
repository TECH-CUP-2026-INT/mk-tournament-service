package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.exception.MatchupNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetTournamentByMatchServiceTest {

    @Test
    void getByMatch_cuandoExiste_retornaElTorneo() {
        UUID matchId = UUID.randomUUID();
        UUID tournamentId = UUID.randomUUID();
        Tournament tournament = new Tournament(tournamentId, "Copa ECI", TournamentStatus.IN_PROGRESS);
        TournamentRepositoryPort repository = mock(TournamentRepositoryPort.class);
        when(repository.findByMatchId(matchId)).thenReturn(Optional.of(tournament));

        GetTournamentByMatchService service = new GetTournamentByMatchService(repository);

        Tournament result = service.getByMatch(matchId);

        assertEquals(tournamentId, result.getId());
    }

    @Test
    void getByMatch_cuandoNoExiste_lanzaMatchupNotFoundException() {
        UUID matchId = UUID.randomUUID();
        TournamentRepositoryPort repository = mock(TournamentRepositoryPort.class);
        when(repository.findByMatchId(matchId)).thenReturn(Optional.empty());

        GetTournamentByMatchService service = new GetTournamentByMatchService(repository);

        assertThrows(MatchupNotFoundException.class, () -> service.getByMatch(matchId));
    }
}
