package co.edu.escuelaing.techcup.tournament.service.impl;

import co.edu.escuelaing.techcup.tournament.exception.ChampionAssignmentNotAllowedException;
import co.edu.escuelaing.techcup.tournament.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.service.ChampionAssignment;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.ports.GetChampionUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.TournamentRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class GetChampionService implements GetChampionUseCase {

    private final TournamentRepositoryPort repository;

    public GetChampionService(TournamentRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public ChampionAssignment getChampion(String tournamentId) {
        Tournament tournament = repository.findById(tournamentId)
                .orElseThrow(() -> new TournamentNotFoundException(
                        "No se encontro un torneo con id " + tournamentId));

        if (tournament.getChampionTeamId() == null || tournament.getChampionResolution() == null) {
            throw new ChampionAssignmentNotAllowedException(
                    "El torneo aún no tiene campeón asignado");
        }

        return new ChampionAssignment(tournament.getChampionTeamId(), tournament.getChampionResolution());
    }
}
