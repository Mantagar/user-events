package org.mantagar.web.userevents.user.event;

import lombok.RequiredArgsConstructor;
import org.mantagar.web.userevents.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventPublisher {

    private static final Logger LOG = LoggerFactory.getLogger(UserEventPublisher.class);

    private final KafkaTemplate<String, User> kafkaTemplate;

    public void publishEvent(UserEventTopic topic, User user) {
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
