package co.edu.escuelaing.techcup.tournament.mapper;

import co.edu.escuelaing.techcup.tournament.service.Enrollment;
import co.edu.escuelaing.techcup.tournament.service.EnrollmentStatus;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.TournamentFormat;
import co.edu.escuelaing.techcup.tournament.service.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.service.TournamentType;
import co.edu.escuelaing.techcup.tournament.service.ChampionResolution;
import co.edu.escuelaing.techcup.tournament.entity.document.EnrollmentDocument;
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
                toEnrollmentDomainList(document.getTeams()),
                new ArrayList<>(),
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
                toEnrollmentDocumentList(domain.getTeams()),
                domain.getRulebookFileId(),
                domain.getChampionTeamId(),
                domain.getChampionResolution() != null ? domain.getChampionResolution().name() : null
        );
    }

    private static List<Enrollment> toEnrollmentDomainList(List<EnrollmentDocument> documents) {
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

    private static List<EnrollmentDocument> toEnrollmentDocumentList(List<Enrollment> enrollments) {
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
