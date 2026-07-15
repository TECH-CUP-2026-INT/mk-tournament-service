package co.edu.escuelaing.techcup.tournament.domain.model;

public enum PaymentOrderStatus {
    PENDING,
    AWAITING_BANK_CONFIRMATION,
    APPROVED,
    REJECTED,
    UNKNOWN
}
