package co.edu.escuelaing.techcup.tournament.application.usecase;

import lombok.RequiredArgsConstructor;

import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.FinalizeTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.RecognitionAwardPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentEventPublisherPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FinalizeTournamentService implements FinalizeTournamentUseCase {

    private static final Logger log = LoggerFactory.getLogger(FinalizeTournamentService.class);

    private final TournamentRepositoryPort repository;
    private final RecognitionAwardPort recognitionAwardPort;
    private final TournamentEventPublisherPort tournamentEventPublisher;


    @Override
    public Tournament finalizeTournament(UUID tournamentId) {
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

        tournamentEventPublisher.publishTournamentFinalized(tournamentId);

        return saved;
    }
}
