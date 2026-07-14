package co.edu.escuelaing.techcup.tournament.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Respuesta con el estado de preparación de un torneo")
public record PreparationResponse(
        @Schema(description = "Estado de preparación", example = "incompleto") String status,
        @Schema(description = "Indica si el torneo ya puede pasar a preparación") boolean readyToActivate,
        @Schema(description = "Cantidad de equipos aprobados", example = "6") long approvedTeamsCount,
        @Schema(description = "Requisitos que faltan para poder preparar el torneo") List<String> missingRequirements
) {}
