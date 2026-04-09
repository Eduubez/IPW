import {type ErrorType, fetchApi} from "./FetchApi.tsx";


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


export const UsersApi = {
    async selectRole(role: string): Promise<void> {
        await fetchApi<void>("users/auth/select-role", {
            method: "POST",
            credentials: "include",
            body: JSON.stringify({role} as SelectRoleRequest),
        });
    },

    async getUserRoles(email: string): Promise<UserRolesResponse | ErrorType> {
        return await fetchApi<UserRolesResponse>(`users/roles?email=${encodeURIComponent(email)}`, {
            method: "GET",
            credentials: "include",
        });
    }
}