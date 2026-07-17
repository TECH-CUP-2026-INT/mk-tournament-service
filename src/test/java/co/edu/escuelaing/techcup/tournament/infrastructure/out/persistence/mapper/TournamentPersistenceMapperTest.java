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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class TournamentPersistenceMapperTest {

    private final TournamentPersistenceMapper mapper = Mappers.getMapper(TournamentPersistenceMapper.class);

    @Test
    void toDocumentYToDomain_conservanTeamsYMatches() {
        UUID teamId = UUID.randomUUID();
        UUID homeTeamId = UUID.randomUUID();
        UUID awayTeamId = UUID.randomUUID();
        UUID matchId = UUID.randomUUID();
        Tournament tournament = Tournament.builder()
                .id(UUID.randomUUID()).name("TechCup").type(TournamentType.NORMAL).format(TournamentFormat.BRACKETS)
                .numberOfTeams(4).cost(BigDecimal.ZERO)
                .startDate(LocalDate.now().plusDays(2)).endDate(LocalDate.now().plusDays(10))
                .registrationDeadline(LocalDate.now())
                .status(TournamentStatus.IN_PREPARATION)
                .teams(List.of(new TeamRegistration(teamId, "Equipo 1", RegistrationStatus.APPROVED)))
                .matches(List.of(new Match(matchId, homeTeamId, awayTeamId, MatchStatus.PENDING)))
                .reconstruct();

        TournamentDocument document = mapper.toDocument(tournament);
        Tournament reconstructed = mapper.toDomain(document);

        assertFalse(reconstructed.getTeams().isEmpty());
        assertFalse(reconstructed.getMatches().isEmpty());
        assertEquals(teamId, reconstructed.getTeams().get(0).getTeamId());
        assertEquals(matchId, reconstructed.getMatches().get(0).getMatchId());
        assertEquals(RegistrationStatus.APPROVED, reconstructed.getTeams().get(0).getRegistrationStatus());
        assertEquals(MatchStatus.PENDING, reconstructed.getMatches().get(0).getStatus());
    }

    @Test
    void toDomain_conListasNulasEnDocumento_retornaListasVacias() {
        TournamentDocument document = new TournamentDocument(
                UUID.randomUUID(), "TechCup", "NORMAL", "BRACKETS", 4, BigDecimal.ZERO,
                LocalDate.now(), LocalDate.now().plusDays(1), LocalDate.now().minusDays(1),
                null, null, "ACTIVE", null, null, null, null, null, null, null, null, false, null, null
        );

        Tournament reconstructed = mapper.toDomain(document);

        assertEquals(0, reconstructed.getTeams().size());
        assertEquals(0, reconstructed.getMatches().size());
        assertEquals(0, reconstructed.getBracketNodes().size());
    }
}
