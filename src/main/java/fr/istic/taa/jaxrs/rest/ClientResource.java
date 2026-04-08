package fr.istic.taa.jaxrs.rest;

import fr.istic.taa.jaxrs.dto.ApiResponse;
import fr.istic.taa.jaxrs.dto.ClientDTO;
import fr.istic.taa.jaxrs.dto.ClientGroupeDTO;
import fr.istic.taa.jaxrs.service.ClientService;

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

@Path("/clients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Clients", description = "Gestion des clients : CRUD et recherche dynamique multi-critères")
public class ClientResource {

    private final ClientService clientService = new ClientService();

    @GET
    @Operation(
            summary     = "Lister ou rechercher des clients",
            description = "Retourne tous les clients. Filtrable par email (exact), "
                    + "et/ou par country + sexe via CriteriaQuery dynamique."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Liste des clients",
                    content = @Content(schema = @Schema(implementation = ClientDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Aucun client trouvé pour l'email donné")
    })
    public Response getAllClients(
            @Parameter(description = "Filtrer par email exact")   @QueryParam("email")   String email,
            @Parameter(description = "Filtrer par pays")          @QueryParam("country") String country,
            @Parameter(description = "Filtrer par sexe (M ou F)") @QueryParam("sexe")    String sexe) {

        if (email != null && !email.isBlank()) {
            ClientDTO dto = clientService.findByEmail(email);
            if (dto == null)
                return Response.status(404)
                        .entity(ApiResponse.notFound("Aucun client avec cet email")).build();
            return Response.ok(ApiResponse.ok(dto)).build();
        }
        if ((country != null && !country.isBlank()) || (sexe != null && !sexe.isBlank())) {
            List<ClientDTO> list = clientService.findByCriteria(country, sexe);
            return Response.ok(ApiResponse.ok(list)).build();
        }
        return Response.ok(ApiResponse.ok(clientService.findAllUsers())).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Récupérer un client par son ID")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Client trouvé",
                    content = @Content(schema = @Schema(implementation = ClientDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Client introuvable")
    })
    public Response getClient(
            @Parameter(description = "ID du client", required = true) @PathParam("id") Long id) {
        ClientDTO dto = clientService.findUser(id);
        if (dto == null)
            return Response.status(404).entity(ApiResponse.notFound("Client introuvable")).build();
        return Response.ok(ApiResponse.ok(dto)).build();
    }

    @POST
    @Operation(summary = "Créer un nouveau client")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201", description = "Client créé",
                    content = @Content(schema = @Schema(implementation = ClientDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "Données invalides")
    })
    public Response createClient(
            @RequestBody(description = "Données du client", required = true,
                    content = @Content(schema = @Schema(implementation = ClientDTO.class)))
            ClientDTO dto) {
        ClientDTO created = clientService.createUser(dto);
        return Response.status(201).entity(ApiResponse.created(created)).build();
    }
    
    
    @GET
    @Path("/by-user/{userId}")
    public Response getClientsOfUser(@PathParam("userId") Long userId) {
        try {
            List<ClientDTO> clients = clientService.getClientsByUser(userId);
            return Response.ok(ApiResponse.ok(clients)).build();
        } catch (RuntimeException e) {
            return Response.status(404)
                           .entity(ApiResponse.notFound(e.getMessage()))
                           .build();
        }
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Mettre à jour un client")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Client mis à jour",
                    content = @Content(schema = @Schema(implementation = ClientDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Client introuvable")
    })
    public Response updateClient(
            @Parameter(description = "ID du client", required = true) @PathParam("id") Long id,
            @RequestBody(description = "Nouvelles données du client", required = true,
                    content = @Content(schema = @Schema(implementation = ClientDTO.class)))
            ClientDTO dto) {
        ClientDTO updated = clientService.updateUser(id, dto);
        if (updated == null)
            return Response.status(404).entity(ApiResponse.notFound("Client introuvable")).build();
        return Response.ok(ApiResponse.ok(updated)).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Supprimer un client")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "204", description = "Client supprimé"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Client introuvable")
    })
    public Response deleteClient(
            @Parameter(description = "ID du client", required = true) @PathParam("id") Long id) {
        clientService.deleteUser(id);
        return Response.status(204).entity(ApiResponse.noContent()).build();
    }

    @GET
    @Path("/{id}/groupes")
    @Operation(
            summary     = "Lister les groupes d'un client",
            description = "Retourne tous les groupes auxquels appartient le client."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Liste des groupes",
                    content = @Content(schema = @Schema(implementation = ClientGroupeDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Client introuvable")
    })
    public Response getGroupesOfClient(
            @Parameter(description = "ID du client", required = true) @PathParam("id") Long id) {
        List<ClientGroupeDTO> list = clientService.getGroupesOfClient(id);
        return Response.ok(ApiResponse.ok(list)).build();
    }

    @POST
    @Path("/{clientId}/groupes/{groupeId}")
    @Operation(
            summary     = "Ajouter un client à un groupe",
            description = "Crée l'association entre un client et un groupe."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201", description = "Association créée",
                    content = @Content(schema = @Schema(implementation = ClientGroupeDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Client ou groupe introuvable")
    })
    public Response addClientToGroupe(
            @Parameter(description = "ID du client", required = true) @PathParam("clientId") Long clientId,
            @Parameter(description = "ID du groupe", required = true) @PathParam("groupeId") Long groupeId) {
        ClientGroupeDTO result = clientService.addClientToGroupe(clientId, groupeId);
        if (result == null)
            return Response.status(404)
                    .entity(ApiResponse.notFound("Client ou groupe introuvable")).build();
        return Response.status(201).entity(ApiResponse.created(result)).build();
    }
}