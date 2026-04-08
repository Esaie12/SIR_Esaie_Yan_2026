package fr.istic.taa.jaxrs.rest;

import fr.istic.taa.jaxrs.dto.AccountDTO;
import fr.istic.taa.jaxrs.dto.ClientDTO;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ClientResourceTest {

    private Long testUserId;

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "http://localhost:8080";
    }

    @Before
    public void createPrerequisites() {
        // Un client a obligatoirement besoin d'un User existant
        AccountDTO userDto = new AccountDTO();
        userDto.setEmail("client_owner@test.com");
        userDto.setPassword("pass");
        userDto.setFirstname("Owner");
        userDto.setLastname("User");
        userDto.setType("USER");

        Number idNum = given().contentType(ContentType.JSON).body(userDto)
                .post("/accounts").then().statusCode(201).extract().path("data.id");
        testUserId = idNum.longValue();
    }

    @After
    public void cleanUp() {
        if (testUserId != null) {
            given().delete("/accounts/" + testUserId); // La suppression en cascade supprimera le client
        }
    }

    @Test
    public void testCreateAndUpdateClient() {
        ClientDTO clientDto = new ClientDTO();
        clientDto.setName("API Client");
        clientDto.setEmail("apiclient@test.com");
        clientDto.setCountry("France");
        clientDto.setSexe("M");
        clientDto.setUserId(testUserId);

        // 1. Créer le client
        Number clientIdNum = given()
                .contentType(ContentType.JSON)
                .body(clientDto)
                .when()
                .post("/clients")
                .then()
                .statusCode(201)
                .body("data.name", equalTo("API Client"))
                .extract().path("data.id");

        Long clientId = clientIdNum.longValue();

        // 2. Mettre à jour le client
        clientDto.setCountry("Belgique");
        given()
                .contentType(ContentType.JSON)
                .body(clientDto)
                .when()
                .put("/clients/" + clientId)
                .then()
                .statusCode(200)
                .body("data.country", equalTo("Belgique"));

        // 3. Tester la recherche dynamique (Criteria)
        given()
                .when()
                .get("/clients?country=Belgique")
                .then()
                .statusCode(200)
                .body("data.size()", greaterThanOrEqualTo(1));
    }
}