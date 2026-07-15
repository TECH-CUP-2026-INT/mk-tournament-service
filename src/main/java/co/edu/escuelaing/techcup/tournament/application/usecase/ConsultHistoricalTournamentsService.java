package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.exception.HistoricalTournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ConsultHistoricalTournamentsUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
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
