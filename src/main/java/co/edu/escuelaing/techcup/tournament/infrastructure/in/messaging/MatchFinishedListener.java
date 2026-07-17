package co.edu.escuelaing.techcup.tournament.infrastructure.in.messaging;

import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ProcessMatchResultUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ProcessMatchResultUseCase.ProcessMatchResultCommand;
import co.edu.escuelaing.techcup.tournament.infrastructure.config.RabbitMQConfig;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.messaging.dto.MatchFinishedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Deserializa el evento {@code techcup.match.finished} y delega TODO a
 * {@link ProcessMatchResultUseCase} — sin lógica de negocio en el listener.
 */
@Component
@RequiredArgsConstructor
public class MatchFinishedListener {

    private final ProcessMatchResultUseCase processMatchResult;

    @RabbitListener(queues = RabbitMQConfig.MATCH_FINISHED_QUEUE)
    public void onMatchFinished(MatchFinishedEvent event) {
        processMatchResult.process(new ProcessMatchResultCommand(
                event.matchId(),
                event.tournamentId(),
                event.fase(),
                event.golesA(),
                event.golesB(),
                event.ganadorId(),
                event.ausenteId()));
    }
}
