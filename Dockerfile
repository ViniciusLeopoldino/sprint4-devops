# Etapa 1: Build
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# Copia os arquivos necessários para o build
COPY pom.xml .
COPY src ./src

# Baixa dependências e compila o projeto
RUN mvn clean package -DskipTests

# Etapa 2: Runtime
FROM eclipse-temurin:17-jre-focal
WORKDIR /app

# Copia o .jar gerado do estágio de build
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta padrão do Spring Boot
EXPOSE 8080

# Define o comando de execução
ENTRYPOINT ["java", "-jar", "app.jar"]
