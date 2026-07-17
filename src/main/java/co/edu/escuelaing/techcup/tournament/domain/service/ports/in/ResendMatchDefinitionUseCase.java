package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import java.util.UUID;

/**
 * Reintento manual del envío de la definición de un partido a Matches (ver
 * MatchDefinitionPort), para los casos en que el envío automático al
 * programar (ScheduleMatchService) falló y el partido quedó marcado
 * pendiente de reenvío.
 */
public interface ResendMatchDefinitionUseCase {
    void resend(UUID matchId);
}
