package co.edu.escuelaing.techcup.tournament.application.usecase;

import lombok.RequiredArgsConstructor;

import co.edu.escuelaing.techcup.tournament.domain.exception.MatchNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.Match;
import co.edu.escuelaing.techcup.tournament.domain.model.MatchPhase;
import co.edu.escuelaing.techcup.tournament.domain.model.MatchStatus;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ProcessMatchResultUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.RecordMatchFinishedForSanctionsUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.RecognitionAwardPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentEventPublisherPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Manejador único de "resultado de partido" (LÓGICA 2 y 4 del contrato):
 * graba el resultado, y según la fase recalcula la tabla de grupo (cerrando
 * la fase de grupos y generando la llave si corresponde) o avanza la llave
 * eliminatoria. Sin lógica de negocio propia más allá de la orquestación —
 * las reglas viven en {@link Tournament}/{@link Match}.
 * <p>
 * Si el comando trae {@code absentTeamId} (ausenteId, agregado por Matches en
 * el commit 3958872), el partido es un walkover: se marca FINISHED_NO_SHOW en
 * vez de FINISHED (ver {@link Match#markWalkover}) en ambas fases. En GRUPOS,
 * GroupStandingsCalculator ya sabe leer ese dato directamente del partido
 * para acreditar la victoria al presente. En ELIMINATORIA, winnerTeamId ya
 * identifica al presente, así que advanceBracket resuelve igual.
 */
@Service
@RequiredArgsConstructor
public class ProcessMatchResultService implements ProcessMatchResultUseCase {

    private static final Logger log = LoggerFactory.getLogger(ProcessMatchResultService.class);

    private final TournamentRepositoryPort repository;
    private final RecordMatchFinishedForSanctionsUseCase recordMatchFinishedForSanctions;
    private final RecognitionAwardPort recognitionAwardPort;
    private final TournamentEventPublisherPort tournamentEventPublisher;


    @Override
    public void process(ProcessMatchResultCommand command) {
        Tournament tournament = repository.findById(command.tournamentId())
                .orElseThrow(() -> new TournamentNotFoundException(
                        "No se encontro un torneo con id " + command.tournamentId()));

        if (tournament.getStatus() == TournamentStatus.FINISHED) {
            log.info("Torneo '{}' ya está Finalizado: se ignora el resultado del partido '{}'",
                    command.tournamentId(), command.matchId());
            return;
        }
        if (tournament.isPaused() || !tournament.isActive()) {
            log.info("Torneo '{}' pausado o inactivo: se ignora el resultado del partido '{}'",
                    command.tournamentId(), command.matchId());
            return;
        }

        Match match = tournament.getMatches().stream()
                .filter(m -> m.getMatchId().equals(command.matchId()))
                .findFirst()
                .orElseThrow(() -> new MatchNotFoundException(tournament.getId(), command.matchId()));

        if (isAlreadyResolved(match)) {
            log.info("Partido '{}' ya estaba resuelto: reentrega idempotente ignorada", command.matchId());
            return;
        }

        if (command.absentTeamId() != null) {
            match.markWalkover(command.absentTeamId());
        } else {
            match.finishWithExternalResult(command.homeScore(), command.awayScore(), command.winnerTeamId());
        }
        recordMatchFinishedForSanctions.recordMatchFinished();

        if (command.phase() == MatchPhase.GRUPOS) {
            closeGroupStageIfComplete(tournament);
        } else if (command.phase() == MatchPhase.ELIMINATORIA) {
            tournament.advanceBracket(command.matchId());
        }

        boolean justFinished = tournament.getStatus() == TournamentStatus.FINISHED;

        repository.save(tournament);

        if (justFinished) {
            publishFinalization(tournament);
        }
    }

    private boolean isAlreadyResolved(Match match) {
        return match.getStatus() == MatchStatus.FINISHED || match.getStatus() == MatchStatus.FINISHED_NO_SHOW;
    }

    /**
     * La tabla de posiciones se recalcula al vuelo desde los partidos finalizados
     * (ver GroupStandingsCalculator) — no hay estado propio que "recomputar" aquí,
     * más allá de que el resultado ya haya quedado grabado en el Match. Solo
     * queda revisar si con este resultado se cerraron todos los grupos.
     */
    private void closeGroupStageIfComplete(Tournament tournament) {
        boolean allGroupsFinished = tournament.getMatches().stream()
                .filter(m -> m.getGroupName() != null)
                .allMatch(m -> m.getStatus() == MatchStatus.FINISHED || m.getStatus() == MatchStatus.FINISHED_NO_SHOW);
        if (allGroupsFinished && tournament.getBracketNodes().isEmpty()) {
            tournament.generateEliminationBracket();
        }
    }

    /**
     * Mismos efectos secundarios que FinalizeTournamentService cuando un
     * torneo pasa a FINISHED — necesarios aquí porque Tournament#advanceBracket
     * deja el torneo en FINISHED directamente al resolver la Final (ver su
     * javadoc) sin pasar por ese caso de uso.
     */
    private void publishFinalization(Tournament tournament) {
        try {
            recognitionAwardPort.triggerAwards(tournament.getId());
        } catch (RuntimeException e) {
            log.warn("No se pudieron disparar los reconocimientos para el torneo '{}'", tournament.getId(), e);
        }
        tournamentEventPublisher.publishTournamentFinalized(tournament.getId());
    }
}
