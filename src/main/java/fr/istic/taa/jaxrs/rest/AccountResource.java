package fr.istic.taa.jaxrs.rest;

import java.util.List;

import fr.istic.taa.jaxrs.dto.AccountDTO;
import fr.istic.taa.jaxrs.dto.ApiResponse;
import fr.istic.taa.jaxrs.service.AccountService;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.*;


@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountResource {
	
	private final AccountService accountService = new AccountService();

    // ─── GET /accounts ───────────────────────────────────────────────────────
    @GET
    public Response getAllAccounts(
            @QueryParam("email") String email,
            @QueryParam("type") String type,
            @QueryParam("active") Boolean active) {

        // 🔍 recherche par email
        if (email != null && !email.isBlank()) {
            AccountDTO dto = accountService.findByEmail(email);
            if (dto == null)
                return Response.status(404)
                        .entity(ApiResponse.notFound("Aucun compte avec cet email"))
                        .build();

            return Response.ok(ApiResponse.ok(dto)).build();
        }

        // 🔍 filtre par type
        if (type != null && !type.isBlank()) {
            List<AccountDTO> list;

            switch (type.toUpperCase()) {

                case "ADMIN":
                    list = accountService.findAdmins();
                    break;

                case "USER":
                    list = accountService.findActiveUsers();
                    break;

                case "MORAL":
                    list = accountService.findMoralAccounts();
                    break;

                case "PHYSIQUE":
                    list = accountService.findPhysiqueAccounts();
                    break;

                default:
                    return Response.status(400)
                            .entity(ApiResponse.error("Type invalide : " + type))
                            .build();
            }

            return Response.ok(ApiResponse.ok(list)).build();
        }

        // 🔍 filtre comptes actifs
        if (active != null && active) {
            return Response.ok(ApiResponse.ok(accountService.findActiveUsers())).build();
        }

        // 🔁 défaut : tous les comptes
        return Response.ok(ApiResponse.ok(accountService.findAllAccounts())).build();
    }

    // ─── GET /accounts/{id} ──────────────────────────────────────────────────
    @GET
    @Path("/{id}")
    public Response getAccount(@PathParam("id") Long id) {
        AccountDTO dto = accountService.findAccount(id);

        if (dto == null)
            return Response.status(404)
                    .entity(ApiResponse.notFound("Compte introuvable"))
                    .build();

        return Response.ok(ApiResponse.ok(dto)).build();
    }

    // ─── POST /accounts ──────────────────────────────────────────────────────
    @POST
    public Response createAccount(AccountDTO dto) {
        try {
            AccountDTO created = accountService.createAccount(dto);

            return Response.status(201)
                    .entity(ApiResponse.created(created))
                    .build();

        } catch (RuntimeException e) {
            return Response.status(400)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        }
    }

    // ─── PUT /accounts/{id} ──────────────────────────────────────────────────
    @PUT
    @Path("/{id}")
    public Response updateAccount(@PathParam("id") Long id, AccountDTO dto) {

        AccountDTO updated = accountService.updateAccount(id, dto);

        if (updated == null)
            return Response.status(404)
                    .entity(ApiResponse.notFound("Compte introuvable"))
                    .build();

        return Response.ok(ApiResponse.ok(updated)).build();
    }

    // ─── DELETE /accounts/{id} ───────────────────────────────────────────────
    @DELETE
    @Path("/{id}")
    public Response deleteAccount(@PathParam("id") Long id) {
        accountService.deleteAccount(id);

        return Response.status(204)
                .entity(ApiResponse.noContent())
                .build();
    }

    // ─── POST /accounts/login ────────────────────────────────────────────────
    @POST
    @Path("/login")
    public Response login(AccountDTO dto) {

        AccountDTO account = accountService.login(dto.getEmail(), dto.getPassword());

        if (account == null)
            return Response.status(401)
                    .entity(ApiResponse.error("Email ou mot de passe incorrect"))
                    .build();

        return Response.ok(ApiResponse.ok(account)).build();
    }
    
}
