package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.adapter;

import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mapper.ScheduledMatchPersistenceMapper;
import co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.mongo.ScheduledMatchMongoRepository;
import co.edu.escuelaing.techcup.tournament.domain.model.ScheduledMatch;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.ScheduledMatchRepositoryPort;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class ScheduledMatchRepositoryAdapter implements ScheduledMatchRepositoryPort {

    private final ScheduledMatchMongoRepository mongoRepository;
    private final ScheduledMatchPersistenceMapper mapper;

    public ScheduledMatchRepositoryAdapter(ScheduledMatchMongoRepository mongoRepository,
                                            ScheduledMatchPersistenceMapper mapper) {
        this.mongoRepository = mongoRepository;
        this.mapper = mapper;
    }

    @Override
    public ScheduledMatch save(ScheduledMatch scheduledMatch) {
        var saved = mongoRepository.save(mapper.toDocument(scheduledMatch));
        return mapper.toDomain(saved);
    }

    @Override
    public boolean existsConflict(String courtId, String refereeId, LocalDate matchDate, LocalTime matchTime) {
        return mongoRepository.existsByCourtIdAndMatchDateAndMatchTimeOrRefereeIdAndMatchDateAndMatchTime(
                courtId, matchDate, matchTime, refereeId, matchDate, matchTime);
    }
}
