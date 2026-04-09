// talvez Colocar num .env
const API_URL = "http://localhost:8080/"



export type ResponseApi = ErrorType | SuccessType

export type ErrorType = {
    type: string,
    status: number,
    message: string
}

export type SuccessType = {
    status: number,
    data: any
}

export async function fetchApi<T>(
    endpoint: string,
    options: RequestInit = {}
): Promise<T| ErrorType> {
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
            .catch(() => ({ type: "unknown", message: "Unknown error" }));
        return {type : error.status, status: error.type, message: error.message}
    }

    if (response.status === 204) return undefined as T;

    return response.json();
}
