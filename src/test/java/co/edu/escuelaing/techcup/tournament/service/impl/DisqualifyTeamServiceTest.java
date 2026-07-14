package co.edu.escuelaing.techcup.tournament.service.impl;

import co.edu.escuelaing.techcup.tournament.exception.TeamDisqualificationNotAllowedException;
import co.edu.escuelaing.techcup.tournament.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.service.DisqualificationReason;
import co.edu.escuelaing.techcup.tournament.service.RegistrationStatus;
import co.edu.escuelaing.techcup.tournament.service.TeamRegistration;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.TournamentFormat;
import co.edu.escuelaing.techcup.tournament.service.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.service.TournamentType;
import co.edu.escuelaing.techcup.tournament.service.ports.TournamentRepositoryPort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DisqualifyTeamServiceTest {

    private Tournament sampleTournament(List<TeamRegistration> teams) {
        return Tournament.reconstruct(
                "1", "Copa Enero", TournamentType.NORMAL, TournamentFormat.BRACKETS,
                8, BigDecimal.valueOf(50000),
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 20), LocalDate.of(2026, 2, 20),
                null, null, TournamentStatus.ACTIVE,
                teams, List.of(), null, null
        );
    }

    @Test
    void disqualify_equipoInscrito_descalificaYGuarda() {
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        Tournament tournament = sampleTournament(List.of(
                new TeamRegistration("e1", "Equipo 1", RegistrationStatus.APPROVED)
        ));

        when(repositoryMock.findById("1")).thenReturn(Optional.of(tournament));
        when(repositoryMock.save(tournament)).thenReturn(tournament);

        DisqualifyTeamService service = new DisqualifyTeamService(repositoryMock);

        Tournament result = service.disqualify("1", "e1", DisqualificationReason.RULES_VIOLATION);

        assertEquals(RegistrationStatus.DISQUALIFIED, result.getTeams().get(0).getRegistrationStatus());
        verify(repositoryMock).save(tournament);
    }

    @Test
    void disqualify_torneoNoExiste_lanzaTournamentNotFoundException() {
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        when(repositoryMock.findById("99")).thenReturn(Optional.empty());

        DisqualifyTeamService service = new DisqualifyTeamService(repositoryMock);

        assertThrows(TournamentNotFoundException.class,
                () -> service.disqualify("99", "e1", DisqualificationReason.RULES_VIOLATION));
    }

    @Test
    void disqualify_equipoNoInscrito_lanzaTeamDisqualificationNotAllowedException() {
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        Tournament tournament = sampleTournament(List.of(
                new TeamRegistration("e1", "Equipo 1", RegistrationStatus.APPROVED)
        ));
        when(repositoryMock.findById("1")).thenReturn(Optional.of(tournament));

        DisqualifyTeamService service = new DisqualifyTeamService(repositoryMock);

        assertThrows(TeamDisqualificationNotAllowedException.class,
                () -> service.disqualify("1", "e2", DisqualificationReason.RULES_VIOLATION));
    }
}
