package fr.istic.taa.jaxrs.rest;

import fr.istic.taa.jaxrs.dto.ApiResponse;
import fr.istic.taa.jaxrs.dto.MessageDTO;
import fr.istic.taa.jaxrs.service.MessageService;

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

@Path("/messages")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Messages", description = "Gestion des messages")
public class MessageResource {

    private final MessageService messageService = new MessageService();

    @GET
    @Operation(summary = "Lister les messages reçus d'un client ou d'un groupe")
    public Response getMessages(
            @QueryParam("userId")   Long userId,
            @QueryParam("groupeId") Long groupeId) {

        if (userId == null && groupeId == null)
            return Response.status(400).entity(ApiResponse.error("userId ou groupeId est requis")).build();
        if (userId != null && groupeId != null)
            return Response.status(400).entity(ApiResponse.error("Fournir userId OU groupeId, pas les deux")).build();
        if (userId != null)
            return Response.ok(ApiResponse.ok(messageService.getMessagesByUser(userId))).build();
        return Response.ok(ApiResponse.ok(messageService.getMessagesByGroupe(groupeId))).build();
    }

    // Récupère un message par son ID — nécessaire pour la page de modification
    @GET
    @Path("/{id}")
    @Operation(summary = "Récupérer un message par son ID")
    public Response getMessageById(@PathParam("id") Long id) {
        MessageDTO dto = messageService.getMessageById(id);
        if (dto == null)
            return Response.status(404).entity(ApiResponse.notFound("Message introuvable")).build();
        return Response.ok(ApiResponse.ok(dto)).build();
    }

    @GET
    @Path("/sent/{senderId}")
    @Operation(summary = "Mes messages envoyés")
    public Response getMesMessages(@PathParam("senderId") Long senderId) {
        try {
            return Response.ok(ApiResponse.ok(messageService.getMesMessages(senderId))).build();
        } catch (RuntimeException e) {
            return Response.status(404).entity(ApiResponse.notFound(e.getMessage())).build();
        }
    }

    @POST
    @Operation(summary = "Envoyer un message")
    public Response createMessage(MessageDTO dto) {
        try {
            return Response.status(201).entity(ApiResponse.created(messageService.createMessage(dto))).build();
        } catch (RuntimeException e) {
            return Response.status(400).entity(ApiResponse.error(e.getMessage())).build();
        }
    }

    // Mise à jour d'un message existant
    @PUT
    @Path("/{id}")
    @Operation(summary = "Mettre à jour un message")
    public Response updateMessage(@PathParam("id") Long id, MessageDTO dto) {
        try {
            MessageDTO updated = messageService.updateMessage(id, dto);
            if (updated == null)
                return Response.status(404).entity(ApiResponse.notFound("Message introuvable")).build();
            return Response.ok(ApiResponse.ok(updated)).build();
        } catch (RuntimeException e) {
            return Response.status(400).entity(ApiResponse.error(e.getMessage())).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Supprimer un message")
    public Response deleteMessage(@PathParam("id") Long id) {
        messageService.deleteMessage(id);
        return Response.status(204).entity(ApiResponse.noContent()).build();
    }
}