package co.edu.escuelaing.techcup.tournament.service.ports;

import co.edu.escuelaing.techcup.tournament.service.Tournament;

import java.util.List;

public interface ConsultHistoricalTournamentsUseCase {

    List<Tournament> findAll();

    Tournament findById(String tournamentId);
}
