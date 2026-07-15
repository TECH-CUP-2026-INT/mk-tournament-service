package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;

import java.util.List;

public interface ConsultHistoricalTournamentsUseCase {

    List<Tournament> findAll();

    Tournament findById(String tournamentId);
}
