package com.eddmash.app.authentication.controller;

import java.time.LocalDateTime;

import org.assertj.core.api.Assertions;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.eddmash.app.authentication.model.User;
import com.eddmash.app.authentication.repository.UserRepository;
import com.eddmash.app.authentication.service.JwtService;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class AuthenticationControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:15.2"));
    @LocalServerPort
    private Integer port;

    @Autowired
    JwtService jwtService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;

    String token = "";

    @BeforeAll
    static void beforeAll() {
        postgresContainer.start();
    }

    @AfterAll
    static void afterAll() {
        postgresContainer.stop();
    }

    @AfterEach
    void clean() {
        userRepository.deleteAll();
        token = "";
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        User user = userRepository.save(User.builder()
                .active(1)
                .name("Jobs")
                .email("jobs@apple.com")
                .password(passwordEncoder.encode("test"))
                .dateCreated(LocalDateTime.now())
                .dateModified(LocalDateTime.now())
                .insertedBy(1L)
                .updatedBy(1L)
                .build());
        token = jwtService.generateToken(user);
    }

    @Test
    void testUserSignedUpWhenUserDoesNotExist() {
        RestAssured.given().contentType(ContentType.JSON)
                .body("""
                        {
                            "name":"edd",
                            "email":"edd@gmail.com",
                            "password":"edd"
                          }
                            """)
                .when()
                .post("/auth/signup")
                .then()
                .statusCode(200)
                .body("status", Matchers.equalTo(200))
                .body("data.id", Matchers.notNullValue())
                .body("data.dateCreated", Matchers.notNullValue())
                .body("data.name", Matchers.equalTo("edd"));

        User u = userRepository.findByEmail("edd@gmail.com").get();
        Assertions.assertThat(u).isNotNull()
                .hasFieldOrPropertyWithValue("name", "edd");
    }

    @Test
    void testUserSignedUpFailsWhenUserAlreadyExist() {
        RestAssured.given().contentType(ContentType.JSON)
                .body("""
                        {
                            "name":"edd",
                            "email":"jobs@apple.com",
                            "password":"edd"
                          }
                            """)
                .when()
                .post("/auth/signup")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("status", Matchers.equalTo(HttpStatus.BAD_REQUEST.value()))
                .body("message", Matchers.containsStringIgnoringCase("Email already registerd"));
    }

    @Test
    void testWhenCorrectCredentialsAreProvidedTokenIsCreated() {
        RestAssured.given().contentType(ContentType.JSON)
                .body("""
                        {
                            "email":"jobs@apple.com",
                            "password":"test"
                          }
                            """)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("accessToken",
                        Matchers.matchesPattern("^([a-zA-Z0-9_=]+)\\.([a-zA-Z0-9_=]+)\\.([a-zA-Z0-9_\\-\\+\\/=]*)"));
    }

    @Test
    void testWhenCorrectCredentialsAreProvidedTokenIsNotCreated() {
        RestAssured.given().contentType(ContentType.JSON)
                .body("""
                        {
                            "email":"jobs@gmail.com",
                            "password":"long"
                          }
                            """)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("status", Matchers.equalTo(HttpStatus.BAD_REQUEST.value()))
                .body("message", Matchers.containsStringIgnoringCase("invalid"))
                .body("data", Matchers.nullValue());
    }

    @Test
    void testWhenValidTokenIsProvidedIntrospectionReturnUserDetails() {
        RestAssured.given().contentType(ContentType.JSON)
                .body("""
                        {
                            "accessToken": "token"
                          }
                                """.replace("token", token))
                .when()
                .post("/auth/tokenInfo")
                .then()
                .statusCode(200)
                .body("id", Matchers.notNullValue())
                .body("dateCreated", Matchers.notNullValue())
                .body("name", Matchers.containsStringIgnoringCase("jobs"));
    }

    @Test
    void testWhenInValidTokenIsProvidedNoUserDetailsAreReturned() {
        RestAssured.given().contentType(ContentType.JSON)
                .body("""
                        {
                            "accessToken": "token"
                          }
                                """.replace("token", "token"))
                .when()
                .post("/auth/tokenInfo")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("status", Matchers.equalTo(HttpStatus.BAD_REQUEST.value()))
                .body("message", Matchers.containsStringIgnoringCase("invalid"));
    }
}
