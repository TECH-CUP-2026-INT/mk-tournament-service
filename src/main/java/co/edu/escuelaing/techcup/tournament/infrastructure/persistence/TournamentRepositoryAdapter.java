package co.edu.escuelaing.techcup.tournament.infrastructure.persistence;

import co.edu.escuelaing.techcup.tournament.domain.model.*;
import co.edu.escuelaing.techcup.tournament.domain.port.out.TournamentRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class TournamentRepositoryAdapter implements TournamentRepositoryPort {

    private final TournamentJpaRepository mongo;

    public TournamentRepositoryAdapter(TournamentJpaRepository mongo) {
        this.mongo = mongo;
    }

    @Override
    public Tournament save(Tournament tournament) {
        return toDomain(mongo.save(toEntity(tournament)));
    }

    @Override
    public Optional<Tournament> findById(String id) {
        return mongo.findById(id).map(this::toDomain);
    }

    private TournamentEntity toEntity(Tournament t) {
        TournamentEntity e = new TournamentEntity();
        e.setId(t.getId());
        e.setName(t.getName());
        e.setStartDate(t.getStartDate());
        e.setEndDate(t.getEndDate());
        e.setDurationDays(t.getDurationDays());
        e.setStatus(t.getStatus());
        e.setEliminationType(t.getEliminationType());

        List<TournamentEntity.TeamRegistrationEmbedded> teams = new ArrayList<>();
        for (TeamRegistration tr : t.getTeams()) {
            TournamentEntity.TeamRegistrationEmbedded te = new TournamentEntity.TeamRegistrationEmbedded();
            te.setTeamId(tr.getTeamId());
            te.setTeamName(tr.getTeamName());
            te.setRegistrationStatus(tr.getRegistrationStatus());
            te.setPoints(tr.getPoints());
            teams.add(te);
        }
        e.setTeams(teams);

        List<TournamentEntity.MatchEmbedded> matches = new ArrayList<>();
        for (Match m : t.getMatches()) {
            TournamentEntity.MatchEmbedded me = new TournamentEntity.MatchEmbedded();
            me.setMatchId(m.getMatchId());
            me.setHomeTeamId(m.getHomeTeamId());
            me.setAwayTeamId(m.getAwayTeamId());
            me.setStatus(m.getStatus());
            matches.add(me);
        }
        e.setMatches(matches);
        return e;
    }

    private Tournament toDomain(TournamentEntity e) {
        Tournament t = new Tournament(e.getId(), e.getName(), e.getStartDate(), e.getEndDate(), e.getEliminationType());
        t.setDurationDays(e.getDurationDays());
        t.setStatus(e.getStatus());

        List<TeamRegistration> teams = new ArrayList<>();
        if (e.getTeams() != null) {
            for (TournamentEntity.TeamRegistrationEmbedded te : e.getTeams()) {
                TeamRegistration tr = new TeamRegistration(te.getTeamId(), te.getTeamName(), te.getRegistrationStatus());
                tr.setPoints(te.getPoints());
                teams.add(tr);
            }
        }
        t.setTeams(teams);

        List<Match> matches = new ArrayList<>();
        if (e.getMatches() != null) {
            for (TournamentEntity.MatchEmbedded me : e.getMatches()) {
                matches.add(new Match(me.getMatchId(), me.getHomeTeamId(), me.getAwayTeamId(), me.getStatus()));
            }
        }
        t.setMatches(matches);
        return t;
    }
}
