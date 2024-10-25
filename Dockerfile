# Use a base image with Java and Gradle
FROM gradle:7.4.2-jdk17 as build

# Set working directory
WORKDIR /app

# Copy only the necessary files for dependency resolution
COPY settings.gradle.kts build.gradle.kts /app/
COPY gradle /app/gradle

# Fetch dependencies
RUN gradle build -i --stacktrace || return 0

# Copy the remaining project files and build the app
COPY . .
RUN gradle clean build -x test

# Step 2: Run stage
FROM openjdk:17-jdk-slim

# Set working directory for the JAR file
WORKDIR /app

# Copy the built JAR file from the Gradle image
COPY --from=build /app/build/libs/app-0.0.1.jar app.jar

# Expose the port (Render usually sets this automatically)
EXPOSE $PORT

# Run the application
CMD ["java", "-Dserver.port=${PORT}", "-jar", "app.jar"]
