package co.edu.escuelaing.techcup.tournament.application.usecase;

import lombok.RequiredArgsConstructor;

import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.ChampionAssignment;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.AssignChampionUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Punto de integración pendiente: asume que el partido final ya quedó
 * finalizado (marcador y, si hubo empate, resultado de penales) por una
 * historia separada de "Registrar resultado del partido" (no construida
 * aún en este proyecto — mismo criterio que RecordMatchFinishedForSanctionsService
 * con "Finalizar partido"). Esta clase solo reacciona a esa finalización ya
 * ocurrida: valida las reglas de negocio (partido final, finalizado,
 * penales si hay empate) y persiste al campeón junto con el resto del
 * torneo en un único guardado (atómico a nivel de documento Mongo).
 */
@Service
@RequiredArgsConstructor
public class AssignChampionService implements AssignChampionUseCase {

    private final TournamentRepositoryPort repository;


    @Override
    public ChampionAssignment assignChampion(UUID tournamentId, UUID matchId) {
        Tournament tournament = repository.findById(tournamentId)
                .orElseThrow(() -> new TournamentNotFoundException(
                        "No se encontro un torneo con id " + tournamentId));

        ChampionAssignment assignment = tournament.assignChampionWhenFinalMatchFinished(matchId);
        repository.save(tournament);
        return assignment;
    }
}
