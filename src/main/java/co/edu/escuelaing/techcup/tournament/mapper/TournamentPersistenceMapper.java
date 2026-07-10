package co.edu.escuelaing.techcup.tournament.mapper;

import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.entity.document.TournamentDocument;

import java.util.ArrayList;

public class TournamentPersistenceMapper {

    private TournamentPersistenceMapper() {}

    public static Tournament toDomain(TournamentDocument document) {
        return Tournament.reconstruct(
                document.getId(),
                document.getName(),
                document.getNumberOfTeams(),
                document.getCost(),
                document.getStartDate(),
                document.getEndDate(),
                document.getRegistrationDeadline(),
                TournamentStatus.valueOf(document.getStatus()),
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    public static TournamentDocument toDocument(Tournament domain) {
        return new TournamentDocument(
                domain.getId(),
                domain.getName(),
                domain.getNumberOfTeams(),
                domain.getCost(),
                domain.getStartDate(),
                domain.getEndDate(),
                domain.getRegistrationDeadline(),
                domain.getStatus().name(),
                domain.getRulebookFileId()
        );
    }
}
