package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.Court;
import co.edu.escuelaing.techcup.tournament.domain.model.CourtSection;

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
