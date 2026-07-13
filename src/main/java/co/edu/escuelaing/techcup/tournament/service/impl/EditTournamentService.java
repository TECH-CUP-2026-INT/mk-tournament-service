package co.edu.escuelaing.techcup.tournament.service.impl;

import co.edu.escuelaing.techcup.tournament.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.ports.EditTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.TournamentRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class EditTournamentService implements EditTournamentUseCase {

    private final TournamentRepositoryPort tournamentRepository;

    public EditTournamentService(TournamentRepositoryPort tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    @Override
    public Tournament edit(EditTournamentCommand command) {
        Tournament tournament = tournamentRepository.findById(command.tournamentId())
                .orElseThrow(() -> new TournamentNotFoundException(command.tournamentId()));

        tournament.update(
                command.name(),
                command.tournamentType(),
                command.tournamentFormat(),
                command.numberOfTeams(),
                command.cost(),
                command.registrationDeadline(),
                command.startDate(),
                command.endDate()
        );

        return tournamentRepository.save(tournament);
    }
}
