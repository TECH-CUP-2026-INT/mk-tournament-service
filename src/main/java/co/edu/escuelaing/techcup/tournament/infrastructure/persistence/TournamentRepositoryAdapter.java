package co.edu.escuelaing.techcup.tournament.infrastructure.persistence;

import co.edu.escuelaing.techcup.tournament.domain.model.*;
import co.edu.escuelaing.techcup.tournament.domain.port.out.TournamentRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class TournamentRepositoryAdapter implements TournamentRepositoryPort {

    private final TournamentMongoRepository mongoRepository;

    public TournamentRepositoryAdapter(TournamentMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public Tournament save(Tournament tournament) {
        TournamentDocument saved = mongoRepository.save(toDocument(tournament));
        return toDomain(saved);
    }

    @Override
    public Optional<Tournament> findById(String id) {
        return mongoRepository.findById(id).map(this::toDomain);
    }

    private TournamentDocument toDocument(Tournament tournament) {
        return new TournamentDocument(
                tournament.getId(),
                tournament.getName(),
                tournament.getType().name(),
                tournament.getFormat().name(),
                tournament.getNumberOfTeams(),
                tournament.getCost(),
                tournament.getStartDate(),
                tournament.getEndDate(),
                tournament.getRegistrationDeadline(),
                tournament.getMatchStartTime(),
                tournament.getMatchEndTime(),
                tournament.getStatus().name(),
                tournament.getRulebookFileId()
        );
    }

    private Tournament toDomain(TournamentDocument document) {
        Tournament tournament = Tournament.reconstruct(
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
                new ArrayList<>(),
                new ArrayList<>()
        );
        tournament.setRulebookFileId(document.getRulebookFileId());
        return tournament;
    }
}
