package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.EditTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
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
                command.type(),
                command.format(),
                command.numberOfTeams(),
                command.cost(),
                command.registrationDeadline(),
                command.startDate(),
                command.endDate(),
                command.matchStartTime(),
                command.matchEndTime()
        );

        return tournamentRepository.save(tournament);
    }
}
