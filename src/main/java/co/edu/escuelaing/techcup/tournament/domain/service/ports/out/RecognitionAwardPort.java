package co.edu.escuelaing.techcup.tournament.domain.service.ports.out;

/**
 * Puerto para disparar el reconocimiento/premiación al finalizar un torneo
 * (integración pendiente, sin implementación real todavía más allá de un log).
 */
public interface RecognitionAwardPort {
    void triggerAwards(String tournamentId);
}
