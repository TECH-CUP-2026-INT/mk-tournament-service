package co.edu.escuelaing.techcup.tournament.service.impl;

import co.edu.escuelaing.techcup.tournament.exception.CourtNotFoundException;
import co.edu.escuelaing.techcup.tournament.exception.MatchupNotFoundException;
import co.edu.escuelaing.techcup.tournament.exception.ScheduleConflictException;
import co.edu.escuelaing.techcup.tournament.service.Court;
import co.edu.escuelaing.techcup.tournament.service.CourtSection;
import co.edu.escuelaing.techcup.tournament.service.ScheduledMatch;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.service.ports.CourtRepositoryPort;
import co.edu.escuelaing.techcup.tournament.service.ports.ScheduleMatchUseCase.ScheduleMatchCommand;
import co.edu.escuelaing.techcup.tournament.service.ports.ScheduledMatchRepositoryPort;
import co.edu.escuelaing.techcup.tournament.service.ports.TournamentRepositoryPort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ScheduleMatchServiceTest {

    private Tournament sampleTournament() {
        return Tournament.reconstruct(
                "t1", "Copa Enero", 8, BigDecimal.valueOf(50000),
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 20), LocalDate.of(2026, 2, 20),
                TournamentStatus.IN_PREPARATION
        );
    }

    private Court sampleCourt() {
        return Court.create("t1", CourtSection.CANCHA_1, "Cancha techada");
    }

    private ScheduleMatchCommand sampleCommand() {
        return new ScheduleMatchCommand(
                "matchup-1", LocalDate.of(2026, 8, 1), LocalTime.of(15, 0), "court-1", "referee-1");
    }

    @Test
    void schedule_flujoFeliz_guardaYAsignaCancha() {
        TournamentRepositoryPort tournamentRepository = mock(TournamentRepositoryPort.class);
        CourtRepositoryPort courtRepository = mock(CourtRepositoryPort.class);
        ScheduledMatchRepositoryPort scheduledMatchRepository = mock(ScheduledMatchRepositoryPort.class);
        Court court = sampleCourt();

        when(tournamentRepository.findByMatchId("matchup-1")).thenReturn(Optional.of(sampleTournament()));
        when(courtRepository.findById("court-1")).thenReturn(Optional.of(court));
        when(scheduledMatchRepository.existsConflict(eq("court-1"), eq("referee-1"), any(), any())).thenReturn(false);
        when(scheduledMatchRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(courtRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ScheduleMatchService service = new ScheduleMatchService(tournamentRepository, courtRepository, scheduledMatchRepository);

        ScheduledMatch result = service.schedule(sampleCommand());

        assertEquals("matchup-1", result.getMatchupId());
        assertEquals("court-1", result.getCourtId());
        assertEquals("referee-1", result.getRefereeId());
        assertEquals("matchup-1", court.getMatchId());
        verify(courtRepository).save(court);
    }

    @Test
    void schedule_matchupNoExiste_lanzaMatchupNotFoundExceptionYNoLlamaNadaMas() {
        TournamentRepositoryPort tournamentRepository = mock(TournamentRepositoryPort.class);
        CourtRepositoryPort courtRepository = mock(CourtRepositoryPort.class);
        ScheduledMatchRepositoryPort scheduledMatchRepository = mock(ScheduledMatchRepositoryPort.class);

        when(tournamentRepository.findByMatchId("matchup-1")).thenReturn(Optional.empty());

        ScheduleMatchService service = new ScheduleMatchService(tournamentRepository, courtRepository, scheduledMatchRepository);

        assertThrows(MatchupNotFoundException.class, () -> service.schedule(sampleCommand()));

        verify(courtRepository, never()).findById(any());
        verify(scheduledMatchRepository, never()).save(any());
    }

    @Test
    void schedule_canchaNoExiste_lanzaCourtNotFoundException() {
        TournamentRepositoryPort tournamentRepository = mock(TournamentRepositoryPort.class);
        CourtRepositoryPort courtRepository = mock(CourtRepositoryPort.class);
        ScheduledMatchRepositoryPort scheduledMatchRepository = mock(ScheduledMatchRepositoryPort.class);

        when(tournamentRepository.findByMatchId("matchup-1")).thenReturn(Optional.of(sampleTournament()));
        when(courtRepository.findById("court-1")).thenReturn(Optional.empty());

        ScheduleMatchService service = new ScheduleMatchService(tournamentRepository, courtRepository, scheduledMatchRepository);

        assertThrows(CourtNotFoundException.class, () -> service.schedule(sampleCommand()));

        verify(scheduledMatchRepository, never()).save(any());
    }

    @Test
    void schedule_conflictoDeDisponibilidad_lanzaScheduleConflictExceptionYNuncaGuarda() {
        TournamentRepositoryPort tournamentRepository = mock(TournamentRepositoryPort.class);
        CourtRepositoryPort courtRepository = mock(CourtRepositoryPort.class);
        ScheduledMatchRepositoryPort scheduledMatchRepository = mock(ScheduledMatchRepositoryPort.class);

        when(tournamentRepository.findByMatchId("matchup-1")).thenReturn(Optional.of(sampleTournament()));
        when(courtRepository.findById("court-1")).thenReturn(Optional.of(sampleCourt()));
        when(scheduledMatchRepository.existsConflict(eq("court-1"), eq("referee-1"), any(), any())).thenReturn(true);

        ScheduleMatchService service = new ScheduleMatchService(tournamentRepository, courtRepository, scheduledMatchRepository);

        assertThrows(ScheduleConflictException.class, () -> service.schedule(sampleCommand()));

        verify(scheduledMatchRepository, never()).save(any());
        verify(courtRepository, never()).save(any());
    }
}
