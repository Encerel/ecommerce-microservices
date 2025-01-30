FROM eclipse-temurin:21-jdk-alpine

RUN apk add --no-cache curl

WORKDIR /app

COPY target/discovery-service-0.0.1-SNAPSHOT.jar /app/discovery-service.jar

ENTRYPOINT ["java", "-jar", "/app/discovery-service.jar"]