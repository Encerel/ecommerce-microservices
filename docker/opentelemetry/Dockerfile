FROM alpine:latest

WORKDIR /otel

RUN apk add --no-cache curl \
    && curl -L -o opentelemetry-javaagent.jar \
       https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v2.14.0/opentelemetry-javaagent.jar

CMD ["sleep", "infinity"]