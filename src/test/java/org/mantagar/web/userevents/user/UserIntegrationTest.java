package org.mantagar.web.userevents.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mantagar.web.userevents.user.dto.CreateUserRequest;
import org.mantagar.web.userevents.user.event.UserEventTopic;
import org.mantagar.web.userevents.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.web.servlet.client.RestTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"user-created", "user-browsed"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserIntegrationTest {
    @LocalServerPort private int port;
    private RestTestClient restTestClient;

    @Autowired JdbcTemplate jdbcTemplate;

    @Autowired private EmbeddedKafkaBroker embeddedKafkaBroker;
    private Consumer<String, User> kafkaConsumer;

    @AfterEach
    void cleanupDb() {
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("ALTER SEQUENCE user_seq RESTART WITH 1");
    }

    @BeforeAll
    public void init() {
        restTestClient =
                RestTestClient.bindToServer()
                        .baseUrl("http://localhost:%d".formatted(port))
                        .build();

        var consumerProperties =
                KafkaTestUtils.consumerProps(embeddedKafkaBroker, "test-group", false);
        consumerProperties.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);
        consumerProperties.put(JacksonJsonDeserializer.VALUE_DEFAULT_TYPE, User.class);
        var consumerFactory = new DefaultKafkaConsumerFactory<String, User>(consumerProperties);
        kafkaConsumer = consumerFactory.createConsumer();
    }

    @Test
    @DisplayName(
            "Create a user and then fetch it, verifying that the appropriate events are published to Kafka")
    void createUser_getUser() {
        // STEP 1: create a User using REST API
        var createUserRequest = new CreateUserRequest("test", "test");
        Long userId = UserRestUtils.createUser(restTestClient, createUserRequest);

        // STEP 2: verify "user-created" event was published
        consumeKafkaEvent(UserEventTopic.USER_CREATED);

        // STEP 3: fetch the created User using REST API
        UserRestUtils.getUser(restTestClient, userId);

        // STEP 4: verify "user-browsed" event was published
        consumeKafkaEvent(UserEventTopic.USER_BROWSED);
    }

    @Test
    @DisplayName("Fetch a user that does not exist, expecting a 404 Not Found response")
    void getUser_userNotFound() {
        restTestClient.get().uri("/users/1").exchange().expectStatus().isNotFound();
    }

    @Test
    @DisplayName("Attempt to create a user that already exists, expecting a 409 Conflict response")
    void createUser_userAlreadyExists() {
        // STEP 1: create a User using REST API
        var createUserRequest = new CreateUserRequest("test", "test");
        UserRestUtils.createUser(restTestClient, createUserRequest);

        // STEP 2: create the same User again using REST API and expect 409 Conflict
        restTestClient
                .post()
                .uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(createUserRequest)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.CONFLICT);
    }

    private void consumeKafkaEvent(UserEventTopic userEventTopic) {
        var topic = userEventTopic.getName();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(kafkaConsumer, topic);
        var record = KafkaTestUtils.getSingleRecord(kafkaConsumer, topic);

        assertNotNull(record.value());
        assertEquals(1, record.value().getId());
        assertEquals("test", record.value().getName());
        assertEquals("test", record.value().getSurname());
    }
}
