FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
ARG JAR_FILE
COPY target/battleship_online-1.0.4-SNAPSHOT.jar battleship_online.jar
ENTRYPOINT ["java","-jar","/battleship_online.jar"]