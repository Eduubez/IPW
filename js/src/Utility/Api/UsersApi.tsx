import {fetchApi, type ResponseApi} from "./FetchApi.tsx";


export type CreateUserResponse = {
    id: number;
    name: string;
    email: string;
    areaId: number | null;
    roles: string[];
};

type CreateUserRequest = {
    name: string,
    password: string,
    roles: string[]
}
// ideia - por se tratar de um empresa, quando criamos o utilizador o email é automaticamente criado tendo em conta o nome do utilizaodr

type SelectRoleRequest = {
    role: string;
};

type UserRolesResponse = {
    roles: string[];
};



export const UsersApi = {selectRole, getUserRoles, create}


 // selectRole e getUserRoles nao estao no .md mas estao no controller
async function selectRole(role: string): Promise<ResponseApi<void>> {
    return await fetchApi<void>("users/auth/select-role", {
        method: "POST",
        credentials: "include",
        body: JSON.stringify({role} as SelectRoleRequest),
    });
}

async function getUserRoles(email: string): Promise < ResponseApi < UserRolesResponse >> {
    return await fetchApi<UserRolesResponse>(`users/roles?email=${encodeURIComponent(email)}`, {
        method: "GET",
        credentials: "include",
    });
}

async function create(input: CreateUserRequest): Promise<ResponseApi<CreateUserResponse>> {
    return await fetchApi<CreateUserResponse>("users", {
        method: "POST",
        body: JSON.stringify(input),
    });
}

//get by id

//update

//getAll
