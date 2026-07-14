package co.edu.escuelaing.techcup.tournament.service.impl;

import co.edu.escuelaing.techcup.tournament.exception.HistoricalTournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.service.ports.ConsultHistoricalTournamentsUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.TournamentRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsultHistoricalTournamentsService implements ConsultHistoricalTournamentsUseCase {

    private final TournamentRepositoryPort tournamentRepository;

    public ConsultHistoricalTournamentsService(TournamentRepositoryPort tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    @Override
    public List<Tournament> findAll() {
        return tournamentRepository.findAllByStatus(TournamentStatus.FINISHED);
    }

    @Override
    public Tournament findById(String tournamentId) {
        return tournamentRepository.findByIdAndStatus(tournamentId, TournamentStatus.FINISHED)
                .orElseThrow(() -> new HistoricalTournamentNotFoundException(tournamentId));
    }
}
