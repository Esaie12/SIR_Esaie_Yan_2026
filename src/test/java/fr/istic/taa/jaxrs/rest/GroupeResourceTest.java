package fr.istic.taa.jaxrs.rest;

import fr.istic.taa.jaxrs.dto.AccountDTO;
import fr.istic.taa.jaxrs.dto.GroupeDTO;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class GroupeResourceTest {

    private Long testUserId;

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "http://localhost:8080";
    }

    @Before
    public void createPrerequisites() {
        AccountDTO userDto = new AccountDTO();
        userDto.setEmail("groupe_owner@test.com");
        userDto.setPassword("pass");
        userDto.setFirstname("G");
        userDto.setLastname("O");
        userDto.setType("USER");

        Number idNum = given().contentType(ContentType.JSON).body(userDto)
                .post("/accounts").then().extract().path("data.id");
        testUserId = idNum.longValue();
    }

    @After
    public void cleanUp() {
        if (testUserId != null) {
            given().delete("/accounts/" + testUserId);
        }
    }

    @Test
    public void testCreateAndGetGroupe() {
        GroupeDTO dto = new GroupeDTO();
        dto.setLibelle("VIP API");
        dto.setColor("#000000");
        dto.setUserId(testUserId);

        Number groupeIdNum = given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .post("/groupes")
                .then()
                .statusCode(201)
                .body("data.libelle", equalTo("VIP API"))
                .extract().path("data.id");

        Long groupeId = groupeIdNum.longValue();

        // Récupérer le groupe
        given()
                .when()
                .get("/groupes/" + groupeId)
                .then()
                .statusCode(200)
                .body("data.color", equalTo("#000000"));

        // Récupérer les groupes d'un utilisateur
        given()
                .when()
                .get("/groupes/by-user/" + testUserId)
                .then()
                .statusCode(200)
                .body("data.size()", greaterThanOrEqualTo(1));
    }
}