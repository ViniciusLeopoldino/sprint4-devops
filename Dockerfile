# Estágio 1: Build da aplicação com Maven
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# Copia todo o código fonte e o pom.xml
COPY . .

# Executa o build do projeto, gerando o arquivo .jar
RUN mvn clean package -DskipTests

# Estágio 2: Criação da imagem final, otimizada e segura
FROM eclipse-temurin:17-jre-focal
WORKDIR /app

RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser
RUN chown -R appuser:appgroup /app
USER appuser

# Copia o JAR gerado no estágio anterior
COPY --from=build /app/target/mottu-control-*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["/bin/sh", "-c", "echo 'Aguardando 120 segundos para o banco de dados iniciar...' && sleep 120 && java -jar app.jar"]
