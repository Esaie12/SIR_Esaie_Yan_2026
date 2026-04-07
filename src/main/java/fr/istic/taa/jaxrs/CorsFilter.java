package fr.istic.taa.jaxrs;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class CorsFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {

        responseContext.getHeaders().add("Access-Control-Allow-Origin",      "http://localhost:4200");
        responseContext.getHeaders().add("Access-Control-Allow-Headers",     "Content-Type, Accept, Authorization");
        responseContext.getHeaders().add("Access-Control-Allow-Methods",     "GET, POST, PUT, DELETE, OPTIONS");
        responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");

        // ✅répondre immédiatement aux requêtes preflight OPTIONS
        // Angular envoie un OPTIONS avant chaque POST/PUT pour vérifier les droits CORS.
        // Sans ça, Undertow peut retourner 404 ou 405 et bloquer la requête réelle.
        if ("OPTIONS".equalsIgnoreCase(requestContext.getMethod())) {
            requestContext.abortWith(Response.ok().build());
        }
    }
}