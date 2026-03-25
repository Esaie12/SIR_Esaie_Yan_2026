package fr.istic.taa.jaxrs.rest;

import fr.istic.taa.jaxrs.dto.ApiResponse;
import fr.istic.taa.jaxrs.dto.ClientDTO;
import fr.istic.taa.jaxrs.dto.GroupeDTO;
import fr.istic.taa.jaxrs.service.ClientService;
import fr.istic.taa.jaxrs.service.GroupeService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/groupes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GroupeResource {

    private final GroupeService groupeService = new GroupeService();
    private final ClientService clientService = new ClientService();

    // ─── GET /groupes ────────────────────────────────────────────────────────
    @GET
    public Response getAllGroupes() {
        List<GroupeDTO> list = groupeService.findAllGroupes();
        return Response.ok(ApiResponse.ok(list)).build();
    }

    // ─── GET /groupes/{id} ───────────────────────────────────────────────────
    @GET
    @Path("/{id}")
    public Response getGroupe(@PathParam("id") Long id) {
        GroupeDTO dto = groupeService.findGroupe(id);
        if (dto == null)
            return Response.status(404)
                    .entity(ApiResponse.notFound("Groupe introuvable")).build();
        return Response.ok(ApiResponse.ok(dto)).build();
    }

    // ─── POST /groupes ───────────────────────────────────────────────────────
    @POST
    public Response createGroupe(GroupeDTO dto) {
        GroupeDTO created = groupeService.createGroupe(dto);
        return Response.status(201)
                .entity(ApiResponse.created(created)).build();
    }
    
    @GET
    @Path("/by-user/{userId}")
    public Response getGroupesOfUser(@PathParam("userId") Long userId) {
        try {
            return Response.ok(ApiResponse.ok(groupeService.getGroupesByUser(userId))).build();
        } catch (RuntimeException e) {
            return Response.status(404)
                    .entity(ApiResponse.notFound(e.getMessage())).build();
        }
    }

    // ─── PUT /groupes/{id} ───────────────────────────────────────────────────
    @PUT
    @Path("/{id}")
    public Response updateGroupe(@PathParam("id") Long id, GroupeDTO dto) {
        GroupeDTO updated = groupeService.updateGroupe(id, dto);
        if (updated == null)
            return Response.status(404)
                    .entity(ApiResponse.notFound("Groupe introuvable")).build();
        return Response.ok(ApiResponse.ok(updated)).build();
    }

    // ─── DELETE /groupes/{id} ────────────────────────────────────────────────
    @DELETE
    @Path("/{id}")
    public Response deleteGroupe(@PathParam("id") Long id) {
        groupeService.deleteGroupe(id);
        return Response.status(204)
                .entity(ApiResponse.noContent()).build();
    }

    // ─── GET /groupes/{id}/clients ───────────────────────────────────────────
    @GET
    @Path("/{id}/clients")
    public Response getClientsOfGroupe(@PathParam("id") Long id) {
        List<ClientDTO> list = clientService.findByGroupe(id);
        return Response.ok(ApiResponse.ok(list)).build();
    }
}