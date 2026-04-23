package fr.istic.taa.jaxrs.rest;

import fr.istic.taa.jaxrs.dto.AccountDTO;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AccountResourceTest {

    @BeforeClass
    public static void setup() {
        // Indique à REST Assured où taper
        RestAssured.baseURI = "http://localhost:8080";
    }

    @Test
    public void testCreateAndGetAccount() {
        AccountDTO dto = new AccountDTO();
        dto.setEmail("api_admin@test.com");
        dto.setPassword("secret");
        dto.setFirstname("Api");
        dto.setLastname("Admin");
        dto.setType("ADMIN");
        dto.setPseudo("super_api");

        // 1. Création (POST)
        Number idNum = given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .post("/accounts")
                .then()
                .statusCode(201)
                .body("message", equalTo("Cree"))
                .body("data.email", equalTo("api_admin@test.com"))
                .extract().path("data.id");

        Long accountId = idNum.longValue();

        // 2. Récupération (GET)
        given()
                .when()
                .get("/accounts/" + accountId)
                .then()
                .statusCode(200)
                .body("data.firstname", equalTo("Api"));

        // 3. Nettoyage (DELETE)
        given().when().delete("/accounts/" + accountId).then().statusCode(204);
    }

    @Test
    public void testLogin() {
        AccountDTO dto = new AccountDTO();
        dto.setEmail("login_api@test.com");
        dto.setPassword("mypass");
        dto.setFirstname("Log");
        dto.setLastname("In");
        dto.setType("USER");

        Number idNum = given().contentType(ContentType.JSON).body(dto)
                .post("/accounts").then().statusCode(201).extract().path("data.id");

        // Test du login
        AccountDTO credentials = new AccountDTO();
        credentials.setEmail("login_api@test.com");
        credentials.setPassword("mypass");

        given()
                .contentType(ContentType.JSON)
                .body(credentials)
                .when()
                .post("/accounts/login")
                .then()
                .statusCode(200)
                .body("data.email", equalTo("login_api@test.com"));

        // Mauvais mot de passe
        credentials.setPassword("wrong");
        given().contentType(ContentType.JSON).body(credentials)
                .post("/accounts/login").then().statusCode(401);

        // Nettoyage
        given().delete("/accounts/" + idNum.longValue());
    }
}