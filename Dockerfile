FROM openjdk:17-alpine

ARG JAR_FILE_PATH
COPY ./artifacts/app.jar /application.jar

RUN adduser -D myuser
USER myuser

EXPOSE 8080

ENTRYPOINT ["java","-jar","/application.jar"]