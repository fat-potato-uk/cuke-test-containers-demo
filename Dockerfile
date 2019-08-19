FROM maven:3.6.1-jdk-12 AS build

# Resolve the dependencies as an independent layer first
COPY pom.xml /usr/src/app/pom.xml
WORKDIR /usr/src/app
RUN mvn dependency:go-offline

# Copy and build
COPY src /usr/src/app/src
RUN mvn clean package

# Move artifact into slim container
FROM openjdk:12-alpine
COPY --from=build /usr/src/app/target/rest-demo-2-1.0-SNAPSHOT.jar /usr/app/rest-demo-2-1.0-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/app/rest-demo-2-1.0-SNAPSHOT.jar"]