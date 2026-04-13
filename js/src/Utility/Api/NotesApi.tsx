// Each note can be associated to a process or a prove
import {fetchApi, type ResponseApi} from "./FetchApi.tsx";

type NotesResponse = {
    id: number;
    processId: number | null;
    proves: number | null;
    content: string;
    authorId: number;
    createdAt: string;
};


type NotesRequest = {
    processId: number | null,
    proveId: number | null
    content: string;
}

type NotesUpdateRequest = {
    id: number,
    content: string
}

export const NotesApi = {
    createOnProcess,
    getAllByProcessId,
    createOnProve,
    getAllByProveId,
    update,
    deleteById
}
// create a note associated to a process -  Triator ,Investigator, Supervisor, Manager
async function createOnProcess(input: NotesRequest): Promise<ResponseApi<NotesResponse>> {
    return await fetchApi<NotesResponse>(`notes/process/${input.processId}`, {
        method: "POST",
        body: JSON.stringify(input)
    })
}

// get all notes by a process Id - Investigator, Supervisor, Manager
async function getAllByProcessId(processId: number): Promise<ResponseApi<NotesResponse[]>> {
    return await fetchApi<NotesResponse[]>(`notes/process/${processId}`, {
        method: "GET",
    });
}

// create a note associated to a process - Triator ,Investigator, Supervisor, Manager
async function createOnProve(input: NotesRequest): Promise<ResponseApi<NotesResponse>> {
    return await fetchApi(`notes/proves/${input.proveId}`, {
        method: "POST",
        body: JSON.stringify(input)
    })
}

// get all notes by a prove Id - Investigator, Supervisor, Manager
async function getAllByProveId(proveId: number): Promise<ResponseApi<NotesResponse[]>> {
    return await fetchApi<NotesResponse[]>(`notes/proves/${proveId}`, {
        method: "GET",
    })

}

// update a note - Investigator, Supervisor, Manager
async function update(input: NotesUpdateRequest): Promise<ResponseApi<NotesResponse>> {
    return await fetchApi<NotesResponse>(`notes/${input.id}`, {
        method: "PUT",
        body: JSON.stringify(input)
    })
}

// delete a note - Investigator, Supervisor, Manager
async function deleteById (id: number): Promise<ResponseApi<void>> {
    return await fetchApi<void>(`notes/${id}`, {
        method: "DELETE",
    })
}