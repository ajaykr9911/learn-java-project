# ===============================
# 1️⃣ BUILD STAGE
# ===============================
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Copy Maven files first (better cache)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build the JAR
RUN mvn clean package -DskipTests

# ===============================
# 2️⃣ RUNTIME STAGE
# ===============================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy only the built JAR from builder stage
COPY --from=builder /build/target/*.jar app.jar

# Expose app port
EXPOSE 8080

# Run Spring Boot app
ENTRYPOINT ["java","-jar","app.jar"]
