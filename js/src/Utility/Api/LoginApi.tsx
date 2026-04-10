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

export const AuthApi = { login, logout, refreshToken, signUp };

async function login(input: LoginRequest): Promise<ResponseApi<LoginResponse>> {
    return await fetchApi<LoginResponse>("users/login", {
        method: "POST",
        body: JSON.stringify(input),
    });
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

async function signUp(input: LoginRequest): Promise<ResponseApi<void>> {
    return fetchApi<void>("users", {
        method: "POST",
        body: JSON.stringify(input),
    });
}


