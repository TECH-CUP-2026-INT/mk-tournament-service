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
import java.util.UUID;

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
        UUID id = UUID.randomUUID();
        Tournament tournament = new Tournament(id, "Copa ECI", TournamentStatus.ACTIVE);
        tournament.setRulebookFileId("file123");

        RulebookRetrievalPort.RulebookFile file = new RulebookRetrievalPort.RulebookFile(
                "reglamento.pdf", "application/pdf", new ByteArrayInputStream(new byte[]{1, 2, 3})
        );

        when(tournamentRepository.findById(id)).thenReturn(Optional.of(tournament));
        when(rulebookRetrieval.retrieve("file123")).thenReturn(file);

        ConsultRulebookUseCase.RulebookResource result = service.consult(id);

        assertEquals("reglamento.pdf", result.fileName());
        assertEquals("application/pdf", result.contentType());
        assertNotNull(result.content());
        verify(rulebookRetrieval).retrieve("file123");
    }

    @Test
    void consult_whenTournamentNotFound_throwsTournamentNotFoundException() {
        UUID id = UUID.randomUUID();
        when(tournamentRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(TournamentNotFoundException.class, () -> service.consult(id));
        verifyNoInteractions(rulebookRetrieval);
    }

    @Test
    void consult_whenNoRulebookAttached_throwsRulebookNotAttachedException() {
        UUID id = UUID.randomUUID();
        Tournament tournament = new Tournament(id, "Copa ECI", TournamentStatus.ACTIVE);

        when(tournamentRepository.findById(id)).thenReturn(Optional.of(tournament));

        assertThrows(RulebookNotAttachedException.class, () -> service.consult(id));
        verifyNoInteractions(rulebookRetrieval);
    }

    @Test
    void consult_whenRulebookFileIdIsBlank_throwsRulebookNotAttachedException() {
        UUID id = UUID.randomUUID();
        Tournament tournament = new Tournament(id, "Copa ECI", TournamentStatus.ACTIVE);
        tournament.setRulebookFileId("   ");

        when(tournamentRepository.findById(id)).thenReturn(Optional.of(tournament));

        assertThrows(RulebookNotAttachedException.class, () -> service.consult(id));
        verifyNoInteractions(rulebookRetrieval);
    }
}
