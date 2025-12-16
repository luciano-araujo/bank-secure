# BankSecure

Aplicacao full-stack para gestao de seguros, apolices e clientes. Backend em Spring Boot com H2 em memoria e documentacao OpenAPI; frontend em Angular 19.

## Arquitetura e stack
- Backend: Java 17, Spring Boot 3.5, Spring Data JPA, Validation, Springdoc OpenAPI, Lombok, H2.
- Frontend: Angular 19 (standalone components), RxJS, Angular CLI.
- Build/test: Maven, npm.

## Pre-requisitos
- Java 17 e Maven 3.9+.
- Node 20+ e npm; Angular CLI 19 (`npm install -g @angular/cli`).
- Porta 8080 livre para o backend e 4200 para o frontend.

## Como executar
### Backend (API + console)
```bash
cd bank-secure
mvn spring-boot:run
```
Endpoints sob `http://localhost:8080`. O console interativo inicia por padrao (ver `BankSecureApplication`); para subir apenas a API, altere `runConsoleMenu` para `false`.

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- H2 Console: `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:bankdb`, user `sa`, senha vazia)

### Frontend
```bash
cd bank-secure/frontend
npm install
npm start
```
Acesse `http://localhost:4200`. O frontend espera a API em `http://localhost:8080`; ajuste `apiUrl` em `src/app/services/apolice.service.ts` se precisar apontar para outro host.

## Autenticacao
- Endpoint: `POST /auth`
- Payload exemplo:
```json
{
  "email": "admin@banksecure.com",
  "senha": "admin123"
}
```
- Resposta exemplo:
```json
{
  "authenticated": true,
  "usuarioId": "UUID",
  "nome": "Administrador",
  "tipoUsuario": "FUNCIONARIO"
}
```
- Usuario seed: `admin@banksecure.com` / `admin123` (criado no `DataInitializer`). Para outros usuarios, crie via `/cliente` ou `/funcionario`; a senha enviada em texto plano sera armazenada com BCrypt.

## Endpoints principais
- Autenticacao: `POST /auth`
- Clientes: `GET/POST /cliente`, `GET/PUT/DELETE /cliente/{id}`
- Funcionarios: `GET/POST /funcionario`, `GET/PUT/DELETE /funcionario/{id}`
- Seguros: `GET/POST /seguro`, `GET/PUT/DELETE /seguro/{id}`
- Bens: `GET/POST /bem`, `GET/PUT/DELETE /bem/{id}`
- Cotacoes: `GET /cotacao`, `GET /cotacao/{id}`, `POST /cotacao`
- Apolices: `GET /apolice`, `GET /apolice/vencer`, `GET /apolice/vencidas`, `POST /apolice`, `POST /apolice/renovacao/{id}`, dashboard em `GET /apolice/dashboard`

## Exemplos rapidos
- Criar apolice:
```http
POST /apolice
Content-Type: application/json

{
  "clienteId": "UUID",
  "seguroId": "UUID",
  "totalCobertura": 50000
}
```
- Renovar apolice: `POST /apolice/renovacao/{id}` (regra: apenas faltando 30 dias ou menos para o vencimento).
- Cotacao: `POST /cotacao` com `{ "clienteId": "...", "seguroId": "..." }`.

## Testes
- Backend: `mvn test`
- Frontend: `cd frontend && npm test`

## Build para producao
- Backend: `mvn clean package` gera `target/bankSecure-0.0.1-SNAPSHOT.jar`.
- Frontend: `cd frontend && npm run build` gera `dist/frontend`, que pode ser servido separadamente ou copiado para um servidor web.

## Observacoes
- Base H2 em memoria; dados sao perdidos ao parar a aplicacao.
- Ha um console de linha de comando (classe `ConsoleMenu`) para operar via terminal durante o desenvolvimento.
