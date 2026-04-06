package fr.istic.taa.jaxrs.rest;

import fr.istic.taa.jaxrs.dto.ApiResponse;
import fr.istic.taa.jaxrs.dto.ClientDTO;
import fr.istic.taa.jaxrs.dto.ClientGroupeDTO;
import fr.istic.taa.jaxrs.service.ClientService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/clients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClientResource {

    private final ClientService clientService = new ClientService();

    // ─── GET /clients ────────────────────────────────────────────────────────
    @GET
    public Response getAllClients(
            @QueryParam("email")   String email,
            @QueryParam("country") String country) {

        if (email != null && !email.isBlank()) {
            ClientDTO dto = clientService.findByEmail(email);
            if (dto == null)
                return Response.status(404)
                        .entity(ApiResponse.notFound("Aucun client avec cet email")).build();
            return Response.ok(ApiResponse.ok(dto)).build();
        }

        if (country != null && !country.isBlank()) {
            List<ClientDTO> list = clientService.findByCountry(country);
            return Response.ok(ApiResponse.ok(list)).build();
        }

        return Response.ok(ApiResponse.ok(clientService.findAllUsers())).build();
    }

    // ─── GET /clients/{id} ───────────────────────────────────────────────────
    @GET
    @Path("/{id}")
    public Response getClient(@PathParam("id") Long id) {
        ClientDTO dto = clientService.findUser(id);
        if (dto == null)
            return Response.status(404)
                    .entity(ApiResponse.notFound("Client introuvable")).build();
        return Response.ok(ApiResponse.ok(dto)).build();
    }

    // ─── POST /clients ───────────────────────────────────────────────────────
    @POST
    public Response createClient(ClientDTO dto) {
        ClientDTO created = clientService.createUser(dto);
        return Response.status(201)
                .entity(ApiResponse.created(created)).build();
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

    // ─── PUT /clients/{id} ───────────────────────────────────────────────────
    @PUT
    @Path("/{id}")
    public Response updateClient(@PathParam("id") Long id, ClientDTO dto) {
        ClientDTO updated = clientService.updateUser(id, dto);
        if (updated == null)
            return Response.status(404)
                    .entity(ApiResponse.notFound("Client introuvable")).build();
        return Response.ok(ApiResponse.ok(updated)).build();
    }

    // ─── DELETE /clients/{id} ────────────────────────────────────────────────
    @DELETE
    @Path("/{id}")
    public Response deleteClient(@PathParam("id") Long id) {
        clientService.deleteUser(id);
        return Response.status(204)
                .entity(ApiResponse.noContent()).build();
    }

    // ─── GET /clients/{id}/groupes ───────────────────────────────────────────
    @GET
    @Path("/{id}/groupes")
    public Response getGroupesOfClient(@PathParam("id") Long id) {
        List<ClientGroupeDTO> list = clientService.getGroupesOfClient(id);
        return Response.ok(ApiResponse.ok(list)).build();
    }

    // ─── POST /clients/{clientId}/groupes/{groupeId} ─────────────────────────
    @POST
    @Path("/{clientId}/groupes/{groupeId}")
    public Response addClientToGroupe(@PathParam("clientId") Long clientId,
                                      @PathParam("groupeId") Long groupeId) {
        ClientGroupeDTO result = clientService.addClientToGroupe(clientId, groupeId);
        if (result == null)
            return Response.status(404)
                    .entity(ApiResponse.notFound("Client ou groupe introuvable")).build();
        return Response.status(201)
                .entity(ApiResponse.created(result)).build();
    }
}