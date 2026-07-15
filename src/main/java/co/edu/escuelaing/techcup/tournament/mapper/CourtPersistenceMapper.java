package co.edu.escuelaing.techcup.tournament.mapper;

import co.edu.escuelaing.techcup.tournament.entity.document.CourtDocument;
import co.edu.escuelaing.techcup.tournament.service.Court;
import co.edu.escuelaing.techcup.tournament.service.CourtSection;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CourtPersistenceMapper {

    default Court toDomain(CourtDocument document) {
        return Court.reconstruct(
                document.getId(),
                document.getTournamentId(),
                CourtSection.valueOf(document.getSection()),
                document.getDescription(),
                document.getImageId(),
                document.getMatchId()
        );
    }

    default CourtDocument toDocument(Court domain) {
        return new CourtDocument(
                domain.getId(),
                domain.getTournamentId(),
                domain.getSection().name(),
                domain.getDescription(),
                domain.getImageId(),
                domain.getMatchId()
        );
    }
}
