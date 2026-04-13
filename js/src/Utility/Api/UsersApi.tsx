import {buildQuery, fetchApi, type ResponseApi} from "./FetchApi.tsx";


export type UserResponse = {
    id: number;
    name: string;
    email: string;
    areaId: number | null;
    roles: string[];
};

type UserRequest = {
    name: string,
    password: string,
    roles: string[]
}

type UserUpdateRequest = {
    id: number;
    name: string;
    email: string;
    areaId: number | null;
    roles: string[];
}

type SelectRoleRequest = {
    role: string;
};

type UserRolesResponse = {
    roles: string[];
};


export const UsersApi = {selectRole, getUserRoles, create, getAll, update, getById}


// selectRole e getUserRoles nao estao no .md mas estao no controller

// choose which role to use in the current session - every user
async function selectRole(role: string): Promise<ResponseApi<void>> {
    return await fetchApi<void>("users/auth/select-role", {
        method: "POST",
        credentials: "include",
        body: JSON.stringify({role} as SelectRoleRequest),
    });
}

// see all roles from a user - every user
async function getUserRoles(email: string): Promise<ResponseApi<UserRolesResponse>> {
    return await fetchApi<UserRolesResponse>(`users/roles?email=${encodeURIComponent(email)}`, {
        method: "GET",
    });
}

// create user - Admin
async function create(input: UserRequest): Promise<ResponseApi<UserResponse>> {
    return await fetchApi<UserResponse>("users", {
        method: "POST",
        body: JSON.stringify(input),

    });
}

//get by id - Admin
async function getById(id:number): Promise<ResponseApi<UserResponse>>{
    return await fetchApi<UserResponse>(`users/${id}`, {
        method: "GET",

    });
}

//update - Admin
async function update(input: UserUpdateRequest): Promise<ResponseApi<UserResponse>> {
    return await fetchApi<UserResponse>("users", {
        method: "PUT",
        body: JSON.stringify(input),

    })
}

//getAll - Admin
async function getAll(offset?: number, limit?: number, areaId?: number): Promise<ResponseApi<UserResponse[]>> {
    const query = buildQuery({offset, limit, area_Id: areaId});

    return await fetchApi<UserResponse[]>(`users${query}`, {
        method: "GET",
    });
}
