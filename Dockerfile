FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM tomcat:10.1-jdk21-temurin
WORKDIR /usr/local/tomcat

RUN rm -rf webapps/*

COPY --from=build /app/target/currency-exchange.war webapps/currency-exchange.war

COPY currency.db /usr/local/tomcat/currency.db

EXPOSE 8080
CMD ["catalina.sh", "run"]