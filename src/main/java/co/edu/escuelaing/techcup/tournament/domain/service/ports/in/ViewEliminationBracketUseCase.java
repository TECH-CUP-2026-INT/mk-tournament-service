package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.BracketNode;

import java.util.List;
import java.util.UUID;

public interface ViewEliminationBracketUseCase {
    List<BracketNode> getBracket(UUID tournamentId);
}
