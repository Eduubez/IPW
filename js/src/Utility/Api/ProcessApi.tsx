import type {CreateUserResponse} from "./UsersApi.tsx";
import {buildQuery, fetchApi, type ResponseApi} from "./FetchApi.tsx";

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

export type ProcessRequest = {
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


export const ProcessApi = {
    create,
    getById,
    getAll,
    update,
    approveSupervisor,
    rejectSupervisor,
    approveManager,
    rejectManager,
    assignInvestigator,
    changePriority,
    cancelProcess,

}

// create a new process - Triator
async function create(process: ProcessRequest): Promise<ResponseApi<ProcessResponse>> {
    return await fetchApi<ProcessResponse>("processes", {
        method: "POST",
        body: JSON.stringify(process),
    });
}

// get a process by id - Investigator, Supervisor, Manager
async function getById(id: number): Promise<ResponseApi<ProcessResponse>> {
    return await fetchApi<ProcessResponse>(`processes/${id}`, {
        method: "GET",
    });
}

// get all processes - Investigator, Supervisor, Manager
async function getAll(offset?: number, limit?: number, areaId?: number): Promise<ResponseApi<ProcessResponse[]>> {

    const query = buildQuery({offset: offset, limit, area_id: areaId});
    return await fetchApi<ProcessResponse[]>(`processes${query}`, {
        method: "GET",
    });
}

// Update info about a process - Supervisor, Manager
async function update(id: number, process: ProcessRequest): Promise<ResponseApi<ProcessResponse>> {
    return await fetchApi<ProcessResponse>(`processes/${id}`, {
        method: "PUT",
        body: JSON.stringify(process),
    });
}

// Assign Investigator to a process - Triator
async function assignInvestigator(processId: number, investigatorId: number): Promise<ResponseApi<void>> {
    return await fetchApi<void>(`processes/${processId}/investigator`, {
        method: "PUT",
        body: JSON.stringify({investigatorId}),
    });
}

// Change the priority of a process - Supervisor, Manager
async function changePriority(processId: number, priority: string): Promise<ResponseApi<void>> {
    return await fetchApi<void>(`processes/${processId}/priority`, {
        method: "PUT",
        body: JSON.stringify({priority}),
    });
}

// Cancel a process - Manager
async function cancelProcess(id: number): Promise<ResponseApi<void>> {
    return await fetchApi<void>(`processes/${id}/cancel`, {
        method: "PUT",
    });
}

// Approve a process in Supervisor view - Supervisor
async function approveSupervisor(id: number): Promise<ResponseApi<void>> {
    return await fetchApi<void>(`processes/${id}/report/approve-supervisor`, {
        method: "POST",
    });
}

// Reject a process in Supervisor view - Supervisor
async function rejectSupervisor(id: number): Promise<ResponseApi<void>> {
    return await fetchApi<void>(`processes/${id}/report/reject-supervisor`, {
        method: "POST",
    });
}

// Approve a process in Manager view - Manager

async function approveManager(id: number): Promise<ResponseApi<void>> {
    return await fetchApi<void>(`processes/${id}/report/approve-manager`, {
        method: "POST",
    });
}

// Reject a process in Manager view - Manager

async function rejectManager(id: number): Promise<ResponseApi<void>> {
    return await fetchApi<void>(`processes/${id}/report/reject-manager`, {
        method: "POST",
    });
}
