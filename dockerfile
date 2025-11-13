# --- ETAPA 1: Compilación (Build) ---
# Usamos una imagen de Maven con JDK 17 (como pide tu pom.xml)
FROM maven:3.9.6-eclipse-temurin-17-focal AS builder

WORKDIR /app

# Copiamos TODO el código fuente
COPY . .

# Ejecutamos el comando de Maven apuntando directamente al POM de 'operacion'
# El flag -f le dice "usa este archivo pom"
RUN mvn clean package -f operacion/pom.xml -am -DskipTests

# --- ETAPA 2: Ejecución (Run) ---
# Usamos una imagen de JRE 17 (Java Runtime Environment) optimizada
FROM eclipse-temurin:17-jre-focal

WORKDIR /app

# Exponemos el puerto 8082 (configurado en `operacion/src/main/resources/application.properties`)
EXPOSE 8082

# Copiamos el .jar que se generó en la etapa 'builder'
COPY --from=builder /app/operacion/target/*.jar app.jar

# Comando para arrancar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]