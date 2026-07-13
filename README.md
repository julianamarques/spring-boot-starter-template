# Spring Boot Starter Template

[![CI](https://github.com/julianamarques/spring-boot-starter-template/actions/workflows/github-ci.yml/badge.svg)](https://github.com/julianamarques/spring-boot-starter-template/actions/workflows/github-ci.yml)
[![Java](https://img.shields.io/badge/Java-25-ED8B00.svg?logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/25/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-6DB33F.svg?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)

Projeto template com algumas configurações comuns já feitas e autenticação JWT implementada para ser usado como base em outros projetos

### 💻 Requisitos Necessários

* Java 25
* Maven 3.9.6+

### 🛠️ Como Configurar?

1. No `application.yml`:
    1. Altere com o nome da aplicação e as credenciais do banco:
       ```yml
        spring:
            application:
                name: application-name
            datasource:
                url: ${DB_URL:jdbc:postgresql://localhost:5432/database_local}
                username: ${USER_DB:postgres}
                password: ${PASSWORD_DB:postgres}
       ``` 
    2. Execute o script da pasta ```src/main/resources/db/migration``` para inicialização do banco de dados
    3. Altere o ```context-path``` para um relacionado ao da sua aplicação. Se necessário, altere também a porta:
       ```yml
       server:
         port: ${PORT:8080}
         servlet:
           context-path: ${CONTEXT_PATH:/base-url}
       ```
    4. Caso use anexo de arquivos, altere o tamanho dos arquivos anexados, se necessário:
   ```yml
   servlet:
      multipart:
      enabled: true
      max-request-size: 50MB
      max-file-size: 50MB
   ```
    5. Altere as configurações de email, caso queira utilizar, o ```JavaEmailSend```. Se não usar, pode remover:
   ```yml
   mail:
    host: smtp.gmail.com
    port: 587
    username: ${USER_EMAIL:noreply@email.com.br}
    password: ${PASSWORD_EMAIL:12345}
    protocol: smtp
   ```
    6. Caso queira habilitar o ```Flyway``` altere o ```enabled``` para ```true``` e coloque os scripts de migração no caminho ```classpath:db/migration``` ou renomeie esse caminho para uma da sua preferência:
   ```yml
   flyway:
    enabled: false
    locations: classpath:db/migration
   ```
   Lembrando que no ```Flyway``` os scripts devem estar nomeados da seguinte forma: ```V1.01__sua_alteracao.sql```. Exemplo: ```V1.01__update_table_usuario.sql```.
2. Renomeie o package ```br.com.project.springboot.starter.template.api``` para ```br.com.nomesuaaplicacao.api```, tanto no ```/src/main/java``` quanto no ```/src/test/java```
3. Renomeie o arquivo ```SprintBootStarterTemplateApiApplication``` e ```SprintBootStarterTemplateApiApplicationTests``` para ```NomeSuaAplicacaoApiApplication``` e ```NomeSuaAplicacaoApiApplicationTests```
4. No ```pom.xml```, altere o ```<groupId>```, ```<name>``` e ```<description>```, para algo correspondente a sua aplicação:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
   <modelVersion>4.0.0</modelVersion>
   <groupId>br.com.project.springboot.starter.template</groupId>
   <artifactId>api</artifactId>
   <version>0.0.1-SNAPSHOT</version>
   <name>springboot-starter-template-api</name>
   <description>Spring Boot Starter Template: Template Project</description>
</project>
```

### 🚀 Executando o Projeto

Antes de rodar, defina o segredo usado na assinatura dos tokens JWT. Ele é obrigatório (a aplicação não sobe sem ele) e precisa ter pelo menos 64 bytes, requisito do algoritmo HS512:

```sh
export KEY_JWT_SECRET=$(openssl rand -base64 64 | tr -d '\n')
```

Por padrão, o CORS libera apenas `http://localhost:4200` e `http://localhost:3000`. Se o front-end rodar em outra origem, defina:

```sh
export CORS_ALLOWED_ORIGINS=https://app.suaaplicacao.com.br
```

Após concluídas as configurações, rode o projeto com um dos comandos abaixo:

```sh
mvn clean install spring-boot:run -DskipTests # SE FOR EXECUTAR LOCAL
```

```sh
docker compose up -d --build # SE FOR EXECUTAR COM DOCKER
```

O `docker-compose.yml` sobe **apenas a aplicação**, construída a partir do `Dockerfile` (build multi-stage). É preciso ter um Postgres acessível separadamente — por padrão, a aplicação se conecta em `host.docker.internal:5432` (ou seja, um Postgres rodando na sua máquina host, fora do Docker). Ajuste `DB_URL`, `USER_DB` e `PASSWORD_DB` no `docker-compose.yml` caso o banco esteja em outro lugar.

A API fica disponível em `http://localhost:8080/base-url` (ex.: `GET /base-url/health/check`).

A porta exposta no host é configurável (útil se a porta padrão já estiver em uso):

```sh
APP_HOST_PORT=8081 docker compose up -d --build
```

> Nota: os **testes não usam** este `docker-compose.yml` — eles sobem um Postgres próprio e efêmero via Testcontainers.

### 🧪 Testes

Os testes de integração usam [Testcontainers](https://testcontainers.com/), que sobe um Postgres real em container automaticamente — não é preciso configurar banco manualmente. Basta ter o **Docker** em execução:

```sh
mvn test
```

### 📖 Documentação da API (Swagger)

A documentação interativa (OpenAPI 3) é gerada automaticamente pelo springdoc. Com a aplicação no ar, acesse:

- Swagger UI: `http://localhost:8080/base-url/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/base-url/v3/api-docs`

O esquema de segurança JWT já está configurado — use o botão **Authorize** e informe o token (obtido em `/auth/login`) para chamar os endpoints protegidos. Para desabilitar em produção, defina `SPRINGDOC_API_DOCS_ENABLED=false` e `SPRINGDOC_SWAGGER_UI_ENABLED=false`.

### ✅ Checkstyle

Você pode verificar o checkstyle e manter o padrão de formatação do seu código através do comando:

```sh
mvn checkstyle:check
```

### 🤝 Contribuições

Contribuições são bem-vindas! Sinta-se à vontade para abrir um pull request para propor melhorias ou correções.
