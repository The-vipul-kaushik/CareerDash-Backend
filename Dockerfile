# 1️⃣ Java 17 base image
FROM openjdk:17-jdk-slim

# 2️⃣ Working directory
WORKDIR /app

# 3️⃣ POM.xml copy aur Maven install
COPY pom.xml .
RUN apt-get update && apt-get install -y maven
RUN mvn dependency:go-offline

# 4️⃣ Baaki code copy karna
COPY . .

# 5️⃣ Maven build with debug
RUN mvn clean package -DskipTests && echo "Build successful" || (echo "Build failed"; exit 1)

# 6️⃣ JAR file ka naam print kar
RUN ls -lh target/

# 7️⃣ Backend start
CMD ["java", "-jar", "$(ls target/*.jar | head -n 1)"]
