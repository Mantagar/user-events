package org.mantagar.web.userevents.publisher;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PublisherTopic {
    USER_BROWSED("user-browsed"),
    USER_CREATED("user-created");

    private final String name;
}
