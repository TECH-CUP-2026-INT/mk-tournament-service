package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.model.Court;
import co.edu.escuelaing.techcup.tournament.domain.model.CourtSection;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.CourtRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ViewMatchCourtServiceTest {

    @Mock
    private CourtRepositoryPort courtRepository;

    @InjectMocks
    private ViewMatchCourtService viewMatchCourtService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getCourtByMatch_whenCourtExists_returnsCourt() {
        UUID courtId = UUID.randomUUID();
        UUID matchId = UUID.randomUUID();
        Court court = Court.reconstruct(courtId, UUID.randomUUID(), CourtSection.CANCHA_1, "Main court", null, matchId);
        when(courtRepository.findByMatchId(matchId)).thenReturn(Optional.of(court));

        Optional<Court> result = viewMatchCourtService.getCourtByMatch(matchId);

        assertTrue(result.isPresent());
        assertEquals(courtId, result.get().getId());
        assertEquals(matchId, result.get().getMatchId());
        verify(courtRepository).findByMatchId(matchId);
    }

    @Test
    void getCourtByMatch_whenNoCourtAssigned_returnsEmpty() {
        UUID matchId = UUID.randomUUID();
        when(courtRepository.findByMatchId(matchId)).thenReturn(Optional.empty());

        Optional<Court> result = viewMatchCourtService.getCourtByMatch(matchId);

        assertTrue(result.isEmpty());
        verify(courtRepository).findByMatchId(matchId);
    }

    @Test
    void getCourtByMatch_courtHasCorrectSection() {
        UUID matchId = UUID.randomUUID();
        Court court = Court.reconstruct(UUID.randomUUID(), UUID.randomUUID(), CourtSection.CANCHA_2, "Side court", "img-1", matchId);
        when(courtRepository.findByMatchId(matchId)).thenReturn(Optional.of(court));

        Optional<Court> result = viewMatchCourtService.getCourtByMatch(matchId);

        assertTrue(result.isPresent());
        assertEquals(CourtSection.CANCHA_2, result.get().getSection());
        assertEquals("img-1", result.get().getImageId());
    }

    @Test
    void getCourtByMatch_courtWithNoImage_returnsCourtWithNullImageId() {
        UUID matchId = UUID.randomUUID();
        Court court = Court.reconstruct(UUID.randomUUID(), UUID.randomUUID(), CourtSection.CANCHA_3, "Court C", null, matchId);
        when(courtRepository.findByMatchId(matchId)).thenReturn(Optional.of(court));

        Optional<Court> result = viewMatchCourtService.getCourtByMatch(matchId);

        assertTrue(result.isPresent());
        assertNull(result.get().getImageId());
    }
}
