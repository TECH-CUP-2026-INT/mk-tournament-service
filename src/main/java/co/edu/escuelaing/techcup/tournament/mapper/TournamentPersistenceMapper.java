package co.edu.escuelaing.techcup.tournament.mapper;

import co.edu.escuelaing.techcup.tournament.service.Match;
import co.edu.escuelaing.techcup.tournament.service.MatchStatus;
import co.edu.escuelaing.techcup.tournament.service.RegistrationStatus;
import co.edu.escuelaing.techcup.tournament.service.TeamRegistration;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.TournamentFormat;
import co.edu.escuelaing.techcup.tournament.service.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.service.TournamentType;
import co.edu.escuelaing.techcup.tournament.service.ChampionResolution;
import co.edu.escuelaing.techcup.tournament.entity.document.MatchDocument;
import co.edu.escuelaing.techcup.tournament.entity.document.TeamRegistrationDocument;
import co.edu.escuelaing.techcup.tournament.entity.document.TournamentDocument;

import java.util.ArrayList;
import java.util.List;

public class TournamentPersistenceMapper {

    private TournamentPersistenceMapper() {}

    public static Tournament toDomain(TournamentDocument document) {
        return Tournament.reconstruct(
                document.getId(),
                document.getName(),
                TournamentType.valueOf(document.getType()),
                TournamentFormat.valueOf(document.getFormat()),
                document.getNumberOfTeams(),
                document.getCost(),
                document.getStartDate(),
                document.getEndDate(),
                document.getRegistrationDeadline(),
                document.getMatchStartTime(),
                document.getMatchEndTime(),
                TournamentStatus.valueOf(document.getStatus()),
                toTeams(document.getTeams()),
                toMatches(document.getMatches()),
                document.getChampionTeamId(),
                document.getChampionResolution() != null
                        ? ChampionResolution.valueOf(document.getChampionResolution())
                        : null
        );
    }

    public static TournamentDocument toDocument(Tournament domain) {
        return new TournamentDocument(
                domain.getId(),
                domain.getName(),
                domain.getType().name(),
                domain.getFormat().name(),
                domain.getNumberOfTeams(),
                domain.getCost(),
                domain.getStartDate(),
                domain.getEndDate(),
                domain.getRegistrationDeadline(),
                domain.getMatchStartTime(),
                domain.getMatchEndTime(),
                domain.getStatus().name(),
                domain.getRulebookFileId(),
                domain.getChampionTeamId(),
                domain.getChampionResolution() != null ? domain.getChampionResolution().name() : null,
                toTeamDocuments(domain.getTeams()),
                toMatchDocuments(domain.getMatches())
        );
    }

    private static List<TeamRegistration> toTeams(List<TeamRegistrationDocument> documents) {
        if (documents == null) return new ArrayList<>();
        return documents.stream()
                .map(d -> new TeamRegistration(d.getTeamId(), d.getTeamName(),
                        RegistrationStatus.valueOf(d.getRegistrationStatus())))
                .toList();
    }

    private static List<TeamRegistrationDocument> toTeamDocuments(List<TeamRegistration> teams) {
        if (teams == null) return new ArrayList<>();
        return teams.stream()
                .map(t -> new TeamRegistrationDocument(t.getTeamId(), t.getTeamName(),
                        t.getRegistrationStatus().name(), t.getPoints()))
                .toList();
    }

    private static List<Match> toMatches(List<MatchDocument> documents) {
        if (documents == null) return new ArrayList<>();
        return documents.stream()
                .map(d -> new Match(d.getMatchId(), d.getHomeTeamId(), d.getAwayTeamId(),
                        MatchStatus.valueOf(d.getStatus()), d.isFinalMatch(), d.getHomeScore(),
                        d.getAwayScore(), d.getPenaltyShootoutWinnerTeamId()))
                .toList();
    }

    private static List<MatchDocument> toMatchDocuments(List<Match> matches) {
        if (matches == null) return new ArrayList<>();
        return matches.stream()
                .map(m -> new MatchDocument(m.getMatchId(), m.getHomeTeamId(), m.getAwayTeamId(),
                        m.getStatus().name(), m.isFinalMatch(), m.getHomeScore(), m.getAwayScore(),
                        m.getPenaltyShootoutWinnerTeamId()))
                .toList();
    }
}
