// domain/port/out/RulebookStoragePort.java
package co.edu.escuelaing.techcup.tournament.service.ports;

import java.io.InputStream;

public interface RulebookStoragePort {
    String store(String fileName, String contentType, long sizeBytes, InputStream content);
}