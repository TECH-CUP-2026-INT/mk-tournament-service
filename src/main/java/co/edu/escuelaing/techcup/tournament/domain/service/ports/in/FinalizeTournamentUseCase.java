package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;

public interface FinalizeTournamentUseCase {
    Tournament finalizeTournament(String tournamentId);
}
