package co.edu.escuelaing.techcup.tournament.application.usecase;

import lombok.RequiredArgsConstructor;

import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.model.Tournament;
import co.edu.escuelaing.techcup.tournament.domain.model.TournamentStatus;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.GetActiveTournamentUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.TournamentRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * Punto de integración para Estadísticas (ver su TournamentClientImpl):
 * resuelve el torneo "activo" = el que está actualmente EN CURSO
 * (IN_PROGRESS). Si hay más de uno, gana el de fecha de inicio más
 * reciente; ante empate exacto de fecha, por id, para que el resultado
 * sea determinista y no dependa del orden en que Mongo devuelva los
 * documentos.
 */
@Service
@RequiredArgsConstructor
public class GetActiveTournamentService implements GetActiveTournamentUseCase {

    private final TournamentRepositoryPort tournamentRepository;

    @Override
    public Tournament getActiveTournament() {
        List<Tournament> inProgress = tournamentRepository.findAllByStatus(TournamentStatus.IN_PROGRESS);
        return inProgress.stream()
                .max(Comparator.comparing(Tournament::getStartDate).thenComparing(Tournament::getId))
                .orElseThrow(() -> new TournamentNotFoundException("No hay ningún torneo en curso"));
    }
}
