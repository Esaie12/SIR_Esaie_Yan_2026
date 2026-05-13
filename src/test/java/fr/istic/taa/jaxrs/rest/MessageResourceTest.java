package fr.istic.taa.jaxrs.rest;

import fr.istic.taa.jaxrs.dto.AccountDTO;
import fr.istic.taa.jaxrs.dto.ClientDTO;
import fr.istic.taa.jaxrs.dto.MessageDTO;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class MessageResourceTest {

    private Long testSenderId;
    private Long testClientId;

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "http://localhost:8080";
    }

    @Before
    public void createPrerequisites() {
        // 1. Créer l'expéditeur (Account/Users)
        AccountDTO userDto = new AccountDTO();
        userDto.setEmail("msg_sender_api@test.com");
        userDto.setPassword("pass");
        userDto.setFirstname("M");
        userDto.setLastname("R");
        userDto.setType("PHYSIQUE"); // ou MORAL, mais pas USER qui n'existe pas dans la factory

        Number idNum = given().contentType(ContentType.JSON).body(userDto)
                .post("/accounts").then().statusCode(201).extract().path("data.id");
        testSenderId = idNum.longValue();

        // 2. Créer le client destinataire lié à cet expéditeur
        ClientDTO clientDto = new ClientDTO();
        clientDto.setName("Client API Test");
        clientDto.setEmail("client_api@test.com");
        clientDto.setCountry("France");
        clientDto.setSexe("M");
        clientDto.setUserId(testSenderId); // Important : lier le client à l'utilisateur

        Number clientIdNum = given().contentType(ContentType.JSON).body(clientDto)
                .post("/clients").then().statusCode(201).extract().path("data.id");
        testClientId = clientIdNum.longValue();
    }

    @After
    public void cleanUp() {
        if (testClientId != null) {
            given().delete("/clients/" + testClientId);
        }
        if (testSenderId != null) {
            given().delete("/accounts/" + testSenderId);
        }
    }

    @Test
    public void testSendMessageToUser() {
        MessageDTO dto = new MessageDTO();
        dto.setTitle("Alerte API");
        dto.setContent("Ceci est un test REST");
        dto.setDateSend(LocalDateTime.now());
        // L'expéditeur
        dto.setSenderId(testSenderId);
        // Le destinataire (Client)
        dto.setUserId(testClientId);

        // 1. Créer le message
        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .post("/messages")
                .then()
                .statusCode(201)
                .body("data.title", equalTo("Alerte API"));

        // 2. Récupérer les messages du client
        given()
                .when()
                .get("/messages?userId=" + testClientId)
                .then()
                .statusCode(200)
                .body("data.size()", greaterThanOrEqualTo(1))
                .body("data[0].title", equalTo("Alerte API"));

        // 3. Test d'erreur : requête mal formée (pas de userId ni de groupeId)
        given()
                .when()
                .get("/messages")
                .then()
                .statusCode(400)
                .body("message", equalTo("userId ou groupeId est requis"));
    }
}