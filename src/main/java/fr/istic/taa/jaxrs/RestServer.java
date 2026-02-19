package fr.istic.taa.jaxrs;

import fr.istic.taa.jaxrs.dao.generic.EntityManagerHelper;
import fr.istic.taa.jaxrs.entity.Client;
import io.undertow.Undertow;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;

import java.util.logging.Logger;

/**
 * RESTfull microservice, based on JAX-RS and JBoss Undertow
 *
 */
public class RestServer {

    private static final Logger logger = Logger.getLogger(RestServer.class.getName());

    public static void main( String[] args ) {

        EntityManager manager = EntityManagerHelper.getEntityManager();

        //fr.istic.taa.jaxrs.dao.generic.EntityManagerHelper.getEntityManager();
        EntityTransaction tx = manager.getTransaction();
        tx.begin();

        /*Client c = new Client();
        c.setName("Test Marketing");
        manager.persist(c);*/
        manager.getTransaction().commit();

        UndertowJaxrsServer ut = new UndertowJaxrsServer();

        TestApplication ta = new TestApplication();

        ut.deploy(ta);

        ut.start(
                Undertow.builder()
                        .addHttpListener(8080, "localhost")

        );

        logger.info("JAX-RS based micro-service running!");
    }
}
