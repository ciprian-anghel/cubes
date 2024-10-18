# Stage 1: Build the application
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY application_default_credentials.json .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the Docker image
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/cubes-0.0.1-SNAPSHOT.jar /app/app.jar
COPY --from=build /app/application_default_credentials.json /app/application_default_credentials.json
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]