package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CheckTeamActiveEnrollmentServiceTest {

    @Test
    void hasActiveEnrollment_cuandoElRepositorioConfirma_retornaTrue() {
        UUID teamId = UUID.randomUUID();
        TournamentRepositoryPort repository = mock(TournamentRepositoryPort.class);
        when(repository.existsActiveEnrollmentForTeam(teamId)).thenReturn(true);

        CheckTeamActiveEnrollmentService service = new CheckTeamActiveEnrollmentService(repository);

        assertTrue(service.hasActiveEnrollment(teamId));
    }

    @Test
    void hasActiveEnrollment_cuandoElRepositorioNiega_retornaFalse() {
        UUID teamId = UUID.randomUUID();
        TournamentRepositoryPort repository = mock(TournamentRepositoryPort.class);
        when(repository.existsActiveEnrollmentForTeam(teamId)).thenReturn(false);

        CheckTeamActiveEnrollmentService service = new CheckTeamActiveEnrollmentService(repository);

        assertFalse(service.hasActiveEnrollment(teamId));
    }
}
