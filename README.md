# Summary

Expose endpoints using Spring MVC.

POST endpoint creates a user entry in Postgres and publishes it to 'user-created' Kafka topic.

GET endpoint returns a user entry from Postgres and publishes it to 'user-browsed' Kafka topic.

# Build

    ./mvnw verify

# Run
    
    docker compose up
    java -jar target/UserEvents-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev 

# Debug

REST ENDPOINTS: http://localhost:8080/swagger-ui/index.html

PGADMIN: http://localhost:5050

