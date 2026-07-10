package co.edu.escuelaing.techcup.tournament.infrastructure.persistence.mapper;

import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.infrastructure.persistence.TournamentDocument;

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
                domain.getStatus().name()
        );
    }
}
