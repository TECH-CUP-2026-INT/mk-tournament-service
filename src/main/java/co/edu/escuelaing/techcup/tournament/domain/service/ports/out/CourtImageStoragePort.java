package co.edu.escuelaing.techcup.tournament.domain.service.ports.out;

import java.io.InputStream;

/**
 * Puerto de almacenamiento para la imagen de una cancha.
 */
public interface CourtImageStoragePort {
    String store(String fileName, String contentType, long sizeBytes, InputStream content);
}
