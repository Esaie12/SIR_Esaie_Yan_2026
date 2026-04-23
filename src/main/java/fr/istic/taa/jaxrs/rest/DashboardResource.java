package fr.istic.taa.jaxrs.rest;

import fr.istic.taa.jaxrs.dto.ApiResponse;
import fr.istic.taa.jaxrs.dto.DashboardDTO;
import fr.istic.taa.jaxrs.service.DashboardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/dashboard")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Dashboard", description = "Statistiques d'un utilisateur : groupes, messages envoyés, clients")
public class DashboardResource {

    private final DashboardService dashboardService = new DashboardService();

    @GET
    @Path("/{userId}")
    @Operation(
            summary     = "Statistiques d'un utilisateur",
            description = "Retourne le nombre de groupes créés, de messages envoyés et de clients créés par l'utilisateur."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Statistiques trouvées",
                    content = @Content(schema = @Schema(implementation = DashboardDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Utilisateur introuvable")
    })
    public Response getStats(
            @Parameter(description = "ID de l'utilisateur", required = true)
            @PathParam("userId") Long userId) {
        try {
            DashboardDTO stats = dashboardService.getStats(userId);
            return Response.ok(ApiResponse.ok(stats)).build();
        } catch (RuntimeException e) {
            return Response.status(404).entity(ApiResponse.notFound(e.getMessage())).build();
        }
    }
}