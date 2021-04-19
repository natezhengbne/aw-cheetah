FROM openjdk:11 AS build
WORKDIR /workspace/app
COPY . /workspace/app

RUN ./gradlew clean build

FROM openjdk:11
VOLUME /tmp

ARG VERSION=0.0.1-SNAPSHOT
ARG AWCHEET_DIR=/workspace/app/build/libs
COPY --from=build ${AWCHEET_DIR}/aw-cheetah-${VERSION}.jar app.jar

ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /app.jar" ]