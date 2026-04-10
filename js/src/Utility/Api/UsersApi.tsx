import {fetchApi, type ResponseApi} from "./FetchApi.tsx";


export type CreateUserResponse = {
    id: number;
    name: string;
    email: string;
    areaId: number | null;
    roles: string[];
};

type SelectRoleRequest = {
    role: string;
};

type UserRolesResponse = {
    roles: string[];
};



export const UsersApi = {selectRole, getUserRoles}

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
