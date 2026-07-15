package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mapper;

import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo.TournamentDocument;
import co.edu.escuelaing.techcup.tournament.domain.model.Match;
import co.edu.escuelaing.techcup.tournament.domain.model.MatchStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.RegistrationStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.TeamRegistration;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentFormat;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentType;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class TournamentPersistenceMapperTest {

    private final TournamentPersistenceMapper mapper = Mappers.getMapper(TournamentPersistenceMapper.class);

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

        TournamentDocument document = mapper.toDocument(tournament);
        Tournament reconstructed = mapper.toDomain(document);

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
                null, null, "ACTIVE", null, null, null, null, null, null, false, null, null
        );

        Tournament reconstructed = mapper.toDomain(document);

        assertEquals(0, reconstructed.getTeams().size());
        assertEquals(0, reconstructed.getMatches().size());
    }
}
