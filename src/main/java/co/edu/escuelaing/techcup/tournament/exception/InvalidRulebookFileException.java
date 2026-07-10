// domain/exception/InvalidRulebookFileException.java
package co.edu.escuelaing.techcup.tournament.exception;

public class InvalidRulebookFileException extends RuntimeException {

    public static final long MAX_SIZE_BYTES = 10L * 1024 * 1024;
    public static final String ALLOWED_CONTENT_TYPE = "application/pdf";

    public InvalidRulebookFileException(String message) {
        super(message);
    }

    public static InvalidRulebookFileException invalidFormat(String actualContentType) {
        return new InvalidRulebookFileException(
                "Solo se aceptan archivos PDF para el reglamento (recibido: " + actualContentType + ")");
    }

    public static InvalidRulebookFileException fileTooLarge(long actualSizeBytes) {
        return new InvalidRulebookFileException(
                "El reglamento no puede superar 10 MB (recibido: " + (actualSizeBytes / (1024 * 1024)) + " MB)");
    }
}