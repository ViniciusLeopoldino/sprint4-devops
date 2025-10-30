# Estágio 1: Build da aplicação com Maven
FROM maven:3.8.5-openjdk-17 AS build
# Define o diretório de trabalho dentro do container
WORKDIR /app
# Copia os arquivos do projeto para o diretório de trabalho
COPY . .
# Baixa as dependências do projeto
RUN mvn dependency:go-offline
# Executa o build do projeto, gerando o arquivo .jar
RUN mvn package -DskipTests
# Estágio 2: Criação da imagem final, otimizada e segura
FROM eclipse-temurin:17-jre-focal
# Define o diretório de trabalho
WORKDIR /app
# Adiciona um grupo e um usuário não-root para rodar a aplicação
# REQUISITO 8.2: O container não pode rodar como root
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser
# Muda o proprietário do diretório de trabalho para o novo usuário
RUN chown -R appuser:appgroup /app
# Muda o usuário padrão do container
USER appuser
# Copia o arquivo .jar gerado no estágio de build
COPY --from=build /app/target/mottu-control-*.jar app.jar
# Expõe a porta que a aplicação vai usar
EXPOSE 8080
# Comando para executar a aplicação quando o container iniciar
ENTRYPOINT ["/bin/sh", "-c", "echo 'Aguardando 120 segundos para o banco de dados iniciar...' && sleep 120 && java -jar app.jar"]