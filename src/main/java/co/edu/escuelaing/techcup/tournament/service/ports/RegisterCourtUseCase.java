package co.edu.escuelaing.techcup.tournament.service.ports;

import co.edu.escuelaing.techcup.tournament.service.Court;
import co.edu.escuelaing.techcup.tournament.service.CourtSection;

import java.io.InputStream;

public interface RegisterCourtUseCase {

    Court register(RegisterCourtCommand command);

    record RegisterCourtCommand(
            String tournamentId,
            CourtSection section,
            String description,
            String imageFileName,
            String imageContentType,
            Long imageSizeBytes,
            InputStream imageContent
    ) {}
}
