package co.edu.escuelaing.techcup.tournament.service;

import co.edu.escuelaing.techcup.tournament.exception.ChampionAssignmentNotAllowedException;
import co.edu.escuelaing.techcup.tournament.exception.ChampionPendingPenaltiesException;
import co.edu.escuelaing.techcup.tournament.exception.InvalidTournamentDataException;
import co.edu.escuelaing.techcup.tournament.exception.InvalidTournamentDateRangeException;
import co.edu.escuelaing.techcup.tournament.exception.MatchNotFoundException;
import co.edu.escuelaing.techcup.tournament.exception.TeamRemovalNotAllowedException;
import co.edu.escuelaing.techcup.tournament.exception.TournamentCannotBeEditedException;
import co.edu.escuelaing.techcup.tournament.exception.TournamentCannotBeFinalizedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Tournament extends AggregateRoot {

    private static final int MAX_NAME_LENGTH = 100;
    // TC-25 / TCF-50 (ya aprobado): la cantidad mínima de equipos para CREAR un torneo es 2.
    private static final int MIN_TEAMS_TO_CREATE = 2;
    // TC-28: para que un torneo esté "listo para activarse" se requieren al menos 3 equipos aprobados.
    private static final int MIN_APPROVED_TEAMS_TO_ACTIVATE = 3;

    private String name;
    private int numberOfTeams;
    private BigDecimal cost;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate registrationDeadline;
    private TournamentStatus status;
    private List<TeamRegistration> teams;
    private List<Match> matches;
    private String rulebookFileId;
    private String championTeamId;
    private ChampionResolution championResolution;
    private TournamentType tournamentType;
    private TournamentFormat tournamentFormat;

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
        this.tournamentType = TournamentType.NORMAL;
        this.tournamentFormat = TournamentFormat.BRACKETS;
    }

    public Tournament(String id, String name, TournamentStatus status) {
        this(id, name, 0, BigDecimal.ZERO, null, null, null, status);
    }

    public boolean isDraft() {
        return TournamentStatus.DRAFT == this.status;
    }

    /**
     * TC-25: crea un torneo nuevo. Siempre nace en DRAFT,
     * sin importar lo que envíe el cliente.
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
     * Reconstruye un torneo desde la base de datos sin aplicar
     * reglas de creación ni forzar el estado a DRAFT.
     */
    public static Tournament reconstruct(String id, String name, int numberOfTeams, BigDecimal cost,
                                         LocalDate startDate, LocalDate endDate,
                                         LocalDate registrationDeadline, TournamentStatus status,
                                         List<TeamRegistration> teams, List<Match> matches,
                                         String championTeamId, ChampionResolution championResolution,
                                         TournamentType tournamentType, TournamentFormat tournamentFormat) {
        Tournament t = new Tournament(id, name, numberOfTeams, cost, startDate, endDate,
                registrationDeadline, status);
        t.teams = teams != null ? new ArrayList<>(teams) : new ArrayList<>();
        t.matches = matches != null ? new ArrayList<>(matches) : new ArrayList<>();
        t.championTeamId = championTeamId;
        t.championResolution = championResolution;
        t.tournamentType = tournamentType != null ? tournamentType : TournamentType.NORMAL;
        t.tournamentFormat = tournamentFormat != null ? tournamentFormat : TournamentFormat.BRACKETS;
        return t;
    }

    public static Tournament reconstruct(String id, String name, int numberOfTeams, BigDecimal cost,
                                         LocalDate startDate, LocalDate endDate,
                                         LocalDate registrationDeadline, TournamentStatus status,
                                         List<TeamRegistration> teams, List<Match> matches,
                                         String championTeamId, ChampionResolution championResolution) {
        return reconstruct(id, name, numberOfTeams, cost, startDate, endDate, registrationDeadline,
                status, teams, matches, championTeamId, championResolution, null, null);
    }

    public static Tournament reconstruct(String id, String name, int numberOfTeams, BigDecimal cost,
                                         LocalDate startDate, LocalDate endDate,
                                         LocalDate registrationDeadline, TournamentStatus status,
                                         List<TeamRegistration> teams, List<Match> matches) {
        return reconstruct(id, name, numberOfTeams, cost, startDate, endDate,
                registrationDeadline, status, teams, matches, null, null);
    }

    /**
     * Sobrecarga de compatibilidad: reconstruye sin equipos ni partidos
     * (torneos que aún no tienen inscripciones/llaves generadas).
     */
    public static Tournament reconstruct(String id, String name, int numberOfTeams, BigDecimal cost,
                                         LocalDate startDate, LocalDate endDate,
                                         LocalDate registrationDeadline, TournamentStatus status) {
        return reconstruct(id, name, numberOfTeams, cost, startDate, endDate,
                registrationDeadline, status, null, null);
    }

    // --- Preparación del torneo ---

    public PreparationResult checkPreparation() {
        List<String> missing = new ArrayList<>();

        if (startDate == null || endDate == null)
            missing.add("Fechas de inicio y fin son obligatorias");
        else if (!endDate.isAfter(startDate))
            missing.add("La fecha de fin debe ser posterior a la de inicio");

        long approvedCount = countApprovedTeams();
        if (approvedCount < MIN_APPROVED_TEAMS_TO_ACTIVATE)
            missing.add("Se requieren al menos " + MIN_APPROVED_TEAMS_TO_ACTIVATE + " equipos aprobados, faltan " + (MIN_APPROVED_TEAMS_TO_ACTIVATE - approvedCount));

        boolean ready = missing.isEmpty();
        return new PreparationResult(ready, missing, approvedCount);
    }

    private long countApprovedTeams() {
        return teams.stream()
                .filter(t -> t.getRegistrationStatus() == RegistrationStatus.APPROVED)
                .count();
    }

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

    /**
     * TC-30: finaliza el torneo. Solo procede si estaba En Progreso
     * y la fecha de fin ya se alcanzó. Recibe la fecha "actual" como
     * parámetro (no llama a LocalDate.now() aquí) para que la regla
     * sea probable de forma determinista en las pruebas unitarias.
     */
    public void finish(LocalDate currentDate) {
        if (status != TournamentStatus.IN_PROGRESS) {
            throw new TournamentCannotBeFinalizedException(
                    "El torneo debe estar En Progreso para poder finalizarse");
        }
        if (endDate.isAfter(currentDate)) {
            throw new TournamentCannotBeFinalizedException(
                    "La fecha de fin no ha sido alcanzada");
        }
        this.status = TournamentStatus.FINISHED;
    }

    /**
     * TC-41: actualiza cualquier campo editable del torneo. Todos los parámetros
     * son opcionales (null = no se toca ese campo); solo se validan y aplican
     * los que llegan con un valor nuevo. No se puede editar un torneo Finalizado.
     */
    public void update(String name, TournamentType tournamentType, TournamentFormat tournamentFormat,
                       Integer numberOfTeams, BigDecimal cost, LocalDate registrationDeadline,
                       LocalDate startDate, LocalDate endDate) {
        if (status == TournamentStatus.FINISHED) {
            throw new TournamentCannotBeEditedException("No se puede editar un torneo en estado Finalizado");
        }

        String newName = name != null ? name : this.name;
        int newNumberOfTeams = numberOfTeams != null ? numberOfTeams : this.numberOfTeams;
        BigDecimal newCost = cost != null ? cost : this.cost;
        LocalDate newRegistrationDeadline = registrationDeadline != null ? registrationDeadline : this.registrationDeadline;
        LocalDate newStartDate = startDate != null ? startDate : this.startDate;
        LocalDate newEndDate = endDate != null ? endDate : this.endDate;

        validateName(newName);
        validateNumberOfTeams(newNumberOfTeams);
        validateCost(newCost);
        validateDateRange(newStartDate, newEndDate, newRegistrationDeadline);

        this.name = newName;
        this.numberOfTeams = newNumberOfTeams;
        this.cost = newCost;
        this.registrationDeadline = newRegistrationDeadline;
        this.startDate = newStartDate;
        this.endDate = newEndDate;
        this.tournamentType = tournamentType != null ? tournamentType : this.tournamentType;
        this.tournamentFormat = tournamentFormat != null ? tournamentFormat : this.tournamentFormat;
    }

    /**
     * Asigna automáticamente al campeón cuando el partido marcado como Final
     * pasa a estado Finalizado. Si hay empate en tiempo reglamentario, espera
     * el resultado de penales registrado en el partido.
     */
    public ChampionAssignment assignChampionWhenFinalMatchFinished(String matchId) {
        Match match = findMatch(matchId);

        if (!match.isFinalMatch()) {
            throw new ChampionAssignmentNotAllowedException(
                    "Solo se asigna campeón cuando el partido está marcado como Final");
        }
        if (!match.isFinished()) {
            throw new ChampionAssignmentNotAllowedException(
                    "Solo se asigna campeón cuando el partido final está Finalizado");
        }

        String winnerTeamId = match.resolveChampionTeamId();
        if (winnerTeamId == null) {
            throw new ChampionPendingPenaltiesException(matchId);
        }

        ChampionResolution resolution = match.isTiedInRegulation()
                ? ChampionResolution.PENALTIES
                : ChampionResolution.REGULATION_TIME;

        this.championTeamId = winnerTeamId;
        this.championResolution = resolution;

        return new ChampionAssignment(championTeamId, championResolution);
    }

    private Match findMatch(String matchId) {
        return matches.stream()
                .filter(m -> m.getMatchId().equals(matchId))
                .findFirst()
                .orElseThrow(() -> new MatchNotFoundException(getId(), matchId));
    }

    // --- Validaciones privadas ---

    private static void validateName(String name) {
        if (name == null || name.isBlank())
            throw new InvalidTournamentDataException("El nombre del torneo es obligatorio");
        if (name.length() > MAX_NAME_LENGTH)
            throw new InvalidTournamentDataException("El nombre no puede superar los " + MAX_NAME_LENGTH + " caracteres");
    }

    private static void validateNumberOfTeams(int numberOfTeams) {
        if (numberOfTeams < MIN_TEAMS_TO_CREATE)
            throw new InvalidTournamentDataException("La cantidad de equipos debe ser mayor o igual a " + MIN_TEAMS_TO_CREATE);
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
    public String getChampionTeamId() { return championTeamId; }
    public ChampionResolution getChampionResolution() { return championResolution; }
    public TournamentType getTournamentType() { return tournamentType; }
    public TournamentFormat getTournamentFormat() { return tournamentFormat; }
}
