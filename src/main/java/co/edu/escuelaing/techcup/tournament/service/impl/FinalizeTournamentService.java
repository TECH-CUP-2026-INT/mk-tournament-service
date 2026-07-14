package co.edu.escuelaing.techcup.tournament.service.impl;

import co.edu.escuelaing.techcup.tournament.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.service.Tournament;
import co.edu.escuelaing.techcup.tournament.service.ports.FinalizeTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.service.ports.RecognitionAwardPort;
import co.edu.escuelaing.techcup.tournament.service.ports.TournamentRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class FinalizeTournamentService implements FinalizeTournamentUseCase {

    private static final Logger log = LoggerFactory.getLogger(FinalizeTournamentService.class);

    private final TournamentRepositoryPort repository;
    private final RecognitionAwardPort recognitionAwardPort;

    public FinalizeTournamentService(TournamentRepositoryPort repository,
                                      RecognitionAwardPort recognitionAwardPort) {
        this.repository = repository;
        this.recognitionAwardPort = recognitionAwardPort;
    }

    @Override
    public Tournament finalizeTournament(String tournamentId) {
        Tournament tournament = repository.findById(tournamentId)
                .orElseThrow(() -> new TournamentNotFoundException(
                        "No se encontro un torneo con id " + tournamentId));

        tournament.finish(LocalDate.now());

        Tournament saved = repository.save(tournament);

        // Efecto secundario: no debe bloquear la finalización si falla
        // (Constraint de la historia: "must not block finalization if it fails").
        try {
            recognitionAwardPort.triggerAwards(tournamentId);
        } catch (RuntimeException e) {
            log.warn("No se pudieron disparar los reconocimientos para el torneo '{}'", tournamentId, e);
        }

        return saved;
    }
}
