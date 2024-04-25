FROM docker.io/maven:4.0.0-openjdk-21 AS build
COPY . /home/src
WORKDIR /home/src
RUN mvn clean package -DskipTests

FROM openjdk:21
EXPOSE 8080

RUN mkdir /app
COPY --from=build /home/src/target/*.jar /app/project.jar

ENTRYPOINT ["java", "-jar", "/app/project.jar"]