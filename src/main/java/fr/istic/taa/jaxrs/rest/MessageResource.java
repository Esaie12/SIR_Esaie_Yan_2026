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
@Tag(name = "Messages", description = "Gestion des messages envoyés par les utilisateurs (Moral ou Physique)")
public class MessageResource {

    private final MessageService messageService = new MessageService();

    @GET
    @Operation(
            summary     = "Lister les messages d'un utilisateur",
            description = "Retourne tous les messages d'un utilisateur identifié par son userId, "
                    + "triés par date d'envoi décroissante. Le paramètre userId est obligatoire."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Liste des messages",
                    content = @Content(schema = @Schema(implementation = MessageDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "Paramètre userId manquant")
    })
    public Response getMessages(
            @Parameter(description = "ID de l'utilisateur (obligatoire)", required = true)
            @QueryParam("userId") Long userId) {

        if (userId == null)
            return Response.status(400)
                    .entity(ApiResponse.error("userId requis")).build();

        List<MessageDTO> list = messageService.getMessagesByUser(userId);
        return Response.ok(ApiResponse.ok(list)).build();
    }

    @POST
    @Operation(
            summary     = "Créer un nouveau message",
            description = "Crée un message associé à un utilisateur existant (Moral ou Physique). "
                    + "Le champ userId doit correspondre à un utilisateur valide en base."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201", description = "Message créé avec succès",
                    content = @Content(schema = @Schema(implementation = MessageDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "Utilisateur introuvable ou données invalides")
    })
    public Response createMessage(
            @RequestBody(
                    description = "Données du message : title, content, dateSend, userId",
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
    @Operation(
            summary     = "Supprimer un message",
            description = "Supprime définitivement le message identifié par son ID."
    )
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