FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
ARG JAR_FILE
COPY target/battleship_online-0.0.1-SNAPSHOT.jar battleship_online.jar
ENTRYPOINT ["java","-jar","/battleship_online.jar"]