package co.edu.escuelaing.techcup.tournament.domain.service.ports.out;

import java.io.InputStream;

/**
 * Puerto para recuperar el archivo del reglamento previamente almacenado.
 */
public interface RulebookRetrievalPort {

    RulebookFile retrieve(String fileId);

    record RulebookFile(
            String fileName,
            String contentType,
            InputStream content
    ) {}
}
