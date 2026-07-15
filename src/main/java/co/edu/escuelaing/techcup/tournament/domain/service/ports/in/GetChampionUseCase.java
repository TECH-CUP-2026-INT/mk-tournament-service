package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.ChampionAssignment;

public interface GetChampionUseCase {

    ChampionAssignment getChampion(String tournamentId);
}
