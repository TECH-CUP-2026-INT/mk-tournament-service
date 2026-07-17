package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.exception.GroupStageNotCompleteException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.Match;
import co.edu.escuelaing.techcup.tournament.domain.model.MatchStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GenerateEliminationBracketServiceTest {

    private Tournament sampleTournament(UUID id, List<Match> matches) {
        return Tournament.builder()
                .id(id).name("Copa Enero").numberOfTeams(8).cost(BigDecimal.valueOf(50000))
                .startDate(LocalDate.of(2026, Month.MARCH, 1)).endDate(LocalDate.of(2026, Month.MARCH, 20))
                .registrationDeadline(LocalDate.of(2026, Month.FEBRUARY, 20))
                .status(TournamentStatus.IN_PROGRESS).teams(List.of()).matches(new ArrayList<>(matches))
                .reconstruct();
    }

    @Test
    void generate_torneoNoExiste_lanzaTournamentNotFoundException() {
        UUID id = UUID.randomUUID();
        TournamentRepositoryPort repository = mock(TournamentRepositoryPort.class);
        when(repository.findById(id)).thenReturn(Optional.empty());

        GenerateEliminationBracketService service = new GenerateEliminationBracketService(repository);

        assertThrows(TournamentNotFoundException.class, () -> service.generate(id));
    }

    @Test
    void generate_gruposSinTerminar_lanzaGroupStageNotCompleteExceptionYNoGuarda() {
        UUID id = UUID.randomUUID();
        TournamentRepositoryPort repository = mock(TournamentRepositoryPort.class);
        Match pending = Match.builder().matchId(UUID.randomUUID()).homeTeamId(UUID.randomUUID())
                .awayTeamId(UUID.randomUUID()).status(MatchStatus.PENDING).groupName("Grupo A").build();
        Tournament tournament = sampleTournament(id, List.of(pending));
        when(repository.findById(id)).thenReturn(Optional.of(tournament));

        GenerateEliminationBracketService service = new GenerateEliminationBracketService(repository);

        assertThrows(GroupStageNotCompleteException.class, () -> service.generate(id));
        verify(repository, never()).save(any());
    }

    @Test
    void generate_gruposCompletos_generaYGuardaLaLlave() {
        UUID id = UUID.randomUUID();
        UUID a1 = UUID.randomUUID(); UUID a2 = UUID.randomUUID(); UUID a3 = UUID.randomUUID(); UUID a4 = UUID.randomUUID();
        UUID b1 = UUID.randomUUID(); UUID b2 = UUID.randomUUID(); UUID b3 = UUID.randomUUID(); UUID b4 = UUID.randomUUID();
        List<Match> matches = new ArrayList<>();
        matches.addAll(fullyFinishedGroup("Grupo A", a1, a2, a3, a4));
        matches.addAll(fullyFinishedGroup("Grupo B", b1, b2, b3, b4));
        TournamentRepositoryPort repository = mock(TournamentRepositoryPort.class);
        Tournament tournament = sampleTournament(id, matches);
        when(repository.findById(id)).thenReturn(Optional.of(tournament));
        when(repository.save(tournament)).thenReturn(tournament);

        GenerateEliminationBracketService service = new GenerateEliminationBracketService(repository);
        Tournament result = service.generate(id);

        assertEquals(3, result.getBracketNodes().size());
        verify(repository).save(tournament);
    }

    private List<Match> fullyFinishedGroup(String groupName, UUID t1, UUID t2, UUID t3, UUID t4) {
        return new ArrayList<>(List.of(
                finished(t1, t2, 3, 0, groupName),
                finished(t1, t3, 3, 0, groupName),
                finished(t1, t4, 3, 0, groupName),
                finished(t2, t3, 2, 0, groupName),
                finished(t2, t4, 2, 0, groupName),
                finished(t3, t4, 1, 0, groupName)));
    }

    private Match finished(UUID home, UUID away, int homeScore, int awayScore, String group) {
        return Match.builder().matchId(UUID.randomUUID()).homeTeamId(home).awayTeamId(away)
                .status(MatchStatus.FINISHED).homeScore(homeScore).awayScore(awayScore).groupName(group).build();
    }
}
