# --- ETAPA 1: Compilación (Build) ---
# Usamos una imagen de Maven con JDK 17
FROM maven:3.9.6-eclipse-temurin-17-focal AS builder

WORKDIR /app

# Copiamos TODO el código fuente
COPY . .

# Ejecutamos el comando de Maven para construir SOLO el módulo 'tarifa'
# (¡Asumimos que crearás una carpeta 'tarifa' para el código Java!)
RUN mvn clean package -f tarifa/pom.xml -am -DskipTests

# --- ETAPA 2: Ejecución (Run) ---
# Usamos una imagen de JRE 17
FROM eclipse-temurin:17-jre-focal

WORKDIR /app

# Copiamos el .jar que se generó en la etapa 'builder'
COPY --from=builder /app/tarifa/target/*.jar app.jar

# Comando para arrancar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]