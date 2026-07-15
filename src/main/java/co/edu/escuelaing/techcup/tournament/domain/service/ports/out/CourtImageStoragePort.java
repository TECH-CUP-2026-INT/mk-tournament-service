package co.edu.escuelaing.techcup.tournament.domain.service.ports.out;

import java.io.InputStream;

public interface CourtImageStoragePort {
    String store(String fileName, String contentType, long sizeBytes, InputStream content);
}
