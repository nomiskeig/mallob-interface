# syntax=docker/dockerfile:1
FROM maven:3.8.6-eclipse-temurin-17-alpine 
WORKDIR /app
COPY . .
RUN mvn package 
ENTRYPOINT ["java", "-jar", "target/dbAccess-1.0-SNAPSHOT-jar-with-dependencies.jar"];
