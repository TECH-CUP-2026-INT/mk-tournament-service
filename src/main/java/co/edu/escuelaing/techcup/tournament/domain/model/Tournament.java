package co.edu.escuelaing.techcup.tournament.domain.model;

import co.edu.escuelaing.techcup.tournament.application.usecase.EnrollTeamInTournamentService;
import co.edu.escuelaing.techcup.tournament.application.usecase.StartTournamentPreparationService;

import co.edu.escuelaing.techcup.tournament.domain.exception.ChampionAssignmentNotAllowedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.ChampionPendingPenaltiesException;
import co.edu.escuelaing.techcup.tournament.domain.exception.InvalidTournamentDataException;
import co.edu.escuelaing.techcup.tournament.domain.exception.InvalidTournamentDateRangeException;
import co.edu.escuelaing.techcup.tournament.domain.exception.InsufficientApprovedTeamsException;
import co.edu.escuelaing.techcup.tournament.domain.exception.MatchNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.exception.NoAvailableSlotsException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TeamDisqualificationNotAllowedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TeamInactivationNotAllowedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TeamRemovalNotAllowedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TeamRosterSizeInvalidException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentCannotBeEditedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentCannotBeFinalizedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentInactivationNotAllowedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentInactiveException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentNotActiveForEnrollmentException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentPauseNotAllowedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentPreparationNotAllowedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Agregado raíz del dominio: el torneo con su ciclo de vida completo (borrador →
 * activo → en progreso → finalizado), equipos, inscripciones, partidos y reglas
 * de negocio asociadas.
 */
public class Tournament extends AggregateRoot {

    private static final int MAX_NAME_LENGTH = 100;
    // TCF-52: 3 es el mínimo de equipos para poder armar un triangular.
    private static final int MIN_TEAMS_TO_CREATE = 3;
    // TC-28: para que un torneo esté "listo para activarse" se requieren al menos 3 equipos aprobados.
    private static final int MIN_APPROVED_TEAMS_TO_ACTIVATE = 3;

    private String name;
    private TournamentType type;
    private TournamentFormat format;
    private int numberOfTeams;
    private BigDecimal cost;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate registrationDeadline;
    private LocalTime matchStartTime;
    private LocalTime matchEndTime;
    private TournamentStatus status;
    private List<TeamRegistration> teams;
    private List<Enrollment> enrollments;
    private List<Match> matches;
    // GridFS ObjectId (hex string, no es formato UUID) — ver GridFsRulebookStorageAdapter.
    private String rulebookFileId;
    private UUID championTeamId;
    private ChampionResolution championResolution;
    private boolean paused;
    private boolean active = true;
    private Long version;

    private Tournament(UUID id) {
        super(id);
        this.teams = new ArrayList<>();
        this.enrollments = new ArrayList<>();
        this.matches = new ArrayList<>();
    }

    public Tournament(UUID id, String name, TournamentStatus status) {
        this(id);
        this.name = name;
        this.type = TournamentType.NORMAL;
        this.format = TournamentFormat.BRACKETS;
        this.numberOfTeams = 0;
        this.cost = BigDecimal.ZERO;
        this.status = status;
    }

    public boolean isDraft() {
        return TournamentStatus.DRAFT == this.status;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Reemplaza a las 8 sobrecargas de {@code create}/{@code reconstruct} (S107:
     * demasiados parámetros posicionales). Los valores por defecto de cada campo
     * reproducen los que tenían las sobrecargas de compatibilidad que existían
     * para no romper llamadas anteriores a TCF-52/TCF-153/TCF-154/TC-109.
     */
    public static final class Builder {
        private UUID id;
        private String name;
        private TournamentType type = TournamentType.NORMAL;
        private TournamentFormat format = TournamentFormat.BRACKETS;
        private int numberOfTeams;
        private BigDecimal cost;
        private LocalDate startDate;
        private LocalDate endDate;
        private LocalDate registrationDeadline;
        private LocalTime matchStartTime;
        private LocalTime matchEndTime;
        private TournamentStatus status;
        private List<TeamRegistration> teams;
        private List<Match> matches;
        private UUID championTeamId;
        private ChampionResolution championResolution;
        private boolean paused = false;
        private boolean active = true;
        private List<Enrollment> enrollments;
        private Long version;

        private Builder() {}

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder type(TournamentType type) { this.type = type; return this; }
        public Builder format(TournamentFormat format) { this.format = format; return this; }
        public Builder numberOfTeams(int numberOfTeams) { this.numberOfTeams = numberOfTeams; return this; }
        public Builder cost(BigDecimal cost) { this.cost = cost; return this; }
        public Builder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public Builder endDate(LocalDate endDate) { this.endDate = endDate; return this; }
        public Builder registrationDeadline(LocalDate registrationDeadline) {
            this.registrationDeadline = registrationDeadline;
            return this;
        }
        public Builder matchStartTime(LocalTime matchStartTime) { this.matchStartTime = matchStartTime; return this; }
        public Builder matchEndTime(LocalTime matchEndTime) { this.matchEndTime = matchEndTime; return this; }
        public Builder status(TournamentStatus status) { this.status = status; return this; }
        public Builder teams(List<TeamRegistration> teams) { this.teams = teams; return this; }
        public Builder matches(List<Match> matches) { this.matches = matches; return this; }
        public Builder championTeamId(UUID championTeamId) { this.championTeamId = championTeamId; return this; }
        public Builder championResolution(ChampionResolution championResolution) {
            this.championResolution = championResolution;
            return this;
        }
        public Builder paused(boolean paused) { this.paused = paused; return this; }
        public Builder active(boolean active) { this.active = active; return this; }
        public Builder enrollments(List<Enrollment> enrollments) { this.enrollments = enrollments; return this; }
        public Builder version(Long version) { this.version = version; return this; }

        /**
         * TCF-52: crea un torneo nuevo. Siempre nace en ACTIVE con un id nuevo,
         * sin importar lo que se haya seteado en el builder para esos campos.
         * Para torneos Lightning, endDate se deriva automáticamente de startDate
         * (torneo de un solo día).
         */
        public Tournament create() {
            validateName(name);
            validateType(type);
            validateFormat(format);
            validateNumberOfTeams(numberOfTeams);
            validateCost(cost);
            validateCommonDates(startDate, registrationDeadline);
            validateNormalSchedule(type, startDate, endDate);
            validateLightningSchedule(type, matchStartTime, matchEndTime);

            LocalDate effectiveEndDate = type == TournamentType.LIGHTNING ? startDate : endDate;

            Tournament t = new Tournament(UUID.randomUUID());
            t.name = name;
            t.type = type;
            t.format = format;
            t.numberOfTeams = numberOfTeams;
            t.cost = cost;
            t.startDate = startDate;
            t.endDate = effectiveEndDate;
            t.registrationDeadline = registrationDeadline;
            t.matchStartTime = matchStartTime;
            t.matchEndTime = matchEndTime;
            t.status = TournamentStatus.ACTIVE;
            return t;
        }

        /**
         * Reconstruye un torneo desde la base de datos (o desde un test) sin
         * aplicar reglas de creación ni forzar el estado.
         */
        public Tournament reconstruct() {
            Tournament t = new Tournament(id);
            t.name = name;
            t.type = type;
            t.format = format;
            t.numberOfTeams = numberOfTeams;
            t.cost = cost;
            t.startDate = startDate;
            t.endDate = endDate;
            t.registrationDeadline = registrationDeadline;
            t.matchStartTime = matchStartTime;
            t.matchEndTime = matchEndTime;
            t.status = status;
            t.teams = teams != null ? new ArrayList<>(teams) : new ArrayList<>();
            t.matches = matches != null ? new ArrayList<>(matches) : new ArrayList<>();
            t.championTeamId = championTeamId;
            t.championResolution = championResolution;
            t.paused = paused;
            t.active = active;
            t.enrollments = enrollments != null ? new ArrayList<>(enrollments) : new ArrayList<>();
            t.version = version;
            return t;
        }
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

    public List<Match> removeTeam(UUID teamId, RemovalReason reason) {
        assertActive();
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

    /**
     * TC-48: descalifica un equipo del torneo. A diferencia de removeTeam(),
     * el equipo NO se elimina de la lista: se conserva junto con sus estadísticas
     * y resultados previos, pero queda excluido de la generación de partidos futuros
     * (startPreparation() solo toma equipos con estado Aprobado). Es una acción
     * permanente, a diferencia de la inactivación del torneo.
     */
    public void disqualifyTeam(UUID teamId, DisqualificationReason reason) {
        assertActive();
        if (reason == null) {
            throw new TeamDisqualificationNotAllowedException("El motivo de descalificación es obligatorio");
        }

        TeamRegistration team = teams.stream()
                .filter(t -> t.getTeamId().equals(teamId))
                .findFirst()
                .orElseThrow(() -> new TeamDisqualificationNotAllowedException("El equipo no está inscrito en este torneo"));

        if (team.getRegistrationStatus() == RegistrationStatus.DISQUALIFIED) {
            throw new TeamDisqualificationNotAllowedException("El equipo ya está descalificado");
        }

        team.setRegistrationStatus(RegistrationStatus.DISQUALIFIED);
    }

    private static final int MIN_ROSTER_SIZE = 7;
    private static final int MAX_ROSTER_SIZE = 12;
    private static final int RESERVATION_MINUTES = 60;

    /**
     * Inscribe un equipo al torneo, reservando un cupo mientras se confirma el pago
     * (ver EnrollTeamInTournamentService, que consulta el Team Service para el roster
     * y llama al Payment Service inmediatamente después de que este método retorna).
     */
    public Enrollment enrollTeam(UUID teamId, String teamName, int rosterSize) {
        assertActive();
        if (status != TournamentStatus.ACTIVE) {
            throw new TournamentNotActiveForEnrollmentException(
                    "Solo se pueden inscribir equipos en torneos en estado Activo");
        }
        if (rosterSize < MIN_ROSTER_SIZE || rosterSize > MAX_ROSTER_SIZE) {
            throw new TeamRosterSizeInvalidException(
                    "El equipo debe tener entre " + MIN_ROSTER_SIZE + " y " + MAX_ROSTER_SIZE
                            + " jugadores registrados, tiene " + rosterSize);
        }
        if (countActiveEnrollments() >= numberOfTeams) {
            throw new NoAvailableSlotsException(
                    "No hay cupos disponibles en el torneo '" + getId() + "'");
        }

        Enrollment enrollment = new Enrollment(teamId, teamName, EnrollmentStatus.RESERVED);
        enrollment.setReservationExpiresAt(LocalDateTime.now().plusMinutes(RESERVATION_MINUTES));
        enrollments.add(enrollment);
        return enrollment;
    }

    private long countActiveEnrollments() {
        return enrollments.stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.ENROLLED || e.getStatus() == EnrollmentStatus.RESERVED)
                .count();
    }

    /**
     * TC-44: inactiva un equipo dentro del torneo. Es una medida administrativa
     * temporal; no implica descalificación ni eliminación del equipo.
     */
    public void inactivateTeam(UUID teamId) {
        assertActive();

        TeamRegistration team = teams.stream()
                .filter(t -> t.getTeamId().equals(teamId))
                .findFirst()
                .orElseThrow(() -> new TeamInactivationNotAllowedException("El equipo no está inscrito en este torneo"));

        if (team.getRegistrationStatus() == RegistrationStatus.INACTIVE) {
            throw new TeamInactivationNotAllowedException("El equipo ya está inactivo en este torneo");
        }

        team.setRegistrationStatus(RegistrationStatus.INACTIVE);
    }

    /**
     * Valida que el torneo pueda pasar a Preparación: debe estar Activo
     * y tener al menos {@value #MIN_APPROVED_TEAMS_TO_ACTIVATE} equipos aprobados.
     * Se expone por separado de startPreparation() para poder validar
     * antes de generar el fixture.
     */
    public void validateReadyForPreparation() {
        assertActive();
        if (status != TournamentStatus.ACTIVE)
            throw new TournamentPreparationNotAllowedException(
                    "Solo se puede iniciar la preparación de torneos en estado Activo");
        long approved = countApprovedTeams();
        if (approved < MIN_APPROVED_TEAMS_TO_ACTIVATE)
            throw new InsufficientApprovedTeamsException(
                    "Se requieren al menos " + MIN_APPROVED_TEAMS_TO_ACTIVATE
                            + " equipos aprobados para iniciar la preparación, hay " + approved);
    }

    /**
     * Pasa el torneo a Preparación adjuntando el fixture ya generado
     * (ver StartTournamentPreparationService, que genera el fixture de
     * forma aleatoria antes de invocar este método).
     */
    public void startPreparation(List<Match> generatedMatches) {
        validateReadyForPreparation();
        this.matches = new ArrayList<>(generatedMatches);
        this.status = TournamentStatus.IN_PREPARATION;
    }

    public void attachRulebook(String rulebookFileId) {
        assertActive();
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
        assertActive();
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
     * TC-42: pausa el torneo, suspendiendo la inscripción de equipos.
     * El torneo se mantiene visible y consultable; solo se puede pausar
     * si está Activo o En Progreso.
     */
    public void pause() {
        assertActive();
        if (paused) {
            throw new TournamentPauseNotAllowedException("El torneo ya está pausado");
        }
        if (status != TournamentStatus.ACTIVE && status != TournamentStatus.IN_PROGRESS) {
            throw new TournamentPauseNotAllowedException(
                    "Solo se puede pausar un torneo en estado Activo o En Progreso");
        }
        this.paused = true;
    }

    /**
     * TC-42: reanuda un torneo pausado, restaurando la inscripción de equipos.
     */
    public void resume() {
        assertActive();
        if (!paused) {
            throw new TournamentPauseNotAllowedException("El torneo no está pausado");
        }
        this.paused = false;
    }

    /**
     * TC-43: inactiva el torneo, bloqueando todas las funcionalidades asociadas
     * (a diferencia de pausar, ni siquiera se puede consultar mientras está inactivo).
     * Solo se puede inactivar un torneo Activo o En Progreso.
     */
    public void inactivate() {
        if (!active) {
            throw new TournamentInactivationNotAllowedException("El torneo ya está inactivo");
        }
        if (status != TournamentStatus.ACTIVE && status != TournamentStatus.IN_PROGRESS) {
            throw new TournamentInactivationNotAllowedException(
                    "Solo se puede inactivar un torneo en estado Activo o En Progreso");
        }
        this.active = false;
    }

    /**
     * TC-43: reactiva el torneo, restaurando todas sus funcionalidades.
     */
    public void reactivate() {
        if (active) {
            throw new TournamentInactivationNotAllowedException("El torneo no está inactivo");
        }
        this.active = true;
    }

    /**
     * Bloquea cualquier operación sobre el torneo (consulta o escritura)
     * mientras esté inactivo. Se usa en todas las funcionalidades asociadas
     * al torneo, no solo en las de escritura.
     */
    public void assertActive() {
        if (!active) {
            throw new TournamentInactiveException(
                    "El torneo está inactivo, no se puede realizar esta operación");
        }
    }

    /**
     * TC-41: actualiza cualquier campo editable del torneo. Todos los parámetros
     * son opcionales (null = no se toca ese campo). No se puede editar un torneo
     * Finalizado. Tipo y formato (y las horas de partido asociadas a Lightning)
     * solo se pueden cambiar mientras el torneo está en estado Activo.
     */
    public void update(String name, TournamentType type, TournamentFormat format,
                       Integer numberOfTeams, BigDecimal cost, LocalDate registrationDeadline,
                       LocalDate startDate, LocalDate endDate,
                       LocalTime matchStartTime, LocalTime matchEndTime) {
        assertActive();
        if (status == TournamentStatus.FINISHED) {
            throw new TournamentCannotBeEditedException("No se puede editar un torneo en estado Finalizado");
        }
        boolean changesTypeOrFormat = type != null || format != null || matchStartTime != null || matchEndTime != null;
        if (changesTypeOrFormat && status != TournamentStatus.ACTIVE) {
            throw new TournamentCannotBeEditedException(
                    "El tipo, formato y horario del torneo solo se pueden editar mientras está en estado Activo");
        }

        String newName = name != null ? name : this.name;
        TournamentType newType = type != null ? type : this.type;
        TournamentFormat newFormat = format != null ? format : this.format;
        int newNumberOfTeams = numberOfTeams != null ? numberOfTeams : this.numberOfTeams;
        BigDecimal newCost = cost != null ? cost : this.cost;
        LocalDate newRegistrationDeadline = registrationDeadline != null ? registrationDeadline : this.registrationDeadline;
        LocalDate newStartDate = startDate != null ? startDate : this.startDate;
        LocalDate newEndDate = endDate != null ? endDate : this.endDate;
        LocalTime newMatchStartTime = matchStartTime != null ? matchStartTime : this.matchStartTime;
        LocalTime newMatchEndTime = matchEndTime != null ? matchEndTime : this.matchEndTime;

        validateName(newName);
        validateType(newType);
        validateFormat(newFormat);
        validateNumberOfTeams(newNumberOfTeams);
        validateCost(newCost);
        validateCommonDates(newStartDate, newRegistrationDeadline);
        validateNormalSchedule(newType, newStartDate, newEndDate);
        validateLightningSchedule(newType, newMatchStartTime, newMatchEndTime);

        LocalDate effectiveEndDate = newType == TournamentType.LIGHTNING ? newStartDate : newEndDate;

        this.name = newName;
        this.type = newType;
        this.format = newFormat;
        this.numberOfTeams = newNumberOfTeams;
        this.cost = newCost;
        this.registrationDeadline = newRegistrationDeadline;
        this.startDate = newStartDate;
        this.endDate = effectiveEndDate;
        this.matchStartTime = newMatchStartTime;
        this.matchEndTime = newMatchEndTime;
    }

    /**
     * Asigna automáticamente al campeón cuando el partido marcado como Final
     * pasa a estado Finalizado. Si hay empate en tiempo reglamentario, espera
     * el resultado de penales registrado en el partido.
     */
    public ChampionAssignment assignChampionWhenFinalMatchFinished(UUID matchId) {
        assertActive();
        Match match = findMatch(matchId);

        if (!match.isFinalMatch()) {
            throw new ChampionAssignmentNotAllowedException(
                    "Solo se asigna campeón cuando el partido está marcado como Final");
        }
        if (!match.isFinished()) {
            throw new ChampionAssignmentNotAllowedException(
                    "Solo se asigna campeón cuando el partido final está Finalizado");
        }

        UUID winnerTeamId = match.resolveChampionTeamId();
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

    /**
     * TC-50: registra el ganador de la tanda de penales de un partido, previo
     * a poder asignar campeón cuando ese partido terminó empatado en tiempo
     * reglamentario (ver {@link #assignChampionWhenFinalMatchFinished}, que
     * exige este dato ya cargado para resolver al campeón en ese caso).
     */
    public void recordPenaltyShootoutWinner(UUID matchId, UUID winnerTeamId) {
        assertActive();
        Match match = findMatch(matchId);
        match.recordPenaltyShootoutWinner(winnerTeamId);
    }

    private Match findMatch(UUID matchId) {
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
    public List<Enrollment> getEnrollments() { return enrollments; }
    public void setEnrollments(List<Enrollment> enrollments) { this.enrollments = enrollments; }
    public List<Match> getMatches() { return matches; }
    public void setMatches(List<Match> matches) { this.matches = matches; }
    public String getRulebookFileId() { return rulebookFileId; }
    public void setRulebookFileId(String rulebookFileId) { this.rulebookFileId = rulebookFileId; }
    public UUID getChampionTeamId() { return championTeamId; }
    public ChampionResolution getChampionResolution() { return championResolution; }
    public boolean isPaused() { return paused; }
    public boolean isActive() { return active; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
