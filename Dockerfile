# 1️⃣ Java 17 base image
FROM eclipse-temurin:17-jdk-focal

# 2️⃣ Working directory
WORKDIR /app

# 3️⃣ POM.xml copy aur Maven install
COPY pom.xml .
RUN apt-get update && apt-get install -y maven
RUN mvn dependency:go-offline

# 4️⃣ Baaki code copy karna
COPY . .

# 5️⃣ Build command
RUN mvn clean package -DskipTests || (echo "BUILD FAILED"; exit 1)

# 6️⃣ JAR file ka naam print kar (Debugging step)
RUN echo "Checking target folder..." && ls -lh target/

# 7️⃣ Backend start karne ka proper tareeka
CMD ["java", "-jar", "target/job-application-tracker-0.0.1-SNAPSHOT.jar"]
