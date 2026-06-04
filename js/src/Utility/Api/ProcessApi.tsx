import type { UserResponse } from "./UsersApi.tsx";
import { buildQuery, fetchApi, type ResponseApi } from "./FetchApi.tsx";
import { userStore } from "../Store/UserStore.tsx";
import type { PriorityType } from "../../Components/Badge/PriorityBadge/PriorityBadge.tsx";
import { enqueueSnackbar } from "notistack";

export type ProcessResponseApi = {
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
  proves: ProvesType[];
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
  submit,
  update,
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
    enqueueSnackbar("Process created successfully", { variant: "success" });
  } else {
    enqueueSnackbar("Failed to create process", { variant: "error" });
  }
  return response;
}

// get a process by id - Investigator, Supervisor, Manager
async function getById(id: number): Promise<ResponseApi<ProcessResponse>> {
  return await fetchApi<ProcessResponse>(`process/${id}`, {
    method: "GET",
    headers: {
      Authorization: `Bearer ${userStore.getAccessToken()}`,
    },
  });
}

async function submit(id: number): Promise<ResponseApi<void>> {
  const response = await fetchApi<void>(`process/${id}/submit`, {
    method: "Post",
    headers: {
      Authorization: `Bearer ${userStore.getAccessToken()}`,
    },
  });
  if (response.success) {
    enqueueSnackbar("Process submitted successfully", { variant: "success" });
  } else {
    enqueueSnackbar("Failed to submit process", { variant: "error" });
  }

  return response;
}

// get all processes - Investigator, Supervisor, Manager
async function getAll(
  offset?: number,
  limit?: number,
): Promise<ResponseApi<ProcessResponseApi>> {
  const query = buildQuery({ offset: offset, limit });
  return await fetchApi<ProcessResponseApi>(`process${query}`, {
    method: "GET",
    headers: {
      Authorization: `Bearer ${userStore.getAccessToken()}`,
    },
  });
}

// Update info about a process - Supervisor, Manager
async function update(
  id: number,
  process: ProcessRequest,
): Promise<ResponseApi<ProcessResponse>> {
  return await fetchApi<ProcessResponse>(`process/${id}`, {
    method: "PUT",
    body: JSON.stringify(process),
  });
}

// Assign Investigator to a process - Triator
async function assignInvestigator(
  processId: number,
  investigatorId: number,
): Promise<ResponseApi<void>> {
  return await fetchApi<void>(`process/${processId}/investigator`, {
    method: "PUT",
    body: JSON.stringify({ investigatorId }),
  });
}

// Change the priority of a process - Supervisor, Manager
async function changePriority(
  processId: number,
  priority: PriorityType,
): Promise<ResponseApi<void>> {
  return await fetchApi<void>(`process/${processId}/priority`, {
    method: "PUT",
    body: JSON.stringify({ priority }),
  });
}

// Cancel a process - Manager
async function cancelProcess(id: number): Promise<ResponseApi<void>> {
  return await fetchApi<void>(`process/${id}/cancel`, {
    method: "PUT",
  });
}

// Approve a process - Supervisor, Manager
async function approve(id: number): Promise<ResponseApi<void>> {
  const response = await fetchApi<void>(`process/${id}/report/approve`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${userStore.getAccessToken()}`,
    },
  });
  if (response.success) {
    enqueueSnackbar("Process approved successfully", { variant: "success" });
  } else {
    enqueueSnackbar("Failed to approve process", { variant: "error" });
  }
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
