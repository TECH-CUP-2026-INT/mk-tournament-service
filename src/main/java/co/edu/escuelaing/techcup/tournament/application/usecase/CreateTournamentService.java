package co.edu.escuelaing.techcup.tournament.application.usecase;

import lombok.RequiredArgsConstructor;

import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.CreateTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateTournamentService implements CreateTournamentUseCase {

    private final TournamentRepositoryPort repository;


    @Override
    public Tournament create(CreateTournamentCommand command) {
        Tournament newTournament = Tournament.create(
                command.name(),
                command.type(),
                command.format(),
                command.numberOfTeams(),
                command.cost(),
                command.startDate(),
                command.endDate(),
                command.registrationDeadline(),
                command.matchStartTime(),
                command.matchEndTime()
        );
        return repository.save(newTournament);
    }
}
