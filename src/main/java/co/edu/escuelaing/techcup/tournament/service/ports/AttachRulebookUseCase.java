// domain/port/in/AttachRulebookUseCase.java
package co.edu.escuelaing.techcup.tournament.service.ports;

import co.edu.escuelaing.techcup.tournament.service.Tournament;
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