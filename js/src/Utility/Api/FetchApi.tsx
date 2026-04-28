// talvez Colocar num .env
const API_URL = "/api/"

export type ResponseApi<T> =
    | { success: true; data: T; status: number }
    | { success: false; type: string; status: number; message: string }

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

        return {
            success: false,
            type: error.type ?? "unknown",
            status: response.status,
            message: error.message ?? "Unknown error",
        };
    }

    const data: T = await response.json();

    return {
        success: true,
        data,
        status: response.status,
    };
}


export function buildQuery(params: Record<string, string | number | undefined>): string {
    const urlParams = new URLSearchParams();
    for (const [key, value] of Object.entries(params)) {
        if (value !== undefined && value !== null) {
            urlParams.append(key, value.toString());
        }
    }
    const query = urlParams.toString();
    return query ? `?${query}` : "";
}
