FROM eclipse-temurin:17-jre-focal
WORKDIR /app

# Copia o JAR gerado pelo Maven
COPY target/mottu-control-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
