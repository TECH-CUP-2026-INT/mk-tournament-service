package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.exception.CourtNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.exception.MatchupNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.exception.ScheduleConflictException;
import co.edu.escuelaing.techcup.tournament.domain.model.Court;
import co.edu.escuelaing.techcup.tournament.domain.model.CourtSection;
import co.edu.escuelaing.techcup.tournament.domain.model.ScheduledMatch;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.CourtRepositoryPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ScheduleMatchUseCase.ScheduleMatchCommand;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.ScheduledMatchRepositoryPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ScheduleMatchServiceTest {

    private final UUID tournamentId = UUID.randomUUID();
    private final UUID matchupId = UUID.randomUUID();
    private final UUID courtId = UUID.randomUUID();
    private final UUID refereeId = UUID.randomUUID();

    private Tournament sampleTournament() {
        return Tournament.builder()
                .id(tournamentId).name("Copa Enero").numberOfTeams(8).cost(BigDecimal.valueOf(50000))
                .startDate(LocalDate.of(2026, Month.MARCH, 1)).endDate(LocalDate.of(2026, Month.MARCH, 20))
                .registrationDeadline(LocalDate.of(2026, Month.FEBRUARY, 20))
                .status(TournamentStatus.IN_PREPARATION)
                .reconstruct();
    }

    private Court sampleCourt() {
        return Court.create(tournamentId, CourtSection.CANCHA_1, "Cancha techada");
    }

    private ScheduleMatchCommand sampleCommand() {
        return new ScheduleMatchCommand(
                matchupId, LocalDate.of(2026, Month.AUGUST, 1), LocalTime.of(15, 0), courtId, refereeId);
    }

    @Test
    void schedule_flujoFeliz_guardaYAsignaCancha() {
        TournamentRepositoryPort tournamentRepository = mock(TournamentRepositoryPort.class);
        CourtRepositoryPort courtRepository = mock(CourtRepositoryPort.class);
        ScheduledMatchRepositoryPort scheduledMatchRepository = mock(ScheduledMatchRepositoryPort.class);
        Court court = sampleCourt();

        when(tournamentRepository.findByMatchId(matchupId)).thenReturn(Optional.of(sampleTournament()));
        when(courtRepository.findById(courtId)).thenReturn(Optional.of(court));
        when(scheduledMatchRepository.existsConflict(eq(courtId), eq(refereeId), any(), any())).thenReturn(false);
        when(scheduledMatchRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(courtRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ScheduleMatchService service = new ScheduleMatchService(tournamentRepository, courtRepository, scheduledMatchRepository);

        ScheduledMatch result = service.schedule(sampleCommand());

        assertEquals(matchupId, result.getMatchupId());
        assertEquals(courtId, result.getCourtId());
        assertEquals(refereeId, result.getRefereeId());
        assertEquals(matchupId, court.getMatchId());
        verify(courtRepository).save(court);
    }

    @Test
    void schedule_matchupNoExiste_lanzaMatchupNotFoundExceptionYNoLlamaNadaMas() {
        TournamentRepositoryPort tournamentRepository = mock(TournamentRepositoryPort.class);
        CourtRepositoryPort courtRepository = mock(CourtRepositoryPort.class);
        ScheduledMatchRepositoryPort scheduledMatchRepository = mock(ScheduledMatchRepositoryPort.class);

        when(tournamentRepository.findByMatchId(matchupId)).thenReturn(Optional.empty());

        ScheduleMatchService service = new ScheduleMatchService(tournamentRepository, courtRepository, scheduledMatchRepository);
        ScheduleMatchCommand command = sampleCommand();

        assertThrows(MatchupNotFoundException.class, () -> service.schedule(command));

        verify(courtRepository, never()).findById(any());
        verify(scheduledMatchRepository, never()).save(any());
    }

    @Test
    void schedule_canchaNoExiste_lanzaCourtNotFoundException() {
        TournamentRepositoryPort tournamentRepository = mock(TournamentRepositoryPort.class);
        CourtRepositoryPort courtRepository = mock(CourtRepositoryPort.class);
        ScheduledMatchRepositoryPort scheduledMatchRepository = mock(ScheduledMatchRepositoryPort.class);

        when(tournamentRepository.findByMatchId(matchupId)).thenReturn(Optional.of(sampleTournament()));
        when(courtRepository.findById(courtId)).thenReturn(Optional.empty());

        ScheduleMatchService service = new ScheduleMatchService(tournamentRepository, courtRepository, scheduledMatchRepository);
        ScheduleMatchCommand command = sampleCommand();

        assertThrows(CourtNotFoundException.class, () -> service.schedule(command));

        verify(scheduledMatchRepository, never()).save(any());
    }

    @Test
    void schedule_conflictoDeDisponibilidad_lanzaScheduleConflictExceptionYNuncaGuarda() {
        TournamentRepositoryPort tournamentRepository = mock(TournamentRepositoryPort.class);
        CourtRepositoryPort courtRepository = mock(CourtRepositoryPort.class);
        ScheduledMatchRepositoryPort scheduledMatchRepository = mock(ScheduledMatchRepositoryPort.class);

        when(tournamentRepository.findByMatchId(matchupId)).thenReturn(Optional.of(sampleTournament()));
        when(courtRepository.findById(courtId)).thenReturn(Optional.of(sampleCourt()));
        when(scheduledMatchRepository.existsConflict(eq(courtId), eq(refereeId), any(), any())).thenReturn(true);

        ScheduleMatchService service = new ScheduleMatchService(tournamentRepository, courtRepository, scheduledMatchRepository);
        ScheduleMatchCommand command = sampleCommand();

        assertThrows(ScheduleConflictException.class, () -> service.schedule(command));

        verify(scheduledMatchRepository, never()).save(any());
        verify(courtRepository, never()).save(any());
    }
}
