package co.edu.escuelaing.techcup.tournament.mapper;

import co.edu.escuelaing.techcup.tournament.entity.document.TournamentParticipantDocument;
import co.edu.escuelaing.techcup.tournament.service.ParticipantStatus;
import co.edu.escuelaing.techcup.tournament.service.TournamentParticipant;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TournamentParticipantPersistenceMapper {

    default TournamentParticipant toDomain(TournamentParticipantDocument document) {
        return TournamentParticipant.reconstruct(
                document.getId(),
                document.getTournamentId(),
                document.getUserId(),
                ParticipantStatus.valueOf(document.getStatus())
        );
    }

    default TournamentParticipantDocument toDocument(TournamentParticipant domain) {
        return new TournamentParticipantDocument(
                domain.getId(),
                domain.getTournamentId(),
                domain.getUserId(),
                domain.getStatus().name()
        );
    }
}
