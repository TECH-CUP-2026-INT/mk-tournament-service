package co.edu.escuelaing.techcup.tournament.service.ports;

import co.edu.escuelaing.techcup.tournament.service.ChampionAssignment;

public interface GetChampionUseCase {

    ChampionAssignment getChampion(String tournamentId);
}
