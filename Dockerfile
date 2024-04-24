ARG SERVICE_NAME
ARG PATH_TO_SERVICE=./${SERVICE_NAME}

FROM gradle:8-jdk21 AS BUILD

ARG SERVICE_NAME
ARG PATH_TO_SERVICE

COPY ${PATH_TO_SERVICE} ./${SERVICE_NAME}
COPY ./common ./common

WORKDIR ./${SERVICE_NAME}

# Build the application
RUN gradle build --no-daemon

FROM openjdk:21-slim AS RUNNER

ARG SERVICE_NAME
ARG PATH_TO_SERVICE

WORKDIR /application

COPY --from=BUILD /home/gradle/${SERVICE_NAME}/build/libs/*.jar ./
RUN mv ./${SERVICE_NAME}-0.0.1-SNAPSHOT.jar ./application.jar

ENTRYPOINT ["java", "-jar", "./application.jar"]
