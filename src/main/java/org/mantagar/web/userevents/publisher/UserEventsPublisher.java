package org.mantagar.web.userevents.publisher;

import lombok.RequiredArgsConstructor;
import org.mantagar.web.userevents.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventsPublisher {

    private static final Logger LOG = LoggerFactory.getLogger(UserEventsPublisher.class);

    private final KafkaTemplate<String, User> kafkaTemplate;

    public void publishEvent(PublisherTopic topic, User user) {
        kafkaTemplate
                .send(topic.getName(), user)
                .whenComplete(
                        (res, e) -> {
                            if (e != null) {
                                LOG.error("Failed to publish: {}", user, e);
                            }
                        });
    }
}
