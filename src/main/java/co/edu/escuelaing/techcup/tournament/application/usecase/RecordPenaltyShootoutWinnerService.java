package co.edu.escuelaing.techcup.tournament.application.usecase;

import lombok.RequiredArgsConstructor;

import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.RecordPenaltyShootoutWinnerUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecordPenaltyShootoutWinnerService implements RecordPenaltyShootoutWinnerUseCase {

    private final TournamentRepositoryPort repository;

    @Override
    public void recordWinner(RecordPenaltyShootoutWinnerCommand command) {
        Tournament tournament = repository.findById(command.tournamentId())
                .orElseThrow(() -> new TournamentNotFoundException(command.tournamentId().toString()));

        tournament.recordPenaltyShootoutWinner(command.matchId(), command.winnerTeamId());
        repository.save(tournament);
    }
}
