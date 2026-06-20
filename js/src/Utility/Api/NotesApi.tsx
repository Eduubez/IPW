// Each note can be associated to a process or a prove
import { fetchApi, type ResponseApi } from "./FetchApi.tsx";
import { userStore } from "../Store/UserStore.tsx";

type NotesResponse = {
  id: number;
  processId: number | null;
  provesId: number | null;
  content: string;
  authorId: number;
  createdAt: string;
};

type NotesListResponse = {
  results: NotesResponse[];
};

type NotesRequest = {
  processId: number | null;
  proveId: number | null;
  content: string;
};

type NotesUpdateRequest = {
  id: number;
  content: string;
};

export const NotesApi = {
  createNote,
  getAllByProcessId,
  getAllByProveId,
  update,
};

// create a note associated to a process -  Triator ,Investigator, Supervisor, Manager
async function createNote(
  processId: number,
  input: NotesRequest,
): Promise<ResponseApi<NotesResponse>> {
  return await fetchApi<NotesResponse>(`process/${processId}/note`, {
    method: "POST",
    body: JSON.stringify(input),
    headers: {
      Authorization: `Bearer ${userStore.getAccessToken()}`,
    },
  });
}

// get all notes by a process Id - Investigator, Supervisor, Manager
async function getAllByProcessId(
  processId: number,
): Promise<ResponseApi<NotesResponse[]>> {
  return await fetchApi<NotesResponse[]>(`process/${processId}/note`, {
    method: "GET",
    headers: {
      Authorization: `Bearer ${userStore.getAccessToken()}`,
    },
  });
}

// get all notes by a prove Id - Investigator, Supervisor, Manager
async function getAllByProveId(
  proveId: number,
  processId: number,
): Promise<ResponseApi<NotesListResponse>> {
  return await fetchApi<NotesListResponse>(
    `process/${processId}/proves/${proveId}/note`,
    {
      method: "GET",
      headers: {
        Authorization: `Bearer ${userStore.getAccessToken()}`,
      },
    },
  );
}

// update a note - Investigator, Supervisor, Manager
async function update(
  processId: number,
  input: NotesUpdateRequest,
): Promise<ResponseApi<NotesResponse>> {
  return await fetchApi<NotesResponse>(
    `process/${processId}/note/${input.id}`,
    {
      method: "PATCH",
      body: JSON.stringify(input),
      headers: {
        Authorization: `Bearer ${userStore.getAccessToken()}`,
      },
    },
  );
}
