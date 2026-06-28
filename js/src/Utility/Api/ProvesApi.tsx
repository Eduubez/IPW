import { fetchApi, type ResponseApi } from "./FetchApi.tsx";
import { userStore } from "../Store/UserStore.tsx";

export type ProveResponse = {
    id: number;
    processId: number;
    fileName: string;
    contentType: string;
    fileSize: number;
    createdBy: number;
    authorName: string;
    createdAt: string;
};

export type ProvesResponseApi = {
    results: ProveResponse[];
};

export type CreateProveUploadUrlResponse = {
    uploadUrl: string;
    storageKey: string;
};

export type CreateProveResponse = {
    id: number;
};

export type ProveAccessUrlResponse = {
    url: string;
    contentType: string;
    fileName: string;
};

export const ProvesApi = {
    createUploadUrl,
    uploadFile,
    create,
    getByProcessId,
    getAccessUrl,
    delete: deleteProve,
};

async function createUploadUrl(
    processId: number,
    file: File,
): Promise<ResponseApi<CreateProveUploadUrlResponse>> {
    return await fetchApi<CreateProveUploadUrlResponse>(
        `process/${processId}/proves/upload-url`,
        {
            method: "POST",
            body: JSON.stringify({
                fileName: file.name,
                contentType: file.type,
                fileSize: file.size,
            }),
            headers: {
                Authorization: `Bearer ${userStore.getAccessToken()}`,
            },
        },
    );
}

async function uploadFile(
    uploadUrl: string,
    file: File,
): Promise<ResponseApi<void>> {
    const response = await fetch(uploadUrl, {
        method: "PUT",
        body: file,
        headers: {
            "Content-Type": file.type,
        },
    });

    if (!response.ok) {
        return {
            success: false,
            type: "storage-upload-failed",
            status: response.status,
            message: "Failed to upload file",
        };
    }

    return {
        success: true,
        data: undefined,
        status: response.status,
    };
}

async function create(
    processId: number,
    file: File,
    storageKey: string,
): Promise<ResponseApi<CreateProveResponse>> {
    return await fetchApi<CreateProveResponse>(`process/${processId}/proves`, {
        method: "POST",
        body: JSON.stringify({
            fileName: file.name,
            contentType: file.type,
            fileSize: file.size,
            storageKey: storageKey,
        }),
        headers: {
            Authorization: `Bearer ${userStore.getAccessToken()}`,
        },
    });
}

async function getByProcessId(
    processId: number,
): Promise<ResponseApi<ProvesResponseApi>> {
    return await fetchApi<ProvesResponseApi>(`process/${processId}/proves`, {
        method: "GET",
        headers: {
            Authorization: `Bearer ${userStore.getAccessToken()}`,
        },
    });
}

async function getAccessUrl(
    processId: number,
    proveId: number,
): Promise<ResponseApi<ProveAccessUrlResponse>> {
    return await fetchApi<ProveAccessUrlResponse>(
        `process/${processId}/proves/${proveId}/url`,
        {
            method: "GET",
            headers: {
                Authorization: `Bearer ${userStore.getAccessToken()}`,
            },
        },
    );
}

async function deleteProve(
    processId: number,
    proveId: number,
): Promise<ResponseApi<void>> {
    return await fetchApi<void>(`process/${processId}/proves/${proveId}`, {
        method: "DELETE",
        headers: {
            Authorization: `Bearer ${userStore.getAccessToken()}`,
        },
    });
}
