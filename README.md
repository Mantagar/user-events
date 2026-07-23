# Summary

Expose endpoints using Spring MVC.

| Endpoint                       | Method | Function                                          |
|:-------------------------------|:------:|:--------------------------------------------------|
| /users                         |  GET   | fetch ids of all the users                        |
| /users                         |  POST  | create a user, publish 'user-created' Kafka event |
| /users/{userId}                |  GET   | fetch a user, publish 'user-browsed' Kafka event  |
| /users/{userId}/posts          |  GET   | fetch ids of all posts owned by the user          |
| /users/{userId}/posts          |  POST  | create a post                                     |
| /users/{userId}/posts/{postId} |  GET   | fetch a post                                      |

# Build

    ./gradlew build

# Run
    
    docker compose up
    java -jar target/UserEvents-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev 

# Debug

REST ENDPOINTS: http://localhost:8080/swagger-ui/index.html

PGADMIN: http://localhost:5050

