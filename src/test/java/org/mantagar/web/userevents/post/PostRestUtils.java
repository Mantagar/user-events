package org.mantagar.web.userevents.post;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import org.mantagar.web.userevents.post.dto.CreatePostRequest;
import org.mantagar.web.userevents.post.dto.PostNoUserResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

public final class PostRestUtils {

    private PostRestUtils() {}

    public static List<Long> getPostIds(RestTestClient restTestClient, Long userId) {
        List<Long> ids =
                restTestClient
                        .get()
                        .uri("/users/%d/posts".formatted(userId))
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(new ParameterizedTypeReference<List<Long>>() {})
                        .returnResult()
                        .getResponseBody();
        assertNotNull(ids);
        return ids;
    }

    public static Long createPost(
            RestTestClient restTestClient, CreatePostRequest createPostRequest) {
        PostNoUserResponse p =
                restTestClient
                        .post()
                        .uri("/users/%d/posts".formatted(createPostRequest.userId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(createPostRequest)
                        .exchange()
                        .expectStatus()
                        .isCreated()
                        .expectBody(PostNoUserResponse.class)
                        .value(
                                post -> {
                                    assertNotNull(post);
                                    assertEquals(createPostRequest.content(), post.content());
                                })
                        .returnResult()
                        .getResponseBody();
        assertNotNull(p);
        return p.id();
    }

    public static PostNoUserResponse getPost(
            RestTestClient restTestClient, Long userId, Long postId) {
        PostNoUserResponse p =
                restTestClient
                        .get()
                        .uri("/users/%d/posts/%d".formatted(userId, postId))
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(PostNoUserResponse.class)
                        .value(
                                post -> {
                                    assertNotNull(post);
                                    assertEquals(postId, post.id());
                                })
                        .returnResult()
                        .getResponseBody();
        assertNotNull(p);
        return p;
    }
}
