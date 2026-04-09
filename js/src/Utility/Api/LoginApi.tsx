import { type ErrorType, fetchApi} from "./FetchApi.tsx";

type LoginRequest = {
    email: string;
    password: string;
};

export type LoginResponse = {
    token: TokenResponse;
    userId: number;
    roles: string[]
}

type TokenResponse = {
    value: string,
    expiresAt: string
}


/*
* Possibilidade de trocar a autenticação de cookies por Header,
*
*
* */


export const AuthApi = {
    async login(input: LoginRequest): Promise<void | ErrorType> {
        const response = await fetchApi<LoginResponse>("users/login", {
            method: "POST",
            body: JSON.stringify(input),
        })
        //localStorage.setItem("roles", )
    },

    async logout(): Promise<void| ErrorType> {
        await fetchApi<void>("users/logout", {
            method: "POST",

        });

        //localStorage.removeItem("roles")
    },

    async refreshToken(): Promise<void| ErrorType> {
        await fetchApi<void>("users/refresh-token", {
            method: "POST",
        });
    },

    async signUp(): Promise<void| ErrorType>{
        await fetchApi<void>("users", {
            method: "POST",
        });
    }

}
