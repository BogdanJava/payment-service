FROM openjdk:17-alpine

RUN ls
COPY ./app.jar /application.jar

RUN adduser -D myuser
USER myuser

EXPOSE 8080

ENTRYPOINT ["java","-jar","/application.jar"]