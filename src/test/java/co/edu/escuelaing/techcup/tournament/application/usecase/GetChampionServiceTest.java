package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.exception.ChampionAssignmentNotAllowedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.ChampionAssignment;
import co.edu.escuelaing.techcup.tournament.domain.model.ChampionResolution;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetChampionServiceTest {

    @Test
    void getChampion_whenAssigned_retornaElCampeonPublicado() {
        UUID tournamentId = UUID.randomUUID();
        UUID championTeamId = UUID.randomUUID();
        TournamentRepositoryPort repository = mock(TournamentRepositoryPort.class);
        Tournament tournament = Tournament.builder()
                .id(tournamentId).name("TechCup").numberOfTeams(4).cost(BigDecimal.ZERO)
                .startDate(LocalDate.now().plusDays(2)).endDate(LocalDate.now().plusDays(10))
                .registrationDeadline(LocalDate.now())
                .status(TournamentStatus.IN_PROGRESS).teams(new ArrayList<>()).matches(new ArrayList<>())
                .championTeamId(championTeamId).championResolution(ChampionResolution.REGULATION_TIME)
                .reconstruct();

        when(repository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        GetChampionService service = new GetChampionService(repository);
        ChampionAssignment result = service.getChampion(tournamentId);

        assertEquals(championTeamId, result.championTeamId());
        assertEquals(ChampionResolution.REGULATION_TIME, result.resolution());
    }

    @Test
    void getChampion_whenNotYetAssigned_lanzaChampionAssignmentNotAllowedException() {
        UUID tournamentId = UUID.randomUUID();
        TournamentRepositoryPort repository = mock(TournamentRepositoryPort.class);
        Tournament tournament = Tournament.builder()
                .id(tournamentId).name("TechCup").numberOfTeams(4).cost(BigDecimal.ZERO)
                .startDate(LocalDate.now().plusDays(2)).endDate(LocalDate.now().plusDays(10))
                .registrationDeadline(LocalDate.now())
                .status(TournamentStatus.IN_PROGRESS).teams(new ArrayList<>()).matches(new ArrayList<>())
                .reconstruct();

        when(repository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        GetChampionService service = new GetChampionService(repository);

        assertThrows(ChampionAssignmentNotAllowedException.class, () -> service.getChampion(tournamentId));
    }

    @Test
    void getChampion_whenTournamentNotFound_lanzaTournamentNotFoundException() {
        UUID tournamentId = UUID.randomUUID();
        TournamentRepositoryPort repository = mock(TournamentRepositoryPort.class);
        when(repository.findById(tournamentId)).thenReturn(Optional.empty());

        GetChampionService service = new GetChampionService(repository);

        assertThrows(TournamentNotFoundException.class, () -> service.getChampion(tournamentId));
    }
}
