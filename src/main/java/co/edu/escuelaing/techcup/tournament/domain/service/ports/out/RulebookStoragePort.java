// domain/port/out/RulebookStoragePort.java
package co.edu.escuelaing.techcup.tournament.domain.service.ports.out;

import java.io.InputStream;

/**
 * Puerto de almacenamiento para el archivo PDF del reglamento de un torneo.
 */
public interface RulebookStoragePort {
    String store(String fileName, String contentType, long sizeBytes, InputStream content);
}