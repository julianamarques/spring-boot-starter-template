# Como Contribuir?

Obrigado por considerar contribuir com o Spring Boot Starter Template. Este guia descreve o fluxo recomendado para propor correções, melhorias e ajustes de documentação.

## Fluxo de Trabalho

- Faça um fork do repositório e clone o projeto.
- Crie uma branch a partir da branch principal.
- Use nomes de branch objetivos, como `feature/nome-da-feature` ou
  `fix/descricao-do-ajuste`.
- Consulte o `README.md` para configurações locais e execução da aplicação.
- Mantenha pull requests pequenos e focados em uma mudança principal.
- Explique no pull request o problema resolvido, a solução aplicada e como a
  alteração foi validada.

## Padrão de Código

- Siga a estrutura de pacotes já existente.
- Prefira nomes claros para classes, métodos e variáveis.
- Mantenha controllers, services, repositories, DTOs e handlers com responsabilidades separadas.
- Use o `checkstyle.xml` do projeto como referência de formatação.

## Commits

Use o padrão Conventional Commits, com mensagens objetivas e no imperativo, por exemplo:

```text
feat: Adiciona endpoint de consulta de usuarios
fix: Corrige validacao de token JWT
chore: Atualiza configuracao do Flyway
```

## Validação

Antes de abrir um pull request, execute:

```sh
mvn test
mvn checkstyle:check
```

Por padrão, os testes usam H2 em memória e `mvn test` não exige Docker nem um banco
local. Para validar a suíte contra um PostgreSQL real, tenha o Docker em execução e rode:

```sh
mvn test -Dtestcontainers.enabled=true
```

Também reveja se:

- A alteração está limitada ao escopo proposto.
- Novas regras de negócio possuem testes quando aplicável.
- Migrations seguem o padrão `V1.01__sua_alteracao.sql`.
- Nenhuma credencial, token ou dado sensível foi versionado.
- A documentação foi atualizada quando a alteração muda o uso do projeto.

## Pull Requests

Ao abrir um pull request, inclua:

- Resumo da alteração.
- Como a mudança foi testada.
- Evidências relevantes, como logs, prints ou exemplos de payload, quando ajudarem na revisão.
- Observações sobre impactos em banco de dados, configuração ou compatibilidade.

Pull requests pequenos e focados tendem a ser revisados com mais facilidade.

## Issues

Ao abrir uma issue, informe:

- Descrição clara do problema ou melhoria.
- Passos para reproduzir, quando for um bug.
- Comportamento esperado e comportamento atual.
- Versões de Java, Maven e banco utilizadas.
- Logs ou stack traces relevantes.
