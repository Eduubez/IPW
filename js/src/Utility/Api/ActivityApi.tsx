import { userStore } from "../Store/UserStore.tsx";
import {buildQuery, fetchApi, type ResponseApi} from "./FetchApi.tsx";


export type ActivityResponse = {
    id: number,
    processId: number,
    userId: number,
    action: string,
    description: string,
    createdAt: string

}

export const ActivityApi = {getActivityByProcess, getActivityByUser}



async function getActivityByProcess(processId: number, offset:number, limit: number ): Promise<ResponseApi<ActivityResponse[]>> {
    const query = buildQuery({offset, limit})
    return await fetchApi<ActivityResponse[]>(`activity/process/${processId}${query}`, {
        method: "GET",
    })
}

async function getActivityByUser(userId: number, offset:number, limit: number): Promise<ResponseApi<ActivityResponse[]>> {
    const query = buildQuery({offset, limit})

    return await fetchApi<ActivityResponse[]>(`activity/users/${userId}${query}`, {
        method: "GET",
        headers: {
            "Authorization": `Bearer ${userStore.getAccessToken()}`
        }
    })
}