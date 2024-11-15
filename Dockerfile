# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Add the Maven package manager to build the Spring Boot application
RUN apt-get update && apt-get install -y maven

# Copy the Maven project files into the container
COPY . .

# Build the application using Maven
RUN mvn clean package -DskipTests

# Expose the port your Spring Boot application runs on
EXPOSE 8081

# Run the Spring Boot application
CMD ["java", "-jar", "target/Social-App-0.0.1-SNAPSHOT.jar"]
