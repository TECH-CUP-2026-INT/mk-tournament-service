package co.edu.escuelaing.techcup.tournament.service.impl;

import co.edu.escuelaing.techcup.tournament.service.Court;
import co.edu.escuelaing.techcup.tournament.service.CourtSection;
import co.edu.escuelaing.techcup.tournament.service.ports.CourtRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

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
        Court court = Court.reconstruct("court-1", "tournament-1", CourtSection.CANCHA_1, "Main court", null, "match-1");
        when(courtRepository.findByMatchId("match-1")).thenReturn(Optional.of(court));

        Optional<Court> result = viewMatchCourtService.getCourtByMatch("match-1");

        assertTrue(result.isPresent());
        assertEquals("court-1", result.get().getId());
        assertEquals("match-1", result.get().getMatchId());
        verify(courtRepository).findByMatchId("match-1");
    }

    @Test
    void getCourtByMatch_whenNoCourtAssigned_returnsEmpty() {
        when(courtRepository.findByMatchId("match-2")).thenReturn(Optional.empty());

        Optional<Court> result = viewMatchCourtService.getCourtByMatch("match-2");

        assertTrue(result.isEmpty());
        verify(courtRepository).findByMatchId("match-2");
    }

    @Test
    void getCourtByMatch_courtHasCorrectSection() {
        Court court = Court.reconstruct("court-2", "tournament-1", CourtSection.CANCHA_2, "Side court", "img-1", "match-3");
        when(courtRepository.findByMatchId("match-3")).thenReturn(Optional.of(court));

        Optional<Court> result = viewMatchCourtService.getCourtByMatch("match-3");

        assertTrue(result.isPresent());
        assertEquals(CourtSection.CANCHA_2, result.get().getSection());
        assertEquals("img-1", result.get().getImageId());
    }

    @Test
    void getCourtByMatch_courtWithNoImage_returnsCourtWithNullImageId() {
        Court court = Court.reconstruct("court-3", "tournament-1", CourtSection.CANCHA_3, "Court C", null, "match-4");
        when(courtRepository.findByMatchId("match-4")).thenReturn(Optional.of(court));

        Optional<Court> result = viewMatchCourtService.getCourtByMatch("match-4");

        assertTrue(result.isPresent());
        assertNull(result.get().getImageId());
    }
}
