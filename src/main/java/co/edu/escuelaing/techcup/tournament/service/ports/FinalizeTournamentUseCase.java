package co.edu.escuelaing.techcup.tournament.service.ports;

import co.edu.escuelaing.techcup.tournament.service.Tournament;

public interface FinalizeTournamentUseCase {
    Tournament finalizeTournament(String tournamentId);
}
