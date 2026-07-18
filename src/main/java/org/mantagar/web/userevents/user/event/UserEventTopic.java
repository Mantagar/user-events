package org.mantagar.web.userevents.user.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserEventTopic {
    USER_BROWSED("user-browsed"),
    USER_CREATED("user-created");

    private final String name;
}
