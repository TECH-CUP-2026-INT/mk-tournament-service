package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.model.PlayerSanction;
import co.edu.escuelaing.techcup.tournament.domain.model.SanctionType;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.PlayerSanctionRepositoryPort;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RecordMatchFinishedForSanctionsServiceTest {

    @Test
    void recordMatchFinished_decrementaTodasLasActivasYLasGuarda() {
        PlayerSanctionRepositoryPort repository = mock(PlayerSanctionRepositoryPort.class);
        PlayerSanction sanction1 = PlayerSanction.reconstruct("s1", "p1", SanctionType.RED_CARD, 1);
        PlayerSanction sanction2 = PlayerSanction.reconstruct("s2", "p2", SanctionType.CONDUCT, 3);

        when(repository.findAllActive()).thenReturn(List.of(sanction1, sanction2));

        RecordMatchFinishedForSanctionsService service = new RecordMatchFinishedForSanctionsService(repository);

        service.recordMatchFinished();

        assertEquals(0, sanction1.getMatchesRemaining());
        assertEquals(2, sanction2.getMatchesRemaining());
        verify(repository, times(2)).save(any());
    }

    @Test
    void recordMatchFinished_sinSancionesActivas_noHaceNada() {
        PlayerSanctionRepositoryPort repository = mock(PlayerSanctionRepositoryPort.class);
        when(repository.findAllActive()).thenReturn(List.of());

        RecordMatchFinishedForSanctionsService service = new RecordMatchFinishedForSanctionsService(repository);

        service.recordMatchFinished();

        verify(repository, times(0)).save(any());
    }
}
