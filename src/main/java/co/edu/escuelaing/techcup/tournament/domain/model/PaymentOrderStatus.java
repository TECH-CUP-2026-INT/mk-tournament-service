package co.edu.escuelaing.techcup.tournament.domain.model;

/**
 * Estados posibles de una orden de pago, según la respuesta del Payment Service.
 */
public enum PaymentOrderStatus {
    PENDING,
    AWAITING_BANK_CONFIRMATION,
    APPROVED,
    REJECTED,
    UNKNOWN
}
