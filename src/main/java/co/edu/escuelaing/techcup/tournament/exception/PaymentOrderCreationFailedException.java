package co.edu.escuelaing.techcup.tournament.exception;

public class PaymentOrderCreationFailedException extends RuntimeException {
    public PaymentOrderCreationFailedException(String enrollmentId, Throwable cause) {
        super("No se pudo crear la orden de pago para la inscripción '" + enrollmentId
                + "'. La inscripción fue revertida.", cause);
    }
}
