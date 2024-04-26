FROM openjdk:17-jdk-slim
COPY ./target/blps-1.0.jar /usr/local/lib/blps.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/blps.jar"]