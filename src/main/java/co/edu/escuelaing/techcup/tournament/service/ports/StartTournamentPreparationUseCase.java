package co.edu.escuelaing.techcup.tournament.service.ports;

import co.edu.escuelaing.techcup.tournament.service.Tournament;

public interface StartTournamentPreparationUseCase {
    Tournament startPreparation(String tournamentId);
}
