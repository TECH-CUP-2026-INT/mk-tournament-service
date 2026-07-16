package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.ChampionAssignment;

import java.util.UUID;

public interface AssignChampionUseCase {

    ChampionAssignment assignChampion(UUID tournamentId, UUID matchId);
}
