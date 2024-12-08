# Image Dockerfile is your notebook
# Install Java
FROM eclipse-temurin:23-jdk

LABEL maintainer="joyie"

## How to build the application

# Create a directory /app and change directory into /app
# Outside of /app
WORKDIR /app

# Inside /app directory
# Copy files over src dst
COPY ./mvnw .
COPY .mvn .mvn

COPY pom.xml .
COPY src src

# Build the application
RUN chmod a+x ./mvnw && ./mvnw package -Dmaven.test.skip=true 

# If build is successful then the jar is in
# ./target/day12-0.0.1-SNAPSHOT.jar

## How to run the application
#ENV SERVER_PORT=8080 
# for Railway
ENV PORT=8080
ENV SPRING_DATA_REDIS_HOST=localhost SPRING_DATA_REDIS_PORT=6379
ENV SPRING_DATA_REDIS_USERNAME="" SPRING_DATA_REDIS_PASSWORD=""


# what port does the application need
#EXPOSE ${SERVER_PORT}
EXPOSE ${PORT}

SHELL [ "/bin/sh", "-c" ]

# run the application
ENTRYPOINT SERVER_PORT=${PORT} java -jar target/vttp5_ssf_day16w_practice-0.0.1-SNAPSHOT.jar