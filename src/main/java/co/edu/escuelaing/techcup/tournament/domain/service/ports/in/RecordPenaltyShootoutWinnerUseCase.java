package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import java.util.UUID;

public interface RecordPenaltyShootoutWinnerUseCase {

    void recordWinner(RecordPenaltyShootoutWinnerCommand command);

    record RecordPenaltyShootoutWinnerCommand(UUID tournamentId, UUID matchId, UUID winnerTeamId) {}
}
