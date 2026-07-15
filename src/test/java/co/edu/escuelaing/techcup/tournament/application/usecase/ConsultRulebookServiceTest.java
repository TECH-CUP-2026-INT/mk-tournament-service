package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.exception.RulebookNotAttachedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ConsultRulebookUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.RulebookRetrievalPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConsultRulebookServiceTest {

    private TournamentRepositoryPort tournamentRepository;
    private RulebookRetrievalPort rulebookRetrieval;
    private ConsultRulebookService service;

    @BeforeEach
    void setUp() {
        tournamentRepository = mock(TournamentRepositoryPort.class);
        rulebookRetrieval = mock(RulebookRetrievalPort.class);
        service = new ConsultRulebookService(tournamentRepository, rulebookRetrieval);
    }

    @Test
    void consult_whenRulebookExists_returnsResource() {
        Tournament tournament = new Tournament("t1", "Copa ECI", TournamentStatus.ACTIVE);
        tournament.setRulebookFileId("file123");

        RulebookRetrievalPort.RulebookFile file = new RulebookRetrievalPort.RulebookFile(
                "reglamento.pdf", "application/pdf", new ByteArrayInputStream(new byte[]{1, 2, 3})
        );

        when(tournamentRepository.findById("t1")).thenReturn(Optional.of(tournament));
        when(rulebookRetrieval.retrieve("file123")).thenReturn(file);

        ConsultRulebookUseCase.RulebookResource result = service.consult("t1");

        assertEquals("reglamento.pdf", result.fileName());
        assertEquals("application/pdf", result.contentType());
        assertNotNull(result.content());
        verify(rulebookRetrieval).retrieve("file123");
    }

    @Test
    void consult_whenTournamentNotFound_throwsTournamentNotFoundException() {
        when(tournamentRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThrows(TournamentNotFoundException.class, () -> service.consult("unknown"));
        verifyNoInteractions(rulebookRetrieval);
    }

    @Test
    void consult_whenNoRulebookAttached_throwsRulebookNotAttachedException() {
        Tournament tournament = new Tournament("t2", "Copa ECI", TournamentStatus.ACTIVE);

        when(tournamentRepository.findById("t2")).thenReturn(Optional.of(tournament));

        assertThrows(RulebookNotAttachedException.class, () -> service.consult("t2"));
        verifyNoInteractions(rulebookRetrieval);
    }

    @Test
    void consult_whenRulebookFileIdIsBlank_throwsRulebookNotAttachedException() {
        Tournament tournament = new Tournament("t3", "Copa ECI", TournamentStatus.ACTIVE);
        tournament.setRulebookFileId("   ");

        when(tournamentRepository.findById("t3")).thenReturn(Optional.of(tournament));

        assertThrows(RulebookNotAttachedException.class, () -> service.consult("t3"));
        verifyNoInteractions(rulebookRetrieval);
    }
}
