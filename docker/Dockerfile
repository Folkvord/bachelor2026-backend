# Bygg staget
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /usr/app
COPY src .
COPY pom.xml .
RUN mvn clean package -DskipTests

# Kjør staget
FROM eclipse-temurin:21
WORKDIR /usr/app
COPY --from=build /usr/app/target/bachelor26-0.0.1-SNAPSHOT.jar main.jar
EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "main.jar" ]