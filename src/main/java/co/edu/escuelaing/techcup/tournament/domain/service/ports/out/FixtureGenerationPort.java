package co.edu.escuelaing.techcup.tournament.domain.service.ports.out;

import co.edu.escuelaing.techcup.tournament.domain.model.Match;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentFormat;

import java.util.List;

public interface FixtureGenerationPort {

    List<Match> generateFixture(FixtureGenerationRequest request);

    record FixtureGenerationRequest(
            String tournamentId, List<String> approvedTeamIds, TournamentFormat format) {}
}
