# Stage 1 
FROM eclipse-temurin:21-jdk-alpine as builder
WORKDIR /app
COPY . .
RUN apk update && apk add maven
RUN mvn install -DskipTests

# Stage 2
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
RUN rm -rf ./*
COPY --from=builder /app/restapi/target/restapi-1.0-SNAPSHOT.jar .
ENTRYPOINT ["java", "-jar", "restapi-1.0-SNAPSHOT.jar"]
