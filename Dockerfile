## Multi-stage Dockerfile for building and running the Spring Boot application
## Build stage: use Maven with Java 21 to compile and package
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /home/app

# copy maven files first to cache dependencies
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN mvn dependency:go-offline -B

# copy source and build
COPY src ./src
RUN mvn -B -DskipTests package

## Runtime stage: use a smaller JRE image
FROM eclipse-temurin:21-jre
WORKDIR /app

# copy jar from builder
COPY --from=builder /home/app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/app.jar"]
