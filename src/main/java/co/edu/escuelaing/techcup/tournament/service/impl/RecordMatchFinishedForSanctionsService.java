package co.edu.escuelaing.techcup.tournament.service.impl;

import co.edu.escuelaing.techcup.tournament.service.PlayerSanction;
import co.edu.escuelaing.techcup.tournament.service.ports.PlayerSanctionRepositoryPort;
import co.edu.escuelaing.techcup.tournament.service.ports.RecordMatchFinishedForSanctionsUseCase;
import org.springframework.stereotype.Service;

/**
 * Punto de integración pendiente: la futura historia "Finalizar partido"
 * debe invocar este caso de uso cuando un partido finaliza. Hoy no existe
 * seguimiento de alineación/asistencia de jugadores, así que se descuenta
 * un partido a TODAS las sanciones activas, sin verificar si el jugador
 * sancionado efectivamente jugaba ese partido (decisión explícita).
 */
@Service
public class RecordMatchFinishedForSanctionsService implements RecordMatchFinishedForSanctionsUseCase {

    private final PlayerSanctionRepositoryPort repository;

    public RecordMatchFinishedForSanctionsService(PlayerSanctionRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public void recordMatchFinished() {
        for (PlayerSanction sanction : repository.findAllActive()) {
            sanction.serveMatch();
            repository.save(sanction);
        }
    }
}
