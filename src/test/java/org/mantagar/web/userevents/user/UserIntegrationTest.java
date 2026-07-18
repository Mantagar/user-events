package org.mantagar.web.userevents.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mantagar.web.userevents.publisher.PublisherTopic;
import org.mantagar.web.userevents.user.dto.CreateUserRequest;
import org.mantagar.web.userevents.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.web.servlet.client.RestTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@EmbeddedKafka(topics = {"user-created", "user-browsed"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserIntegrationTest {
    // TODO rename methods to follow BDD, use @DisplayName for descriptions
    @LocalServerPort private int port;
    @Autowired private UserController userController;
    private RestTestClient restTestClient;

    @Autowired private EmbeddedKafkaBroker embeddedKafkaBroker;
    private Consumer<String, User> kafkaConsumer;

    @BeforeAll
    public void init() {
        restTestClient =
                RestTestClient.bindToController(userController)
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
    void userCreationAndBrowsingWorkflow() {
        // STEP 1: create a User using REST API
        var createUserRequest = new CreateUserRequest("test", "test");
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
                            assertEquals(1, user.getId());
                            assertEquals("test", user.getName());
                            assertEquals("test", user.getSurname());
                        });

        // STEP 2: verify "user-created" event was published
        var record = consumeKafkaEvent(PublisherTopic.USER_CREATED);
        assertNotNull(record.value());
        assertEquals(1, record.value().getId());
        assertEquals("test", record.value().getName());
        assertEquals("test", record.value().getSurname());

        // STEP 3: fetch the created User using REST API
        restTestClient
                .get()
                .uri("/users")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(List.class)
                .value(
                        userIds -> {
                            assertNotNull(userIds);
                            assertEquals(1, userIds.size());
                            assertEquals(1, userIds.getFirst());
                        });
        ;
        restTestClient
                .get()
                .uri("/users/1")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(User.class)
                .value(
                        user -> {
                            assertNotNull(user);
                            assertEquals(1, user.getId());
                            assertEquals("test", user.getName());
                            assertEquals("test", user.getSurname());
                        });

        // STEP 4: verify "user-browsed" event was published
        record = consumeKafkaEvent(PublisherTopic.USER_BROWSED);
        assertNotNull(record.value());
        assertEquals(1, record.value().getId());
        assertEquals("test", record.value().getName());
        assertEquals("test", record.value().getSurname());
    }

    private ConsumerRecord<String, User> consumeKafkaEvent(PublisherTopic publisherTopic) {
        var topic = publisherTopic.getName();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(kafkaConsumer, topic);
        return KafkaTestUtils.getSingleRecord(kafkaConsumer, topic);
    }
}
