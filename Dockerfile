# Stage 1: Build the JAR file
FROM maven:3-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
# Download dependencies to cache them
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Production execution environment
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

