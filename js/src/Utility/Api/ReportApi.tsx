import { userStore } from "../Store/UserStore";
import { fetchApi, type ResponseApi } from "./FetchApi";

const BASE_URL = "process/";

export type Report = {
  id: number;
  processId: number;
  content: string;
  createdAt: Date;
  updatedAt: Date;
};

type CreateReportResponse = {
  id: number;
};

const createReport = async (
  processId: number,
  content: string,
): Promise<ResponseApi<CreateReportResponse>> => {
  const response = await fetchApi<CreateReportResponse>(
    `${BASE_URL}${processId}/report`,
    {
      method: "POST",
      body: JSON.stringify({
        content,
      }),
      headers: {
        Authorization: `Bearer ${userStore.getAccessToken()}`,
      },
    },
  );
  return response;
};

const getReportByProcessId = async (
  processId: number,
): Promise<ResponseApi<Report>> => {
  const response = await fetchApi<Report>(`${BASE_URL}${processId}/report`, {
    method: "GET",
    headers: {
      Authorization: `Bearer ${userStore.getAccessToken()}`,
    },
  });
  return response;
};

const updateReport = async (
  processId: number,
  content: string,
): Promise<ResponseApi<CreateReportResponse>> => {
  const response = await fetchApi<CreateReportResponse>(
    `${BASE_URL}${processId}/report`,
    {
      method: "PUT",
      body: JSON.stringify({
        content,
      }),
      headers: {
        Authorization: `Bearer ${userStore.getAccessToken()}`,
      },
    },
  );
  return response;
};

const approveReport = async (processId: number): Promise<ResponseApi<void>> => {
  const response = await fetchApi<void>(
    `${BASE_URL}${processId}/report/approve`,
    {
      method: "POST",
      headers: {
        Authorization: `Bearer ${userStore.getAccessToken()}`,
      },
    },
  );
  return response;
};

const rejectReport = async (processId: number): Promise<ResponseApi<void>> => {
  const response = await fetchApi<void>(
    `${BASE_URL}${processId}/report/reject`,
    {
      method: "POST",
      headers: {
        Authorization: `Bearer ${userStore.getAccessToken()}`,
      },
    },
  );
  return response;
};

export const ReportApi = {
  createReport,
  getReportByProcessId,
  updateReport,
  approveReport,
  rejectReport,
};
