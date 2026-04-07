package fr.istic.taa.jaxrs;

import fr.istic.taa.jaxrs.dao.EntityManagerHelper;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.util.Headers;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import java.util.logging.Logger;

public class RestServer {

    private static final Logger logger = Logger.getLogger(RestServer.class.getName());
    private static final String SWAGGER_VERSION = "5.17.14";

    public static void main(String[] args) throws Exception {

        EntityManager manager = EntityManagerHelper.getEntityManager();
        EntityTransaction tx = manager.getTransaction();
        tx.begin();
        tx.commit();

        UndertowJaxrsServer ut = new UndertowJaxrsServer();

        // passage de la classe et non de l'instance
        DeploymentInfo deploymentInfo = ut.undertowDeployment(TestApplication.class);

        deploymentInfo.setDeploymentName("REST")
                .setContextPath("/")
                .setClassLoader(RestServer.class.getClassLoader());

        DeploymentManager deploymentManager = Servlets.defaultContainer()
                .addDeployment(deploymentInfo);
        deploymentManager.deploy();
        HttpHandler restHandler = deploymentManager.start();

        HttpHandler initializerHandler = exchange -> {
            String js = "window.onload = function() {\n"
                    + "  window.ui = SwaggerUIBundle({\n"
                    + "    url: 'http://localhost:8080/openapi.json',\n"
                    + "    dom_id: '#swagger-ui',\n"
                    + "    presets: [SwaggerUIBundle.presets.apis, SwaggerUIStandalonePreset],\n"
                    + "    layout: 'StandaloneLayout'\n"
                    + "  });\n"
                    + "};\n";
            exchange.getResponseHeaders()
                    .put(Headers.CONTENT_TYPE, "application/javascript");
            exchange.getResponseSender().send(js);
        };

        ResourceHandler swaggerHandler = new ResourceHandler(
                new ClassPathResourceManager(
                        RestServer.class.getClassLoader(),
                        "META-INF/resources/webjars/swagger-ui/" + SWAGGER_VERSION
                )
        ).setDirectoryListingEnabled(false);

        PathHandler pathHandler = new PathHandler(restHandler)
                .addExactPath("/swagger-ui/swagger-initializer.js", initializerHandler)
                .addPrefixPath("/swagger-ui", swaggerHandler);

        Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(pathHandler)
                .build();

        server.start();

        logger.info("Serveur      : http://localhost:8080");
        logger.info("OpenAPI JSON : http://localhost:8080/openapi.json");
        logger.info("Swagger UI   : http://localhost:8080/swagger-ui/index.html");
    }
}