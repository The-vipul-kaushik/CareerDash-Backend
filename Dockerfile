# 1️⃣ Java 17 ka base image use karo
FROM openjdk:17-jdk-slim

# 2️⃣ Working directory set karo
WORKDIR /app

# 3️⃣ POM.xml aur source code copy karo
COPY pom.xml .
COPY src/ src/

# 4️⃣ Maven install aur dependencies download karo
RUN apt-get update && apt-get install -y maven
RUN mvn dependency:go-offline

# 5️⃣ Baaki files copy karo
COPY . .

# 6️⃣ Build command run karo & logs dikhane ka system lagao
RUN mvn clean package -DskipTests || (cat target/surefire-reports/*.txt && false)

# 7️⃣ Check karo ki JAR file exist karti hai
RUN ls -lh target/

# 8️⃣ Backend run karo (Fixed CMD command)
CMD ["java", "-jar", "target/*.jar"]
