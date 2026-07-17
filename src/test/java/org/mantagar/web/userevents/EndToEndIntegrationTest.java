package org.mantagar.web.userevents;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.mantagar.web.userevents.dto.UserNameSurnameDTO;
import org.mantagar.web.userevents.entity.User;
import org.mantagar.web.userevents.publisher.PublisherTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
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
public class EndToEndIntegrationTest {

    @LocalServerPort private int port;

    @Autowired private TestRestTemplate restTemplate;
    @Autowired private EmbeddedKafkaBroker embeddedKafkaBroker;
    private Consumer<String, User> kafkaConsumer;

    @BeforeAll
    public void initKafkaConsumer() {
        Map<java.lang.String, Object> consumerProperties =
                KafkaTestUtils.consumerProps(embeddedKafkaBroker, "test-group", false);
        consumerProperties.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);
        consumerProperties.put(
                JacksonJsonDeserializer.VALUE_DEFAULT_TYPE,
                org.mantagar.web.userevents.entity.User.class);
        DefaultKafkaConsumerFactory<String, User> consumerFactory =
                new DefaultKafkaConsumerFactory<>(consumerProperties);
        kafkaConsumer = consumerFactory.createConsumer();
    }

    @Test
    @Order(1)
    void shouldPublishUserCreated_whenPOST() {
        String url = "http://localhost:%d/users".formatted(port);
        UserNameSurnameDTO rqUserNameSurnameDTO = new UserNameSurnameDTO("test", "test");

        ResponseEntity<User> response =
                restTemplate.postForEntity(url, rqUserNameSurnameDTO, User.class);

        // verify that the db was reached and the entry was created
        assertEquals(201, response.getStatusCode().value());
        User rsUser = response.getBody();
        assertNotNull(rsUser);
        assertEquals(1, rsUser.getId());
        assertEquals("test", rsUser.getName());
        assertEquals("test", rsUser.getSurname());

        ConsumerRecord<String, User> record = consumeKafkaEvent(PublisherTopic.USER_CREATED);

        // verify that kafka event was published
        assertNotNull(record.value());
        assertEquals(rsUser.getId(), record.value().getId());
        assertEquals(rsUser.getName(), record.value().getName());
        assertEquals(rsUser.getSurname(), record.value().getSurname());
    }

    @Test
    @Order(2)
    void shouldPublishUserBrowsed_whenGET() {
        String url = "http://localhost:%d/users/1".formatted(port);

        ResponseEntity<User> response = restTemplate.getForEntity(url, User.class);

        // verify that the db was reached and the entry was returned
        assertEquals(200, response.getStatusCode().value());
        User rsUser = response.getBody();
        assertNotNull(rsUser);
        assertEquals(1, rsUser.getId());
        assertEquals("test", rsUser.getName());
        assertEquals("test", rsUser.getSurname());

        ConsumerRecord<String, User> record = consumeKafkaEvent(PublisherTopic.USER_BROWSED);

        // verify that kafka event was published
        assertNotNull(record.value());
        assertEquals(rsUser.getId(), record.value().getId());
        assertEquals(rsUser.getName(), record.value().getName());
        assertEquals(rsUser.getSurname(), record.value().getSurname());
    }

    private ConsumerRecord<String, User> consumeKafkaEvent(PublisherTopic publisherTopic) {
        String topic = publisherTopic.getName();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(kafkaConsumer, topic);
        return KafkaTestUtils.getSingleRecord(kafkaConsumer, topic);
    }
}
