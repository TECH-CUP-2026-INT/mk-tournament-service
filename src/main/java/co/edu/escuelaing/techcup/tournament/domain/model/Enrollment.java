package co.edu.escuelaing.techcup.tournament.domain.model;

import co.edu.escuelaing.techcup.tournament.application.usecase.SyncEnrollmentStatusService;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Inscripción de un equipo a un torneo, con su estado de reserva/pago. Se crea
 * con un slot reservado hasta que el Payment Service confirma el pago.
 */
public class Enrollment {

    private String enrollmentId;
    private String teamId;
    private String teamName;
    private EnrollmentStatus status;
    private int points;
    private LocalDateTime confirmationDate;
    private LocalDateTime reservationExpiresAt;

    public Enrollment() {}

    public Enrollment(String teamId, String teamName, EnrollmentStatus status) {
        this(UUID.randomUUID().toString(), teamId, teamName, status, null, null);
    }

    public Enrollment(String enrollmentId, String teamId, String teamName, EnrollmentStatus status,
                       LocalDateTime confirmationDate, LocalDateTime reservationExpiresAt) {
        this.enrollmentId = enrollmentId;
        this.teamId = teamId;
        this.teamName = teamName;
        this.status = status;
        this.points = 0;
        this.confirmationDate = confirmationDate;
        this.reservationExpiresAt = reservationExpiresAt;
    }

    public String getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(String enrollmentId) { this.enrollmentId = enrollmentId; }

    public String getTeamId() { return teamId; }
    public void setTeamId(String teamId) { this.teamId = teamId; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public EnrollmentStatus getStatus() { return status; }
    public void setStatus(EnrollmentStatus status) { this.status = status; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public LocalDateTime getConfirmationDate() { return confirmationDate; }
    public void setConfirmationDate(LocalDateTime confirmationDate) { this.confirmationDate = confirmationDate; }

    public LocalDateTime getReservationExpiresAt() { return reservationExpiresAt; }
    public void setReservationExpiresAt(LocalDateTime reservationExpiresAt) { this.reservationExpiresAt = reservationExpiresAt; }

    /**
     * SyncEnrollmentStatusService: el Payment Service confirmó el pago
     * (PaymentOrderStatus.APPROVED) — la inscripción queda confirmada.
     */
    public void markEnrolled(LocalDateTime confirmationDate) {
        this.status = EnrollmentStatus.ENROLLED;
        this.confirmationDate = confirmationDate;
    }

    /**
     * SyncEnrollmentStatusService: el Payment Service rechazó el pago
     * (PaymentOrderStatus.REJECTED) — el cupo queda libre automáticamente,
     * ya que availableSlots se calcula en vivo sobre ENROLLED+RESERVED.
     */
    public void markRejected() {
        this.status = EnrollmentStatus.REJECTED;
    }
}
