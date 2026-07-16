# Etapa 1: compilar
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Se Copia solo el pom primero para aprovechar la caché de capas de Docker.
# Si el código cambia pero las dependencias no, esta capa no se reconstruye.
COPY pom.xml .
RUN mvn dependency:go-offline -q

COPY src ./src
RUN mvn clean package -DskipTests -q

# Etapa 2: ejecutar 
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 5623
ENTRYPOINT ["java", "-jar", "app.jar"]
