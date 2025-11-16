# --- ETAPA 1: Build ---
FROM maven:3.9.6-eclipse-temurin-17-focal AS builder

WORKDIR /app

# Copiamos TODO el proyecto (el contexto lo da docker-compose)
COPY . .

# Compilamos SOLO el módulo seguimiento
RUN mvn -f seguimiento/pom.xml clean package -DskipTests

# --- ETAPA 2: Run ---
FROM eclipse-temurin:17-jre-focal

WORKDIR /app

# Copiamos el JAR del micro de seguimiento
COPY --from=builder /app/seguimiento/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
