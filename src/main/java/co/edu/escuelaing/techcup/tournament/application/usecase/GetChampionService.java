package co.edu.escuelaing.techcup.tournament.application.usecase;

import lombok.RequiredArgsConstructor;

import co.edu.escuelaing.techcup.tournament.domain.exception.ChampionAssignmentNotAllowedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.ChampionAssignment;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.GetChampionUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetChampionService implements GetChampionUseCase {

    private final TournamentRepositoryPort repository;


    @Override
    public ChampionAssignment getChampion(String tournamentId) {
        Tournament tournament = repository.findById(tournamentId)
                .orElseThrow(() -> new TournamentNotFoundException(
                        "No se encontro un torneo con id " + tournamentId));

        tournament.assertActive();

        if (tournament.getChampionTeamId() == null || tournament.getChampionResolution() == null) {
            throw new ChampionAssignmentNotAllowedException(
                    "El torneo aún no tiene campeón asignado");
        }

        return new ChampionAssignment(tournament.getChampionTeamId(), tournament.getChampionResolution());
    }
}
