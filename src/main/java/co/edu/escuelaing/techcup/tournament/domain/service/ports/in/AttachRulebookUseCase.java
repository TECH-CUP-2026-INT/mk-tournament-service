// domain/port/in/AttachRulebookUseCase.java
package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import java.io.InputStream;
import java.util.UUID;

public interface AttachRulebookUseCase {

    Tournament attach(AttachRulebookCommand command);

    record AttachRulebookCommand(
            UUID tournamentId,
            String fileName,
            String contentType,
            long sizeBytes,
            InputStream content
    ) {}
}
