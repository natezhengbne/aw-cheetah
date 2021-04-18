FROM openjdk:11.0.10-jdk

ARG VERSION=0.0.1-SNAPSHOT

COPY ./build/libs/aw-cheetah-${VERSION}.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /app.jar" ]