package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mapper;

import co.edu.escuelaing.techcup.tournament.domain.model.Enrollment;
import co.edu.escuelaing.techcup.tournament.domain.model.EnrollmentStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.Match;
import co.edu.escuelaing.techcup.tournament.domain.model.MatchStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.RegistrationStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.TeamRegistration;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentFormat;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentType;
import co.edu.escuelaing.techcup.tournament.domain.model.ChampionResolution;
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
                        : null,
                document.isPaused(),
                document.getActive() == null || document.getActive(),
                toEnrollmentDomainList(document.getEnrollments()),
                document.getVersion()
        );
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
                domain.getChampionResolution() != null ? domain.getChampionResolution().name() : null,
                toTeamDocuments(domain.getTeams()),
                toMatchDocuments(domain.getMatches()),
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
                .map(d -> new Match(d.getMatchId(), d.getHomeTeamId(), d.getAwayTeamId(),
                        MatchStatus.valueOf(d.getStatus()), d.isFinalMatch(), d.getHomeScore(),
                        d.getAwayScore(), d.getPenaltyShootoutWinnerTeamId(),
                        d.getActive() == null || d.getActive()))
                .toList();
    }

    private List<MatchDocument> toMatchDocuments(List<Match> matches) {
        if (matches == null) return new ArrayList<>();
        return matches.stream()
                .map(m -> new MatchDocument(m.getMatchId(), m.getHomeTeamId(), m.getAwayTeamId(),
                        m.getStatus().name(), m.isFinalMatch(), m.getHomeScore(), m.getAwayScore(),
                        m.getPenaltyShootoutWinnerTeamId(), m.isActive()))
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
