FROM openjdk:17-alpine

COPY ./app.jar /application.jar
RUN ls

RUN adduser -D myuser
USER myuser

EXPOSE 8080

ENTRYPOINT ["java","-jar","/application.jar"]