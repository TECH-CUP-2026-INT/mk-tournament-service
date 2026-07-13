package co.edu.escuelaing.techcup.tournament.service;

import co.edu.escuelaing.techcup.tournament.exception.InsufficientApprovedTeamsException;
import co.edu.escuelaing.techcup.tournament.exception.TournamentPreparationNotAllowedException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TournamentPreparationTest {

    private Tournament buildTournament(TournamentStatus status, List<TeamRegistration> teams) {
        return Tournament.reconstruct(
                "t1", "TechCup", TournamentType.NORMAL, TournamentFormat.BRACKETS, 8, BigDecimal.ZERO,
                LocalDate.now().plusDays(2), LocalDate.now().plusDays(10), LocalDate.now(),
                null, null,
                status,
                new ArrayList<>(teams), new ArrayList<>(),
                null, null
        );
    }

    private List<TeamRegistration> approvedTeams(int count) {
        List<TeamRegistration> teams = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            teams.add(new TeamRegistration("e" + i, "Equipo " + i, RegistrationStatus.APPROVED));
        }
        return teams;
    }

    @Test
    void startPreparation_activoConTresAprobados_pasaAInPreparationYFijaMatches() {
        Tournament tournament = buildTournament(TournamentStatus.ACTIVE, approvedTeams(3));
        List<Match> generated = List.of(new Match("m1", "e0", "e1", MatchStatus.PENDING));

        tournament.startPreparation(generated);

        assertEquals(TournamentStatus.IN_PREPARATION, tournament.getStatus());
        assertEquals(1, tournament.getMatches().size());
    }

    @Test
    void startPreparation_noActivo_lanzaTournamentPreparationNotAllowedException() {
        Tournament tournament = buildTournament(TournamentStatus.DRAFT, approvedTeams(3));

        assertThrows(TournamentPreparationNotAllowedException.class,
                () -> tournament.startPreparation(List.of()));
    }

    @Test
    void startPreparation_menosDeTresAprobados_lanzaInsufficientApprovedTeamsException() {
        Tournament tournament = buildTournament(TournamentStatus.ACTIVE, approvedTeams(2));

        assertThrows(InsufficientApprovedTeamsException.class,
                () -> tournament.startPreparation(List.of()));
    }

    @Test
    void validateReadyForPreparation_activoConTresAprobados_noLanza() {
        Tournament tournament = buildTournament(TournamentStatus.ACTIVE, approvedTeams(3));
        tournament.validateReadyForPreparation();
    }
}
