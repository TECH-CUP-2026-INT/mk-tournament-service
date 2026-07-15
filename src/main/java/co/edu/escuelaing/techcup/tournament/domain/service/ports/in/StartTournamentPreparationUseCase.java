package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;

public interface StartTournamentPreparationUseCase {
    Tournament startPreparation(String tournamentId);
}
