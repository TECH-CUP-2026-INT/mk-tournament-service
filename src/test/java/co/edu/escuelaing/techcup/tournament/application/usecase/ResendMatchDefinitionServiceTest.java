package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.exception.MatchNotScheduledException;
import co.edu.escuelaing.techcup.tournament.domain.exception.MatchupNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.Match;
import co.edu.escuelaing.techcup.tournament.domain.model.MatchPhase;
import co.edu.escuelaing.techcup.tournament.domain.model.MatchStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.ScheduledMatch;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.MatchDefinitionPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.ScheduledMatchRepositoryPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ResendMatchDefinitionServiceTest {

    private final TournamentRepositoryPort tournamentRepository = mock(TournamentRepositoryPort.class);
    private final ScheduledMatchRepositoryPort scheduledMatchRepository = mock(ScheduledMatchRepositoryPort.class);
    private final MatchDefinitionPort matchDefinitionPort = mock(MatchDefinitionPort.class);
    private final ResendMatchDefinitionService service =
            new ResendMatchDefinitionService(tournamentRepository, scheduledMatchRepository, matchDefinitionPort);

    private final UUID matchId = UUID.randomUUID();
    private final UUID tournamentId = UUID.randomUUID();

    private Match sampleMatch() {
        return Match.builder().matchId(matchId).homeTeamId(UUID.randomUUID()).awayTeamId(UUID.randomUUID())
                .status(MatchStatus.PENDING).active(true).phase(MatchPhase.ELIMINATORIA).tournamentId(tournamentId)
                .definitionSyncPending(true).build();
    }

    private Tournament sampleTournament(Match match) {
        return Tournament.builder()
                .id(tournamentId).name("Copa Enero").numberOfTeams(8).cost(BigDecimal.valueOf(50000))
                .startDate(LocalDate.of(2026, Month.MARCH, 1)).endDate(LocalDate.of(2026, Month.MARCH, 20))
                .registrationDeadline(LocalDate.of(2026, Month.FEBRUARY, 20))
                .status(TournamentStatus.IN_PROGRESS).matches(List.of(match))
                .reconstruct();
    }

    @Test
    void resend_flujoFeliz_reenviaYLimpiaElFlagDePendiente() {
        Match match = sampleMatch();
        Tournament tournament = sampleTournament(match);
        ScheduledMatch scheduledMatch = ScheduledMatch.reconstruct(UUID.randomUUID(), matchId,
                UUID.randomUUID(), UUID.randomUUID(), LocalDate.of(2026, Month.AUGUST, 1), LocalTime.of(15, 0));

        when(tournamentRepository.findByMatchId(matchId)).thenReturn(Optional.of(tournament));
        when(scheduledMatchRepository.findByMatchupId(matchId)).thenReturn(Optional.of(scheduledMatch));

        service.resend(matchId);

        verify(matchDefinitionPort).sendDefinition(any());
        verify(tournamentRepository).save(tournament);
        assertFalse(match.isDefinitionSyncPending());
    }

    @Test
    void resend_torneoNoExiste_lanzaMatchupNotFoundException() {
        when(tournamentRepository.findByMatchId(matchId)).thenReturn(Optional.empty());

        assertThrows(MatchupNotFoundException.class, () -> service.resend(matchId));
    }

    @Test
    void resend_partidoNoProgramadoTodavia_lanzaMatchNotScheduledException() {
        Match match = sampleMatch();
        Tournament tournament = sampleTournament(match);
        when(tournamentRepository.findByMatchId(matchId)).thenReturn(Optional.of(tournament));
        when(scheduledMatchRepository.findByMatchupId(matchId)).thenReturn(Optional.empty());

        assertThrows(MatchNotScheduledException.class, () -> service.resend(matchId));
    }

    @Test
    void resend_fallaElEnvio_marcaPendienteDeNuevoYPropagaLaExcepcion() {
        Match match = sampleMatch();
        match.clearDefinitionSyncPending();
        Tournament tournament = sampleTournament(match);
        ScheduledMatch scheduledMatch = ScheduledMatch.reconstruct(UUID.randomUUID(), matchId,
                UUID.randomUUID(), UUID.randomUUID(), LocalDate.of(2026, Month.AUGUST, 1), LocalTime.of(15, 0));

        when(tournamentRepository.findByMatchId(matchId)).thenReturn(Optional.of(tournament));
        when(scheduledMatchRepository.findByMatchupId(matchId)).thenReturn(Optional.of(scheduledMatch));
        doThrow(new RuntimeException("Matches no disponible")).when(matchDefinitionPort).sendDefinition(any());

        assertThrows(RuntimeException.class, () -> service.resend(matchId));

        assertTrue(match.isDefinitionSyncPending());
        verify(tournamentRepository).save(tournament);
    }
}
