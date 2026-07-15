package co.edu.escuelaing.techcup.tournament.application.usecase;

import lombok.RequiredArgsConstructor;

import co.edu.escuelaing.techcup.tournament.domain.exception.InvalidCourtImageException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.Court;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.CourtImageStoragePort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.CourtRepositoryPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.RegisterCourtUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterCourtService implements RegisterCourtUseCase {

    private final TournamentRepositoryPort tournamentRepository;
    private final CourtRepositoryPort courtRepository;
    private final CourtImageStoragePort imageStorage;


    @Override
    public Court register(RegisterCourtCommand command) {
        Tournament tournament = tournamentRepository.findById(command.tournamentId())
                .orElseThrow(() -> new TournamentNotFoundException(command.tournamentId()));
        tournament.assertActive();

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
