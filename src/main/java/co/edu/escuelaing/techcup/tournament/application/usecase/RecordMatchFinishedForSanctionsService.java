package co.edu.escuelaing.techcup.tournament.application.usecase;

import lombok.RequiredArgsConstructor;

import co.edu.escuelaing.techcup.tournament.domain.model.PlayerSanction;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.PlayerSanctionRepositoryPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.RecordMatchFinishedForSanctionsUseCase;
import org.springframework.stereotype.Service;

/**
 * Punto de integración pendiente: la futura historia "Finalizar partido"
 * debe invocar este caso de uso cuando un partido finaliza. Hoy no existe
 * seguimiento de alineación/asistencia de jugadores, así que se descuenta
 * un partido a TODAS las sanciones activas, sin verificar si el jugador
 * sancionado efectivamente jugaba ese partido (decisión explícita).
 */
@Service
@RequiredArgsConstructor
public class RecordMatchFinishedForSanctionsService implements RecordMatchFinishedForSanctionsUseCase {

    private final PlayerSanctionRepositoryPort repository;


    @Override
    public void recordMatchFinished() {
        for (PlayerSanction sanction : repository.findAllActive()) {
            sanction.serveMatch();
            repository.save(sanction);
        }
    }
}
