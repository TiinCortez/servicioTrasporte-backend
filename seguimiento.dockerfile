# --- ETAPA 1: Compilación (Build) ---
FROM maven:3.9.6-eclipse-temurin-17-focal AS builder

WORKDIR /app

COPY seguimiento/pom.xml seguimiento/pom.xml
RUN mvn -f seguimiento/pom.xml dependency:go-offline -B

COPY seguimiento/src seguimiento/src

RUN mvn clean package -f seguimiento/pom.xml -DskipTests

# --- ETAPA 2: Ejecución (Run) ---
FROM eclipse-temurin:17-jre-focal

WORKDIR /app

EXPOSE 8085

COPY --from=builder /app/seguimiento/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
