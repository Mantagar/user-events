package org.mantagar.web.userevents.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.mantagar.web.userevents.publisher.PublisherTopic;
import org.mantagar.web.userevents.user.dto.CreateUserRequest;
import org.mantagar.web.userevents.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;

/**
 * DO NOT CALL SELECT TESTS - they need to be called in a specific order as some rely on the state
 * created by the others.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@EmbeddedKafka(topics = {"user-created", "user-browsed"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserIntegrationTest {
    // TODO rename methods to follow BDD, use @DisplayName for descriptions
    @LocalServerPort private int port;

    @Autowired private TestRestTemplate restTemplate;
    @Autowired private EmbeddedKafkaBroker embeddedKafkaBroker;
    private Consumer<String, User> kafkaConsumer;

    @BeforeAll
    public void initKafkaConsumer() {
        var consumerProperties =
                KafkaTestUtils.consumerProps(embeddedKafkaBroker, "test-group", false);
        consumerProperties.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);
        consumerProperties.put(JacksonJsonDeserializer.VALUE_DEFAULT_TYPE, User.class);
        var consumerFactory = new DefaultKafkaConsumerFactory<String, User>(consumerProperties);
        kafkaConsumer = consumerFactory.createConsumer();
    }

    @Test
    @Order(1)
    void shouldPublishUserCreated_whenPOST() {
        var url = "http://localhost:%d/users".formatted(port);
        var createUserRequest = new CreateUserRequest("test", "test");

        var responseEntity = restTemplate.postForEntity(url, createUserRequest, User.class);

        // verify that the db was reached and the entry was created
        assertEquals(201, responseEntity.getStatusCode().value());
        var rsUser = responseEntity.getBody();
        assertNotNull(rsUser);
        assertEquals(1, rsUser.getId());
        assertEquals("test", rsUser.getName());
        assertEquals("test", rsUser.getSurname());

        var record = consumeKafkaEvent(PublisherTopic.USER_CREATED);

        // verify that kafka event was published
        assertNotNull(record.value());
        assertEquals(rsUser.getId(), record.value().getId());
        assertEquals(rsUser.getName(), record.value().getName());
        assertEquals(rsUser.getSurname(), record.value().getSurname());
    }

    @Test
    @Order(2)
    void shouldPublishUserBrowsed_whenGET() {
        var url = "http://localhost:%d/users/1".formatted(port);

        var responseEntity = restTemplate.getForEntity(url, User.class);

        // verify that the db was reached and the entry was returned
        assertEquals(200, responseEntity.getStatusCode().value());
        var rsUser = responseEntity.getBody();
        assertNotNull(rsUser);
        assertEquals(1, rsUser.getId());
        assertEquals("test", rsUser.getName());
        assertEquals("test", rsUser.getSurname());

        var record = consumeKafkaEvent(PublisherTopic.USER_BROWSED);

        // verify that kafka event was published
        assertNotNull(record.value());
        assertEquals(rsUser.getId(), record.value().getId());
        assertEquals(rsUser.getName(), record.value().getName());
        assertEquals(rsUser.getSurname(), record.value().getSurname());
    }

    private ConsumerRecord<String, User> consumeKafkaEvent(PublisherTopic publisherTopic) {
        var topic = publisherTopic.getName();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(kafkaConsumer, topic);
        return KafkaTestUtils.getSingleRecord(kafkaConsumer, topic);
    }
}
