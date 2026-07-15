package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mapper;

import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo.PlayerSanctionDocument;
import co.edu.escuelaing.techcup.tournament.domain.model.PlayerSanction;
import co.edu.escuelaing.techcup.tournament.domain.model.SanctionType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlayerSanctionPersistenceMapper {

    default PlayerSanction toDomain(PlayerSanctionDocument document) {
        return PlayerSanction.reconstruct(
                document.getId(),
                document.getPlayerId(),
                SanctionType.valueOf(document.getType()),
                document.getMatchesRemaining()
        );
    }

    default PlayerSanctionDocument toDocument(PlayerSanction domain) {
        return new PlayerSanctionDocument(
                domain.getId(),
                domain.getPlayerId(),
                domain.getType().name(),
                domain.getMatchesRemaining()
        );
    }
}
