package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.exception.RulebookNotAttachedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ConsultRulebookUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.RulebookRetrievalPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
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

        tournament.assertActive();

        String fileId = tournament.getRulebookFileId();
        if (fileId == null || fileId.isBlank()) {
            throw new RulebookNotAttachedException(tournamentId);
        }

        RulebookRetrievalPort.RulebookFile file = rulebookRetrieval.retrieve(fileId);
        return new RulebookResource(file.fileName(), file.contentType(), file.content());
    }
}
