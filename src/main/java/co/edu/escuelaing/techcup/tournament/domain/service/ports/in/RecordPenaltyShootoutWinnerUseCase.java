package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

public interface RecordPenaltyShootoutWinnerUseCase {

    void recordWinner(RecordPenaltyShootoutWinnerCommand command);

    record RecordPenaltyShootoutWinnerCommand(String tournamentId, String matchId, String winnerTeamId) {}
}
