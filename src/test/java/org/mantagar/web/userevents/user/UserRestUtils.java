package org.mantagar.web.userevents.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import org.mantagar.web.userevents.user.dto.CreateUserRequest;
import org.mantagar.web.userevents.user.model.User;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

public final class UserRestUtils {

    private UserRestUtils() {}

    public static List<Long> getUserIds(RestTestClient restTestClient) {
        List<Long> ids =
                restTestClient
                        .get()
                        .uri("/users")
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(new ParameterizedTypeReference<List<Long>>() {})
                        .returnResult()
                        .getResponseBody();
        assertNotNull(ids);
        return ids;
    }

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

    public static User getUser(RestTestClient restTestClient, Long userId) {
        User u =
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
                                })
                        .returnResult()
                        .getResponseBody();
        assertNotNull(u);
        return u;
    }
}
