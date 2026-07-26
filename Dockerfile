# ---------- Stage 1: Build ----------
FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /build

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw

# Download dependencies first (better layer caching)
RUN ./mvnw dependency:go-offline

COPY src src

RUN ./mvnw clean package -DskipTests

# ---------- Stage 2: Run ----------
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=builder /build/target/hostelchatbot-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]