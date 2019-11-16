FROM maven:3.6.2-jdk-8-slim

ENV ACTIVE_PROFILE=dev \
    DB_PASSWORD="" \
    DB_USER="" \
    DB_DDL="" \
    DB_DRIVER="" \
    DB_URL="" \
    DB_DIALECT=""

COPY . /app
WORKDIR /app

RUN mvn install -DskipTests && mv target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-Dspring.profiles.active=${ACTIVE_PROFILE}", "-jar","app.jar"]