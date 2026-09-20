# Secure User API

## Objetivo do Projeto

Este projeto consiste em uma API REST para gerenciamento de usuários. A aplicação implementa operações CRUD completas de usuários, autenticação baseada em JWT (JSON Web Token) e controle de acesso baseado em papéis (RBAC), simulando um cenário de integração segura entre um sistema e aplicações parceiras.

A API contempla três perfis de usuário com diferentes níveis de permissão: Administrador, Operador e Cliente. 

## Tecnologias Utilizadas

- Java 17
- Spring Boot 4.1.1
- Spring Web
- Spring Data JPA
- Spring Security
- PostgreSQL
- JJWT (Java JWT) 0.12.6
- Lombok
- Maven

## Como Instalar

### Pré-requisitos

- Java 17 instalado
- PostgreSQL instalado e em execução localmente
- Não é necessário instalar o Maven separadamente; o projeto inclui o Maven Wrapper (`mvnw`/`mvnw.cmd`)

### Passos

1. Clone o repositório:
   ```bash
   git clone https://github.com/<seu-usuario>/secure-user-api.git
   cd secure-user-api
    ```

2. Crie o banco de dados no PostgreSQL:
```sql
   CREATE DATABASE "secure-user-api";
```

3. Execute o script de criação da tabela, localizado em `database/schema.sql`, no pgAdmin ou via `psql`:
```bash
   psql -U postgres -d secure-user-api -f database/schema.sql
```

4. Crie o arquivo `src/main/resources/application-secrets.properties` (não versionado) com o seguinte conteúdo, substituindo os valores de exemplo:
```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/secure-user-api
   spring.datasource.username=postgres
   spring.datasource.password=sua-senha-aqui
   spring.datasource.driver-class-name=org.postgresql.Driver

   jwt.secret=uma-string-aleatoria-de-pelo-menos-32-caracteres
   jwt.expiration-ms=3600000
```

## Como Executar

Na raiz do projeto, execute:
```bash
  ./mvnw spring-boot:run
```

(No Windows, utilize `.\mvnw.cmd spring-boot:run`.)

A aplicação será iniciada em `http://localhost:8080`.

Alternativamente, é possível gerar e executar o artefato `.jar`:
```bash
   ./mvnw clean package
   java -jar target/secure-user-api-0.0.1-SNAPSHOT.jar
```

## Como Testar a Aplicação

A aplicação não possui interface web; todos os testes foram realizados através do Postman.

### Criando o primeiro Administrador

Como o cadastro público (`POST /auth/register`) sempre cria usuários com o perfil Cliente, e a criação de usuários com outros perfis (`POST /users`) exige autenticação como Administrador, o primeiro Administrador precisa ser promovido manualmente após o cadastro:

1. Registre um usuário normalmente via `POST /auth/register`.
2. No banco de dados, promova esse usuário a Administrador:
```sql
   UPDATE users SET role = 'ADMIN' WHERE email = 'seu-email@exemplo.com';
```

### Fluxo básico de teste

1. Realize o login (`POST /auth/login`) com o usuário Administrador e copie o token JWT retornado.
2. Utilize esse token no cabeçalho `Authorization: Bearer <token>` para acessar os endpoints protegidos.
3. Utilize `POST /users` para criar usuários adicionais com os perfis Operador e Cliente.
4. Faça login com cada um dos perfis criados e teste o acesso aos diferentes endpoints, observando que:
   - Administradores têm acesso completo a todas as operações;
   - Operadores podem consultar e atualizar usuários, mas não excluir nem alterar perfis de acesso;
   - Clientes têm acesso restrito apenas aos próprios dados, através dos endpoints `/users/me`.
