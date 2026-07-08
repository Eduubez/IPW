import type { UserResponse } from "./UsersApi.tsx";
import { buildQuery, fetchApi, type ResponseApi } from "./FetchApi.tsx";
import { userStore } from "../Store/UserStore.tsx";
import type { PriorityType } from "../../Components/Badge/PriorityBadge/PriorityBadge.tsx";
import { enqueueSnackbar } from "notistack";
import type { ProveResponse } from "./ProvesApi.tsx";
import i18n from "i18next";

export type ProcessResponseApi = {
  totalCount: number;
  hasNext: boolean;
  results: ProcessResponse[];
};

export type ProcessResponse = {
  id: number;
  name: string;
  location: LocationType;
  creationDate: string;
  dueDate: string;
  priority: PriorityType;
  area: string;
  typification: string;
  triator: UserResponse;
  investigator: UserResponse;
  supervisor: UserResponse;
  state: string;
  proves: ProveResponse[];
  report: ReportType;
  notes: NotesType[];
  activity: ActivityType[];
};

export type ProcessRequest = {
  name: string;
  street: string;
  county: string;
  district: string;
  latitude: number | null;
  longitude: number | null;
  area: string;
  priority: "normal" | "with_priority" | "urgent";
  expiresAt: string;
  investigatorId: number;
  supervisorId: number;
  canBeFraud: boolean;
  note: string | null;
};

type LocationType = {
  id: number;
  district: string;
  county: string;
  street: string;
  latitude: string;
  longitude: string;
};

type ActivityType = {
  id: number;
  processId: number;
  userId: number;
  action: string;
  description: string;
  createdAt: string;
};

export type NotesType = {
  id: number;
  processId: number | null;
  proves: number | null;
  content: string;
  authorId: number;
  authorName: string;
  createdAt: string;
};

export type ReportType = {
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
  getHistory,
  submit,
  approve,
  reject,
  assignInvestigator,
  changePriority,
  cancelProcess,
};

// create a new process - Triator
async function create(
  process: ProcessRequest,
): Promise<ResponseApi<ProcessResponse>> {
  const response = await fetchApi<ProcessResponse>("process", {
    method: "POST",
    body: JSON.stringify(process),
    headers: {
      Authorization: `Bearer ${userStore.getAccessToken()}`,
    },
  });
  if (response.success) {
    enqueueSnackbar(i18n.t("CreateProcessPage.createSuccess"), { variant: "success" });
  } 
  return response;
}

// get a process by id - Investigator, Supervisor, Manager
async function getById(id: number): Promise<ResponseApi<ProcessResponse>> {
  const response =  await fetchApi<ProcessResponse>(`process/${id}`, {
    method: "GET",
    headers: {
      Authorization: `Bearer ${userStore.getAccessToken()}`,
    },
  });

  return response;
}

async function submit(id: number): Promise<ResponseApi<void>> {
  const response = await fetchApi<void>(`process/${id}/submit`, {
    method: "Post",
    headers: {
      Authorization: `Bearer ${userStore.getAccessToken()}`,
    },
  });
  return response;
}

// get all processes - Investigator, Supervisor, Manager
async function getAll(
  offset?: number,
  limit?: number,
  priority?: string,
  name?: string,
  state?: string
): Promise<ResponseApi<ProcessResponseApi>> {
  const query = buildQuery({ offset: offset, limit: limit, name: name, priority: priority, state: state });
  return await fetchApi<ProcessResponseApi>(`process${query}`, {
    method: "GET",
    headers: {
      Authorization: `Bearer ${userStore.getAccessToken()}`,
    },
  });
}

async function getHistory(
    offset?: number,
    limit?: number,
    priority?: string,
    name?: string,
): Promise<ResponseApi<ProcessResponseApi>> {
  const query = buildQuery({ offset: offset, limit: limit, history: true, priority: priority, name: name });
  return await fetchApi<ProcessResponseApi>(`process${query}`, {
    method: "GET",
    headers: {
      Authorization: `Bearer ${userStore.getAccessToken()}`,
    },
  });
}

// Assign Investigator to a process - Triator
async function assignInvestigator(
  processId: number,
  investigatorId: number,
): Promise<ResponseApi<void>> {
  return await fetchApi<void>(`process/${processId}/investigator`, {
    method: "PATCH",
    body: JSON.stringify({ investigatorId }),
  });
}

// Change the priority of a process - Supervisor, Manager
async function changePriority(
  processId: number,
  priority: PriorityType,
): Promise<ResponseApi<void>> {
  const response = await fetchApi<void>(`process/${processId}/priority`, {
    method: "PATCH",
    headers: {
      Authorization: `Bearer ${userStore.getAccessToken()}`,
    },
    body: JSON.stringify({ priority }),
  });
  return response;
}

// Cancel a process - Manager
async function cancelProcess(id: number): Promise<ResponseApi<void>> {
  const response = await fetchApi<void>(`process/${id}/cancel`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${userStore.getAccessToken()}`,
    },
  });
  return response;
}

// Approve a process - Supervisor, Manager
async function approve(id: number): Promise<ResponseApi<void>> {
  const response = await fetchApi<void>(`process/${id}/report/approve`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${userStore.getAccessToken()}`,
    },
  });
  return response;
}

// Reject a process - Supervisor, Manager

async function reject(id: number): Promise<ResponseApi<void>> {
  const response = await fetchApi<void>(`process/${id}/report/reject`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${userStore.getAccessToken()}`,
    },
  });
  if (response.success) {
    enqueueSnackbar("Process rejected successfully", { variant: "success" });
  } else {
    enqueueSnackbar("Failed to reject process", { variant: "error" });
  }
  return response;
}
