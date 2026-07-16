package co.edu.escuelaing.techcup.tournament.domain.service.ports.out;

import java.util.UUID;

/**
 * Puerto de salida para publicar eventos del ciclo de vida del torneo hacia
 * el resto del ecosistema TechCup (ej. Estadísticas), vía el exchange
 * compartido de RabbitMQ.
 */
public interface TournamentEventPublisherPort {

    /**
     * Publica que un torneo fue finalizado. No debe lanzar excepción si el
     * broker no está disponible — es un efecto secundario que no debe
     * bloquear la finalización del torneo (mismo criterio que
     * {@link RecognitionAwardPort}).
     */
    void publishTournamentFinalized(UUID tournamentId);
}
