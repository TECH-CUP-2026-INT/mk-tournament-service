package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.exception.ChampionPendingPenaltiesException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.ChampionAssignment;
import co.edu.escuelaing.techcup.tournament.domain.model.ChampionResolution;
import co.edu.escuelaing.techcup.tournament.domain.model.Match;
import co.edu.escuelaing.techcup.tournament.domain.model.MatchStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AssignChampionServiceTest {

    @Test
    void assignChampion_whenNoTie_persistsChampionByRegulationTime() {
        UUID tournamentId = UUID.randomUUID();
        UUID matchId = UUID.randomUUID();
        UUID homeTeamId = UUID.randomUUID();
        UUID awayTeamId = UUID.randomUUID();
        TournamentRepositoryPort repository = mock(TournamentRepositoryPort.class);
        Match finalMatch = Match.builder().matchId(matchId).homeTeamId(homeTeamId).awayTeamId(awayTeamId)
                .status(MatchStatus.FINISHED).finalMatch(true).homeScore(3).awayScore(1).build();
        Tournament tournament = Tournament.builder()
                .id(tournamentId).name("TechCup").numberOfTeams(4).cost(BigDecimal.ZERO)
                .startDate(LocalDate.now().plusDays(2)).endDate(LocalDate.now().plusDays(10))
                .registrationDeadline(LocalDate.now())
                .status(TournamentStatus.IN_PROGRESS).teams(new ArrayList<>())
                .matches(new ArrayList<>(List.of(finalMatch)))
                .reconstruct();

        when(repository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(repository.save(tournament)).thenReturn(tournament);

        AssignChampionService service = new AssignChampionService(repository);
        ChampionAssignment result = service.assignChampion(tournamentId, matchId);

        assertEquals(homeTeamId, result.championTeamId());
        assertEquals(ChampionResolution.REGULATION_TIME, result.resolution());
        verify(repository).save(tournament);
    }

    @Test
    void assignChampion_whenTieWithPenalties_persistsChampionByPenalties() {
        UUID tournamentId = UUID.randomUUID();
        UUID matchId = UUID.randomUUID();
        UUID homeTeamId = UUID.randomUUID();
        UUID awayTeamId = UUID.randomUUID();
        TournamentRepositoryPort repository = mock(TournamentRepositoryPort.class);
        Match finalMatch = Match.builder().matchId(matchId).homeTeamId(homeTeamId).awayTeamId(awayTeamId)
                .status(MatchStatus.FINISHED).finalMatch(true).homeScore(2).awayScore(2)
                .penaltyShootoutWinnerTeamId(awayTeamId).build();
        Tournament tournament = Tournament.builder()
                .id(tournamentId).name("TechCup").numberOfTeams(4).cost(BigDecimal.ZERO)
                .startDate(LocalDate.now().plusDays(2)).endDate(LocalDate.now().plusDays(10))
                .registrationDeadline(LocalDate.now())
                .status(TournamentStatus.IN_PROGRESS).teams(new ArrayList<>())
                .matches(new ArrayList<>(List.of(finalMatch)))
                .reconstruct();

        when(repository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(repository.save(tournament)).thenReturn(tournament);

        AssignChampionService service = new AssignChampionService(repository);
        ChampionAssignment result = service.assignChampion(tournamentId, matchId);

        assertEquals(awayTeamId, result.championTeamId());
        assertEquals(ChampionResolution.PENALTIES, result.resolution());
    }

    @Test
    void assignChampion_whenTieWithoutPenalties_throwsPendingPenalties() {
        UUID tournamentId = UUID.randomUUID();
        UUID matchId = UUID.randomUUID();
        UUID homeTeamId = UUID.randomUUID();
        UUID awayTeamId = UUID.randomUUID();
        TournamentRepositoryPort repository = mock(TournamentRepositoryPort.class);
        Match finalMatch = Match.builder().matchId(matchId).homeTeamId(homeTeamId).awayTeamId(awayTeamId)
                .status(MatchStatus.FINISHED).finalMatch(true).homeScore(1).awayScore(1).build();
        Tournament tournament = Tournament.builder()
                .id(tournamentId).name("TechCup").numberOfTeams(4).cost(BigDecimal.ZERO)
                .startDate(LocalDate.now().plusDays(2)).endDate(LocalDate.now().plusDays(10))
                .registrationDeadline(LocalDate.now())
                .status(TournamentStatus.IN_PROGRESS).teams(new ArrayList<>())
                .matches(new ArrayList<>(List.of(finalMatch)))
                .reconstruct();

        when(repository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        AssignChampionService service = new AssignChampionService(repository);

        assertThrows(ChampionPendingPenaltiesException.class,
                () -> service.assignChampion(tournamentId, matchId));
    }

    @Test
    void assignChampion_whenTournamentNotFound_throwsException() {
        UUID tournamentId = UUID.randomUUID();
        UUID matchId = UUID.randomUUID();
        TournamentRepositoryPort repository = mock(TournamentRepositoryPort.class);
        when(repository.findById(tournamentId)).thenReturn(Optional.empty());

        AssignChampionService service = new AssignChampionService(repository);

        assertThrows(TournamentNotFoundException.class,
                () -> service.assignChampion(tournamentId, matchId));
    }
}
