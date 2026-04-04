FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /electionBackend

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre

WORKDIR /electionBackend

COPY --from=builder /electionBackend/target/*.jar app.jar

EXPOSE 8081



ENTRYPOINT ["java", "-jar", "app.jar"]
