FROM gradle:8-jdk21 AS BUILD

COPY . .
# Build the application
RUN gradle build --no-daemon

FROM openjdk:21-slim AS RUNNER

WORKDIR /application

COPY --from=BUILD /home/gradle/build/libs/*.jar ./

ENTRYPOINT ["java", "-jar", "/application/authentication-service-0.0.1-SNAPSHOT.jar"]
