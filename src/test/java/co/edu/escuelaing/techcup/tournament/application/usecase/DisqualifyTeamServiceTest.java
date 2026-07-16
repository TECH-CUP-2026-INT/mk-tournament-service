package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.exception.TeamDisqualificationNotAllowedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.DisqualificationReason;
import co.edu.escuelaing.techcup.tournament.domain.model.RegistrationStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.TeamRegistration;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentFormat;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentType;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DisqualifyTeamServiceTest {

    private Tournament sampleTournament(UUID id, List<TeamRegistration> teams) {
        return Tournament.reconstruct(
                id, "Copa Enero", TournamentType.NORMAL, TournamentFormat.BRACKETS,
                8, BigDecimal.valueOf(50000),
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 20), LocalDate.of(2026, 2, 20),
                null, null, TournamentStatus.ACTIVE,
                teams, List.of(), null, null
        );
    }

    @Test
    void disqualify_equipoInscrito_descalificaYGuarda() {
        UUID id = UUID.randomUUID();
        UUID teamId = UUID.randomUUID();
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        Tournament tournament = sampleTournament(id, List.of(
                new TeamRegistration(teamId, "Equipo 1", RegistrationStatus.APPROVED)
        ));

        when(repositoryMock.findById(id)).thenReturn(Optional.of(tournament));
        when(repositoryMock.save(tournament)).thenReturn(tournament);

        DisqualifyTeamService service = new DisqualifyTeamService(repositoryMock);

        Tournament result = service.disqualify(id, teamId, DisqualificationReason.RULES_VIOLATION);

        assertEquals(RegistrationStatus.DISQUALIFIED, result.getTeams().get(0).getRegistrationStatus());
        verify(repositoryMock).save(tournament);
    }

    @Test
    void disqualify_torneoNoExiste_lanzaTournamentNotFoundException() {
        UUID id = UUID.randomUUID();
        UUID teamId = UUID.randomUUID();
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        when(repositoryMock.findById(id)).thenReturn(Optional.empty());

        DisqualifyTeamService service = new DisqualifyTeamService(repositoryMock);

        assertThrows(TournamentNotFoundException.class,
                () -> service.disqualify(id, teamId, DisqualificationReason.RULES_VIOLATION));
    }

    @Test
    void disqualify_equipoNoInscrito_lanzaTeamDisqualificationNotAllowedException() {
        UUID id = UUID.randomUUID();
        UUID teamId = UUID.randomUUID();
        UUID otherTeamId = UUID.randomUUID();
        TournamentRepositoryPort repositoryMock = mock(TournamentRepositoryPort.class);
        Tournament tournament = sampleTournament(id, List.of(
                new TeamRegistration(teamId, "Equipo 1", RegistrationStatus.APPROVED)
        ));
        when(repositoryMock.findById(id)).thenReturn(Optional.of(tournament));

        DisqualifyTeamService service = new DisqualifyTeamService(repositoryMock);

        assertThrows(TeamDisqualificationNotAllowedException.class,
                () -> service.disqualify(id, otherTeamId, DisqualificationReason.RULES_VIOLATION));
    }
}
