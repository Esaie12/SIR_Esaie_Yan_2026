package fr.istic.taa.jaxrs.rest;

import fr.istic.taa.jaxrs.dto.AccountDTO;
import fr.istic.taa.jaxrs.dto.ApiResponse;
import fr.istic.taa.jaxrs.service.AccountService;

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

@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Accounts", description = "Gestion des comptes utilisateurs : Admin, User, Moral, Physique")
public class AccountResource {

    private final AccountService accountService = new AccountService();

    @GET
    @Operation(
            summary     = "Lister ou filtrer les comptes",
            description = "Retourne tous les comptes. Filtrable par email, type (ADMIN, USER, MORAL, PHYSIQUE) "
                    + "ou par statut actif."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Liste des comptes",
                    content = @Content(schema = @Schema(implementation = AccountDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "Type de filtre invalide"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Aucun compte trouvé pour l'email donné")
    })
    public Response getAllAccounts(
            @Parameter(description = "Filtrer par email exact")                         @QueryParam("email")  String email,
            @Parameter(description = "Filtrer par type : ADMIN, USER, MORAL, PHYSIQUE") @QueryParam("type")   String type,
            @Parameter(description = "Filtrer les comptes actifs uniquement")           @QueryParam("active") Boolean active) {

        if (email != null && !email.isBlank()) {
            AccountDTO dto = accountService.findByEmail(email);
            if (dto == null)
                return Response.status(404)
                        .entity(ApiResponse.notFound("Aucun compte avec cet email")).build();
            return Response.ok(ApiResponse.ok(dto)).build();
        }

        if (type != null && !type.isBlank()) {
            List<AccountDTO> list;
            switch (type.toUpperCase()) {
                case "ADMIN":    list = accountService.findAdmins();           break;
                case "USER":     list = accountService.findActiveUsers();      break;
                case "MORAL":    list = accountService.findMoralAccounts();    break;
                case "PHYSIQUE": list = accountService.findPhysiqueAccounts(); break;
                default:
                    return Response.status(400)
                            .entity(ApiResponse.error("Type invalide : " + type)).build();
            }
            return Response.ok(ApiResponse.ok(list)).build();
        }

        if (active != null && active) {
            return Response.ok(ApiResponse.ok(accountService.findActiveUsers())).build();
        }

        return Response.ok(ApiResponse.ok(accountService.findAllAccounts())).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Récupérer un compte par son ID")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Compte trouvé",
                    content = @Content(schema = @Schema(implementation = AccountDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Compte introuvable")
    })
    public Response getAccount(
            @Parameter(description = "ID du compte", required = true) @PathParam("id") Long id) {
        AccountDTO dto = accountService.findAccount(id);
        if (dto == null)
            return Response.status(404).entity(ApiResponse.notFound("Compte introuvable")).build();
        return Response.ok(ApiResponse.ok(dto)).build();
    }

    @POST
    @Operation(
            summary     = "Créer un nouveau compte",
            description = "Crée un compte selon le type fourni dans le DTO : ADMIN, USER, MORAL ou PHYSIQUE. "
                    + "L'email doit être unique."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201", description = "Compte créé",
                    content = @Content(schema = @Schema(implementation = AccountDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "Email déjà utilisé ou données invalides")
    })
    public Response createAccount(
            @RequestBody(description = "Données du compte à créer. Le champ 'type' est obligatoire.",
                    required = true,
                    content  = @Content(schema = @Schema(implementation = AccountDTO.class)))
            AccountDTO dto) {
        try {
            AccountDTO created = accountService.createAccount(dto);
            return Response.status(201).entity(ApiResponse.created(created)).build();
        } catch (RuntimeException e) {
            return Response.status(400).entity(ApiResponse.error(e.getMessage())).build();
        }
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Mettre à jour un compte")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Compte mis à jour",
                    content = @Content(schema = @Schema(implementation = AccountDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Compte introuvable")
    })
    public Response updateAccount(
            @Parameter(description = "ID du compte", required = true) @PathParam("id") Long id,
            @RequestBody(description = "Nouvelles données du compte", required = true,
                    content = @Content(schema = @Schema(implementation = AccountDTO.class)))
            AccountDTO dto) {
        AccountDTO updated = accountService.updateAccount(id, dto);
        if (updated == null)
            return Response.status(404).entity(ApiResponse.notFound("Compte introuvable")).build();
        return Response.ok(ApiResponse.ok(updated)).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Supprimer un compte")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "204", description = "Compte supprimé"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Compte introuvable")
    })
    public Response deleteAccount(
            @Parameter(description = "ID du compte", required = true) @PathParam("id") Long id) {
        accountService.deleteAccount(id);
        return Response.status(204).entity(ApiResponse.noContent()).build();
    }

    @POST
    @Path("/login")
    @Operation(
            summary     = "Authentification d'un compte",
            description = "Vérifie l'email et le mot de passe. Retourne le compte si les identifiants sont valides."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Authentification réussie",
                    content = @Content(schema = @Schema(implementation = AccountDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "Email ou mot de passe incorrect")
    })
    public Response login(
            @RequestBody(description = "Identifiants : email + password", required = true,
                    content = @Content(schema = @Schema(implementation = AccountDTO.class)))
            AccountDTO dto) {
        AccountDTO account = accountService.login(dto.getEmail(), dto.getPassword());
        if (account == null)
            return Response.status(401)
                    .entity(ApiResponse.error("Email ou mot de passe incorrect")).build();
        return Response.ok(ApiResponse.ok(account)).build();
    }
}