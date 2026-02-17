FROM amazoncorretto:17-alpine

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java","-Xms256m","-Xmx512m","-jar","/app.jar"]