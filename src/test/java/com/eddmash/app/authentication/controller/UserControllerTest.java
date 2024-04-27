package com.eddmash.app.authentication.controller;

import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.isNotNull;

import java.time.LocalDateTime;

import org.assertj.core.api.Assertions;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.eddmash.app.authentication.model.User;
import com.eddmash.app.authentication.repository.UserRepository;
import com.eddmash.app.authentication.service.JwtService;
import com.eddmash.app.shared.config.AppConfig;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class UserControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:15.2"));
    @LocalServerPort
    private Integer port;

    @Autowired
    AppConfig appConfig;
    @Autowired
    JwtService jwtService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;

    String token = "";
    private User user;

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
        user = null;
    }

    @BeforeEach
    void setUp() {
        appConfig.getAuth().setAuthUrl("http://localhost:"+port);
        RestAssured.port = port;
        user = userRepository.save(User.builder()
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
    void testUseGetsDeActivedWhenUserDeleteIsExecutedAndUserExists() {
        RestAssured.given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/users/" + user.getId())
                .then()
                .statusCode(200);
        User u = userRepository.findById(user.getId()).get();
        Assertions.assertThat(u).isNotNull()
                .hasFieldOrPropertyWithValue("active", 0);
    }

    @Test
    void testUserDetailsGetUpdated() {
        RestAssured.given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body("""
                    {
                        "name":"Jobs Bond",
                        "email":"jobs@apple.com",
                        "password":"test"
                      }   
                """)
                .when()
                .put("/users")
                .then()
                .statusCode(200)
                .body("status", Matchers.equalTo(200));
        User u = userRepository.findById(user.getId()).get();
        Assertions.assertThat(u).isNotNull()
                .hasFieldOrPropertyWithValue("name", "Jobs Bond");
    }

}
