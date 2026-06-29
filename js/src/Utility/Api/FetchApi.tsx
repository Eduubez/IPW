import { notifyUser } from "../Helpers/NotifyHelper";

// talvez Colocar num .env
const API_URL = "/api/"

export type ResponseApi<T> =
    | { success: true; data: T; status: number }
    | { success: false; type: string; status: number; message: string ;errorCode:string}

export async function fetchApi<T>(
    endpoint: string,
    options: RequestInit = {}
): Promise<ResponseApi<T>> {
    const response = await fetch(`${API_URL}${endpoint}`, {
        ...options,
        credentials: "include",
        headers: {
            "Content-Type": "application/json",
            ...options.headers,
        },
    });

    if (!response.ok) {
        const error = await response
            .json()
            .catch(() => ({type: "unknown", message: "Unknown error"}));
        
        notifyUser(error.errorCode ?? "UNKNOWN_ERROR");

        return {
            success: false,
            type: error.type ?? "unknown",
            status: response.status,
            message: error.message ?? "Unknown error",
            errorCode: error.erroCode ?? "UNKNOWN_ERROR"
        };
    }

    if (response.status === 204) {
        return {
            success: true,
            data: undefined as T,
            status: response.status,
        }
    }

    const data: T = await response.json();

    return {
        success: true,
        data,
        status: response.status,
    };
}


export function buildQuery(params: Record<string, string | number | boolean | undefined>): string {
    const urlParams = new URLSearchParams();
    for (const [key, value] of Object.entries(params)) {
        if (value !== undefined && value !== null) {
            urlParams.append(key, value.toString());
        }
    }
    const query = urlParams.toString();
    return query ? `?${query}` : "";
}
