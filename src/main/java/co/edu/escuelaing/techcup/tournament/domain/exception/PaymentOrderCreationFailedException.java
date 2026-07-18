package co.edu.escuelaing.techcup.tournament.domain.exception;

import java.util.UUID;

public class PaymentOrderCreationFailedException extends RuntimeException {
    public PaymentOrderCreationFailedException(UUID enrollmentId, Throwable cause) {
        super("No se pudo crear la orden de pago para la inscripción '" + enrollmentId
                + "'. La inscripción fue revertida.", cause);
    }
}
