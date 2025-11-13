# --- ETAPA 1: Compilación (Build) ---
FROM maven:3.9.6-eclipse-temurin-17-focal AS builder

WORKDIR /app
COPY . .

# --- CAMBIO 1 ---
# Cambia 'operacion' por 'transporte'
RUN mvn clean package -f transporte/pom.xml -am -DskipTests

# --- ETAPA 2: Ejecución (Run) ---
FROM eclipse-temurin:17-jre-focal

WORKDIR /app

# (No necesitas EXPOSE, lo hacemos en docker-compose)

# --- CAMBIO 2 ---
# Cambia 'operacion' por 'transporte'
COPY --from=builder /app/transporte/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]