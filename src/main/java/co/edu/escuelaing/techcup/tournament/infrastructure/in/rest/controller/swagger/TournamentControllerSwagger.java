package co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.controller.swagger;

import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.request.CreateTournamentRequest;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.request.DisqualifyTeamRequest;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.request.EditTournamentRequest;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.request.EnrollTeamRequest;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.request.InactivateTournamentRequest;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.request.PauseTournamentRequest;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.ChampionResponse;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.CourtResponse;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.DeleteTournamentResponse;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.DisqualifyTeamResponse;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.EnrollmentResponse;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.HistoricalTournamentResponse;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.InactivateTeamResponse;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.InactivateTournamentResponse;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.InactivateUserResponse;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.MatchCourtResponse;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.MatchupResponse;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.PauseTournamentResponse;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.PreparationResponse;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.RegisteredTeamResponse;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.RegisteredTeamsResponse;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.RulebookResponse;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.rest.dto.response.TournamentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "Tournaments", description = "Creating, editing, and managing the full tournament lifecycle")
public interface TournamentControllerSwagger {

    @Operation(summary = "Create tournament",
            description = "Creates a tournament directly in ACTIVE status. See CreateTournamentRequest for the "
                    + "NORMAL vs LIGHTNING field rules.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tournament created",
                    content = @Content(schema = @Schema(implementation = TournamentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
    })
    ResponseEntity<TournamentResponse> create(CreateTournamentRequest request);

    @Operation(summary = "Finalize tournament",
            description = "Moves the tournament to FINISHED and archives it to the historical read-only view. "
                    + "Only allowed once the tournament is In Progress and the end date has been reached.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tournament finalized",
                    content = @Content(schema = @Schema(implementation = TournamentResponse.class))),
            @ApiResponse(responseCode = "409", description = "The tournament cannot be finalized in its current state", content = @Content)
    })
    ResponseEntity<TournamentResponse> finalize(
            @Parameter(description = "Tournament ID", example = "abc123") String id);

    @Operation(summary = "Start preparation phase",
            description = "Moves the tournament to IN_PREPARATION and generates its fixture (matchups) at random, "
                    + "based on the tournament's format. Requires at least 3 approved teams.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Preparation started, fixture generated",
                    content = @Content(schema = @Schema(implementation = TournamentResponse.class))),
            @ApiResponse(responseCode = "409", description = "The tournament does not meet the requirements to enter preparation", content = @Content)
    })
    ResponseEntity<TournamentResponse> prepare(
            @Parameter(description = "Tournament ID", example = "abc123") String id);

    @Operation(summary = "Check preparation readiness",
            description = "Reports whether the tournament already meets the requirements to move into preparation "
                    + "(at least 3 approved teams), and lists what's missing otherwise.")
    @ApiResponse(responseCode = "200", description = "Tournament preparation readiness",
            content = @Content(schema = @Schema(implementation = PreparationResponse.class)))
    ResponseEntity<PreparationResponse> checkPreparation(
            @Parameter(description = "Tournament ID", example = "abc123") String tournamentId);

    @Operation(summary = "Assign tournament champion",
            description = "Triggered when the match marked as the final finishes. If the score is tied in "
                    + "regulation time, the penalty shootout winner must already have been recorded first.")
    @ApiResponse(responseCode = "200", description = "Champion assigned",
            content = @Content(schema = @Schema(implementation = ChampionResponse.class)))
    ResponseEntity<ChampionResponse> assignChampion(
            @Parameter(description = "Tournament ID", example = "abc123") String tournamentId,
            @Parameter(description = "ID of the final match", example = "m01") String matchId);

    @Operation(summary = "Get tournament champion")
    @ApiResponse(responseCode = "200", description = "Tournament champion",
            content = @Content(schema = @Schema(implementation = ChampionResponse.class)))
    ResponseEntity<ChampionResponse> getChampion(
            @Parameter(description = "Tournament ID", example = "abc123") String tournamentId);

    @Operation(summary = "Delete tournament (Draft status only)",
            description = "Permanently deletes a tournament. Only allowed while it is still in Draft status.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tournament deleted",
                    content = @Content(schema = @Schema(implementation = DeleteTournamentResponse.class))),
            @ApiResponse(responseCode = "409", description = "The tournament is not in Draft status", content = @Content)
    })
    ResponseEntity<DeleteTournamentResponse> delete(
            @Parameter(description = "Tournament ID", example = "abc123") String id);

    @Operation(summary = "Get fixture / bracket",
            description = "Returns every matchup generated for the tournament, with current results. Slots not yet "
                    + "resolved (future bracket rounds) are returned with null team IDs.")
    @ApiResponse(responseCode = "200", description = "List of tournament matchups",
            content = @Content(schema = @Schema(implementation = MatchupResponse.class)))
    ResponseEntity<List<MatchupResponse>> getMatchups(
            @Parameter(description = "Tournament ID", example = "abc123") String tournamentId);

    @Operation(summary = "Get the court assigned to a match",
            description = "Returns a pending placeholder (200, with a message) instead of 404 if the match hasn't "
                    + "been scheduled to a court yet.")
    @ApiResponse(responseCode = "200", description = "Court assigned to the match (or a pending state if not scheduled yet)",
            content = @Content(schema = @Schema(implementation = MatchCourtResponse.class)))
    ResponseEntity<MatchCourtResponse> getMatchCourt(
            @Parameter(description = "Match ID", example = "m01") String matchId);

    @Operation(summary = "List registered teams",
            description = "Legacy registration view (name + status). For payment/reservation details, use "
                    + "GET /tournaments/{tournamentId}/enrollments instead.")
    @ApiResponse(responseCode = "200", description = "List of teams registered in the tournament",
            content = @Content(schema = @Schema(implementation = RegisteredTeamResponse.class)))
    ResponseEntity<List<RegisteredTeamResponse>> getRegisteredTeams(
            @Parameter(description = "Tournament ID", example = "abc123") String tournamentId);

    @Operation(summary = "List enrolled and reserved teams",
            description = "Returns teams with a confirmed (paid) enrollment separately from teams with a slot "
                    + "reservation still awaiting payment confirmation, plus how many slots remain open.")
    @ApiResponse(responseCode = "200", description = "Enrolled teams and reserved teams",
            content = @Content(schema = @Schema(implementation = RegisteredTeamsResponse.class)))
    ResponseEntity<RegisteredTeamsResponse> getEnrolledTeams(
            @Parameter(description = "Tournament ID", example = "abc123") String tournamentId);

    @Operation(summary = "Enroll team in tournament",
            description = "The team captain enrolls their team. The tournament must be ACTIVE and have open slots; "
                    + "the enrollment starts as RESERVED/PENDING_PAYMENT until payment-service confirms payment.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Team enrolled",
                    content = @Content(schema = @Schema(implementation = EnrollmentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "409", description = "The team is already enrolled, or there are no open slots", content = @Content)
    })
    ResponseEntity<EnrollmentResponse> enrollTeam(
            @Parameter(description = "Tournament ID", example = "abc123") String tournamentId,
            EnrollTeamRequest request);

    @Operation(summary = "List historical tournaments",
            description = "Read-only list of every finished tournament, accessible without authentication.")
    @ApiResponse(responseCode = "200", description = "List of finished tournaments",
            content = @Content(schema = @Schema(implementation = HistoricalTournamentResponse.class)))
    ResponseEntity<List<HistoricalTournamentResponse>> getHistory();

    @Operation(summary = "Get historical tournament by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Historical tournament found",
                    content = @Content(schema = @Schema(implementation = HistoricalTournamentResponse.class))),
            @ApiResponse(responseCode = "404", description = "The tournament doesn't exist or hasn't finished yet", content = @Content)
    })
    ResponseEntity<HistoricalTournamentResponse> getHistoricalById(
            @Parameter(description = "Tournament ID", example = "abc123") String tournamentId);

    @Operation(summary = "Download rulebook (PDF)",
            description = "Streams the tournament's rulebook file inline as application/pdf.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rulebook PDF file",
                    content = @Content(mediaType = MediaType.APPLICATION_PDF_VALUE)),
            @ApiResponse(responseCode = "404", description = "The tournament has no rulebook attached", content = @Content)
    })
    ResponseEntity<InputStreamResource> consultRulebook(
            @Parameter(description = "Tournament ID", example = "abc123") String tournamentId);

    @Operation(summary = "Attach rulebook (PDF)",
            description = "Uploads and attaches the official rulebook PDF (max. 10 MB) to the tournament. "
                    + "The request body is multipart/form-data.")
    @ApiResponse(responseCode = "200", description = "Rulebook attached",
            content = @Content(schema = @Schema(implementation = RulebookResponse.class)))
    ResponseEntity<RulebookResponse> attachRulebook(
            @Parameter(description = "Tournament ID", example = "abc123") String tournamentId,
            @Parameter(description = "Rulebook PDF file (max. 10 MB)") MultipartFile file) throws IOException;

    @Operation(summary = "Register court",
            description = "Registers a court on one of the 4 campus map sections, with an optional description and "
                    + "image. The request body is multipart/form-data.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Court registered",
                    content = @Content(schema = @Schema(implementation = CourtResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid court section or invalid image", content = @Content)
    })
    ResponseEntity<CourtResponse> registerCourt(
            @Parameter(description = "Tournament ID", example = "abc123") String tournamentId,
            @Parameter(description = "Campus map section for the court: CANCHA_1, CANCHA_2, CANCHA_3 or CANCHA_4",
                    example = "CANCHA_1") String section,
            @Parameter(description = "Court description", example = "Synthetic grass court, north side of campus")
            String description,
            @Parameter(description = "Court image file") MultipartFile image) throws IOException;

    @Operation(summary = "Edit tournament",
            description = "Updates any field defined at tournament creation. Every field in the request body is "
                    + "optional — omitted (null) fields keep their current value. Blocked once the tournament is FINISHED.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tournament edited",
                    content = @Content(schema = @Schema(implementation = TournamentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "404", description = "The tournament doesn't exist", content = @Content)
    })
    ResponseEntity<TournamentResponse> edit(
            @Parameter(description = "Tournament ID", example = "abc123") String id,
            EditTournamentRequest request);

    @Operation(summary = "Pause or resume tournament",
            description = "Pausing suspends new event registration but keeps all existing data queryable; "
                    + "resuming lifts that suspension.")
    @ApiResponse(responseCode = "200", description = "Tournament paused or resumed",
            content = @Content(schema = @Schema(implementation = PauseTournamentResponse.class)))
    ResponseEntity<PauseTournamentResponse> pause(
            @Parameter(description = "Tournament ID", example = "abc123") String id,
            PauseTournamentRequest request);

    @Operation(summary = "Inactivate or reactivate tournament",
            description = "Inactivating blocks ALL functionality of the tournament, including read queries — "
                    + "not just writes (unlike pause).")
    @ApiResponse(responseCode = "200", description = "Tournament inactivated or reactivated",
            content = @Content(schema = @Schema(implementation = InactivateTournamentResponse.class)))
    ResponseEntity<InactivateTournamentResponse> inactivate(
            @Parameter(description = "Tournament ID", example = "abc123") String id,
            InactivateTournamentRequest request);

    @Operation(summary = "Disqualify team",
            description = "Marks the team as disqualified. It stays in the records with its past results, but is "
                    + "excluded from any future matchups.")
    @ApiResponse(responseCode = "200", description = "Team disqualified",
            content = @Content(schema = @Schema(implementation = DisqualifyTeamResponse.class)))
    ResponseEntity<DisqualifyTeamResponse> disqualifyTeam(
            @Parameter(description = "Tournament ID", example = "abc123") String tournamentId,
            @Parameter(description = "Team ID", example = "team_xyz789") String teamId,
            DisqualifyTeamRequest request);

    @Operation(summary = "Inactivate team in tournament",
            description = "The inactivated team receives no scheduling or points; this is a temporary "
                    + "administrative measure, distinct from disqualification.")
    @ApiResponse(responseCode = "200", description = "Team inactivated",
            content = @Content(schema = @Schema(implementation = InactivateTeamResponse.class)))
    ResponseEntity<InactivateTeamResponse> inactivateTeam(
            @Parameter(description = "Tournament ID", example = "abc123") String tournamentId,
            @Parameter(description = "Team ID", example = "team_xyz789") String teamId);

    @Operation(summary = "Inactivate participant user",
            description = "The inactivated user cannot be included in lineups or accumulate statistics in that "
                    + "tournament.")
    @ApiResponse(responseCode = "200", description = "User inactivated",
            content = @Content(schema = @Schema(implementation = InactivateUserResponse.class)))
    ResponseEntity<InactivateUserResponse> inactivateUser(
            @Parameter(description = "Tournament ID", example = "abc123") String tournamentId,
            @Parameter(description = "User ID", example = "user_123") String userId);
}
