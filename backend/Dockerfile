FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jre

WORKDIR /app
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/career-pilot-backend-0.0.1-SNAPSHOT.jar app.jar

ENV SERVER_PORT=8081
ENV SQL_INIT_MODE=never

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
