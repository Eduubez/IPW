# IPW - Insurance Portal Workflow

Projeto desenvolvido no âmbito da unidade curricular de Projeto e Seminário da Licenciatura em Engenharia Informática e de Computadores, no Instituto Superior de Engenharia de Lisboa.

O objetivo da aplicação é apoiar o fluxo de averiguação de processos de seguro, centralizando a criação, atribuição, acompanhamento e validação de processos por diferentes papéis da organização.

## Estado do projeto

Versão beta disponível através da tag `v1.0`.

## Stack tecnológica

- Frontend: React, TypeScript e Vite
- Backend: Kotlin e Spring Boot
- Base de dados: PostgreSQL
- Armazenamento de anexos: MinIO
- Autenticação e autorização: Spring Security e JSON Web Tokens (JWT)
- Execução local: Docker Compose

## Organização do repositório

- `modules-backend` - código do backend, organizado por módulos de domínio, serviços, repositórios, camada HTTP e aplicação.
- `js` - código do frontend em React e TypeScript.
- `docker` - scripts de inicialização usados pelos containers, incluindo a configuração inicial da base de dados.
- `SQL` - scripts SQL usados durante o desenvolvimento.
- `Docs` - documentação do projeto e materiais das entregas.
- `docker-compose.yml` - definição dos serviços necessários para executar a aplicação localmente.

## Pré-requisitos

Para executar a aplicação localmente é necessário ter instalado:

- Git
- Docker Desktop
- Um navegador web

Não é necessário instalar manualmente PostgreSQL, Node.js ou Gradle para correr a versão beta através de Docker Compose.

## Execução da versão beta

Clone o repositório:

```powershell
git clone https://github.com/Eduubez/IPW.git
```

Entre na pasta do projeto:

```powershell
cd IPW
```

Selecione a tag da versão beta:

```powershell
git checkout v1.0
```

Construa e inicie os serviços:

```powershell
docker compose up -d --build
```

Após a inicialização, os principais serviços ficam disponíveis em:

- Frontend: http://localhost:5173
- Backend: http://localhost:8080
- MinIO Console: http://localhost:9001

Credenciais da consola MinIO:

- Utilizador: `ipw`
- Password: `ipw-password`

## Parar a aplicação

Para parar os serviços:

```powershell
docker compose down
```

Para parar os serviços e remover também os volumes locais, incluindo os dados da base de dados e do MinIO:

```powershell
docker compose down -v
```

## Utilizadores de teste

Os dados iniciais são inseridos automaticamente pelos scripts de inicialização da base de dados.

Todos os utilizadores abaixo têm a password `12345`.

| Email | Papel |
| --- | --- |
| `alice@ipw.pt` | Triador |
| `bob@ipw.pt` | Averiguador |
| `carol@ipw.pt` | Supervisor |
| `david@ipw.pt` | Gestor |
| `eve@ipw.pt` | Administrador |

## Desenvolvimento

Durante o desenvolvimento, também é possível correr partes da aplicação fora de Docker:

- Backend: através do Gradle/Spring Boot
- Frontend: a partir da pasta `js`, com `npm run dev`

No entanto, para testar a versão beta de forma simples e reproduzível, recomenda-se o uso de `docker compose up -d --build`.

## Equipa

- Eduardo Franco Bezerra - 46362
- Francisco Duarte Tavares - 51618
- Rafael Henrique Souza Reis - 51870

Orientadores:

- Artur Jorge Ferreira
- Pedro Miguel Florindo Miguens Matutino
