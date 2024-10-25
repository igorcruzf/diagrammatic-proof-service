# Use a base image with Java
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the application JAR file to the container
COPY build/libs/app-0.0.1.jar app.jar

# Expose the port your app will run on
EXPOSE $PORT

# Set the command to run your app
CMD ["java", "-Dserver.port=${PORT}", "-jar", "app.jar"]