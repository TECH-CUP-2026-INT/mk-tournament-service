package co.edu.escuelaing.techcup.tournament.domain.exception;

import java.util.Set;

public class InvalidCourtImageException extends RuntimeException {

    public static final long MAX_SIZE_BYTES = 5L * 1024 * 1024;
    public static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png");

    public InvalidCourtImageException(String message) {
        super(message);
    }

    public static InvalidCourtImageException invalidFormat(String actualContentType) {
        return new InvalidCourtImageException(
                "Solo se aceptan imágenes JPG o PNG para la cancha (recibido: " + actualContentType + ")");
    }

    public static InvalidCourtImageException fileTooLarge(long actualSizeBytes) {
        return new InvalidCourtImageException(
                "La imagen de la cancha no puede superar 5 MB (recibido: " + (actualSizeBytes / (1024 * 1024)) + " MB)");
    }
}
