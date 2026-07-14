package co.edu.escuelaing.techcup.tournament.service.ports;

import co.edu.escuelaing.techcup.tournament.service.ChampionAssignment;

public interface AssignChampionUseCase {

    ChampionAssignment assignChampion(String tournamentId, String matchId);
}
