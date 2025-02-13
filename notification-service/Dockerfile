FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY target/notification-service-0.0.1-SNAPSHOT.jar /app/notification-service.jar

ENTRYPOINT ["java", "-jar", "/app/notification-service.jar"]