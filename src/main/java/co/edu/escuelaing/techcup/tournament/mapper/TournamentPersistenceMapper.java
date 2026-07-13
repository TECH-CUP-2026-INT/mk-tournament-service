package co.edu.escuelaing.techcup.tournament.mapper;

import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.service.ChampionResolution;
import co.edu.escuelaing.techcup.tournament.service.TournamentType;
import co.edu.escuelaing.techcup.tournament.service.TournamentFormat;
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
                new ArrayList<>(),
                document.getChampionTeamId(),
                document.getChampionResolution() != null
                        ? ChampionResolution.valueOf(document.getChampionResolution())
                        : null,
                document.getTournamentType() != null
                        ? TournamentType.valueOf(document.getTournamentType())
                        : null,
                document.getTournamentFormat() != null
                        ? TournamentFormat.valueOf(document.getTournamentFormat())
                        : null
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
                domain.getRulebookFileId(),
                domain.getChampionTeamId(),
                domain.getChampionResolution() != null ? domain.getChampionResolution().name() : null,
                domain.getTournamentType() != null ? domain.getTournamentType().name() : null,
                domain.getTournamentFormat() != null ? domain.getTournamentFormat().name() : null
        );
    }
}
