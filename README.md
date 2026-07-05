# Spring Boot Starter Template

Projeto template com algumas configurações comuns já feitas e autenticação JWT implementada para ser usado como base em outros projetos

### Requisitos Necessários 💻

* Java 25+
* Maven 3.9.6+

### Como usar? ⚙️

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

### Executando o Projeto ▶️

Depois de concluídas as configurações, rode o projeto com:

```sh
mvn clean install -DskipTests spring-boot:run
```

### Checkstyle ✅

Você pode verificar o checkstyle e manter o padrão de formatação do seu código através do comando:

```sh
mvn checkstyle:check
```

### Contribuições 🤝

Contribuições são bem-vindas! Sinta-se à vontade para abrir um pull request para propor melhorias ou correções.
