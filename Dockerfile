FROM eclipse-temurin:26-jre

WORKDIR /app

ARG JAR_FILE=target/lockstock-*.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
