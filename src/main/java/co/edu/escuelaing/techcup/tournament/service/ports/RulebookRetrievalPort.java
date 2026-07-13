package co.edu.escuelaing.techcup.tournament.service.ports;

import java.io.InputStream;

public interface RulebookRetrievalPort {

    RulebookFile retrieve(String fileId);

    record RulebookFile(
            String fileName,
            String contentType,
            InputStream content
    ) {}
}
