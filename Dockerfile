# Fase de construcción
FROM maven:3.8.5-openjdk-17 AS build

# Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar los archivos pom.xml y descargar las dependencias del proyecto
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiar el resto del código fuente
COPY src ./src

# Compilar el proyecto y empaquetarlo en un archivo JAR
RUN mvn clean install -DskipTests

# Fase de ejecución
FROM openjdk:17-jdk-alpine

# Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el archivo JAR construido desde la fase anterior
COPY --from=build /app/target/*.jar /app/app.jar

# Exponer el puerto que utilizará la aplicación
EXPOSE 8080

# Ejecutar la aplicación
ENTRYPOINT ["java","-jar","/app/app.jar"]
