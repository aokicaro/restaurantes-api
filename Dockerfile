# Build (Java 21)
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy the deps first
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# Copy the code and build
COPY src ./src
RUN mvn -q -DskipTests package

# Runtime (Java 21)
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]
