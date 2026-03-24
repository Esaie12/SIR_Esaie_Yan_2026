package fr.istic.taa.jaxrs.rest;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

import fr.istic.taa.jaxrs.dto.ApiResponse;
import fr.istic.taa.jaxrs.dto.MessageDTO;
import fr.istic.taa.jaxrs.service.MessageService;
import jakarta.ws.rs.*;


@Path("/messages")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class MessageResource {

    private final MessageService messageService = new MessageService();

    // ─── GET /messages ───────────────────────────────────────────────────────
    @GET
    public Response getMessages(@QueryParam("userId") Long userId) {

        // 🔍 messages d’un utilisateur
        if (userId != null) {
            List<MessageDTO> list = messageService.getMessagesByUser(userId);
            return Response.ok(ApiResponse.ok(list)).build();
        }

        // (optionnel) → si tu ajoutes findAll plus tard
        return Response.status(400)
                .entity(ApiResponse.error("userId requis"))
                .build();
    }

    // ─── POST /messages ──────────────────────────────────────────────────────
    @POST
    public Response createMessage(MessageDTO dto) {
        try {
            MessageDTO created = messageService.createMessage(dto);

            return Response.status(201)
                    .entity(ApiResponse.created(created))
                    .build();

        } catch (RuntimeException e) {
            return Response.status(400)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        }
    }

    // ─── DELETE /messages/{id} ───────────────────────────────────────────────
    @DELETE
    @Path("/{id}")
    public Response deleteMessage(@PathParam("id") Long id) {

        messageService.deleteMessage(id);

        return Response.status(204)
                .entity(ApiResponse.noContent())
                .build();
    }
}