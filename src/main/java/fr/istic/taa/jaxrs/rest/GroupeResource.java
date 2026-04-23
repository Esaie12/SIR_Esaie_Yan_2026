package fr.istic.taa.jaxrs.rest;

import fr.istic.taa.jaxrs.dto.ApiResponse;
import fr.istic.taa.jaxrs.dto.ClientDTO;
import fr.istic.taa.jaxrs.dto.GroupeDTO;
import fr.istic.taa.jaxrs.service.ClientService;
import fr.istic.taa.jaxrs.service.GroupeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/groupes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Groupes", description = "Gestion des groupes de clients")
public class GroupeResource {

    private final GroupeService groupeService = new GroupeService();
    private final ClientService clientService = new ClientService();

    @GET
    @Operation(
            summary     = "Lister tous les groupes",
            description = "Retourne la liste complète des groupes, triée par date de création décroissante."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Liste des groupes",
                    content = @Content(schema = @Schema(implementation = GroupeDTO.class)))
    })
    public Response getAllGroupes() {
        List<GroupeDTO> list = groupeService.findAllGroupes();
        return Response.ok(ApiResponse.ok(list)).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Récupérer un groupe par son ID")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Groupe trouvé",
                    content = @Content(schema = @Schema(implementation = GroupeDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Groupe introuvable")
    })
    public Response getGroupe(
            @Parameter(description = "ID du groupe", required = true) @PathParam("id") Long id) {
        GroupeDTO dto = groupeService.findGroupe(id);
        if (dto == null)
            return Response.status(404).entity(ApiResponse.notFound("Groupe introuvable")).build();
        return Response.ok(ApiResponse.ok(dto)).build();
    }

    @POST
    @Operation(
            summary     = "Créer un nouveau groupe",
            description = "Crée un groupe avec un libellé et une couleur. La date de création est générée automatiquement."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201", description = "Groupe créé",
                    content = @Content(schema = @Schema(implementation = GroupeDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "Données invalides")
    })
    public Response createGroupe(
            @RequestBody(description = "Données du groupe (libelle, color)", required = true,
                    content = @Content(schema = @Schema(implementation = GroupeDTO.class)))
            GroupeDTO dto) {
        GroupeDTO created = groupeService.createGroupe(dto);
        return Response.status(201).entity(ApiResponse.created(created)).build();
    }

    @GET
    @Path("/by-user/{userId}")
    @Operation(summary = "Lister les groupes d'un utilisateur")
    public Response getGroupesOfUser(@PathParam("userId") Long userId) {
        try {
            return Response.ok(ApiResponse.ok(groupeService.getGroupesByUser(userId))).build();
        } catch (RuntimeException e) {
            return Response.status(404).entity(ApiResponse.notFound(e.getMessage())).build();
        }
    }

    @PUT
    @Path("/{id}")
    @Operation(
            summary     = "Mettre à jour un groupe",
            description = "Modifie le libellé et/ou la couleur d'un groupe. La dateCreate n'est pas modifiable."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Groupe mis à jour",
                    content = @Content(schema = @Schema(implementation = GroupeDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Groupe introuvable")
    })
    public Response updateGroupe(
            @Parameter(description = "ID du groupe", required = true) @PathParam("id") Long id,
            @RequestBody(description = "Nouvelles données du groupe", required = true,
                    content = @Content(schema = @Schema(implementation = GroupeDTO.class)))
            GroupeDTO dto) {
        GroupeDTO updated = groupeService.updateGroupe(id, dto);
        if (updated == null)
            return Response.status(404).entity(ApiResponse.notFound("Groupe introuvable")).build();
        return Response.ok(ApiResponse.ok(updated)).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Supprimer un groupe")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "204", description = "Groupe supprimé"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Groupe introuvable")
    })
    public Response deleteGroupe(
            @Parameter(description = "ID du groupe", required = true) @PathParam("id") Long id) {
        groupeService.deleteGroupe(id);
        return Response.status(204).entity(ApiResponse.noContent()).build();
    }

    @GET
    @Path("/{id}/clients")
    @Operation(
            summary     = "Lister les clients d'un groupe",
            description = "Retourne tous les clients appartenant au groupe identifié."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Liste des clients du groupe",
                    content = @Content(schema = @Schema(implementation = ClientDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Groupe introuvable")
    })
    public Response getClientsOfGroupe(
            @Parameter(description = "ID du groupe", required = true) @PathParam("id") Long id) {
        List<ClientDTO> list = clientService.findByGroupe(id);
        return Response.ok(ApiResponse.ok(list)).build();
    }

    @DELETE
    @Path("/{groupeId}/clients/{clientId}")
    @Operation(
            summary     = "Retirer un client d'un groupe",
            description = "Supprime l'association entre le client et le groupe identifiés."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "204", description = "Client retiré du groupe"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Client, groupe ou association introuvable")
    })
    public Response removeClientFromGroupe(
            @Parameter(description = "ID du groupe",  required = true) @PathParam("groupeId")  Long groupeId,
            @Parameter(description = "ID du client",  required = true) @PathParam("clientId")  Long clientId) {
        try {
            clientService.removeClientFromGroupe(clientId, groupeId);
            return Response.status(204).entity(ApiResponse.noContent()).build();
        } catch (RuntimeException e) {
            return Response.status(404).entity(ApiResponse.notFound(e.getMessage())).build();
        }
    }

    @GET
    @Path("/{groupeId}/clients/not-in/user/{userId}")
    @Operation(
            summary     = "Clients d'un user n'appartenant pas à un groupe",
            description = "Retourne les clients créés par l'utilisateur qui ne sont pas membres du groupe."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Liste des clients hors groupe",
                    content = @Content(schema = @Schema(implementation = ClientDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Groupe ou utilisateur introuvable")
    })
    public Response getClientsNotInGroupe(
            @Parameter(description = "ID du groupe",      required = true) @PathParam("groupeId") Long groupeId,
            @Parameter(description = "ID de l'utilisateur", required = true) @PathParam("userId")   Long userId) {
        try {
            List<ClientDTO> list = clientService.findClientsNotInGroupe(groupeId, userId);
            return Response.ok(ApiResponse.ok(list)).build();
        } catch (RuntimeException e) {
            return Response.status(404).entity(ApiResponse.notFound(e.getMessage())).build();
        }
    }
}