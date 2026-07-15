package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.exception.FixtureGenerationFailedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.EnrollmentStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.Match;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.FixtureGenerationPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.StartTournamentPreparationUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StartTournamentPreparationService implements StartTournamentPreparationUseCase {

    private final TournamentRepositoryPort repository;
    private final FixtureGenerationPort fixtureGenerationPort;

    public StartTournamentPreparationService(TournamentRepositoryPort repository,
                                              FixtureGenerationPort fixtureGenerationPort) {
        this.repository = repository;
        this.fixtureGenerationPort = fixtureGenerationPort;
    }

    @Override
    public Tournament startPreparation(String tournamentId) {
        Tournament tournament = repository.findById(tournamentId)
                .orElseThrow(() -> new TournamentNotFoundException(tournamentId));

        tournament.validateReadyForPreparation();

        List<String> enrolledTeamIds = tournament.getEnrollments().stream()
                .filter(team -> team.getStatus() == EnrollmentStatus.ENROLLED)
                .map(team -> team.getTeamId())
                .toList();

        List<Match> generatedMatches;
        try {
            generatedMatches = fixtureGenerationPort.generateFixture(
                    new FixtureGenerationPort.FixtureGenerationRequest(
                            tournamentId, enrolledTeamIds, tournament.getFormat()));
        } catch (RuntimeException e) {
            throw new FixtureGenerationFailedException(tournamentId, e);
        }

        tournament.startPreparation(generatedMatches);
        return repository.save(tournament);
    }
}
