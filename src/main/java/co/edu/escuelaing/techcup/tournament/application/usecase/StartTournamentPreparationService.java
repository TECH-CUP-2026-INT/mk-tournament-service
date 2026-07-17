package co.edu.escuelaing.techcup.tournament.application.usecase;

import lombok.RequiredArgsConstructor;

import co.edu.escuelaing.techcup.tournament.domain.exception.FixtureGenerationFailedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.InvalidGroupStageTeamCountException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.Enrollment;
import co.edu.escuelaing.techcup.tournament.domain.model.EnrollmentStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.Match;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentFormat;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.FixtureGenerationPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.StartTournamentPreparationUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StartTournamentPreparationService implements StartTournamentPreparationUseCase {

    // Formato "mundial" (grupos + eliminatoria): tamaños que arman grupos de 4 completos.
    private static final Set<Integer> VALID_GROUP_STAGE_TEAM_COUNTS = Set.of(8, 16, 32);

    private final TournamentRepositoryPort repository;
    private final FixtureGenerationPort fixtureGenerationPort;


    @Override
    public Tournament startPreparation(UUID tournamentId) {
        Tournament tournament = repository.findById(tournamentId)
                .orElseThrow(() -> new TournamentNotFoundException(tournamentId.toString()));

        tournament.validateReadyForPreparation();

        List<UUID> enrolledTeamIds = tournament.getEnrollments().stream()
                .filter(team -> team.getStatus() == EnrollmentStatus.ENROLLED)
                .map(Enrollment::getTeamId)
                .toList();

        if (tournament.getFormat() == TournamentFormat.GROUPS
                && !VALID_GROUP_STAGE_TEAM_COUNTS.contains(enrolledTeamIds.size())) {
            throw new InvalidGroupStageTeamCountException(
                    "El formato de grupos (mundial) requiere 8, 16 o 32 equipos inscritos, hay "
                            + enrolledTeamIds.size());
        }

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
