# API endpoints - IPW

Cada endpoint inclui:
- método HTTP
- roles que podem aceder
- regras de negócio


## Processes

### Create Process

**Endpoint:** `POST /processes`

**Roles:**
- Triator

**Regras:**
- Cria um novo processo
- Prioridade e seguradora são obrigatórios
- Investigator e supervisor podem ou não ser definidos logo na criação
- O processo fica com estado inicial `not_assigned` ou `assigned` dependendo de já existir investigator


### Get Process By Id

**Endpoint:** `GET /processes/{id}`

**Roles:**
- Investigator
- Supervisor
- Manager
- Triator

**Regras:**
- Triator: pode ver os processos criados por si
- Investigator: 
  - Só pode ver processos atribuídos a si
  - Ou onde tenha tasks atribuídas
- Supervisor: só pode ver processos da sua área
- Manager: pode ver todos os processos


### Get Processes

**Endpoint:** `GET /processes`

**Roles:**
- Investigator
- Supervisor
- Manager
- Triator

**Regras:**
- Investigator: vê apenas os seus processos
- Supervisor: vê apenas processos da sua área
- Manager: vê todos os processos
- Triator: vê apenas processos criados por si


### Update Process

**Endpoint:** `PUT /processes/{id}`

**Roles**:
- Supervisor
- Manager

**Regras:**
- Supervisor: só pode editar processos da sua área
- Manager: pode editar qualquer processo
- este endpoint serve para atualizar campos gerais do processo
- não serve para editar report nem notes


### Assign Investigator

**Endpoint:** `PUT /processes/{id}/investigator`

**Roles:**
- Triator

**Regras:**
- Apenas o triator pode atribuir investigator
- O investigator deve pertencer à mesma área do processo, porém pode haver exceções
- Ao atribuir investigator, o estado passa para `assigned`


### Change Priority

**Endpoint:** `PUT /processes/{id}/priority`

**Roles:**
- Supervisor
- Manager

**Regras:**
- Supervisor: só pode alterar prioridade em processos da sua área
- Manager: pode alterar prioridade em qualquer processo


### Cancel Process

**Endpoint:** `PUT /processes/{id}/cancel`

**Roles:**
- Manager

**Regras:**
- Apenas manager pode cancelar processos
- O estado passa para `canceled`


## Report

### Get Report

**Endpoint:** `GET /processes/{id}/report`

**Roles:**
- Investigator
- Supervisor
- Manager

**Regras:**
- Investigator: 
  - Pode ver se for o responsável pelo processo
  - Ou se tiver task atribuída
- Supervisor: apenas processos da sua área
- Manager: acesso total


### Create / Update Report

**Endpoint:** `PUT /processes/{id}/report`

**Roles:**
- Investigator

**Regras:**
- Investigator: 
  - Pode editar se for o responsável do processo
  - ou se tiver task atribuída
- Este endpoint cria o report caso ainda não exista
- Se já existir, atualiza o content


### Submit Report

**Endpoint:** `POST /processes/{id}/report/submit`

**Roles:**
- Investigator

**Regras:**
- Apenas o investigator do processo pode submeter
- Ao submeter:
    - Estado passa para `waiting_approval_supervisor`
    - Apenas pode voltar a editar após rejeição


### Approve Report (Supervisor)

**Endpoint:** `POST /processes/{id}/report/approve-supervisor`

**Roles:**
- Supervisor

**Regras:**
- Apenas supervisor da área do processo
- Ao aprovar:
    - Estado passa para `waiting_approval_manager`


### Reject Report (Supervisor)

**Endpoint:** `POST /processes/{id}/report/reject-supervisor`

**Roles:**
- Supervisor

**Regras:**
- Apenas supervisor da área do processo
- Deve incluir comentário
- Ao rejeitar:
    - Estado passa para `rejected_by_supervisor`
    - Processo volta ao investigator


### Approve Report (Manager)

**Endpoint:** `POST /processes/{id}/report/approve-manager`

**Roles:**
- Manager

**Regras:**
- Manager pode aprovar qualquer report
- Ao aprovar:
    - Estado passa para `approved_by_manager`


### Reject Report (Manager)

**Endpoint:** `POST /processes/{id}/report/reject-manager`

**Roles:**
- Manager

**Regras:**
- Manager pode rejeitar qualquer report
- Deve incluir comentário
- Ao rejeitar:
    - Estado passa para `rejected_by_manager`
    - Processo volta ao supervisor 



## Tasks (Diligencia)

### Get Tasks

**Endpoint:** `GET /processes/{id}/tasks`

**Roles:**
- Investigator
- Supervisor
- Manager

**Regras:**
- Investigator:
    - Pode ver tasks se for o responsável pelo processo
    - Ou se tiver uma task atribuída nesse processo
- Supervisor:
    - Apenas processos da sua área
- Manager:
    - Acesso total


### Get Task By Id

**Endpoint:** `GET /tasks/{taskId}`

**Roles:**
- Investigator
- Supervisor
- Manager

**Regras:**
- Investigator:
    - Pode ver se for responsável pela task
    - Ou se for o investigator principal do processo
- Supervisor:
    - Apenas tasks de processos da sua área
- Manager:
    - Acesso total


### Create Task

**Endpoint:** `POST /processes/{id}/tasks`

**Roles:**
- Investigator

**Regras:**
- Apenas o investigator responsável pelo processo pode criar tasks
- A task deve ser atribuída a outro investigator
- A task representa um pedido de apoio no terreno
- A task fica com estado inicial `pending`


### Update Task

**Endpoint:** `PUT /tasks/{taskId}`

**Roles:**
- Investigator
- Supervisor
- Manager

**Regras:**
- Investigator:
    - Pode atualizar se for o responsável pela task
- Supervisor:
    - Apenas tasks de processos da sua área
- Manager:
    - Acesso total
- Pode atualizar:
    - Description (resumo do trabalho realizado)
    - Status (`pending`, `on_going`, `completed`, `canceled`)



## Dashboards

### Investigator Dashboard

**Endpoint:** `GET /dashboard/investigator`

**Roles:**
- Investigator

**Regras:**
- Retorna apenas dados do investigator 

**Inclui:**
- Processos em curso (`on_going`)
- Processos a aguardar aprovação do supervisor
- Processos fechados
- Número de processos por mês


### Supervisor Dashboard

**Endpoint:** `GET /dashboard/supervisor`

**Roles:**
- Supervisor

**Regras:**
- Retorna apenas dados da área do supervisor

**Inclui:**
- Processos da área
- Processos em curso
- Processos a aguardar aprovação do manager
- Processos fechados
- Número de processos por mês


### Manager Dashboard

**Endpoint:** `GET /dashboard/manager`

**Roles:**
- Manager

**Regras:**
- Acesso a todos os processos

**Inclui:**
- Todos os processos
- Processos em curso
- Processos fechados
- Processos por área
- Número de processos por mês



## Users

### Get Users

**Endpoint:** `GET /users`

**Roles:**
- Admin

**Regras:**
- Retorna todos os utilizadores


### Get User By Id

**Endpoint:** `GET /users/{id}`

**Roles:**
- Admin

**Regras:**
- Retorna informação de um utilizador específico


### Create User

**Endpoint:** `POST /users`

**Roles:**
- Admin

**Regras:**
- Apenas admin pode criar utilizadores
- Deve definir:
    - Nome
    - Email
    - Password
    - Roles
    - Área (se aplicável)


### Update User

**Endpoint:** `PUT /users/{id}`

**Roles:**
- Admin

**Regras:**
- Apenas admin pode editar utilizadores
- Pode alterar:
    - Nome
    - Email
    - Password
    - Roles
    - Área


### Delete User

**Endpoint:** `DELETE /users/{id}`

**Roles:**
- Admin

**Regras:**
- Apenas admin pode remover utilizadores

