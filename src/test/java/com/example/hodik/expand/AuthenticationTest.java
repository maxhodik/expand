package com.example.hodik.expand;

import com.example.hodik.expand.model.User;
import com.example.hodik.expand.repository.UserRepository;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuthenticationTest {
    private final String expectedUserBody = TestUtils.readResource("expected.user.body.json");
    private final User user = User.builder()
            .username("testuser")
            .password("testpassword")
            .build();
    private final String userBodyWrongName = TestUtils.readResource("expected.user.body.wrong.name.json");
    private final String userBodyWrongPassword = TestUtils.readResource("expected.user.body.wrong.password.json");

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
    }


    @Test
    public void testUserRegistrationAndAuthentication() {

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(expectedUserBody)
                .when()
                .post("/users/add")
                .then()
                .statusCode(201);


        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(expectedUserBody)
                .when()
                .post("/users/authenticate")
                .then()
                .statusCode(200)
                .body("username", equalTo("testuser"))
                .body("access_token", not(emptyOrNullString()))
                .body("refresh_token", not(emptyOrNullString()));
    }

    @Test
    public void testUserWrongRegistration() {
        userRepository.save(user);
        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(expectedUserBody)
                .when()
                .post("/users/add")
                .then()
                .statusCode(400)
                .contentType(ContentType.TEXT)
                .body(equalTo("Username is taken !! "));
    }

    @Test
    public void testUserAuthenticationWrongUsername() {
        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(userBodyWrongName)
                .when()
                .post("/users/authenticate")
                .then()
                .statusCode(403)
                .contentType(ContentType.TEXT)
                .body(equalTo("Invalid email/password combination"));

    }

    @Test
    public void testUserAuthenticationWrongPassword() {
        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(userBodyWrongPassword)
                .when()
                .post("/users/authenticate")
                .then()
                .statusCode(403)
                .contentType(ContentType.TEXT)
                .body(equalTo("Invalid email/password combination"));

    }


}