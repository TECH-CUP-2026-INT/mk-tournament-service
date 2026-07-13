// application/service/AttachRulebookService.java
package co.edu.escuelaing.techcup.tournament.service.impl;

import co.edu.escuelaing.techcup.tournament.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.exception.InvalidRulebookFileException;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.ports.AttachRulebookUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.RulebookStoragePort;
import co.edu.escuelaing.techcup.tournament.service.ports.TournamentRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class AttachRulebookService implements AttachRulebookUseCase {

    private final TournamentRepositoryPort tournamentRepository;
    private final RulebookStoragePort rulebookStorage;

    public AttachRulebookService(TournamentRepositoryPort tournamentRepository,
                                 RulebookStoragePort rulebookStorage) {
        this.tournamentRepository = tournamentRepository;
        this.rulebookStorage = rulebookStorage;
    }

    @Override
    public Tournament attach(AttachRulebookCommand command) {
        Tournament tournament = tournamentRepository.findById(command.tournamentId())
                .orElseThrow(() -> new TournamentNotFoundException(command.tournamentId()));

        validateFile(command.contentType(), command.sizeBytes());

        String fileId = rulebookStorage.store(
                command.fileName(), command.contentType(), command.sizeBytes(), command.content()
        );

        tournament.attachRulebook(fileId);
        return tournamentRepository.save(tournament);
    }

    private void validateFile(String contentType, long sizeBytes) {
        if (contentType == null || !contentType.equalsIgnoreCase(InvalidRulebookFileException.ALLOWED_CONTENT_TYPE))
            throw InvalidRulebookFileException.invalidFormat(contentType);
        if (sizeBytes > InvalidRulebookFileException.MAX_SIZE_BYTES)
            throw InvalidRulebookFileException.fileTooLarge(sizeBytes);
    }
}