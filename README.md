# Mottu Control - Projeto FIAP (Java, Docker & Azure)

![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)
![Docker](https://img.shields.io/badge/Docker-blue.svg)
![Azure](https://img.shields.io/badge/Azure-ACI%20%26%20ACR-blue)

---

## Vídeo de Apresentação    
 
Demonstração completa do aplicativo no YouTube: [Apresentação Mottu Control](https://youtu.be/YBeacDZA1FY)

---

## 1. Descrição da Solução

O **Mottu Control** é uma API RESTful desenvolvida em Java com o framework Spring Boot, projetada para gerenciar o cadastro de motocicletas da empresa Mottu. A solução permite a realização de um CRUD completo (Criação, Leitura, Atualização e Exclusão) sobre uma tabela de motos.

Toda a infraestrutura da aplicação, incluindo o banco de dados PostgreSQL, é containerizada com Docker e implantada na nuvem da Microsoft Azure, utilizando o Azure Container Registry (ACR) para armazenamento de imagens e o Azure Container Instances (ACI) para a execução dos containers.

## 2. Benefícios para o Negócio

Esta solução foi projetada para resolver problemas de controle de inventário manual e descentralizado, trazendo os seguintes benefícios:
* **Centralização e Acuracidade dos Dados:** Garante que as informações da frota sejam consistentes, confiáveis e acessíveis a partir de um único ponto.
* **Agilidade Operacional:** Permite que as equipes consultem, adicionem ou removam motocicletas do sistema de forma rápida e programática.
* **Escalabilidade:** Por ser baseada em nuvem e containers, a solução pode escalar facilmente para suportar o crescimento da frota da Mottu.
* **Fundação para o Futuro:** Serve como a base tecnológica para o desenvolvimento de novas funcionalidades, como sistemas de aluguel, agendamento de manutenção e rastreamento.

## 3. Arquitetura da Solução

O fluxo da solução segue as práticas modernas de DevOps e Cloud Computing:

```
+---------------+      +----------------+      +------------------+
|               |      |                |      |                  |
|  Desenvolvedor|----->|     GitHub     |----->|      Azure       |
|               |      | (Código Fonte) |      | (Nuvem/Cloud)    |
+---------------+      +----------------+      +------------------+
      |                                              |
      | 1. Desenvolve o código e o Dockerfile          | 5. Executa os scripts de deploy (ACI)
      | 2. Envia para o GitHub (push)                  |
      |                                                V
      |------------------------------------------------+
      | 3. Clona o repo localmente                     |
      | 4. Constrói a imagem e envia para o ACR        |
      |    (docker build/push)                         |
      +------------------------------------------------+

      -------------------------- Nuvem Azure ---------------------------
      |                                                                 |
      |  +---------------------------+       +------------------------+ |
      |  |  Azure Container Registry |       |   Grupo de Recursos    | |
      |  |      (ACR)                |<------|                        | |
      |  |  [Armazena imagem da App] |       | +--------------------+ | |
      |  +---------------------------+       | | App Java (ACI)     | | |
      |                                      | | [Container]        | | |
      |                                      | +--------+-----------+ | |
      |                                      |          |             | |
      |                                      |          V             | |
      |                                      | +--------+-----------+ | |
      |                                      | | Banco PG (ACI)     | | |
      |                                      | | [Container]        | | |
      |                                      | +--------------------+ | |
      |                                      +------------------------+ |
      |                                                                 |
      -------------------------------------------------------------------
                                       ^
                                       |
                                +------+------+
                                |             |
                                |   Usuário   |
                                |  (Testes)   |
                                +-------------+
```

## 4. Tecnologias Utilizadas

* **Backend:** Java 17, Spring Boot 3, Spring Data JPA
* **Banco de Dados:** PostgreSQL 15
* **Build:** Apache Maven
* **Containerização:** Docker
* **Cloud:** Microsoft Azure (Azure CLI, ACR, ACI)
* **Controle de Versão:** Git & GitHub

## 5. Pré-requisitos

Para realizar o deploy desta solução, você precisará ter as seguintes ferramentas instaladas e configuradas em sua máquina local:
* Git
* JDK 17 ou superior
* Docker Desktop (em execução)
* Azure CLI (logado com `az login`)

## 6. Guia de Deploy e Teste

Siga os passos abaixo para implantar e testar a solução.

### Passo 1: Clone o Repositório
```powershell
git clone https://github.com/ViniciusLeopoldino/sprint3-devops.git
cd sprint3-devops
```

### Passo 2: Execute o Script de Deploy
O script a seguir automatiza a criação de toda a infraestrutura. Copie o bloco inteiro e execute no seu terminal.

```terminal
# ===================================================================
# ROTEIRO DE DEPLOY - PROJETO MOTTU CONTROL
# ===================================================================

# ----- Bloco de Variáveis -----
$env:RESOURCE_GROUP="rg-mottu-fiap"
$env:LOCATION="brazilsouth"
$env:ACR_NAME="acrmottucontrol557047" 
$env:APP_CONTAINER_NAME="java-app-mottu"
$env:POSTGRES_CONTAINER_NAME = "postgres-db-mottu"
$env:POSTGRES_DB = "mottudb"
$env:POSTGRES_USER = "mottuadmin"
$env:POSTGRES_PASSWORD = "mottu280595"

# ----- 1. Criar Recursos Base -----
Write-Host "Criando Grupo de Recursos e Azure Container Registry..."
az group create --name $env:RESOURCE_GROUP --location $env:LOCATION
az acr create --resource-group $env:RESOURCE_GROUP --name $env:ACR_NAME --sku Basic --admin-enabled true

# ----- 2. Fazer Build e Push da Imagem da Aplicação -----
Write-Host "Fazendo login, build e push da imagem Docker..."
az acr login --name $env:ACR_NAME
docker build -t "$($env:ACR_NAME).azurecr.io/mottu-control:v1" .
docker push "$($env:ACR_NAME).azurecr.io/mottu-control:v1"

# ----- 3. Criar o Container do PostgreSQL -----
Write-Host "Criando o container do PostgreSQL..."
az container create --resource-group $env:RESOURCE_GROUP --name $env:POSTGRES_CONTAINER_NAME --image postgres:15 --os-type Linux --ports 5432 --cpu 1 --memory 1.5 --environment-variables "POSTGRES_DB=$($env:POSTGRES_DB)" "POSTGRES_USER=$($env:POSTGRES_USER)" "POSTGRES_PASSWORD=$($env:POSTGRES_PASSWORD)" --dns-name-label postgres-mottu-$($env:ACR_NAME)

# ----- 4. Aguardar e Obter o IP do Banco de Dados -----
Write-Host "Aguardando 60 segundos para o PostgreSQL iniciar..."
Start-Sleep -Seconds 60
$POSTGRES_IP = $(az container show --resource-group $env:RESOURCE_GROUP --name $env:POSTGRES_CONTAINER_NAME --query ipAddress.ip --output tsv)
Write-Host "IP do PostgreSQL obtido: $POSTGRES_IP"

# ----- 5. Criar o Container da Aplicação -----
$DB_URL = "jdbc:postgresql://$($POSTGRES_IP):5432/$($env:POSTGRES_DB)"
$ACR_PASSWORD = $(az acr credential show --name $env:ACR_NAME --query "passwords[0].value" --output tsv)

Write-Host "Criando o container da aplicação Java..."
az container create --resource-group $env:RESOURCE_GROUP --name $env:APP_CONTAINER_NAME --image "$($env:ACR_NAME).azurecr.io/mottu-control:v1" --os-type Linux --ports 8080 --cpu 1 --memory 1.5 --registry-username $env:ACR_NAME --registry-password $ACR_PASSWORD --environment-variables "DB_URL=$($DB_URL)" "DB_USER=$($env:POSTGRES_USER)" "DB_PASSWORD=$($env:POSTGRES_PASSWORD)" --dns-name-label app-mottu-$($env:ACR_NAME)

# ----- 6. Mensagem de Conclusão -----
Write-Host "------------------------------------------------------------"
Write-Host "✅ Infraestrutura implantada com sucesso!"
Write-Host "Obtenha os IPs da aplicação e banco de dados nos retornos acima."
Write-Host "------------------------------------------------------------"
```

## 7. Acessando o Banco de Dados (PostgreSQL)

Use uma ferramenta como o **DBeaver** ou pgAdmin para se conectar ao banco.
* **Host/Servidor:** O IP obtido no Passo 2.
* **Porta:** `5432`
* **Banco de Dados:** `mottudb`
* **Usuário:** `mottuadmin`
* **Senha:** `mottu280595`

## 8. Como Testar a API com o Postman

Use a URL da API retornada no Passo 2 para montar as requisições no Postman.

---
### **CREATE (POST)** - Criar uma nova moto
* **Método:** `POST`
* **URL:** `http://<IP_DA_SUA_APP>:8080/api/motos`
* **Corpo (Body):** `raw`, `JSON`
    ```json
    {
      "modelo": "Honda Pop 110i",
      "placa": "BRA2E19",
      "ano": 2025
    }
    ```
* **Resultado Esperado:** Status `201 Created`.

---
### **READ (GET)** - Listar todas as motos
* **Método:** `GET`
* **URL:** `http://<IP_DA_SUA_APP>:8080/api/motos`
* **Resultado Esperado:** Status `200 OK`.

---
### **UPDATE (PUT)** - Atualizar uma moto
* **Método:** `PUT`
* **URL:** `http://<IP_DA_SUA_APP>:8080/api/motos/1` (substitua `1` por um ID existente)
* **Corpo (Body):** `raw`, `JSON`
    ```json
    {
      "modelo": "Honda Pop 110i EX",
      "placa": "BRA2E19",
      "ano": 2026
    }
    ```
* **Resultado Esperado:** Status `200 OK`.

---
### **DELETE** - Remover uma moto
* **Método:** `DELETE`
* **URL:** `http://<IP_DA_SUA_APP>:8080/api/motos/1` (substitua `1` por um ID existente)
* **Resultado Esperado:** Status `204 No Content`.

## 9. Limpar os recursos da Azure

```terminal
az group delete --name $env:RESOURCE_GROUP --yes --no-wait
```

## 10. Equipe

* **Vinicius Leopoldino de Oliveira** - RM 557047
* **Pablo Lopes Doria de Andrade** - RM 556834

