# 1. Use Java 21 runtime
FROM eclipse-temurin:21-jre

# 2. Set working directory inside container
WORKDIR /app

# 3. Copy jar file into container
COPY target/*.jar app.jar

# 4. Expose application port
EXPOSE 8080

# 5. Run the application
ENTRYPOINT ["java","-jar","app.jar"]
