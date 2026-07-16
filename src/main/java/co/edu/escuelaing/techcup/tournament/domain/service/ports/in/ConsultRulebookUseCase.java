package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import java.io.InputStream;
import java.util.UUID;

public interface ConsultRulebookUseCase {

    RulebookResource consult(UUID tournamentId);

    record RulebookResource(
            String fileName,
            String contentType,
            InputStream content
    ) {}
}
