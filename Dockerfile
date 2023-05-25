FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
LABEL authors="Luke Prananta"
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]