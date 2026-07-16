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
        Tournament newTournament = Tournament.builder()
                .name(command.name())
                .type(command.type())
                .format(command.format())
                .numberOfTeams(command.numberOfTeams())
                .cost(command.cost())
                .startDate(command.startDate())
                .endDate(command.endDate())
                .registrationDeadline(command.registrationDeadline())
                .matchStartTime(command.matchStartTime())
                .matchEndTime(command.matchEndTime())
                .create();
        return repository.save(newTournament);
    }
}
