package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetActiveTournamentServiceTest {

    private Tournament buildTournament(UUID id, LocalDate startDate) {
        return Tournament.builder()
                .id(id).name("Copa").numberOfTeams(8).cost(BigDecimal.ZERO)
                .startDate(startDate).endDate(startDate.plusDays(20))
                .registrationDeadline(startDate.minusDays(5))
                .status(TournamentStatus.IN_PROGRESS).teams(new ArrayList<>()).matches(new ArrayList<>())
                .reconstruct();
    }

    @Test
    void getActiveTournament_conUnTorneoEnCurso_loRetorna() {
        UUID id = UUID.randomUUID();
        Tournament tournament = buildTournament(id, LocalDate.now());
        TournamentRepositoryPort repository = mock(TournamentRepositoryPort.class);
        when(repository.findAllByStatus(TournamentStatus.IN_PROGRESS)).thenReturn(List.of(tournament));

        GetActiveTournamentService service = new GetActiveTournamentService(repository);

        Tournament result = service.getActiveTournament();

        assertEquals(id, result.getId());
    }

    @Test
    void getActiveTournament_conVariosEnCurso_eligeElDeFechaDeInicioMasReciente() {
        Tournament older = buildTournament(UUID.randomUUID(), LocalDate.now().minusDays(10));
        Tournament newer = buildTournament(UUID.randomUUID(), LocalDate.now().minusDays(1));
        TournamentRepositoryPort repository = mock(TournamentRepositoryPort.class);
        when(repository.findAllByStatus(TournamentStatus.IN_PROGRESS)).thenReturn(List.of(older, newer));

        GetActiveTournamentService service = new GetActiveTournamentService(repository);

        Tournament result = service.getActiveTournament();

        assertEquals(newer.getId(), result.getId());
    }

    @Test
    void getActiveTournament_empateDeFecha_esDeterministaPorIdSinImportarElOrden() {
        LocalDate sameDate = LocalDate.now().minusDays(3);
        Tournament a = buildTournament(UUID.fromString("00000000-0000-0000-0000-000000000001"), sameDate);
        Tournament b = buildTournament(UUID.fromString("00000000-0000-0000-0000-000000000002"), sameDate);
        TournamentRepositoryPort repository = mock(TournamentRepositoryPort.class);
        GetActiveTournamentService service = new GetActiveTournamentService(repository);

        when(repository.findAllByStatus(TournamentStatus.IN_PROGRESS)).thenReturn(List.of(a, b));
        Tournament resultAB = service.getActiveTournament();
        when(repository.findAllByStatus(TournamentStatus.IN_PROGRESS)).thenReturn(List.of(b, a));
        Tournament resultBA = service.getActiveTournament();

        assertEquals(b.getId(), resultAB.getId(), "empate exacto de fecha: gana el id mayor de forma determinista");
        assertEquals(resultAB.getId(), resultBA.getId(), "el orden de entrada no debe cambiar el resultado");
    }

    @Test
    void getActiveTournament_sinNingunoEnCurso_lanzaTournamentNotFoundException() {
        TournamentRepositoryPort repository = mock(TournamentRepositoryPort.class);
        when(repository.findAllByStatus(TournamentStatus.IN_PROGRESS)).thenReturn(List.of());

        GetActiveTournamentService service = new GetActiveTournamentService(repository);

        assertThrows(TournamentNotFoundException.class, service::getActiveTournament);
    }
}
