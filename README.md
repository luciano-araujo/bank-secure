# BankSecure

Aplicação completa para gestão de seguros com:
- API Spring Boot (clientes, seguros, apólices, bens, cotações e autenticação)
- Console interativo (`ConsoleMenu`) pronto para uso local
- Seguros com descrição, tipo (RESIDENCIAL/AUTOMOTIVO/VIDA) e cálculo dinâmico de prêmio/cobertura baseado nos bens
- Novo frontend em React moderno localizado na pasta `frontend/`

## Requisitos
- Java 17+
- Node.js 22.12+ (necessário para evitar os avisos do Vite)

## Executando o backend
```bash
./mvnw spring-boot:run
```
O console interativo carregará automaticamente após o servidor iniciar (`ConsoleMenu`).  
Se desejar desativá-lo ajuste os flags `runConsoleMenu`/`runConsoleRunner` em `BankSecureApplication`.

Um funcionário padrão é criado automaticamente:
- **E-mail:** `admin@banksecure.com`
- **Senha:** `Admin@123`

## Frontend
```bash
cd frontend
cp .env.example .env    # ajuste VITE_API_BASE_URL se precisar
npm install
npm run dev             # http://localhost:5173
```

Para build de produção:
```bash
npm run build
```

> O frontend consome os endpoints REST expostos pelo Spring Boot. Certifique-se de que a API esteja rodando antes de abrir o portal. O login exige um funcionário ativo. Somente usuários do tipo FUNCIONARIO conseguem acessar as áreas protegidas. Apólices e cotações agora exigem o cadastro dos bens segurados (ou do valor total de cobertura) para que o cálculo considere todas as regras de negócio.
