package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import java.io.InputStream;

public interface ConsultRulebookUseCase {

    RulebookResource consult(String tournamentId);

    record RulebookResource(
            String fileName,
            String contentType,
            InputStream content
    ) {}
}
