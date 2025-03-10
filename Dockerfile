FROM openjdk:17-jdk-slim
WORKDIR /app
COPY pom.xml .
RUN apt-get update && apt-get install -y maven
RUN mvn dependency:go-offline
COPY . .
RUN mvn clean package -DskipTests || (cat target/surefire-reports/*.txt && false)
CMD ["java", "-jar", "$(ls target/*.jar | head -n 1)"]
