# ===== 1) Build con Maven =====
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

COPY api-gateway/pom.xml .
RUN mvn dependency:go-offline -B

COPY api-gateway/src ./src

RUN mvn clean package -DskipTests -B

# ===== 2) Runtime =====
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
