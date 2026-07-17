package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mapper;

import co.edu.escuelaing.techcup.tournament.domain.model.BracketNode;
import co.edu.escuelaing.techcup.tournament.domain.model.BracketNodeStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.BracketSlot;
import co.edu.escuelaing.techcup.tournament.domain.model.Enrollment;
import co.edu.escuelaing.techcup.tournament.domain.model.EnrollmentStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.Match;
import co.edu.escuelaing.techcup.tournament.domain.model.MatchPhase;
import co.edu.escuelaing.techcup.tournament.domain.model.MatchStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.RegistrationStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.Round;
import co.edu.escuelaing.techcup.tournament.domain.model.TeamRegistration;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentFormat;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentType;
import co.edu.escuelaing.techcup.tournament.domain.model.ChampionResolution;
import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo.BracketNodeDocument;
import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo.EnrollmentDocument;
import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo.MatchDocument;
import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo.TeamRegistrationDocument;
import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo.TournamentDocument;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code Tournament} se reconstruye vía {@link Tournament#reconstruct} (fábrica estática que
 * resguarda invariantes de agregado), por lo que MapStruct no puede generar la construcción del
 * dominio por reflexión de campos; los métodos por defecto delegan a esa fábrica en vez de a un
 * mapeo automático de propiedades.
 */
@Mapper(componentModel = "spring")
public interface TournamentPersistenceMapper {

    default Tournament toDomain(TournamentDocument document) {
        return Tournament.builder()
                .id(document.getId())
                .name(document.getName())
                .type(TournamentType.valueOf(document.getType()))
                .format(TournamentFormat.valueOf(document.getFormat()))
                .numberOfTeams(document.getNumberOfTeams())
                .cost(document.getCost())
                .startDate(document.getStartDate())
                .endDate(document.getEndDate())
                .registrationDeadline(document.getRegistrationDeadline())
                .matchStartTime(document.getMatchStartTime())
                .matchEndTime(document.getMatchEndTime())
                .status(TournamentStatus.valueOf(document.getStatus()))
                .teams(toTeams(document.getTeams()))
                .matches(toMatches(document.getMatches()))
                .bracketNodes(toBracketNodes(document.getBracketNodes()))
                .championTeamId(document.getChampionTeamId())
                .runnerUpTeamId(document.getRunnerUpTeamId())
                .championResolution(document.getChampionResolution() != null
                        ? ChampionResolution.valueOf(document.getChampionResolution())
                        : null)
                .paused(document.isPaused())
                .active(document.getActive() == null || document.getActive())
                .enrollments(toEnrollmentDomainList(document.getEnrollments()))
                .version(document.getVersion())
                .reconstruct();
    }

    default TournamentDocument toDocument(Tournament domain) {
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
                domain.getRunnerUpTeamId(),
                domain.getChampionResolution() != null ? domain.getChampionResolution().name() : null,
                toTeamDocuments(domain.getTeams()),
                toMatchDocuments(domain.getMatches()),
                toBracketNodeDocuments(domain.getBracketNodes()),
                toEnrollmentDocumentList(domain.getEnrollments()),
                domain.isPaused(),
                domain.isActive(),
                domain.getVersion()
        );
    }

    private List<TeamRegistration> toTeams(List<TeamRegistrationDocument> documents) {
        if (documents == null) return new ArrayList<>();
        return documents.stream()
                .map(d -> new TeamRegistration(d.getTeamId(), d.getTeamName(),
                        RegistrationStatus.valueOf(d.getRegistrationStatus())))
                .toList();
    }

    private List<TeamRegistrationDocument> toTeamDocuments(List<TeamRegistration> teams) {
        if (teams == null) return new ArrayList<>();
        return teams.stream()
                .map(t -> new TeamRegistrationDocument(t.getTeamId(), t.getTeamName(),
                        t.getRegistrationStatus().name(), t.getPoints()))
                .toList();
    }

    private List<Match> toMatches(List<MatchDocument> documents) {
        if (documents == null) return new ArrayList<>();
        return documents.stream()
                .map(d -> Match.builder()
                        .matchId(d.getMatchId())
                        .homeTeamId(d.getHomeTeamId())
                        .awayTeamId(d.getAwayTeamId())
                        .status(MatchStatus.valueOf(d.getStatus()))
                        .finalMatch(d.isFinalMatch())
                        .homeScore(d.getHomeScore())
                        .awayScore(d.getAwayScore())
                        .penaltyShootoutWinnerTeamId(d.getPenaltyShootoutWinnerTeamId())
                        .active(d.getActive() == null || d.getActive())
                        .groupName(d.getGroupName())
                        .matchday(d.getMatchday())
                        .phase(d.getPhase() != null ? MatchPhase.valueOf(d.getPhase()) : null)
                        .tournamentId(d.getTournamentId())
                        .build())
                .toList();
    }

    private List<MatchDocument> toMatchDocuments(List<Match> matches) {
        if (matches == null) return new ArrayList<>();
        return matches.stream()
                .map(m -> new MatchDocument(m.getMatchId(), m.getHomeTeamId(), m.getAwayTeamId(),
                        m.getStatus().name(), m.isFinalMatch(), m.getHomeScore(), m.getAwayScore(),
                        m.getPenaltyShootoutWinnerTeamId(), m.isActive(), m.getGroupName(), m.getMatchday(),
                        m.getPhase() != null ? m.getPhase().name() : null, m.getTournamentId()))
                .toList();
    }

    private List<BracketNode> toBracketNodes(List<BracketNodeDocument> documents) {
        if (documents == null) return new ArrayList<>();
        return documents.stream()
                .map(d -> BracketNode.reconstruct(
                        d.getNodeId(),
                        Round.valueOf(d.getRound()),
                        d.getSlotA(),
                        d.getSlotB(),
                        d.getMatchId(),
                        BracketNodeStatus.valueOf(d.getStatus()),
                        d.getWinnerTeamId(),
                        d.getLoserTeamId(),
                        d.getAdvanceToNodeId(),
                        d.getAdvanceToSlot() != null ? BracketSlot.valueOf(d.getAdvanceToSlot()) : null))
                .toList();
    }

    private List<BracketNodeDocument> toBracketNodeDocuments(List<BracketNode> nodes) {
        if (nodes == null) return new ArrayList<>();
        return nodes.stream()
                .map(n -> new BracketNodeDocument(
                        n.getNodeId(),
                        n.getRound().name(),
                        n.getSlotA(),
                        n.getSlotB(),
                        n.getMatchId(),
                        n.getStatus().name(),
                        n.getWinnerTeamId(),
                        n.getLoserTeamId(),
                        n.getAdvanceToNodeId(),
                        n.getAdvanceToSlot() != null ? n.getAdvanceToSlot().name() : null))
                .toList();
    }

    private List<Enrollment> toEnrollmentDomainList(List<EnrollmentDocument> documents) {
        if (documents == null) return new ArrayList<>();
        return documents.stream()
                .map(d -> new Enrollment(
                        d.getEnrollmentId(),
                        d.getTeamId(),
                        d.getTeamName(),
                        EnrollmentStatus.valueOf(d.getStatus()),
                        d.getConfirmationDate(),
                        d.getReservationExpiresAt()
                ))
                .toList();
    }

    private List<EnrollmentDocument> toEnrollmentDocumentList(List<Enrollment> enrollments) {
        if (enrollments == null) return new ArrayList<>();
        return enrollments.stream()
                .map(e -> new EnrollmentDocument(
                        e.getEnrollmentId(),
                        e.getTeamId(),
                        e.getTeamName(),
                        e.getStatus().name(),
                        e.getPoints(),
                        e.getConfirmationDate(),
                        e.getReservationExpiresAt()
                ))
                .toList();
    }
}
