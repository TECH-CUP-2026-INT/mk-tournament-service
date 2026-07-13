package co.edu.escuelaing.techcup.tournament.mapper;

import co.edu.escuelaing.techcup.tournament.entity.document.TournamentDocument;
import co.edu.escuelaing.techcup.tournament.service.Match;
import co.edu.escuelaing.techcup.tournament.service.MatchStatus;
import co.edu.escuelaing.techcup.tournament.service.RegistrationStatus;
import co.edu.escuelaing.techcup.tournament.service.TeamRegistration;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.TournamentFormat;
import co.edu.escuelaing.techcup.tournament.service.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.service.TournamentType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class TournamentPersistenceMapperTest {

    @Test
    void toDocumentYToDomain_conservanTeamsYMatches() {
        Tournament tournament = Tournament.reconstruct(
                "t1", "TechCup", TournamentType.NORMAL, TournamentFormat.BRACKETS, 4, BigDecimal.ZERO,
                LocalDate.now().plusDays(2), LocalDate.now().plusDays(10), LocalDate.now(),
                null, null, TournamentStatus.IN_PREPARATION,
                List.of(new TeamRegistration("e1", "Equipo 1", RegistrationStatus.APPROVED)),
                List.of(new Match("m1", "e1", "e2", MatchStatus.PENDING)),
                null, null
        );

        TournamentDocument document = TournamentPersistenceMapper.toDocument(tournament);
        Tournament reconstructed = TournamentPersistenceMapper.toDomain(document);

        assertFalse(reconstructed.getTeams().isEmpty());
        assertFalse(reconstructed.getMatches().isEmpty());
        assertEquals("e1", reconstructed.getTeams().get(0).getTeamId());
        assertEquals("m1", reconstructed.getMatches().get(0).getMatchId());
        assertEquals(RegistrationStatus.APPROVED, reconstructed.getTeams().get(0).getRegistrationStatus());
        assertEquals(MatchStatus.PENDING, reconstructed.getMatches().get(0).getStatus());
    }

    @Test
    void toDomain_conListasNulasEnDocumento_retornaListasVacias() {
        TournamentDocument document = new TournamentDocument(
                "t1", "TechCup", "NORMAL", "BRACKETS", 4, BigDecimal.ZERO,
                LocalDate.now(), LocalDate.now().plusDays(1), LocalDate.now().minusDays(1),
                null, null, "ACTIVE", null, null, null, null, null, null, false, null
        );

        Tournament reconstructed = TournamentPersistenceMapper.toDomain(document);

        assertEquals(0, reconstructed.getTeams().size());
        assertEquals(0, reconstructed.getMatches().size());
    }
}
