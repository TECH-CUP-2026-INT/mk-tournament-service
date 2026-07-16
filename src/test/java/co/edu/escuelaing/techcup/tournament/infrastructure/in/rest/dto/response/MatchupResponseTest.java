package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response;

import co.edu.escuelaing.techcup.tournament.domain.model.MatchStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MatchupResponseTest {

    @Test
    void displayHomeTeam_conEquipoAsignado_retornaElId() {
        UUID home = UUID.randomUUID();
        UUID away = UUID.randomUUID();
        MatchupResponse response = new MatchupResponse(UUID.randomUUID(), home, away, MatchStatus.PENDING, 0, 0, false);

        assertEquals(home.toString(), response.displayHomeTeam());
        assertEquals(away.toString(), response.displayAwayTeam());
    }

    @Test
    void displayHomeTeam_sinEquipoAsignado_retornaPorDefinir() {
        MatchupResponse response = new MatchupResponse(UUID.randomUUID(), null, null, MatchStatus.PENDING, 0, 0, false);

        assertEquals("To be defined", response.displayHomeTeam());
        assertEquals("To be defined", response.displayAwayTeam());
    }
}
