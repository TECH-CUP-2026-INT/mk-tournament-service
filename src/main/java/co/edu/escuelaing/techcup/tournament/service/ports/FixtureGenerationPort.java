package co.edu.escuelaing.techcup.tournament.service.ports;

import co.edu.escuelaing.techcup.tournament.service.Match;
import co.edu.escuelaing.techcup.tournament.service.TournamentFormat;

import java.util.List;

public interface FixtureGenerationPort {

    List<Match> generateFixture(FixtureGenerationRequest request);

    record FixtureGenerationRequest(
            String tournamentId, List<String> approvedTeamIds, TournamentFormat format) {}
}
