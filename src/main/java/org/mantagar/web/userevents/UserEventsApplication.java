package org.mantagar.web.userevents;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
@RequiredArgsConstructor
public class UserEventsApplication {

    private final Environment environment;

    @PostConstruct
    public void validateSingleProfile() {
        String[] activeProfiles = environment.getActiveProfiles();
        if (activeProfiles.length > 1) {
            throw new IllegalStateException(
                    "Only one profile can be active. Currently active: "
                            + String.join(", ", activeProfiles));
        }
    }

    static void main(String[] args) {
        SpringApplication.run(UserEventsApplication.class, args);
    }
}
