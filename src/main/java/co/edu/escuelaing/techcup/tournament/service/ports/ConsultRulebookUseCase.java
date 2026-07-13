package co.edu.escuelaing.techcup.tournament.service.ports;

import java.io.InputStream;

public interface ConsultRulebookUseCase {

    RulebookResource consult(String tournamentId);

    record RulebookResource(
            String fileName,
            String contentType,
            InputStream content
    ) {}
}
