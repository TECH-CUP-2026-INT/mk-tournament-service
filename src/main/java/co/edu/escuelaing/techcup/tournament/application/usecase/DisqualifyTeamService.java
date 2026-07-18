package co.edu.escuelaing.techcup.tournament.application.usecase;

import lombok.RequiredArgsConstructor;

import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.DisqualificationReason;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.DisqualifyTeamUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DisqualifyTeamService implements DisqualifyTeamUseCase {

    private final TournamentRepositoryPort repository;


    @Override
    public Tournament disqualify(UUID tournamentId, UUID teamId, DisqualificationReason reason) {
        Tournament tournament = repository.findById(tournamentId)
                .orElseThrow(() -> new TournamentNotFoundException(tournamentId.toString()));

        tournament.disqualifyTeam(teamId, reason);

        return repository.save(tournament);
    }
}
