package fr.istic.taa.jaxrs.rest;

import fr.istic.taa.jaxrs.entity.Client;
import fr.istic.taa.jaxrs.service.ClientService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/clients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClientResource {

    private ClientService clientService = new ClientService();

    @GET
    public List<Client> getClients() {
        return clientService.findAllUsers();
    }

    @POST
    public Response createClient(Client client) {
        Client created = clientService.createUser(client);

        return Response
                .status(Response.Status.CREATED)
                .entity(created)
                .build();
    }

//    @POST
//    @Path("/{groupeId}")
//    public void addClient(@PathParam("groupeId") Long groupeId, String name) {
//        clientService.createUser(name, groupeId);
//    }
}
