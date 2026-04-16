call clean_database();
call reset_database();
-- =========================
-- AREA
-- =========================
insert into Area (name) values
('Lisboa'),
('Porto'),
('Setúbal');

-- =========================
-- ROLE
-- =========================
insert into Role (name) values
('admin'),
('triator'),
('investigator'),
('supervisor'),
('manager');

-- =========================
-- PERMISSION
-- =========================
insert into Permission (name) values
('create_process'),
('add_content_to_process'),
('add_notes'),
('change_permissions'),
('validate_process'),
('change_priority'),
('update_process');

-- =========================
-- ROLE_PERMISSION
-- =========================
insert into Role_Permission (role_name, permission_name) values
('triator', 'create_process'),

('investigator', 'add_content_to_process'),
('investigator', 'add_notes'),

('supervisor', 'add_content_to_process'),
('supervisor', 'add_notes'),
('supervisor', 'change_priority'),
('supervisor', 'update_process'),

('manager', 'add_content_to_process'),
('manager', 'add_notes'),
('manager', 'validate_process'),
('manager', 'change_priority'),
('manager', 'update_process'),

('admin', 'change_permissions');

-- =========================
-- USERS
-- =========================
insert into Users (name, email, password_hash, area_id) values
('Administrador', 'admin@ipw.pt', 'hash_admin_123', null),
('Tiago Triator', 'triator@ipw.pt', 'hash_triator_123', null),
('Ines Investigadora', 'ines@ipw.pt', 'hash_ines_123', 1),
('Carlos Investigador', 'carlos@ipw.pt', 'hash_carlos_123', 2),
('Sofia Supervisora', 'sofia@ipw.pt', 'hash_sofia_123', 1),
('Rafaela Gestora', 'rafaela@ipw.pt', 'hash_rafaela_123', null),
('Marta Supervisora', 'marta@ipw.pt', 'hash_marta_123', 2),
('Joao Investigador', 'joao@ipw.pt', 'hash_joao_123', 1);

-- =========================
-- USER_ROLE
-- =========================
insert into User_Role (user_id, role_name) values
(1, 'admin'),
(2, 'triator'),
(3, 'investigator'),
(4, 'investigator'),
(5, 'supervisor'),
(6, 'manager'),
(7, 'supervisor'),
(8, 'investigator');

-- =========================
-- TOKEN
-- =========================
insert into Token (token, user_id, expires_at) values
('token_admin_001', 1, current_timestamp + interval '7 days'),
('token_triator_001', 2, current_timestamp + interval '7 days'),
('token_investigator_001', 3, current_timestamp + interval '7 days'),
('token_manager_001', 6, current_timestamp + interval '7 days');

-- =========================
-- TYPIFICATION
-- =========================
insert into Typification (name, honorary) values
('Automovel', 150.00),
('Acidente de Trabalho', 200.00),
('Patrimonial', 125.00);

-- =========================
-- PROCESS
-- =========================
insert into Process (
    name,
    location,
    due_date,
    priority,
    insurance_company,
    area_id,
    typification_id,
    triator_id,
    investigator_id,
    supervisor_id
) values
(
'Sinistro automovel - Jose Silva',
'Lisboa - Avenida da Liberdade',
current_timestamp + interval '3 days',
'high',
'Fidelidade',
1,
1,
2,
3,
5
),
(
'Acidente de trabalho - Armazem Norte',
'Porto - Rua do Comercio',
current_timestamp + interval '5 days',
'medium',
'Allianz',
2,
2,
2,
4,
7
);

-- =========================
-- STATE
-- =========================
insert into State (process_id, name, start_date, end_date) values
(1, 'assigned', current_timestamp - interval '2 days', current_timestamp - interval '1 day'),
(1, 'on_going', current_timestamp - interval '1 day', null),

(2, 'assigned', current_timestamp - interval '1 day', current_timestamp - interval '12 hours'),
(2, 'waiting_approval_supervisor', current_timestamp - interval '12 hours', null);

-- =========================
-- REPORT
-- =========================
insert into Report (process_id, content, created_at, updated_at) values
(
1,
'Veiculo com danos frontais compativeis com embate em objeto fixo. Foram recolhidas fotografias do local e do veiculo. O averiguador encontrou indicios coerentes com a descricao inicial do sinistro.',
current_timestamp - interval '1 day',
current_timestamp - interval '2 hours'
),
(
2,
'Foram recolhidos depoimentos preliminares e observados os elementos de seguranca existentes no local. O processo aguarda validacao do supervisor antes de seguir para o gestor.',
current_timestamp - interval '10 hours',
current_timestamp - interval '3 hours'
);

-- =========================
-- PROVES
-- =========================
insert into Proves (process_id, file_name, file_type, file_url, created_at) values
(
1,
'foto_frontal_veiculo.jpg',
'image/jpeg',
'https://storage.example.com/process-1/foto_frontal_veiculo.jpg',
current_timestamp - interval '20 hours'
),
(
1,
'foto_local_acidente.jpg',
'image/jpeg',
'https://storage.example.com/process-1/foto_local_acidente.jpg',
current_timestamp - interval '19 hours'
),
(
2,
'declaracao_inicial.pdf',
'application/pdf',
'https://storage.example.com/process-2/declaracao_inicial.pdf',
current_timestamp - interval '8 hours'
);

-- =========================
-- DILIGENCIA
-- =========================
insert into Diligencia (
    process_id,
    investigator_id,
    description,
    status,
    created_at,
    due_date,
    completed_at
) values
(
1,
8,
'Deslocacao ao local para recolha adicional de fotografias da via e dos danos laterais.',
'completed',
current_timestamp - interval '18 hours',
current_timestamp - interval '15 hours',
current_timestamp - interval '16 hours'
),
(
2,
4,
'Confirmar junto da entidade patronal os registos de entrada e os equipamentos utilizados no momento do acidente.',
'on_going',
current_timestamp - interval '6 hours',
current_timestamp + interval '1 day',
null
);


SELECT id as processId, triator_id
FROM Process
WHERE triator_id = 2

Select * from users;

call sample_data();