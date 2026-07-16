package co.edu.escuelaing.techcup.tournament.domain.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Registro inmutable de una acción exitosa, capturado automáticamente por el aspecto
 * de auditoría (AOP) sobre los casos de uso de {@code application.usecase}.
 */
public class AuditEvent extends AggregateRoot {

    /**
     * Placeholder fijo: no existe atribución de usuario real todavía.
     * Pendiente de integración con el futuro Servicio de Identidad
     * (JWT + roles Admin/Organizer) — mismo criterio que el endpoint de
     * consulta, que hoy queda sin control de acceso real.
     */
    public static final String SYSTEM_ACTOR = "system";

    private final Instant timestamp;
    private final String actor;
    private final String actionType;
    private final String affectedEntityId;

    private AuditEvent(UUID id, Instant timestamp, String actor, String actionType, String affectedEntityId) {
        super(id);
        this.timestamp = timestamp;
        this.actor = actor;
        this.actionType = actionType;
        this.affectedEntityId = affectedEntityId;
    }

    public static AuditEvent create(String actor, String actionType, String affectedEntityId) {
        return new AuditEvent(UUID.randomUUID(), Instant.now(), actor, actionType, affectedEntityId);
    }

    public static AuditEvent reconstruct(UUID id, Instant timestamp, String actor,
                                         String actionType, String affectedEntityId) {
        return new AuditEvent(id, timestamp, actor, actionType, affectedEntityId);
    }

    public Instant getTimestamp() { return timestamp; }
    public String getActor() { return actor; }
    public String getActionType() { return actionType; }
    public String getAffectedEntityId() { return affectedEntityId; }
}
