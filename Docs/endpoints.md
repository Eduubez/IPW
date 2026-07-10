# API endpoints - IPW

Cada endpoint inclui:

- método HTTP
- roles que podem aceder
- informação extra

## Processes

### Create Process

**Endpoint:** `POST /process`

**Roles:**

- Triator

**Info:**

- Cria um novo processo
- Investigator e supervisor são definidos na criação
- O processo fica com o estado `assigned`

### Get Process By Id

**Endpoint:** `GET /process/{id}`

**Roles:**

- Triator
- Investigator
- Supervisor
- Manager

**Info:**

- Permite buscar um processo

- Triator:

    - Só consegue buscar processos sem supervisor e triador associados e no estado `not_assigned`

- Investigator:

    - Só consegue buscar processos associados a si e nos estados `assigned`, `on_going`, `rejected_by_supervisor` e
      `rejected_by_manager`

- Supervisor:

    - Consegue buscar qualquer processo da sua área e no estado `waiting_approval_supervisor`

- Manager:

    - Consegue buscar todos os processos de todas as áreas que estejam no estado `waiting_approval_manager`

### Get Processes

**Endpoint:** `GET /process?offset={offset}&limit={limit}&history={history}&state={state}&priority={priority}&name={name}`

**Roles:**

- Triator
- Investigator
- Supervisor
- Manager

**Info:**

- Triator:

    - Só consegue buscar processos sem supervisor e triador associados e no estado `not_assigned`

- Investigator:

    - Só consegue buscar processos associados a si e nos estados `assigned`, `on_going`, `rejected_by_supervisor` e
      `rejected_by_manager`

- Supervisor:

    - Consegue buscar qualquer processo da sua área e no estado `waiting_approval_supervisor`

- Manager:

    - Consegue buscar todos os processos de todas as áreas que estejam no estado `waiting_approval_manager`

- Suporta paginação através de `offset` e `limit`

- Possui uma flag `history` que define se serão retornados todos os processos terminados, ou seja, nos estados
  `approved_by_manager` e `canceled`, permitindo consultar o histórico

### Assign Investigator

**Endpoint:** `PATCH /process/{id}/investigator`

**Roles:**

- Triator

**Info:**

- Associa um investigator a um processo que ainda não o tenha associado e que esteja no estado `not_assigned`

### Assign Supervisor

**Endpoint:** `PATCH /process/{id}/supervisor`

**Roles:**

- Triator

**Info:**

- Associa um supervisor a um processo que ainda não o tenha associado e que esteja no estado `not_assigned`

### Submit Process

**Endpoint:** `POST /processes/{id}/submit`

**Roles:**

- Investigator

**Info:**

- Submete um processo após a introdução de um relatório e de anexos
- O processo passa para o estado `waiting_approval_supervisor`

### Update Process End Date

**Endpoint:** `PATCH /process/{id}/end-date`

**Roles:**

- Supervisor
- Manager

**Info:**

- Supervisor: só pode alterar a data de término em processos da sua área
- Manager: pode alterar a data de término em qualquer processo

### Change Priority

**Endpoint:** `PATCH /process/{id}/priority`

**Roles:**

- Supervisor
- Manager

**Info:**

- Supervisor: só pode alterar a prioridade em processos da sua área
- Manager: pode alterar a prioridade em qualquer processo

### Cancel Process

**Endpoint:** `POST /processes/{id}/cancel`

**Roles:**

- Manager

**Info:**

- Apenas o manager pode cancelar processos
- O estado passa para `canceled`

## Report

### Get Report

**Endpoint:** `GET /process/{processId}/report`

**Roles:**

- Investigator
- Supervisor
- Manager

**Info:**

- Investigator:
    - Pode ver se tiver escrito o relatório
- Supervisor:
    - Pode ver todos os relatórios dos processos da sua area
- Manager:
    - Pode ver todos os relatórios de todos os processos de todas as areas

### Create Report

**Endpoint:** `POST /process/{processId}/report`

**Roles:**

- Investigator

**Info:**

- Apenas o investigator pode criar um relatório para um certo processo
- Cada processo só pode ter um relatório

### Update Report

**Endpoint:** `PATCH /process/{processId}/report`

**Roles:**

- Investigator

**Info:**

- Apenas o investigator do processo pode atualizar o relatório


### Approve Report

**Endpoint:** `POST /process/{processId}/report/approve`

**Roles:**

- Supervisor
- Manager

**Info:**

- Ao aprovar, se for o supervidor:
    - Estado passa para `waiting_approval_manager`
- Ao aprovar,se for o manager:
    - Estado passa para `approved_by_manager`

### Reject Report

**Endpoint:** `POST /process/{id}/report/reject`

**Roles:**

- Supervisor
- Manager

**Info:**

- Ao ser rejeitado pelo supervisor:
    - Estado passa para `rejected_by_supervisor`
- Ao ser rejeitado pelo manager:
    - Estado passa para `rejected_by_manager`
- Processo volta sempre ao investigator

## Notes


### Create Note

**Endpoint:** `POST /process/{processId}/note`

**Roles:**

- Investigator
- Supervisor
- Manager

**Info:**

- Cria uma nota associada a um processo ou a uma prova
- O autor é automaticamente o utilizador autenticado

### Get Notes by Process

**Endpoint:** `GET /process/{processId}/note`

**Roles:**

- Investigator
- Supervisor
- Manager

**Info:**

- Retorna todas as notas associadas a um processo

### Get Notes by Prove

**Endpoint:** `GET /process/{processId}/proves/{proveId}/note`

**Roles:**

- Investigator
- Supervisor
- Manager

**Info:**

- Retorna notas associadas a uma prova

### Update Note

**Endpoint:** `PATCH /process/{processId}/note/{noteId}`

**Roles:**

- Investigator
- Supervisor
- Manager

**Info:**

- O autor pode editar a sua própria nota
- Supervisor e Manager podem editar qualquer nota

## Users

### Get All Users

**Endpoint:** `GET /users?offset={offset}&limit={limit}&areaId={areaId}&isActive={isActive}`

**Roles:**

- Admin

**Info:**

- Retorna todos os utilizadores
- Suporta paginação através de offset e limit
- Pode filtrar por área e/ou por estado do utilizador (ativo ou inativo)

### Get User

**Endpoint:** `GET /users/me`

**Roles:**

- Triator
- Investigator
- Supervisor
- Manager
- Admin

**Info:**

- Retorna informação do utilizador da sessão atual

### Create User

**Endpoint:** `POST /users`

**Roles:**

- Admin

**Info:**

- Apenas admin pode criar utilizadores
- Deve definir:
    - Nome
    - Email
    - Password
    - Roles
    - Área (se aplicável)

### Login

**Endpoint:** `POST /users/login`

**Roles**

- Triator
- Investigator
- Supervisor
- Manager
- Admin

**Info:**

- Iniciar sessão com um utilizador 

### Logout

**Endpoint:** `POST /users/logout`

**Roles**

- Triator
- Investigator
- Supervisor
- Manager
- Admin

**Info:**

- Terminar sessão de um utilizador

### Get Refresh Token

**Endpoint:** `POST /users/refresh-token`

**Roles**

- Triator
- Investigator
- Supervisor
- Manager
- Admin

**Info:**

- Renovar sessão de um utilizador

### Select User Role

**Endpoint** `Post /users/auth/select-role`

**Roles**

- Triator
- Investigator
- Supervisor
- Manager
- Admin

**Info:**

- Escolher que role o utilizador irá utilizar na sessão corrente

### Get User Roles

**Endpoint** `GET /users/roles`

**Roles**

- Triator
- Investigator
- Supervisor
- Manager
- Admin

**Info:**

- Retorna as roles que um utilizador possui

### Change User Roles

**Endpoint** `PATCH /users/{userId}/roles`

**Roles**

- Admin

**Info:**

- Alterar papéis de um utilizador

### Change User Password

**Endpoint** `PATCH /users/{userId}/password`

**Roles**

- Admin

**Info:**

- Alterar password de um utilizador

### Change User Status

**Endpoint** `PATCH /users/{userId}/status`

**Roles**

- Admin

**Info:**

- Alterar status de um utilizador

### Get all Investigators

**Endpoint** `GET /users/investigators`

**Roles**

- Triator

**Info:**

- Obter todos os averiguadores de todas as areas

### Get all Supervisors

**Endpoint** `GET /users/supervisors`

**Roles**

- Triator

**Info:**

- Obter todos os supervisores de todas as areas


## Activity

### Get Activity Logs by Process

**Endpoint:** `GET /activity/process/{id}?offset={offset}&limit={limit}`

**Roles:**

- Investigator
- Supervisor
- Manager

**Info:**

- Retorna o histórico de atividades de um processo
- As atividades são geradas automaticamente pelo sistema a partir de outras ações
- Suporta paginação através de offset e limit

### Get Activity Logs By User

**Endpoint:** `GET /activity/users/{id}?offset={offset}&limit={limit}`

**Roles**

- Triator
- Investigator
- Supervisor
- Manager
- Admin

**Info:**

- Retorna o histórico de atividades de um utilizador
- As atividades são geradas automaticamente pelo sistema a partir de outras ações
- Suporta paginação através de offset e limit


## Area

### Get All Users

**Endpoint:** `GET /area`

**Roles:**

- Triator
- Admin

**Info:**

- Retorna todas as areas

### Get Area By Id

**Endpoint:** `GET /area/{id}`

**Roles:**

- Triator
- Investigator
- Supervisor
- Manager
- Admin

**Info:**

- Retorna todas as areas

### Update Area Boss

**Endpoint:** `PATCH /area/{id}/boss`

**Roles:**

- Admin

**Info:**

- Retorna todas as areas

## Prove

### Create Upload Url

**Endpoint:** `POST /process/{processId}/proves/upload-url`

**Roles:**

- Investigator
- Supervisor
- Manager

**Info:**

- Criar um url para fazer upload para o MinIO

### Create Prove

**Endpoint:** `POST /process/{processId}/proves`

**Roles:**

- Investigator
- Supervisor
- Manager

**Info:**

- Armazenar metadata da prova criada no MinIO

### Get Proves By Process

**Endpoint:** `GET /process/{processId}/proves`

**Roles:**

- Investigator
- Supervisor
- Manager

**Info:**

- Obter todas as provas associadas a um processo

### Ger Prove Access Url

**Endpoint:** `GET /process/{processId}/proves/{proveId}/url`

**Roles:**

- Investigator
- Supervisor
- Manager

**Info:**

- Obter todas as provas associadas a um processo

### Delete a Prove

**Endpoint:** `DELETE /process/{processId}/proves/{proveId}`

**Roles:**

- Investigator
- Supervisor
- Manager

**Info:**

- Apagar uma prova

## Public

### Get Admin Information

**Endpoint:** `GET /process/{processId}/proves/{proveId}`

**Roles:**

- Não necessita de role

**Info:**

- Obter informação do Admin para o contactar
