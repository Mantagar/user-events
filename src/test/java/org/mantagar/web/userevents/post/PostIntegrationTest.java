package org.mantagar.web.userevents.post;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mantagar.web.userevents.post.dto.CreatePostRequest;
import org.mantagar.web.userevents.user.UserRestUtils;
import org.mantagar.web.userevents.user.dto.CreateUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.web.servlet.client.RestTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@EmbeddedKafka(topics = {"user-created"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PostIntegrationTest {
    @LocalServerPort private int port;
    private RestTestClient restTestClient;

    @Autowired JdbcTemplate jdbcTemplate;

    @Autowired private EmbeddedKafkaBroker embeddedKafkaBroker;

    @AfterEach
    void cleanupDb() {
        jdbcTemplate.execute("DELETE FROM posts");
        jdbcTemplate.execute("ALTER SEQUENCE post_seq RESTART WITH 1");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("ALTER SEQUENCE user_seq RESTART WITH 1");
    }

    @BeforeAll
    public void init() {
        restTestClient =
                RestTestClient.bindToServer()
                        .baseUrl("http://localhost:%d".formatted(port))
                        .build();
    }

    @Test
    @DisplayName("Create a User, then create a Post for that User, and finally fetch the created Post")
    void createUser_createPost_getPost() {
        // STEP 1: create a User using REST API
        var createUserRequest = new CreateUserRequest("test", "test");
        Long userId = UserRestUtils.createUser(restTestClient, createUserRequest);

        // STEP 2: create a Post using REST API
        var createPostRequest = new CreatePostRequest(userId, "test");
        Long postId = PostRestUtils.createPost(restTestClient, createPostRequest);

        // STEP 3: fetch the created Post using REST API
        PostRestUtils.getPost(restTestClient, userId, postId);
    }

    @Test
    @DisplayName("Attempt to fetch posts for a non-existent user, expecting a 404 Not Found response")
    void getPosts_userNotFound() {
        restTestClient.get().uri("/posts/" + 0L).exchange().expectStatus().isNotFound();
    }
}
