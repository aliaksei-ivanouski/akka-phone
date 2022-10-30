FROM gcr.io/distroless/java:11
WORKDIR /app
COPY build/libs/akka-phone-*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
