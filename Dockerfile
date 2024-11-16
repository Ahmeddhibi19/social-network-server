# Use an official OpenJDK runtime as the base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/Social-App-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the application will run on
EXPOSE 8081

# Run the Spring Boot application
CMD ["java", "-jar", "app.jar"]
