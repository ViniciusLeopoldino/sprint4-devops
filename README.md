# Mottu Control - Projeto FIAP (Java, Azure DevOps, Docker & ACI)

![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)
![Docker](https://img.shields.io/badge/Docker-blue.svg)
![Azure DevOps](https://img.shields.io/badge/Azure%20DevOps-Pipelines-blue)
![Azure](https://img.shields.io/badge/Azure-ACI%20%26%20ACR-blue)

---

## Vídeo de Apresentação
 
Demonstração completa da solução, fluxo CI/CD e testes da aplicação: [Vídeo no YouTube](https://youtu.be/Mdl_R1sATzc)

---

## 1. Descrição da Solução

O **Mottu Control** é uma API RESTful desenvolvida em Java 17 com Spring Boot 3, projetada para gerenciar o cadastro de motocicletas da empresa Mottu. A solução permite a realização de um CRUD completo (Criação, Leitura, Atualização e Exclusão) e é protegida por autenticação (Spring Security).

A solução é totalmente gerenciada por práticas de DevOps, utilizando o **Azure DevOps Pipelines** para orquestrar um fluxo completo de CI/CD. Toda a infraestrutura, incluindo a API e o banco de dados PostgreSQL, é containerizada com Docker e implantada automaticamente na nuvem da Microsoft Azure, utilizando o Azure Container Registry (ACR) e o Azure Container Instances (ACI).

## 2. Benefícios para o Negócio

Esta solução foi projetada para resolver problemas de controle de inventário manual e descentralizado, trazendo os seguintes benefícios:
* **Centralização e Acuracidade dos Dados:** Garante que as informações da frota sejam consistentes, confiáveis e acessíveis a partir de um único ponto.
* **Automação de CI/CD:** A pipeline de DevOps garante que novas funcionalidades e correções sejam testadas e implantadas de forma rápida, confiável e automática.
* **Agilidade Operacional:** Permite que as equipes consultem, adicionem ou removam motocicletas do sistema de forma rápida e programática.
* **Segurança:** Os endpoints da API são protegidos por autenticação, garantindo que apenas usuários autorizados possam gerenciar a frota.
* **Escalabilidade:** Por ser baseada em nuvem e containers, a solução pode escalar facilmente para suportar o crescimento da frota da Mottu.

## 3. Arquitetura da Solução e Fluxo CI/CD

O fluxo da solução foi redesenhado para seguir as práticas modernas de CI/CD, onde o **Azure Pipelines é o orquestrador central** de todo o processo, eliminando a necessidade de deploy manual.

<img width="713" height="733" alt="{1F89B4DB-82AA-4483-998A-A5F1EAFF61F9}" src="https://github.com/user-attachments/assets/21848ddc-ba33-4f2c-a7a4-2349a8a99d76" />


## 4. Tecnologias Utilizadas

* **Backend:** Java 17, Spring Boot 3, Spring Data JPA, Spring Security
* **Banco de Dados:** PostgreSQL 15 (Containerizado)
* **Build & Teste:** Apache Maven, JUnit
* **Controle de Versão:** Git & GitHub
* **Orquestração CI/CD:** Azure DevOps (Azure Pipelines)
* **Containerização:** Docker
* **Cloud (Nuvem):** Microsoft Azure
    * **Registro de Imagem:** Azure Container Registry (ACR)
    * **Hospedagem de Containers:** Azure Container Instances (ACI)

## 5. O Processo de CI/CD (Automação)

O deploy manual (descrito anteriormente) foi descontinuado e substituído por uma pipeline de CI/CD automatizada (`azure-pipelines.yml`), que executa as seguintes etapas:

### Estágio 1: CI - Build & Docker (Integração Contínua)

Este estágio é disparado a cada `push` na branch principal do GitHub.

1.  **Build & Test (Manual Script):** O agente do Azure DevOps (Ubuntu) executa o comando `mvn package`, que:
    * Compila todo o código Java.
    * Executa os testes unitários e de integração (incluindo os testes de segurança do `MotoControllerTest`).
2.  **Publish Test Results:** Publica os resultados dos testes (JUnit) na interface do Azure DevOps.
3.  **Publish Artifacts:** Publica o arquivo `.jar` gerado como um artefato (`drop`) no Azure DevOps.
4.  **Login to ACR:** Autentica no Azure Container Registry.
5.  **Build Docker Image:** Constrói a imagem Docker (usando o `Dockerfile`) e aplica as tags (ID do Build e "latest").
6.  **Push Image to ACR:** Envia a imagem Docker recém-construída para o ACR.

### Estágio 2: CD - Deploy to Azure (Entrega Contínua)

Este estágio é disparado automaticamente após a conclusão bem-sucedida do Estágio 1.

1.  **Deploy PostgreSQL & App (Azure CLI):**
    * Um script `az container create` provisiona o container do **PostgreSQL** no ACI, usando as variáveis de ambiente seguras.
    * O script aguarda 60 segundos para o banco de dados iniciar.
    * Um segundo script `az container create` provisiona o container da **Aplicação Java (Mottu Control)**, injetando as credenciais do banco e do ACR. A aplicação automaticamente baixa a imagem correta do ACR.

## 6. Como Testar a API (Pós-Deploy)

Com a pipeline de CI/CD, a aplicação é implantada automaticamente. Para testar, siga os passos:

### Passo 1: Obter o IP ou FQDN da Aplicação
1.  Acesse o [Portal do Azure](https://portal.azure.com).
2.  Navegue até o Grupo de Recursos (ex: `rg-mottu-fiap`).
3.  Clique no recurso "Container instance" da sua aplicação (ex: `app-mottu-fiap-557047`).
4.  Na tela "Properties" (Propriedades), copie o valor do **IP address** ou **FQDN**.
    * A URL base da sua API será: `http://<SEU_IP_OU_FQDN>:8080/api/mos`

### Passo 2: Configurar a Autenticação (Basic Auth) no Postman
Nossa API agora usa **Spring Security**. Todas as requisições (especialmente `POST`, `PUT`, `DELETE`) exigirão autenticação.

Para facilitar os testes no Postman (e para seu vídeo de demonstração), recomendamos definir um usuário e senha estáticos.

1.  **Adicione ao `application.properties` (e faça o commit/push):**
    ```properties
    # Configuração de Segurança Estática para Testes
    spring.security.user.name=mottu
    spring.security.user.password=mottu123
    ```
2.  Aguarde a pipeline executar o deploy novamente.
3.  No Postman, em cada requisição, vá para a aba **"Authorization"**:
    * **Type:** `Basic Auth`
    * **Username:** `mottu`
    * **Password:** `mottu123`

### Passo 3: Executar o CRUD no Postman

---
#### **CREATE (POST)** - Criar uma nova moto
* **Método:** `POST`
* **URL:** `http://<IP_DA_SUA_APP>:8080/api/motos`
* **Authorization:** `Basic Auth (mottu/mottu123)`
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
#### **READ (GET)** - Listar todas as motos
* **Método:** `GET`
* **URL:** `http://<IP_DA_SUA_APP>:8080/api/motos`
* **Authorization:** `Basic Auth (mottu/mottu123)`
* **Resultado Esperado:** Status `200 OK`.

---
#### **UPDATE (PUT)** - Atualizar uma moto
* **Método:** `PUT`
* **URL:** `http://<IP_DA_SUA_APP>:8080/api/motos/1` (substitua `1` por um ID existente)
* **Authorization:** `Basic Auth (mottu/mottu123)`
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
#### **DELETE** - Remover uma moto
* **Método:** `DELETE`
* **URL:** `http://<IP_DA_SUA_APP>:8080/api/motos/1` (substitua `1` por um ID existente)
* **Authorization:** `Basic Auth (mottu/mottu123)`
* **Resultado Esperado:** Status `204 No Content`.

## 7. Limpar os recursos da Azure

Para excluir todos os recursos criados (ACR, ACIs) e parar a cobrança, execute o comando para excluir o grupo de recursos:
```terminal
az group delete --name rg-mottu-fiap --yes --no-wait
```

## 8. Equipe

* **Vinicius Leopoldino de Oliveira** - **RM: 557047**
* **Pablo Lopes Doria de Andrade** - **RM: 556834**


