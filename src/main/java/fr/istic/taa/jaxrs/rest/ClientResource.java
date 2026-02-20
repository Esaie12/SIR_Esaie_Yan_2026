package fr.istic.taa.jaxrs.rest;

import fr.istic.taa.jaxrs.entity.Client;
import fr.istic.taa.jaxrs.service.ClientService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/clients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClientResource {

    private ClientService clientService = new ClientService();

    @GET
    public List<Client> getClients() {
        return clientService.getAllClients();
    }

    @POST
    @Path("/{groupeId}")
    public void addClient(@PathParam("groupeId") Long groupeId, String name) {
        clientService.createClientInGroupe(name, groupeId);
    }
}
