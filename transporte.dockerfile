# --- ETAPA 1: Compilación (Build) ---
FROM maven:3.9.6-eclipse-temurin-17-focal AS builder

WORKDIR /app

# Copiamos SOLO el módulo transporte para que build sea más rápido
COPY transporte/pom.xml transporte/pom.xml
RUN mvn -f transporte/pom.xml dependency:go-offline -B

COPY transporte/src transporte/src

RUN mvn clean package -f transporte/pom.xml -DskipTests

# --- ETAPA 2: Ejecución (Run) ---
FROM eclipse-temurin:17-jre-focal

WORKDIR /app

EXPOSE 8083

COPY --from=builder /app/transporte/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
