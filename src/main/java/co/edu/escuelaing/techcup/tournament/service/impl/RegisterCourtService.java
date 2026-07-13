package co.edu.escuelaing.techcup.tournament.service.impl;

import co.edu.escuelaing.techcup.tournament.exception.InvalidCourtImageException;
import co.edu.escuelaing.techcup.tournament.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.service.Court;
import co.edu.escuelaing.techcup.tournament.service.ports.CourtImageStoragePort;
import co.edu.escuelaing.techcup.tournament.service.ports.CourtRepositoryPort;
import co.edu.escuelaing.techcup.tournament.service.ports.RegisterCourtUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.TournamentRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class RegisterCourtService implements RegisterCourtUseCase {

    private final TournamentRepositoryPort tournamentRepository;
    private final CourtRepositoryPort courtRepository;
    private final CourtImageStoragePort imageStorage;

    public RegisterCourtService(TournamentRepositoryPort tournamentRepository,
                                 CourtRepositoryPort courtRepository,
                                 CourtImageStoragePort imageStorage) {
        this.tournamentRepository = tournamentRepository;
        this.courtRepository = courtRepository;
        this.imageStorage = imageStorage;
    }

    @Override
    public Court register(RegisterCourtCommand command) {
        tournamentRepository.findById(command.tournamentId())
                .orElseThrow(() -> new TournamentNotFoundException(command.tournamentId()));

        Court court = Court.create(command.tournamentId(), command.section(), command.description());

        if (command.imageContent() != null) {
            validateImage(command.imageContentType(), command.imageSizeBytes());
            String imageId = imageStorage.store(
                    command.imageFileName(), command.imageContentType(), command.imageSizeBytes(), command.imageContent()
            );
            court.attachImage(imageId);
        }

        return courtRepository.save(court);
    }

    private void validateImage(String contentType, long sizeBytes) {
        if (contentType == null || !InvalidCourtImageException.ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase()))
            throw InvalidCourtImageException.invalidFormat(contentType);
        if (sizeBytes > InvalidCourtImageException.MAX_SIZE_BYTES)
            throw InvalidCourtImageException.fileTooLarge(sizeBytes);
    }
}
