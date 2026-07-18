package co.edu.escuelaing.techcup.tournament.domain.model;

import co.edu.escuelaing.techcup.tournament.domain.exception.BracketNodeNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.exception.ChampionAssignmentNotAllowedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.ChampionPendingPenaltiesException;
import co.edu.escuelaing.techcup.tournament.domain.exception.EliminationBracketAlreadyGeneratedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.GroupStageNotCompleteException;
import co.edu.escuelaing.techcup.tournament.domain.exception.InvalidTournamentDataException;
import co.edu.escuelaing.techcup.tournament.domain.exception.InvalidTournamentDateRangeException;
import co.edu.escuelaing.techcup.tournament.domain.exception.InsufficientApprovedTeamsException;
import co.edu.escuelaing.techcup.tournament.domain.exception.MatchNotFoundException;
import co.edu.escuelaing.techcup.tournament.domain.exception.NoAvailableSlotsException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TeamDisqualificationNotAllowedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TeamInactivationNotAllowedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TeamRemovalNotAllowedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TeamRosterSizeInvalidException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentActivationNotAllowedException;
import co.edu.escuelaing.techcup.tournament.domain.exception.TournamentBeginNotAllowedException;
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
import java.util.Optional;
import java.util.UUID;

/**
 * Agregado raíz del dominio: el torneo con su ciclo de vida completo (borrador →
 * activo → en progreso → finalizado), equipos, inscripciones, partidos y reglas
 * de negocio asociadas.
 */
@SuppressWarnings("java:S2160") // identidad por id (heredada de AggregateRoot), no por campos: patron DDD intencional
public class Tournament extends AggregateRoot {

    private static final int MAX_NAME_LENGTH = 100;
    // TCF-52: 3 es el mínimo de equipos para poder armar un triangular.
    private static final int MIN_TEAMS_TO_CREATE = 3;
    // TC-28: para que un torneo esté "listo para activarse" se requieren al menos 3 equipos aprobados.
    private static final int MIN_APPROVED_TEAMS_TO_ACTIVATE = 3;
    private static final String TEAM_NOT_ENROLLED_MESSAGE = "El equipo no está inscrito en este torneo";

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
    private List<BracketNode> bracketNodes;
    // GridFS ObjectId (hex string, no es formato UUID) — ver GridFsRulebookStorageAdapter.
    private String rulebookFileId;
    private UUID championTeamId;
    private UUID runnerUpTeamId;
    private ChampionResolution championResolution;
    private boolean paused;
    private boolean active = true;
    private Long version;

    private Tournament(UUID id) {
        super(id);
        this.teams = new ArrayList<>();
        this.enrollments = new ArrayList<>();
        this.matches = new ArrayList<>();
        this.bracketNodes = new ArrayList<>();
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
        private List<BracketNode> bracketNodes;
        private UUID championTeamId;
        private UUID runnerUpTeamId;
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
        public Builder bracketNodes(List<BracketNode> bracketNodes) { this.bracketNodes = bracketNodes; return this; }
        public Builder championTeamId(UUID championTeamId) { this.championTeamId = championTeamId; return this; }
        public Builder runnerUpTeamId(UUID runnerUpTeamId) { this.runnerUpTeamId = runnerUpTeamId; return this; }
        public Builder championResolution(ChampionResolution championResolution) {
            this.championResolution = championResolution;
            return this;
        }
        public Builder paused(boolean paused) { this.paused = paused; return this; }
        public Builder active(boolean active) { this.active = active; return this; }
        public Builder enrollments(List<Enrollment> enrollments) { this.enrollments = enrollments; return this; }
        public Builder version(Long version) { this.version = version; return this; }

        /**
         * TCF-52: crea un torneo nuevo. Siempre nace en DRAFT con un id nuevo,
         * sin importar lo que se haya seteado en el builder para esos campos;
         * requiere una activación explícita (ver {@link Tournament#activate()})
         * antes de poder recibir inscripciones. Para torneos Lightning, endDate
         * se deriva automáticamente de startDate (torneo de un solo día).
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
            t.status = TournamentStatus.DRAFT;
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
            t.bracketNodes = bracketNodes != null ? new ArrayList<>(bracketNodes) : new ArrayList<>();
            t.championTeamId = championTeamId;
            t.runnerUpTeamId = runnerUpTeamId;
            t.championResolution = championResolution;
            t.paused = paused;
            t.active = active;
            t.enrollments = enrollments != null ? new ArrayList<>(enrollments) : new ArrayList<>();
            t.version = version;
            return t;
        }
    }

    /**
     * TC-27: activa el torneo, habilitando la inscripción de equipos.
     * Solo procede desde Borrador (estado en el que nace cualquier torneo nuevo,
     * ver {@link Builder#create()}).
     */
    public void activate() {
        assertActive();
        if (status != TournamentStatus.DRAFT) {
            throw new TournamentActivationNotAllowedException(
                    "Solo se puede activar un torneo en estado Borrador");
        }
        this.status = TournamentStatus.ACTIVE;
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

    // "reason" se valida como obligatoria en la capa REST (RemoveTeamRequest) y se
    // transporta hasta aqui por completitud del caso de uso; el dominio aun no la
    // usa (reservada para un futuro registro de auditoria del retiro de equipos).
    @SuppressWarnings("java:S1172")
    public List<Match> removeTeam(UUID teamId, RemovalReason reason) {
        assertActive();
        if (status != TournamentStatus.ACTIVE && status != TournamentStatus.IN_PROGRESS)
            throw new TeamRemovalNotAllowedException("Solo se puede remover equipos en torneos Activos o En Progreso");

        TeamRegistration team = teams.stream()
                .filter(t -> t.getTeamId().equals(teamId))
                .findFirst()
                .orElseThrow(() -> new TeamRemovalNotAllowedException(TEAM_NOT_ENROLLED_MESSAGE));

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
                .orElseThrow(() -> new TeamDisqualificationNotAllowedException(TEAM_NOT_ENROLLED_MESSAGE));

        if (team.getRegistrationStatus() == RegistrationStatus.DISQUALIFIED) {
            throw new TeamDisqualificationNotAllowedException("El equipo ya está descalificado");
        }

        team.setRegistrationStatus(RegistrationStatus.DISQUALIFIED);

        // Igual que removeTeam(): sus partidos pendientes quedan como walkover
        // (gana el rival presente) y alimentan el recálculo de la tabla de
        // posiciones (ver GroupStandingsCalculator, que trata FINISHED_NO_SHOW
        // como victoria administrativa del equipo no descalificado).
        for (Match match : matches) {
            if (match.isPending() && match.involvesteam(teamId)) {
                match.markAsNoShow();
            }
        }
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
        enrollment.setReservationExpiresAt(LocalDateTime.now(java.time.ZoneOffset.UTC).plusMinutes(RESERVATION_MINUTES));
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
                .orElseThrow(() -> new TeamInactivationNotAllowedException(TEAM_NOT_ENROLLED_MESSAGE));

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

    /**
     * TC-29: inicia el torneo (arranca el juego de los partidos ya programados
     * en la preparación). Solo procede desde En Preparación; a partir de aquí
     * el torneo puede finalizarse una vez se alcance la fecha de fin (ver
     * {@link #finish(LocalDate)}).
     */
    public void begin() {
        assertActive();
        if (status != TournamentStatus.IN_PREPARATION) {
            throw new TournamentBeginNotAllowedException(
                    "Solo se puede iniciar un torneo en estado En Preparación");
        }
        this.status = TournamentStatus.IN_PROGRESS;
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
     * TC-41: actualiza cualquier campo editable del torneo. Todos los campos de
     * {@link UpdateCommand} son opcionales (null = no se toca ese campo). No se
     * puede editar un torneo Finalizado. Tipo y formato (y las horas de partido
     * asociadas a Lightning) solo se pueden cambiar mientras el torneo está en
     * estado Activo.
     */
    public record UpdateCommand(String name, TournamentType type, TournamentFormat format,
                                Integer numberOfTeams, BigDecimal cost, LocalDate registrationDeadline,
                                LocalDate startDate, LocalDate endDate,
                                LocalTime matchStartTime, LocalTime matchEndTime) {}

    public void update(UpdateCommand command) {
        assertActive();
        if (status == TournamentStatus.FINISHED) {
            throw new TournamentCannotBeEditedException("No se puede editar un torneo en estado Finalizado");
        }
        boolean changesTypeOrFormat = command.type() != null || command.format() != null
                || command.matchStartTime() != null || command.matchEndTime() != null;
        if (changesTypeOrFormat && status != TournamentStatus.ACTIVE) {
            throw new TournamentCannotBeEditedException(
                    "El tipo, formato y horario del torneo solo se pueden editar mientras está en estado Activo");
        }

        String newName = command.name() != null ? command.name() : this.name;
        TournamentType newType = command.type() != null ? command.type() : this.type;
        TournamentFormat newFormat = command.format() != null ? command.format() : this.format;
        int newNumberOfTeams = command.numberOfTeams() != null ? command.numberOfTeams() : this.numberOfTeams;
        BigDecimal newCost = command.cost() != null ? command.cost() : this.cost;
        LocalDate newRegistrationDeadline = command.registrationDeadline() != null
                ? command.registrationDeadline() : this.registrationDeadline;
        LocalDate newStartDate = command.startDate() != null ? command.startDate() : this.startDate;
        LocalDate newEndDate = command.endDate() != null ? command.endDate() : this.endDate;
        LocalTime newMatchStartTime = command.matchStartTime() != null ? command.matchStartTime() : this.matchStartTime;
        LocalTime newMatchEndTime = command.matchEndTime() != null ? command.matchEndTime() : this.matchEndTime;

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
        this.runnerUpTeamId = match.resolveRunnerUpTeamId(winnerTeamId);
        this.championResolution = resolution;

        return new ChampionAssignment(championTeamId, runnerUpTeamId, championResolution);
    }

    /**
     * TC-50: registra el ganador de la tanda de penales de un partido, previo
     * a poder asignar campeón cuando ese partido terminó empatado en tiempo
     * reglamentario (ver {@link #assignChampionWhenFinalMatchFinished}, que
     * exige este dato ya cargado para resolver al campeón en ese caso). Si el
     * partido es el de un nodo de la llave eliminatoria que había quedado
     * "pendiente de penales" (ver {@link #advanceBracket}, camino por evento
     * que no resuelve el empate por su cuenta), este mismo endpoint manual
     * también avanza la llave con el ganador ya conocido.
     */
    public void recordPenaltyShootoutWinner(UUID matchId, UUID winnerTeamId) {
        assertActive();
        Match match = findMatch(matchId);
        match.recordPenaltyShootoutWinner(winnerTeamId);

        findBracketNodeByMatchId(matchId)
                .filter(node -> node.getStatus() == BracketNodeStatus.PENDING_PENALTIES)
                .ifPresent(node -> advanceBracket(matchId));
    }

    private Match findMatch(UUID matchId) {
        return matches.stream()
                .filter(m -> m.getMatchId().equals(matchId))
                .findFirst()
                .orElseThrow(() -> new MatchNotFoundException(getId(), matchId));
    }

    // --- Llave eliminatoria ---

    /**
     * LÓGICA 3: cuando todos los partidos de todos los grupos ya terminaron,
     * toma los puestos 1 y 2 de cada grupo (ver {@link GroupStandingsCalculator})
     * y construye el árbol completo de la llave eliminatoria. La primera ronda
     * se siembra cruzada entre grupos adyacentes en orden alfabético (1A-2B,
     * 1B-2A, 1C-2D, 1D-2C, ...), y esos cruces se emparejan de forma que dos
     * equipos del mismo grupo no puedan volver a encontrarse hasta la ronda
     * siguiente a la suya (mismo criterio que usa el bracket de un Mundial:
     * 1A-2B se cruza en la próxima ronda con el ganador de 1C-2D, no con el de
     * 1B-2A). Las rondas futuras nacen con sus dos cupos "Por definir" (null);
     * solo la primera ronda genera matchId de una vez, porque es la única con
     * ambos cupos ya resueltos.
     */
    public void generateEliminationBracket() {
        assertActive();
        if (!bracketNodes.isEmpty()) {
            throw new EliminationBracketAlreadyGeneratedException(
                    "La llave eliminatoria de este torneo ya fue generada");
        }

        List<GroupTable> tables = GroupStandingsCalculator.computeAll(
                matches, GroupStandingsCalculator.ineligibleTeamIds(teams, matches));

        if (tables.isEmpty() || !allGroupMatchesResolved()) {
            throw new GroupStageNotCompleteException(
                    "No se puede generar la llave eliminatoria: aún hay partidos de grupos sin finalizar");
        }

        List<BracketNode> firstRound = seedFirstRound(tables);
        buildTreeFromLeaves(firstRound);
    }

    private boolean allGroupMatchesResolved() {
        return matches.stream()
                .filter(m -> m.getGroupName() != null)
                .allMatch(m -> m.getStatus() == MatchStatus.FINISHED || m.getStatus() == MatchStatus.FINISHED_NO_SHOW);
    }

    private List<BracketNode> seedFirstRound(List<GroupTable> tables) {
        int numGroups = tables.size();
        Round firstRound = roundForNodeCount(numGroups);

        List<UUID> pos1 = new ArrayList<>();
        List<UUID> pos2 = new ArrayList<>();
        for (GroupTable table : tables) {
            pos1.add(table.standings().get(0).teamId());
            pos2.add(table.standings().get(1).teamId());
        }

        List<BracketNode> firstLegs = new ArrayList<>();
        List<BracketNode> secondLegs = new ArrayList<>();
        for (int pairIndex = 0; pairIndex < numGroups / 2; pairIndex++) {
            int x = pairIndex * 2;
            int y = pairIndex * 2 + 1;
            firstLegs.add(newSeededNode(firstRound, pos1.get(x), pos2.get(y)));
            secondLegs.add(newSeededNode(firstRound, pos1.get(y), pos2.get(x)));
        }

        List<BracketNode> leaves = new ArrayList<>(firstLegs);
        leaves.addAll(secondLegs);

        for (BracketNode leaf : leaves) {
            Match match = Match.builder()
                    .matchId(UUID.randomUUID())
                    .homeTeamId(leaf.getSlotA())
                    .awayTeamId(leaf.getSlotB())
                    .status(MatchStatus.PENDING)
                    .active(true)
                    .finalMatch(false)
                    .phase(MatchPhase.ELIMINATORIA)
                    .tournamentId(getId())
                    .build();
            matches.add(match);
            leaf.assignMatch(match.getMatchId());
        }

        bracketNodes.addAll(leaves);
        return leaves;
    }

    private BracketNode newSeededNode(Round round, UUID slotA, UUID slotB) {
        return BracketNode.builder().nodeId(UUID.randomUUID()).round(round).slotA(slotA).slotB(slotB).build();
    }

    private void buildTreeFromLeaves(List<BracketNode> firstRoundNodes) {
        List<BracketNode> currentRound = firstRoundNodes;
        while (currentRound.size() > 1) {
            Round nextRound = roundForNodeCount(currentRound.size() / 2);
            List<BracketNode> nextRoundNodes = new ArrayList<>();
            for (int i = 0; i < currentRound.size(); i += 2) {
                BracketNode nextNode = BracketNode.builder().nodeId(UUID.randomUUID()).round(nextRound).build();
                currentRound.get(i).setAdvanceTo(nextNode.getNodeId(), BracketSlot.A);
                currentRound.get(i + 1).setAdvanceTo(nextNode.getNodeId(), BracketSlot.B);
                nextRoundNodes.add(nextNode);
            }
            bracketNodes.addAll(nextRoundNodes);
            currentRound = nextRoundNodes;
        }
    }

    private static Round roundForNodeCount(int nodeCount) {
        return switch (nodeCount) {
            case 8 -> Round.ROUND_OF_16;
            case 4 -> Round.QUARTERFINAL;
            case 2 -> Round.SEMIFINAL;
            case 1 -> Round.FINAL;
            default -> throw new GroupStageNotCompleteException(
                    "Cantidad de grupos inválida para armar la llave eliminatoria: " + nodeCount);
        };
    }

    /**
     * LÓGICA 4: aplica el resultado ya registrado (ver {@link Match#finishWithExternalResult})
     * de un partido de la llave eliminatoria. Si el partido quedó sin ganador
     * resuelto (empate en tiempo reglamentario sin penales), el nodo queda
     * "pendiente de penales" y no avanza — no se asume que Matches vaya a
     * agregar penales; el organizador debe resolverlo con el endpoint manual
     * ({@link #recordPenaltyShootoutWinner}), que reintenta este mismo avance.
     * Si el ganador ya está resuelto, sube al cupo del nodo siguiente
     * (avanzaA); si ese nodo queda con sus dos cupos llenos, genera su
     * partido. Si el nodo era la Final, asigna campeón/subcampeón y finaliza
     * el torneo. Idempotente: si el nodo ya estaba Finalizado, no hace nada.
     * <p>
     * Decisión intencional: al resolver la Final, este método pone el estado
     * del torneo en FINISHED directamente, SIN pasar por la validación de
     * fecha de fin de {@link #finish(LocalDate)} (la que usa el endpoint
     * manual "finalizar"). Son dos caminos distintos a propósito: terminar de
     * jugarse la llave es en sí mismo el criterio de cierre del torneo (así
     * lo pide LÓGICA 4 del contrato), no depende de si la fecha de fin
     * administrativa ya se alcanzó. Efectos secundarios de la finalización
     * (disparar reconocimientos, publicar el evento de torneo finalizado) NO
     * viven aquí — a diferencia de FinalizeTournamentService, esto es lógica
     * de dominio pura; es responsabilidad del llamador (ver
     * ProcessMatchResultService) dispararlos cuando este método deja el
     * torneo en FINISHED.
     */
    public void advanceBracket(UUID matchId) {
        assertActive();
        BracketNode node = findBracketNodeByMatchId(matchId)
                .orElseThrow(() -> BracketNodeNotFoundException.forMatch(matchId));

        if (node.getStatus() == BracketNodeStatus.FINISHED) {
            return;
        }

        Match match = findMatch(matchId);
        UUID winnerTeamId = match.resolveChampionTeamId();
        if (winnerTeamId == null) {
            node.markPendingPenalties();
            return;
        }

        UUID loserTeamId = match.resolveRunnerUpTeamId(winnerTeamId);
        node.resolve(winnerTeamId, loserTeamId);

        if (node.isFinal()) {
            this.championTeamId = winnerTeamId;
            this.runnerUpTeamId = loserTeamId;
            this.championResolution = resolveChampionResolution(match);
            this.status = TournamentStatus.FINISHED;
            return;
        }

        BracketNode target = findBracketNode(node.getAdvanceToNodeId());
        target.fillSlot(node.getAdvanceToSlot(), winnerTeamId);
        if (target.hasBothSlots()) {
            Match nextMatch = Match.builder()
                    .matchId(UUID.randomUUID())
                    .homeTeamId(target.getSlotA())
                    .awayTeamId(target.getSlotB())
                    .status(MatchStatus.PENDING)
                    .active(true)
                    .finalMatch(target.getRound() == Round.FINAL)
                    .phase(MatchPhase.ELIMINATORIA)
                    .tournamentId(getId())
                    .build();
            matches.add(nextMatch);
            target.assignMatch(nextMatch.getMatchId());
        }
    }

    private static ChampionResolution resolveChampionResolution(Match match) {
        if (match.getStatus() == MatchStatus.FINISHED_NO_SHOW) {
            return ChampionResolution.WALKOVER;
        }
        return match.isTiedInRegulation() ? ChampionResolution.PENALTIES : ChampionResolution.REGULATION_TIME;
    }

    private BracketNode findBracketNode(UUID nodeId) {
        return bracketNodes.stream()
                .filter(n -> n.getNodeId().equals(nodeId))
                .findFirst()
                .orElseThrow(() -> new BracketNodeNotFoundException(
                        "No se encontró el nodo '" + nodeId + "' de la llave eliminatoria"));
    }

    private Optional<BracketNode> findBracketNodeByMatchId(UUID matchId) {
        return bracketNodes.stream()
                .filter(n -> matchId.equals(n.getMatchId()))
                .findFirst();
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
    public List<BracketNode> getBracketNodes() { return bracketNodes; }
    public void setBracketNodes(List<BracketNode> bracketNodes) { this.bracketNodes = bracketNodes; }
    public String getRulebookFileId() { return rulebookFileId; }
    public void setRulebookFileId(String rulebookFileId) { this.rulebookFileId = rulebookFileId; }
    public UUID getChampionTeamId() { return championTeamId; }
    public UUID getRunnerUpTeamId() { return runnerUpTeamId; }
    public ChampionResolution getChampionResolution() { return championResolution; }
    public boolean isPaused() { return paused; }
    public boolean isActive() { return active; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
