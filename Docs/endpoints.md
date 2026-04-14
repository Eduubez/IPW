# API endpoints - IPW

Cada endpoint inclui:

- método HTTP
- roles que podem aceder
- Informação extra

## Processes

### Create Process

**Endpoint:** `POST /processes`

**Roles:**

- Triator

**Info:**

- Cria um novo processo
- Prioridade e seguradora são obrigatórios
- Investigator e supervisor podem ou não ser definidos logo na criação
- O processo fica com estado inicial `not_assigned` ou `assigned` dependendo de já existir investigator e supervisor

### Get Process By Id

**Endpoint:** `GET /processes/{id}`

**Roles:**

- Investigator
- Supervisor
- Manager
- Triator

**Info:**

- Triator: pode ver os processos criados por si
- Investigator:
    - Só pode ver processos atribuídos a si
    - Ou onde tenha tasks atribuídas
- Supervisor: só pode ver processos da sua área
- Manager: pode ver todos os processos

### Get Processes

**Endpoint:** `GET /processes?offset={offset}&limit={limit}&area_id={area_id}`

**Roles:**

- Investigator
- Supervisor
- Manager
- Triator

**Info:**

- Investigator: vê apenas os seus processos
- Supervisor: vê apenas processos da sua área
- Manager: vê todos os processos
- Triator: vê apenas processos criados por si
- Suporta paginação através de offset e limit
- Pode filtrar por área

### Update Process

**Endpoint:** `PUT /processes/{id}`

**Roles**:

- Supervisor
- Manager

**Info:**

- Supervisor: só pode editar processos da sua área
- Manager: pode editar qualquer processo
- este endpoint serve para atualizar campos gerais do processo
- não serve para editar report nem notes

### Assign Investigator (and Supervisor)

**Endpoint:** `PUT /processes/{id}/investigator`

**Roles:**

- Triator

**Info:**

- Apenas o triator pode atribuir investigator
- O investigator deve pertencer à mesma área do processo, porém pode haver exceções
- Se investigator e supervisor ficarem definidos, o processo passa para `assigned`

### Change Priority

**Endpoint:** `PUT /processes/{id}/priority`

**Roles:**

- Supervisor
- Manager

**Info:**

- Supervisor: só pode alterar prioridade em processos da sua área
- Manager: pode alterar prioridade em qualquer processo

### Cancel Process

**Endpoint:** `PUT /processes/{id}/cancel`

**Roles:**

- Manager

**Info:**

- Apenas manager pode cancelar processos
- O estado passa para `canceled`

## Report

### Get Report

**Endpoint:** `GET /report/processes/{id}`

**Roles:**

- Investigator
- Supervisor
- Manager

**Info:**

- Investigator:
    - Pode ver se for o responsável pelo processo
    - Ou se tiver task atribuída

### Create / Update Report

**Endpoint:** `PUT /report/processes/{id}`

**Roles:**

- Investigator

**Info:**

- Investigator:
    - Pode editar se for o responsável do processo
    - ou se tiver task atribuída

### Submit Report

**Endpoint:** `POST /report/processes/{id}/submit`

**Roles:**

- Investigator

**Info:**

- Apenas o investigator do processo pode submeter
- Ao submeter:
  - Estado passa para `waiting_approval_supervisor`

### Approve Report

**Endpoint:** `POST /process/{id}/report/approve`

**Roles:**

- Supervisor
- Manager

**Info:**

- Apenas supervisor da área do processo e manager
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

- Apenas supervisor da área do processo e manager
- Deve incluir comentário
- Ao ser rejeitado pelo supervisor:
    - Estado passa para `rejected_by_supervisor`
    - Processo volta ao investigator
- Ao ser rejeitado pelo manager:
    - Estado passa para `rejected_by_manager`
    - Processo volta ao supervisor
    
  
## Notes

### Get Notes by Process

**Endpoint:** `GET /notes/process/{id}`

**Roles:**

- Investigator
- Supervisor
- Manager

**Info:**

- Retorna todas as notas associadas a um processo

### Create Note on Process

**Endpoint:** `POST /notes/process/{id}`

**Roles:**

- Investigator
- Supervisor
- Manager

**Info:**

- Cria uma nota associada ao processo
- O autor é automaticamente o utilizador autenticado

### Get Notes by Prove

**Endpoint:** `GET /notes/prove/{id}`

**Roles:**

- Investigator
- Supervisor
- Manager

**Info:**

- Retorna notas associadas a um anexo

### Create Note on Prove

**Endpoint:** `POST /notes/prove/{id}`

**Roles:**

- Investigator
- Supervisor
- Manager

**Info:**

- Cria uma nota associada a um anexo

### Update Note

**Endpoint:** `PUT /notes/{id}`

**Roles:**

- Investigator
- Supervisor
- Manager

**Info:**

- O autor pode editar a sua própria nota
- Supervisor e Manager podem editar qualquer nota

### Delete Note

**Endpoint:** `DELETE /notes/{id}`

**Roles:**

- Investigator
- Supervisor
- Manager

**Info:**

- O autor pode apagar a sua nota
- Manager pode apagar qualquer nota

## Users

### Get Users

**Endpoint:** `GET /users?offset={offset}&limit={limit}&area_id={area_id}`

**Roles:**

- Admin

**Info:**

- Retorna todos os utilizadores
- Suporta paginação através de offset e limit
- Pode filtrar por área

### Get User By Id

**Endpoint:** `GET /users/{id}`

**Roles:**

- Admin

**Info:**

- Retorna informação de um utilizador específico

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

### Update User

**Endpoint:** `PUT /users/{id}`

**Roles:**

- Admin

**Info:**

- Apenas admin pode editar utilizadores
- Pode alterar:
    - Nome
    - Email
    - Password
    - Roles
    - Área
    - isActive


### Select User Role

**Endpoint** `Post /users/auth/select-role`

**Roles**

- Every Role

**Info:**

- Escolher que role o utilizador irá utilizar na sessão corrente


### Get User Roles

**Endpoint** `GET /users/roles`

**Roles**

- Every Role

**Info:**

- Retorna as roles que um utilizador possui 

## Process History

### Get the history of processes of an user

**Endpoint:** `GET /history/users/{id}?offset={offset}&limit={limit}`

**Roles**

- Triator
- Investigator
- Manager

**Info:**

- Retorna o histórico de processos 
- Todos os processos terminados e a decorrer são armazenados
- Suporta paginação através de offset e limit

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

- Every Role

**Info:**

- Retorna o histórico de atividades de um utilizador
- As atividades são geradas automaticamente pelo sistema a partir de outras ações
- Suporta paginação através de offset e limit

## Tasks (Diligencia) (Opcional)

### Get Tasks

**Endpoint:** `GET /tasks/processes/{id}`

**Roles:**

- Investigator
- Supervisor
- Manager

**Info:**

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

**Info:**

- Investigator:
    - Pode ver se for responsável pela task
    - Ou se for o investigator principal do processo
- Supervisor:
    - Apenas tasks de processos da sua área
- Manager:
    - Acesso total

### Create Task

**Endpoint:** `POST /tasks/processes/{id}`

**Roles:**

- Investigator

**Info:**

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

**Info:**

- Investigator:
    - Pode atualizar se for o responsável pela task
- Supervisor:
    - Apenas tasks de processos da sua área
- Manager:
    - Acesso total
- Pode atualizar:
    - Description (resumo do trabalho realizado)
    - Status (`pending`, `on_going`, `completed`, `canceled`)
