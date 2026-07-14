package co.edu.escuelaing.techcup.tournament.service.impl;

import co.edu.escuelaing.techcup.tournament.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.service.ChampionAssignment;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.ports.AssignChampionUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.TournamentRepositoryPort;
import org.springframework.stereotype.Service;

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
public class AssignChampionService implements AssignChampionUseCase {

    private final TournamentRepositoryPort repository;

    public AssignChampionService(TournamentRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public ChampionAssignment assignChampion(String tournamentId, String matchId) {
        Tournament tournament = repository.findById(tournamentId)
                .orElseThrow(() -> new TournamentNotFoundException(
                        "No se encontro un torneo con id " + tournamentId));

        ChampionAssignment assignment = tournament.assignChampionWhenFinalMatchFinished(matchId);
        repository.save(tournament);
        return assignment;
    }
}
