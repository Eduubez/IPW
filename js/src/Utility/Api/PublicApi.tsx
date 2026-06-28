import { fetchApi } from "../Api/FetchApi.tsx";
type AdminInformationResponse = {
    name: string;
    email: string;
}


export const adminInformation = async () => {
    return await fetchApi<AdminInformationResponse>(`public/contacts`, {
        method: "GET",
    });
}


export const PublicApi = {
    adminInformation
}