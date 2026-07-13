package co.edu.escuelaing.techcup.tournament.service.ports;

import java.io.InputStream;

public interface CourtImageStoragePort {
    String store(String fileName, String contentType, long sizeBytes, InputStream content);
}
