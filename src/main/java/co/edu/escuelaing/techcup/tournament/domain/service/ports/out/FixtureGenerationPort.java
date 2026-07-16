package co.edu.escuelaing.techcup.tournament.domain.service.ports.out;

import co.edu.escuelaing.techcup.tournament.domain.model.Match;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentFormat;

import java.util.List;
import java.util.UUID;

/**
 * Puerto para generar el fixture (emparejamientos iniciales) de un torneo
 * según su formato. La única implementación real es aleatoria; no se
 * integra ningún proveedor externo.
 */
public interface FixtureGenerationPort {

    List<Match> generateFixture(FixtureGenerationRequest request);

    record FixtureGenerationRequest(
            UUID tournamentId, List<UUID> approvedTeamIds, TournamentFormat format) {}
}
