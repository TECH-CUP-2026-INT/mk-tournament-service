package co.edu.escuelaing.techcup.tournament.domain.service;

import co.edu.escuelaing.techcup.tournament.domain.model.AuditEvent;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AuditEventTest {

    @Test
    void create_asignaTimestampYCamposCorrectamente() {
        AuditEvent event = AuditEvent.create("system", "CreateTournamentService.create", "t1");

        assertNotNull(event.getTimestamp());
        assertEquals("system", event.getActor());
        assertEquals("CreateTournamentService.create", event.getActionType());
        assertEquals("t1", event.getAffectedEntityId());
    }
}
