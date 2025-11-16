# --- ETAPA 1: Compilación (Build) ---
FROM maven:3.9.6-eclipse-temurin-17-focal AS builder

WORKDIR /app

COPY tarifa/pom.xml tarifa/pom.xml
RUN mvn -f tarifa/pom.xml dependency:go-offline -B

COPY tarifa/src tarifa/src

RUN mvn clean package -f tarifa/pom.xml -DskipTests

# --- ETAPA 2: Ejecución (Run) ---
FROM eclipse-temurin:17-jre-focal

WORKDIR /app

EXPOSE 8084

COPY --from=builder /app/tarifa/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
