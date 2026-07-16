package co.edu.escuelaing.techcup.tournament.application.mapper;

import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.TournamentResponse;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentFormat;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

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
        UUID id = UUID.randomUUID();
        Tournament tournament = Tournament.reconstruct(id, "TechCup Fútbol 2026", TournamentType.NORMAL,
                TournamentFormat.BRACKETS, 8, BigDecimal.valueOf(50000), LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(20), LocalDate.now().plusDays(5), null, null, TournamentStatus.ACTIVE,
                new ArrayList<>(), new ArrayList<>(), null, null, false);

        TournamentResponse response = mapper.toResponse(tournament);

        assertEquals(id, response.id());
        assertEquals("TechCup Fútbol 2026", response.name());
        assertEquals(TournamentStatus.ACTIVE, response.status());
    }
}
