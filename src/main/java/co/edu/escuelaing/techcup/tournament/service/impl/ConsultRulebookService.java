package co.edu.escuelaing.techcup.tournament.service.impl;

import co.edu.escuelaing.techcup.tournament.exception.RulebookNotAttachedException;
import co.edu.escuelaing.techcup.tournament.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.ports.ConsultRulebookUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.RulebookRetrievalPort;
import co.edu.escuelaing.techcup.tournament.service.ports.TournamentRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class ConsultRulebookService implements ConsultRulebookUseCase {

    private final TournamentRepositoryPort tournamentRepository;
    private final RulebookRetrievalPort rulebookRetrieval;

    public ConsultRulebookService(TournamentRepositoryPort tournamentRepository,
                                  RulebookRetrievalPort rulebookRetrieval) {
        this.tournamentRepository = tournamentRepository;
        this.rulebookRetrieval = rulebookRetrieval;
    }

    @Override
    public RulebookResource consult(String tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TournamentNotFoundException(tournamentId));

        String fileId = tournament.getRulebookFileId();
        if (fileId == null || fileId.isBlank()) {
            throw new RulebookNotAttachedException(tournamentId);
        }

        RulebookRetrievalPort.RulebookFile file = rulebookRetrieval.retrieve(fileId);
        return new RulebookResource(file.fileName(), file.contentType(), file.content());
    }
}
