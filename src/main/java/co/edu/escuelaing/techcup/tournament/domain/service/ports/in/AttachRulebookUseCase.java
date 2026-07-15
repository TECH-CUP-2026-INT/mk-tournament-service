// domain/port/in/AttachRulebookUseCase.java
package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import java.io.InputStream;

public interface AttachRulebookUseCase {

    Tournament attach(AttachRulebookCommand command);

    record AttachRulebookCommand(
            String tournamentId,
            String fileName,
            String contentType,
            long sizeBytes,
            InputStream content
    ) {}
}