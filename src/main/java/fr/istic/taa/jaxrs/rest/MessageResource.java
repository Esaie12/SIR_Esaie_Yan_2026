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

import java.util.List;

@Path("/messages")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Messages", description = "Gestion des messages — envoi à un utilisateur ou à un groupe entier")
public class MessageResource {

    private final MessageService messageService = new MessageService();

    @GET
    @Operation(
            summary     = "Lister les messages d'un utilisateur ou d'un groupe",
            description = "Fournir userId OU groupeId (pas les deux). "
                    + "Retourne les messages triés par date d'envoi décroissante."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Liste des messages",
                    content = @Content(schema = @Schema(implementation = MessageDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "userId ou groupeId requis (pas les deux)")
    })
    public Response getMessages(
            @Parameter(description = "ID de l'utilisateur destinataire") @QueryParam("userId")   Long userId,
            @Parameter(description = "ID du groupe destinataire")         @QueryParam("groupeId") Long groupeId) {

        if (userId == null && groupeId == null)
            return Response.status(400)
                    .entity(ApiResponse.error("userId ou groupeId est requis")).build();

        if (userId != null && groupeId != null)
            return Response.status(400)
                    .entity(ApiResponse.error("Fournir userId OU groupeId, pas les deux")).build();

        if (userId != null) {
            List<MessageDTO> list = messageService.getMessagesByUser(userId);
            return Response.ok(ApiResponse.ok(list)).build();
        }

        List<MessageDTO> list = messageService.getMessagesByGroupe(groupeId);
        return Response.ok(ApiResponse.ok(list)).build();
    }

    @POST
    @Operation(
            summary     = "Envoyer un message",
            description = "Envoie un message à un utilisateur (userId renseigné, groupeId null) "
                    + "ou à un groupe entier (groupeId renseigné, userId null). "
                    + "Les deux champs ne peuvent pas être renseignés simultanément."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201", description = "Message envoyé",
                    content = @Content(schema = @Schema(implementation = MessageDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "Destinataire invalide ou manquant")
    })
    public Response createMessage(
            @RequestBody(
                    description = "title, content, dateSend + userId OU groupeId",
                    required    = true,
                    content     = @Content(schema = @Schema(implementation = MessageDTO.class)))
            MessageDTO dto) {
        try {
            MessageDTO created = messageService.createMessage(dto);
            return Response.status(201).entity(ApiResponse.created(created)).build();
        } catch (RuntimeException e) {
            return Response.status(400).entity(ApiResponse.error(e.getMessage())).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Supprimer un message")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "204", description = "Message supprimé"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Message introuvable")
    })
    public Response deleteMessage(
            @Parameter(description = "ID du message", required = true) @PathParam("id") Long id) {
        messageService.deleteMessage(id);
        return Response.status(204).entity(ApiResponse.noContent()).build();
    }
}