package co.edu.escuelaing.techcup.tournament.application.usecase;

import lombok.RequiredArgsConstructor;

import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.GroupStandingsCalculator;
import co.edu.escuelaing.techcup.tournament.domain.model.GroupTable;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ViewGroupStandingsUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ViewGroupStandingsService implements ViewGroupStandingsUseCase {

    private final TournamentRepositoryPort repository;


    @Override
    public List<GroupTable> getStandings(UUID tournamentId) {
        Tournament tournament = repository.findById(tournamentId)
                .orElseThrow(() -> new TournamentNotFoundException(
                        "No se encontro un torneo con id " + tournamentId));

        tournament.assertActive();

        return GroupStandingsCalculator.computeAll(tournament.getMatches(),
                GroupStandingsCalculator.ineligibleTeamIds(tournament.getTeams(), tournament.getMatches()));
    }
}
