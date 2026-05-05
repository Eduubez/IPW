import { userStore } from "../Store/UserStore";
import { fetchApi, type ResponseApi } from "./FetchApi";
import { enqueueSnackbar } from "notistack";
import i18next from 'i18next'


export type UserProcessHistoryResponse = {
    userId: number;
    process: number[];
};
export type  AreaProcessHistoryResponse = {
    areaId: number;
    process: number[];
}


async function getUserProcessHistory(userId: number): Promise<ResponseApi<UserProcessHistoryResponse>> {
    return await fetchApi<UserProcessHistoryResponse>(`history/${userId}`, {
        method: "GET",
        headers: {
            "Authorization": `Bearer ${userStore.getAccessToken()}`,
        },
    });
}

async function getAreaHistory(areaId: number): Promise<ResponseApi<AreaProcessHistoryResponse>> {
    const response =await fetchApi<AreaProcessHistoryResponse>(`history/area/${areaId}`, {
        method: "GET",
        headers: {
            "Authorization": `Bearer ${userStore.getAccessToken()}`,
        },
    });
    if(!response.success) {
        enqueueSnackbar(i18next.t("HistoryPage.FetchError"), { variant: "error" });
    }
    return response;
}

export const HistoryApi = { getUserProcessHistory, getAreaHistory };