import {fetchApi, type ResponseApi} from "./FetchApi.tsx";

type LoginRequest = {
    email: string;
    password: string;
};

export type LoginResponse = {
    token: TokenResponse;
    userId: number;
    roles: string[];
};

type TokenResponse = {
    value: string;
    expiresAt: string;
};

export const AuthApi = { login, logout, refreshToken };

async function login(input: LoginRequest): Promise<ResponseApi<LoginResponse>> {
    const response = await fetchApi<LoginResponse>("users/login", {
        method: "POST",
        body: JSON.stringify(input),
    });
    if(response.success) {
        localStorage.setItem("loggedInto", "true");
        localStorage.setItem("roles", JSON.stringify(response.data.roles));
    }
    return response;
}

async function logout(): Promise<ResponseApi<void>> {
    return fetchApi<void>("users/logout", {
        method: "POST",
    });
}

async function refreshToken(): Promise<ResponseApi<void>> {
    return await  fetchApi<void>("users/refresh-token", {
        method: "POST",
    });
}



