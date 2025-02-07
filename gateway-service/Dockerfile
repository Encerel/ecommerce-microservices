FROM eclipse-temurin:21-jdk-alpine

RUN apk add --no-cache curl

WORKDIR /app

COPY target/gateway-service-0.0.1-SNAPSHOT.jar /app/gateway-service.jar

ENTRYPOINT ["java", "-jar", "/app/gateway-service.jar"]