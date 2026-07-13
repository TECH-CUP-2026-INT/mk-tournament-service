package co.edu.escuelaing.techcup.tournament.service;

import co.edu.escuelaing.techcup.tournament.exception.ChampionAssignmentNotAllowedException;
import co.edu.escuelaing.techcup.tournament.exception.ChampionPendingPenaltiesException;
import co.edu.escuelaing.techcup.tournament.exception.InvalidTournamentDataException;
import co.edu.escuelaing.techcup.tournament.exception.InvalidTournamentDateRangeException;
import co.edu.escuelaing.techcup.tournament.exception.MatchNotFoundException;
import co.edu.escuelaing.techcup.tournament.exception.TeamRemovalNotAllowedException;
import co.edu.escuelaing.techcup.tournament.exception.TournamentCannotBeFinalizedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Tournament extends AggregateRoot {

    private static final int MAX_NAME_LENGTH = 100;
    // TCF-52: la cantidad mínima de equipos para CREAR un torneo es 3.
    private static final int MIN_TEAMS_TO_CREATE = 3;
    // TC-28: para que un torneo esté "listo para activarse" se requieren al menos 3 equipos aprobados.
    private static final int MIN_APPROVED_TEAMS_TO_ACTIVATE = 3;

    private final String name;
    private final TournamentType type;
    private final TournamentFormat format;
    private final int numberOfTeams;
    private final BigDecimal cost;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final LocalDate registrationDeadline;
    private final LocalTime matchStartTime;
    private final LocalTime matchEndTime;
    private TournamentStatus status;
    private List<TeamRegistration> teams;
    private List<Match> matches;
    private String rulebookFileId;
    private String championTeamId;
    private ChampionResolution championResolution;

    private Tournament(String id, String name, TournamentType type, TournamentFormat format,
                       int numberOfTeams, BigDecimal cost,
                       LocalDate startDate, LocalDate endDate, LocalDate registrationDeadline,
                       LocalTime matchStartTime, LocalTime matchEndTime,
                       TournamentStatus status) {
        super(id);
        this.name = name;
        this.type = type;
        this.format = format;
        this.numberOfTeams = numberOfTeams;
        this.cost = cost;
        this.startDate = startDate;
        this.endDate = endDate;
        this.registrationDeadline = registrationDeadline;
        this.matchStartTime = matchStartTime;
        this.matchEndTime = matchEndTime;
        this.status = status;
        this.teams = new ArrayList<>();
        this.matches = new ArrayList<>();
    }

    public Tournament(String id, String name, TournamentStatus status) {
        this(id, name, TournamentType.NORMAL, TournamentFormat.BRACKETS, 0, BigDecimal.ZERO,
                null, null, null, null, null, status);
    }

    public boolean isDraft() {
        return TournamentStatus.DRAFT == this.status;
    }

    /**
     * TCF-52: crea un torneo nuevo. Siempre nace en ACTIVE,
     * sin importar lo que envíe el cliente. Para torneos Lightning,
     * endDate se deriva automáticamente de startDate (torneo de un solo día).
     */
    public static Tournament create(String name, TournamentType type, TournamentFormat format,
                                    int numberOfTeams, BigDecimal cost,
                                    LocalDate startDate, LocalDate endDate,
                                    LocalDate registrationDeadline,
                                    LocalTime matchStartTime, LocalTime matchEndTime) {
        validateName(name);
        validateType(type);
        validateFormat(format);
        validateNumberOfTeams(numberOfTeams);
        validateCost(cost);
        validateCommonDates(startDate, registrationDeadline);
        validateNormalSchedule(type, startDate, endDate);
        validateLightningSchedule(type, matchStartTime, matchEndTime);

        LocalDate effectiveEndDate = type == TournamentType.LIGHTNING ? startDate : endDate;

        return new Tournament(null, name, type, format, numberOfTeams, cost, startDate,
                effectiveEndDate, registrationDeadline, matchStartTime, matchEndTime,
                TournamentStatus.ACTIVE);
    }

    /**
     * Reconstruye un torneo desde la base de datos sin aplicar
     * reglas de creación ni forzar el estado.
     */
    public static Tournament reconstruct(String id, String name, TournamentType type, TournamentFormat format,
                                         int numberOfTeams, BigDecimal cost,
                                         LocalDate startDate, LocalDate endDate,
                                         LocalDate registrationDeadline,
                                         LocalTime matchStartTime, LocalTime matchEndTime,
                                         TournamentStatus status,
                                         List<TeamRegistration> teams, List<Match> matches,
                                         String championTeamId, ChampionResolution championResolution) {
        Tournament t = new Tournament(id, name, type, format, numberOfTeams, cost, startDate, endDate,
                registrationDeadline, matchStartTime, matchEndTime, status);
        t.teams = teams != null ? new ArrayList<>(teams) : new ArrayList<>();
        t.matches = matches != null ? new ArrayList<>(matches) : new ArrayList<>();
        t.championTeamId = championTeamId;
        t.championResolution = championResolution;
        return t;
    }

    /**
     * Sobrecarga de compatibilidad: reconstruye sin tipo/formato/horas de partido
     * ni datos de campeón (torneos anteriores a TCF-52 o pruebas que no los necesitan).
     */
    public static Tournament reconstruct(String id, String name, int numberOfTeams, BigDecimal cost,
                                         LocalDate startDate, LocalDate endDate,
                                         LocalDate registrationDeadline, TournamentStatus status,
                                         List<TeamRegistration> teams, List<Match> matches) {
        return reconstruct(id, name, TournamentType.NORMAL, TournamentFormat.BRACKETS, numberOfTeams, cost,
                startDate, endDate, registrationDeadline, null, null, status, teams, matches, null, null);
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

    private static void validateType(TournamentType type) {
        if (type == null)
            throw new InvalidTournamentDataException("El tipo de torneo es obligatorio");
    }

    private static void validateFormat(TournamentFormat format) {
        if (format == null)
            throw new InvalidTournamentDataException("El formato del torneo es obligatorio");
    }

    private static void validateCommonDates(LocalDate startDate, LocalDate registrationDeadline) {
        if (startDate == null || registrationDeadline == null)
            throw new InvalidTournamentDataException("La fecha de inicio y la fecha de cierre de inscripciones son obligatorias");
        if (!startDate.isAfter(registrationDeadline))
            throw new InvalidTournamentDateRangeException("La fecha de inicio debe ser posterior a la fecha de cierre de inscripciones");
    }

    private static void validateNormalSchedule(TournamentType type, LocalDate startDate, LocalDate endDate) {
        if (type != TournamentType.NORMAL) return;
        if (endDate == null)
            throw new InvalidTournamentDataException("La fecha de fin es obligatoria para torneos de tipo Normal");
        if (endDate.isBefore(startDate))
            throw new InvalidTournamentDateRangeException("La fecha de fin debe ser posterior o igual a la fecha de inicio");
    }

    private static void validateLightningSchedule(TournamentType type, LocalTime matchStartTime, LocalTime matchEndTime) {
        if (type != TournamentType.LIGHTNING) return;
        if (matchStartTime == null || matchEndTime == null)
            throw new InvalidTournamentDataException("Las horas de inicio y fin del partido son obligatorias para torneos de tipo Lightning");
        if (!matchEndTime.isAfter(matchStartTime))
            throw new InvalidTournamentDateRangeException("La hora de fin del partido debe ser posterior a la hora de inicio");
    }

    // --- Getters ---

    public String getName() { return name; }
    public TournamentType getType() { return type; }
    public TournamentFormat getFormat() { return format; }
    public LocalTime getMatchStartTime() { return matchStartTime; }
    public LocalTime getMatchEndTime() { return matchEndTime; }
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
}
