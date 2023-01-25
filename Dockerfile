FROM openjdk:17-alpine

COPY app.jar app.jar

RUN adduser -D myuser
USER myuser

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]