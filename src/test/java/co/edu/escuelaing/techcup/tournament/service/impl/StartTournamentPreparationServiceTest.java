package co.edu.escuelaing.techcup.tournament.service.impl;

import co.edu.escuelaing.techcup.tournament.exception.FixtureGenerationFailedException;
import co.edu.escuelaing.techcup.tournament.exception.InsufficientApprovedTeamsException;
import co.edu.escuelaing.techcup.tournament.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.service.Enrollment;
import co.edu.escuelaing.techcup.tournament.service.EnrollmentStatus;
import co.edu.escuelaing.techcup.tournament.service.Match;
import co.edu.escuelaing.techcup.tournament.service.MatchStatus;
import co.edu.escuelaing.techcup.tournament.service.RegistrationStatus;
import co.edu.escuelaing.techcup.tournament.service.TeamRegistration;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.TournamentFormat;
import co.edu.escuelaing.techcup.tournament.service.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.service.TournamentType;
import co.edu.escuelaing.techcup.tournament.service.ports.FixtureGenerationPort;
import co.edu.escuelaing.techcup.tournament.service.ports.TournamentRepositoryPort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StartTournamentPreparationServiceTest {

    private Tournament buildTournament(TournamentStatus status, int approvedCount) {
        List<TeamRegistration> teams = new ArrayList<>();
        List<Enrollment> enrollments = new ArrayList<>();
        for (int i = 0; i < approvedCount; i++) {
            teams.add(new TeamRegistration("e" + i, "Equipo " + i, RegistrationStatus.APPROVED));
            enrollments.add(new Enrollment("e" + i, "Equipo " + i, EnrollmentStatus.ENROLLED));
        }
        Tournament tournament = Tournament.reconstruct(
                "t1", "TechCup", TournamentType.NORMAL, TournamentFormat.BRACKETS, 8, BigDecimal.ZERO,
                LocalDate.now().plusDays(2), LocalDate.now().plusDays(10), LocalDate.now(),
                null, null, status, teams, new ArrayList<>(), null, null
        );
        tournament.setEnrollments(enrollments);
        return tournament;
    }

    @Test
    void startPreparation_flujoFeliz_generaMatchesYGuardaEnInPreparation() {
        TournamentRepositoryPort repository = mock(TournamentRepositoryPort.class);
        FixtureGenerationPort fixturePort = mock(FixtureGenerationPort.class);
        Tournament tournament = buildTournament(TournamentStatus.ACTIVE, 3);
        List<Match> generated = List.of(new Match("m1", "e0", "e1", MatchStatus.PENDING));

        when(repository.findById("t1")).thenReturn(Optional.of(tournament));
        when(fixturePort.generateFixture(any())).thenReturn(generated);
        when(repository.save(tournament)).thenReturn(tournament);

        StartTournamentPreparationService service =
                new StartTournamentPreparationService(repository, fixturePort);

        Tournament result = service.startPreparation("t1");

        assertEquals(TournamentStatus.IN_PREPARATION, result.getStatus());
        assertEquals(1, result.getMatches().size());
        verify(repository).save(tournament);
    }

    @Test
    void startPreparation_torneoNoExiste_lanzaTournamentNotFoundException() {
        TournamentRepositoryPort repository = mock(TournamentRepositoryPort.class);
        FixtureGenerationPort fixturePort = mock(FixtureGenerationPort.class);

        when(repository.findById("t99")).thenReturn(Optional.empty());

        StartTournamentPreparationService service =
                new StartTournamentPreparationService(repository, fixturePort);

        assertThrows(TournamentNotFoundException.class, () -> service.startPreparation("t99"));
    }

    @Test
    void startPreparation_menosDeTresAprobados_noLlamaAlPortNiGuarda() {
        TournamentRepositoryPort repository = mock(TournamentRepositoryPort.class);
        FixtureGenerationPort fixturePort = mock(FixtureGenerationPort.class);
        Tournament tournament = buildTournament(TournamentStatus.ACTIVE, 2);

        when(repository.findById("t1")).thenReturn(Optional.of(tournament));

        StartTournamentPreparationService service =
                new StartTournamentPreparationService(repository, fixturePort);

        assertThrows(InsufficientApprovedTeamsException.class, () -> service.startPreparation("t1"));

        verify(fixturePort, never()).generateFixture(any());
        verify(repository, never()).save(any());
    }

    @Test
    void startPreparation_fallaApiExterna_lanzaFixtureGenerationFailedYNoGuardaNada() {
        TournamentRepositoryPort repository = mock(TournamentRepositoryPort.class);
        FixtureGenerationPort fixturePort = mock(FixtureGenerationPort.class);
        Tournament tournament = buildTournament(TournamentStatus.ACTIVE, 3);

        when(repository.findById("t1")).thenReturn(Optional.of(tournament));
        when(fixturePort.generateFixture(any())).thenThrow(new RuntimeException("timeout"));

        StartTournamentPreparationService service =
                new StartTournamentPreparationService(repository, fixturePort);

        assertThrows(FixtureGenerationFailedException.class, () -> service.startPreparation("t1"));

        assertEquals(TournamentStatus.ACTIVE, tournament.getStatus());
        verify(repository, never()).save(any());
    }
}
