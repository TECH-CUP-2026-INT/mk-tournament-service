package co.edu.escuelaing.techcup.tournament.domain.model;

import co.edu.escuelaing.techcup.tournament.domain.exception.InvalidTournamentDataException;
import co.edu.escuelaing.techcup.tournament.domain.exception.InvalidTournamentDateRangeException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Tournament extends AggregateRoot {

    private static final int MAX_NAME_LENGTH = 100;
    private static final int MIN_TEAMS = 3;

    private final String name;
    private final int numberOfTeams;
    private final BigDecimal cost;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final LocalDate registrationDeadline;
    private TournamentStatus status;
    private List<TeamRegistration> teams;
    private List<Match> matches;
    private String rulebookFileId;

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
        this.teams = new ArrayList<>();
        this.matches = new ArrayList<>();
    }

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

    public static Tournament reconstruct(String id, String name, int numberOfTeams, BigDecimal cost,
                                         LocalDate startDate, LocalDate endDate,
                                         LocalDate registrationDeadline, TournamentStatus status,
                                         List<TeamRegistration> teams, List<Match> matches) {
        Tournament t = new Tournament(id, name, numberOfTeams, cost, startDate, endDate,
                registrationDeadline, status);
        t.teams = teams != null ? new ArrayList<>(teams) : new ArrayList<>();
        t.matches = matches != null ? new ArrayList<>(matches) : new ArrayList<>();
        return t;
    }

    // --- Preparación del torneo (TC-25) ---

    public PreparationResult checkPreparation() {
        List<String> missing = new ArrayList<>();

        if (startDate == null || endDate == null)
            missing.add("Fechas de inicio y fin son obligatorias");
        else if (!endDate.isAfter(startDate))
            missing.add("La fecha de fin debe ser posterior a la de inicio");

        long approvedCount = countApprovedTeams();
        if (approvedCount < MIN_TEAMS)
            missing.add("Se requieren al menos " + MIN_TEAMS + " equipos aprobados, faltan " + (MIN_TEAMS - approvedCount));

        boolean ready = missing.isEmpty();
        return new PreparationResult(ready, missing, approvedCount);
    }

    private long countApprovedTeams() {
        return teams.stream()
                .filter(t -> t.getRegistrationStatus() == RegistrationStatus.APPROVED)
                .count();
    }

    // --- Eliminar equipo (TC-48) ---

    public List<Match> removeTeam(String teamId, RemovalReason reason) {
        if (status != TournamentStatus.ACTIVE && status != TournamentStatus.IN_PROGRESS)
            throw new TeamRemovalNotAllowedException("Solo se puede remover equipos en torneos Activos o En Progreso");

        TeamRegistration team = teams.stream()
                .filter(t -> t.getTeamId().equals(teamId))
                .findFirst()
                .orElseThrow(() -> new TeamRemovalNotAllowedException("El equipo no está inscrito en este torneo"));

        teams.remove(team);

        List<Match> affected = new ArrayList<>();
        for (Match match : matches) {
            if (match.isPending() && match.involvesteam(teamId)) {
                match.markAsNoShow();
                affected.add(match);
            }
        }
        return affected;
    }

    public void attachRulebook(String rulebookFileId) {
        if (rulebookFileId == null || rulebookFileId.isBlank())
            throw new IllegalArgumentException("El id del archivo de reglamento no puede estar vacío");
        this.rulebookFileId = rulebookFileId;
    }

    // --- Validaciones privadas ---

    private static void validateName(String name) {
        if (name == null || name.isBlank())
            throw new InvalidTournamentDataException("El nombre del torneo es obligatorio");
        if (name.length() > MAX_NAME_LENGTH)
            throw new InvalidTournamentDataException("El nombre no puede superar los " + MAX_NAME_LENGTH + " caracteres");
    }

    private static void validateNumberOfTeams(int numberOfTeams) {
        if (numberOfTeams < MIN_TEAMS)
            throw new InvalidTournamentDataException("La cantidad de equipos debe ser mayor o igual a " + MIN_TEAMS);
    }

    private static void validateCost(BigDecimal cost) {
        if (cost == null || cost.compareTo(BigDecimal.ZERO) < 0)
            throw new InvalidTournamentDataException("El costo de inscripción no puede ser negativo");
    }

    private static void validateDateRange(LocalDate startDate, LocalDate endDate, LocalDate registrationDeadline) {
        if (startDate == null || endDate == null || registrationDeadline == null)
            throw new InvalidTournamentDataException("Las fechas del torneo son obligatorias");
        if (!startDate.isAfter(registrationDeadline))
            throw new InvalidTournamentDateRangeException("La fecha de inicio debe ser posterior a la fecha de cierre de inscripciones");
        if (endDate.isBefore(startDate))
            throw new InvalidTournamentDateRangeException("La fecha de fin debe ser posterior o igual a la fecha de inicio");
    }

    // --- Getters ---

    public String getName() { return name; }
    public int getNumberOfTeams() { return numberOfTeams; }
    public BigDecimal getCost() { return cost; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public LocalDate getRegistrationDeadline() { return registrationDeadline; }
    public TournamentStatus getStatus() { return status; }
    public void setStatus(TournamentStatus status) { this.status = status; }
    public List<TeamRegistration> getTeams() { return teams; }
    public void setTeams(List<TeamRegistration> teams) { this.teams = teams; }
    public List<Match> getMatches() { return matches; }
    public void setMatches(List<Match> matches) { this.matches = matches; }
    public String getRulebookFileId() { return rulebookFileId; }
    public void setRulebookFileId(String rulebookFileId) { this.rulebookFileId = rulebookFileId; }
}
