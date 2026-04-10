import type {CreateUserResponse} from "./UsersApi.tsx";
import {fetchApi, type ResponseApi} from "./FetchApi.tsx";

export type ProcessResponse = {
    id: number;
    name: string;
    location: LocationType;
    creationDate: string;
    dueDate: string;
    priority: number;
    area: string;
    typification: string;
    triator: CreateUserResponse;
    investigator: CreateUserResponse;
    supervisor: CreateUserResponse;
    state: string;
    proves: ProvesType;
    report: ReportType;
    notes: NotesType;
    activity: ActivityType;
};

type ProcessRequest = {
    name: string,
    street: string,
    county: string,
    district: string,
    latitude: number | null,
    longitude: number | null,
    area: string,
    priority: string,
    expiresAt: string,
    investigatorId: number,
    supervisorId: number,
    canBeFraud: boolean,
    note?: string
}

type LocationType = {
    id: number;
    district: string;
    county: string;
    street: string;
    latitude: string;
    longitude: string;
};

type ProvesType = {
    id: number;
    processId: number;
    fileName: string;
    fileType: string;
    fileUrl: string;
    createdAt: string;
};

type ActivityType = {
    id: number;
    processId: number;
    userId: number;
    action: string;
    description: string;
    createdAt: string;
};

type NotesType = {
    id: number;
    processId: number | null;
    proves: number | null;
    content: string;
    authorId: number;
    createdAt: string;
};

type ReportType = {
    id: number;
    processId: number;
    content: string;
    createdAt: string;
    updatedAt: string;
};


export const ProcessApi = {create, getById, getAll}

    async function create(process: ProcessRequest): Promise<ResponseApi<ProcessResponse>> {
        return await fetchApi<ProcessResponse>("processes", {
            method: "POST",
            body: JSON.stringify(process),
        });
    }

    async function getById(id:number): Promise<ResponseApi<ProcessResponse>>{
        return await fetchApi<ProcessResponse>(`processes/${id}`, {
            method: "GET",
        });
    }

    async function getAll(offset?: number, limit?: number, areaId?: number): Promise<ResponseApi<ProcessResponse[]>> {
        const params = new URLSearchParams();
        if (offset !== undefined) params.append("offset", offset.toString());
        if (limit  !== undefined) params.append("limit",  limit.toString());
        if (areaId !== undefined) params.append("area_id", areaId.toString());

        const query = params.toString() ? `?${params.toString()}` : "";

        return await fetchApi<ProcessResponse[]>(`processes${query}`, {
            method: "GET",
        });
    }