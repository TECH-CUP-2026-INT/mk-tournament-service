// domain/model/Tournament.java
package co.edu.escuelaing.techcup.tournament.domain.model;

import co.edu.escuelaing.techcup.tournament.domain.exception.InvalidTournamentDataException;
import co.edu.escuelaing.techcup.tournament.domain.exception.InvalidTournamentDateRangeException;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Tournament extends AggregateRoot {

    private static final int MAX_NAME_LENGTH = 100;
    private static final int MIN_TEAMS = 2;

    private final String name;
    private final int numberOfTeams;
    private final BigDecimal cost;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final LocalDate registrationDeadline;
    private TournamentStatus status;

    private Tournament(String id, String name, int numberOfTeams, BigDecimal cost,
                       LocalDate startDate, LocalDate endDate, LocalDate registrationDeadline,
                       TournamentStatus status) {
        super(id);
        this.name = name;
        this.numberOfTeams = numberOfTeams;
        this.cost = cost;
        this.startDate = startDate;
        this.endDate = endDate;
        this.registrationDeadline = registrationDeadline;
        this.status = status;
    }

    /**
     * TC-25: crea un torneo nuevo. Siempre nace en DRAFT,
     * sin importar lo que envíe el cliente — por eso el estado
     * no es un parámetro de este método.
     */
    public static Tournament create(String name, int numberOfTeams, BigDecimal cost,
                                    LocalDate startDate, LocalDate endDate,
                                    LocalDate registrationDeadline) {
        validateName(name);
        validateNumberOfTeams(numberOfTeams);
        validateCost(cost);
        validateDateRange(startDate, endDate, registrationDeadline);

        return new Tournament(null, name, numberOfTeams, cost, startDate, endDate,
                registrationDeadline, TournamentStatus.DRAFT);
    }

    /**
     * Reconstruye un torneo que ya existía en la base de datos
     * (ya pasó las validaciones cuando se creó). No vuelve a
     * aplicar las reglas de creación ni fuerza el estado a DRAFT,
     * porque no está creando nada nuevo, solo rehidratando lo guardado.
     * Lo usa el adaptador de persistencia, nadie más.
     */
    public static Tournament reconstruct(String id, String name, int numberOfTeams, BigDecimal cost,
                                         LocalDate startDate, LocalDate endDate,
                                         LocalDate registrationDeadline, TournamentStatus status) {
        return new Tournament(id, name, numberOfTeams, cost, startDate, endDate,
                registrationDeadline, status);
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidTournamentDataException("El nombre del torneo es obligatorio");
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new InvalidTournamentDataException(
                    "El nombre no puede superar los " + MAX_NAME_LENGTH + " caracteres");
        }
    }

    private static void validateNumberOfTeams(int numberOfTeams) {
        if (numberOfTeams < MIN_TEAMS) {
            throw new InvalidTournamentDataException(
                    "La cantidad de equipos debe ser mayor o igual a " + MIN_TEAMS);
        }
    }

    private static void validateCost(BigDecimal cost) {
        if (cost == null || cost.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidTournamentDataException("El costo de inscripción no puede ser negativo");
        }
    }

    private static void validateDateRange(LocalDate startDate, LocalDate endDate,
                                          LocalDate registrationDeadline) {
        if (startDate == null || endDate == null || registrationDeadline == null) {
            throw new InvalidTournamentDataException("Las fechas del torneo son obligatorias");
        }
        if (!startDate.isAfter(registrationDeadline)) {
            throw new InvalidTournamentDateRangeException(
                    "La fecha de inicio debe ser posterior a la fecha de cierre de inscripciones");
        }
        if (endDate.isBefore(startDate)) {
            throw new InvalidTournamentDateRangeException(
                    "La fecha de fin debe ser posterior o igual a la fecha de inicio");
        }
    }

    public String getName() { return name; }
    public int getNumberOfTeams() { return numberOfTeams; }
    public BigDecimal getCost() { return cost; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public LocalDate getRegistrationDeadline() { return registrationDeadline; }
    public TournamentStatus getStatus() { return status; }
}