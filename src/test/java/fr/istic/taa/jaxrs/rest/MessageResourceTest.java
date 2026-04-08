package fr.istic.taa.jaxrs.rest;

import fr.istic.taa.jaxrs.dto.AccountDTO;
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

    private Long testUserId;

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "http://localhost:8080";
    }

    @Before
    public void createPrerequisites() {
        AccountDTO userDto = new AccountDTO();
        userDto.setEmail("msg_receiver@test.com");
        userDto.setPassword("pass");
        userDto.setFirstname("M");
        userDto.setLastname("R");
        userDto.setType("USER");

        Number idNum = given().contentType(ContentType.JSON).body(userDto)
                .post("/accounts").then().extract().path("data.id");
        testUserId = idNum.longValue();
    }

    @After
    public void cleanUp() {
        if (testUserId != null) {
            given().delete("/accounts/" + testUserId); // Cascadera sur les messages
        }
    }

    @Test
    public void testSendMessageToUser() {
        MessageDTO dto = new MessageDTO();
        dto.setTitle("Alerte API");
        dto.setContent("Ceci est un test REST");
        dto.setDateSend(LocalDateTime.now());
        dto.setUserId(testUserId); // Destinataire : un User

        Number msgIdNum = given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .post("/messages")
                .then()
                .statusCode(201)
                .body("data.title", equalTo("Alerte API"))
                .extract().path("data.id");

        Long msgId = msgIdNum.longValue();

        // Récupérer les messages du user
        given()
                .when()
                .get("/messages?userId=" + testUserId)
                .then()
                .statusCode(200)
                .body("data.size()", greaterThanOrEqualTo(1))
                .body("data[0].title", equalTo("Alerte API"));

        // Test d'erreur : requete mal formée (pas de userId ni de groupeId)
        given()
                .when()
                .get("/messages")
                .then()
                .statusCode(400)
                .body("message", equalTo("userId ou groupeId est requis"));
    }
}