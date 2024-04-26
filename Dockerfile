FROM maven:3.8.4-openjdk-17-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package


FROM openjdk:17-jdk-slim
COPY --from=build /home/app/target/blps-1.0.jar /usr/local/lib/blps.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/blps.jar"]