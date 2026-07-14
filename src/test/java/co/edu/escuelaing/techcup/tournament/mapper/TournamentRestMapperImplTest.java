package co.edu.escuelaing.techcup.tournament.mapper;

import co.edu.escuelaing.techcup.tournament.dto.response.TournamentResponse;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.TournamentFormat;
import co.edu.escuelaing.techcup.tournament.service.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.service.TournamentType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TournamentRestMapperImplTest {

    private final TournamentRestMapper mapper = new TournamentRestMapperImpl();

    @Test
    void toResponse_conNull_retornaNull() {
        assertNull(mapper.toResponse(null));
    }

    @Test
    void toResponse_conTorneo_mapeaTodosLosCampos() {
        Tournament tournament = Tournament.reconstruct("t1", "TechCup Fútbol 2026", TournamentType.NORMAL,
                TournamentFormat.BRACKETS, 8, BigDecimal.valueOf(50000), LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(20), LocalDate.now().plusDays(5), null, null, TournamentStatus.ACTIVE,
                new ArrayList<>(), new ArrayList<>(), null, null, false);

        TournamentResponse response = mapper.toResponse(tournament);

        assertEquals("t1", response.id());
        assertEquals("TechCup Fútbol 2026", response.name());
        assertEquals(TournamentStatus.ACTIVE, response.status());
    }
}
