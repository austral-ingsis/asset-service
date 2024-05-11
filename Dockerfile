FROM gradle:8.7.0-jdk17-jammy AS build
COPY  . /app
WORKDIR /app
RUN ./gradlew bootJar

FROM eclipse-temurin:17-jre-jammy
EXPOSE 8080
RUN mkdir /app
COPY --from=build /app/build/libs/asset-service.jar /app/asset-service.jar
COPY newrelic/newrelic.jar /app/newrelic.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=production","-javaagent:/app/newrelic.jar","/app/asset-service.jar"]