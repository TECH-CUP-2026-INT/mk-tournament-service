package co.edu.escuelaing.techcup.tournament.infrastructure.persistence.mapper;

import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.infrastructure.persistence.TournamentEntity;

public class TournamentPersistenceMapper {

    private TournamentPersistenceMapper() {}

    public static Tournament toDomain(TournamentEntity entity) {
        return new Tournament(
                entity.getId(),
                entity.getName(),
                TournamentStatus.valueOf(entity.getStatus())
        );
    }

    public static TournamentEntity toEntity(Tournament domain) {
        return new TournamentEntity(
                domain.getId(),
                domain.getName(),
                domain.getStatus().name()
        );
    }
}
