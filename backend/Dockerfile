FROM maven:3.8.6-eclipse-temurin-17-alpine
WORKDIR /app
COPY . . 
RUN mvn install -DskipTests
ENTRYPOINT ["java","-jar" ,"target/backend-0.0.1-SNAPSHOT.jar", "./config.json"]
