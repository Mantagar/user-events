package org.mantagar.web.userevents.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.mantagar.web.userevents.user.dto.CreateUserRequest;
import org.mantagar.web.userevents.user.model.User;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

public final class UserRestUtils {

    private UserRestUtils() {}

    public static Long createUser(
            RestTestClient restTestClient, CreateUserRequest createUserRequest) {
        User u =
                restTestClient
                        .post()
                        .uri("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(createUserRequest)
                        .exchange()
                        .expectStatus()
                        .isCreated()
                        .expectBody(User.class)
                        .value(
                                user -> {
                                    assertNotNull(user);
                                    assertEquals(createUserRequest.name(), user.getName());
                                    assertEquals(createUserRequest.surname(), user.getSurname());
                                })
                        .returnResult()
                        .getResponseBody();
        assertNotNull(u);
        return u.getId();
    }

    public static void getUser(RestTestClient restTestClient, Long userId) {
        restTestClient
                .get()
                .uri("/users/%d".formatted(userId))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(User.class)
                .value(
                        user -> {
                            assertNotNull(user);
                            assertEquals(userId, user.getId());
                        });
    }
}
