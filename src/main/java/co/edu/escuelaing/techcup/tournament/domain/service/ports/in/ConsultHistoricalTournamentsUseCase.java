package co.edu.escuelaing.techcup.tournament.domain.service.ports.in;

import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;

import java.util.List;
import java.util.UUID;

public interface ConsultHistoricalTournamentsUseCase {

    List<Tournament> findAll();

    Tournament findById(UUID tournamentId);
}
