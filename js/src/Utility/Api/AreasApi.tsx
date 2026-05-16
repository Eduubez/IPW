import { userStore } from "../Store/UserStore";
import { fetchApi, type ResponseApi } from "./FetchApi";

export type AreaResponse = {
  id: number;
  name: string;
  bossId: number | null;
  bossName: string | null;
};

export type AreaListResponse = {
  areas: AreaResponse[];
};

export const AreasApi = {
  getAll,
};

async function getAll(): Promise<ResponseApi<AreaListResponse>> {
  const token = userStore.getAccessToken()?.trim();

  return await fetchApi<AreaListResponse>("area/", {
    method: "GET",
    headers: {
      Authorization: "Bearer " + token,
    },
  });
}
