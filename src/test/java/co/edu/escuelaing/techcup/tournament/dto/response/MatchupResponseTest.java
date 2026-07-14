package co.edu.escuelaing.techcup.tournament.dto.response;

import co.edu.escuelaing.techcup.tournament.service.MatchStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MatchupResponseTest {

    @Test
    void displayHomeTeam_conEquipoAsignado_retornaElId() {
        MatchupResponse response = new MatchupResponse("m1", "home1", "away1", MatchStatus.PENDING, 0, 0, false);

        assertEquals("home1", response.displayHomeTeam());
        assertEquals("away1", response.displayAwayTeam());
    }

    @Test
    void displayHomeTeam_sinEquipoAsignado_retornaPorDefinir() {
        MatchupResponse response = new MatchupResponse("m1", null, null, MatchStatus.PENDING, 0, 0, false);

        assertEquals("To be defined", response.displayHomeTeam());
        assertEquals("To be defined", response.displayAwayTeam());
    }
}
