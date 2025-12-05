# Dockerfile para Spring Boot Backend
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar archivos de configuración de Maven
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Compilar la aplicación
RUN mvn clean package -DskipTests

# Imagen final
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Instalar wget para healthcheck
RUN apk add --no-cache wget

# Crear directorio para configuración externa
RUN mkdir -p /app/config

# Copiar el JAR compilado
COPY --from=build /app/target/*.jar app.jar

# Copiar archivos de configuración de producción (opcional, ya están en el JAR pero útil para override)
COPY --from=build /app/src/main/resources/application-prod.yml /app/config/application-prod.yml

# Exponer puerto
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:9000/actuator/health || exit 1

# Variables de entorno para producción
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SPRING_PROFILES_ACTIVE=prod
ENV SPRING_CONFIG_ADDITIONAL_LOCATION=file:/app/config/
ENV PORT=9000

# Ejecutar la aplicación con perfil de producción
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --spring.profiles.active=prod --spring.config.additional-location=file:/app/config/"]

