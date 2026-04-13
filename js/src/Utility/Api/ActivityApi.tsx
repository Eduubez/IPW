import {buildQuery, fetchApi, type ResponseApi} from "./FetchApi.tsx";


type ActivityResponse = {
    id: number,
    processId: number,
    userId: number,
    action: number,
    description: string,
    createdAt: string

}

export const ActivityApi = {getActivityByProcess, getActivityByUser}



async function getActivityByProcess(processId: number, offset:number, limit: number ): Promise<ResponseApi<ActivityResponse[]>> {
    const query = buildQuery({offset, limit})
    return await fetchApi<ActivityResponse[]>(`activities/process/${processId}${query}`, {
        method: "GET",
    })
}

// n esta no .md
async function getActivityByUser(userId: number, offset:number, limit: number): Promise<ResponseApi<ActivityResponse[]>> {
    const query = buildQuery({offset, limit})

    return await fetchApi<ActivityResponse[]>(`activities/user/${userId}${query}`, {
        method: "GET",
    })
}