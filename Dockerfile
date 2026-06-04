
FROM maven:3.9.6-eclipse-temurin-21-jammy AS builder

WORKDIR /build

COPY pom.xml .

COPY src ./src

RUN mvn clean package -DskipITs

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=builder /build/target/konta_transakcje_api-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]