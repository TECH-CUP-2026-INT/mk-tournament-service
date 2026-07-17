package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentInactiveException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.BracketNode;
import co.edu.escuelaing.techcup.tournament.domain.model.Round;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ViewEliminationBracketServiceTest {

    private Tournament sampleTournament(UUID id) {
        return Tournament.builder()
                .id(id).name("Copa Enero").numberOfTeams(8).cost(BigDecimal.valueOf(50000))
                .startDate(LocalDate.of(2026, Month.MARCH, 1)).endDate(LocalDate.of(2026, Month.MARCH, 20))
                .registrationDeadline(LocalDate.of(2026, Month.FEBRUARY, 20))
                .status(TournamentStatus.IN_PROGRESS).teams(List.of()).matches(List.of())
                .reconstruct();
    }

    @Test
    void getBracket_torneoNoExiste_lanzaTournamentNotFoundException() {
        UUID id = UUID.randomUUID();
        TournamentRepositoryPort repository = mock(TournamentRepositoryPort.class);
        when(repository.findById(id)).thenReturn(Optional.empty());

        ViewEliminationBracketService service = new ViewEliminationBracketService(repository);

        assertThrows(TournamentNotFoundException.class, () -> service.getBracket(id));
    }

    @Test
    void getBracket_torneoInactivo_lanzaTournamentInactiveException() {
        UUID id = UUID.randomUUID();
        TournamentRepositoryPort repository = mock(TournamentRepositoryPort.class);
        Tournament tournament = sampleTournament(id);
        tournament.inactivate();
        when(repository.findById(id)).thenReturn(Optional.of(tournament));

        ViewEliminationBracketService service = new ViewEliminationBracketService(repository);

        assertThrows(TournamentInactiveException.class, () -> service.getBracket(id));
    }

    @Test
    void getBracket_torneoConLlave_devuelveSusNodos() {
        UUID id = UUID.randomUUID();
        TournamentRepositoryPort repository = mock(TournamentRepositoryPort.class);
        Tournament tournament = sampleTournament(id);
        BracketNode node = BracketNode.builder().nodeId(UUID.randomUUID()).round(Round.FINAL).build();
        tournament.setBracketNodes(List.of(node));
        when(repository.findById(id)).thenReturn(Optional.of(tournament));

        ViewEliminationBracketService service = new ViewEliminationBracketService(repository);
        List<BracketNode> result = service.getBracket(id);

        assertEquals(1, result.size());
        assertEquals(node.getNodeId(), result.get(0).getNodeId());
    }
}
