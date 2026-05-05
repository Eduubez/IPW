import { ToastType } from "../../Types/ToastType.tsx";
import { userStore } from "../Store/UserStore.tsx";
import { buildQuery, fetchApi, type ResponseApi } from "./FetchApi.tsx";
import type { TokenResponse } from "./LoginApi.tsx";
import { enqueueSnackbar } from "notistack";
import i18next from "i18next";

export type UserResponse = {
  id: number;
  name: string;
  email: string;
  areaId: number | null;
  area: string | null;
  isActive: boolean;
  roles: string[];
};

type UserRequest = {
  name: string;
  email: string;
  password: string;
  areaId: number | null;
  roles: string[];
};

type SelectRoleRequest = {
  role: string;
};

type SelectRolesResponse = {
  accessToken: TokenResponse;
  refreshToken: TokenResponse;
  role: string;
};

type UserRolesResponse = {
  roles: string[];
};

export const UsersApi = {
  selectRole,
  getUserRoles,
  create,
  getAll,
  changeUserRoles,
  changeUserPassword,
};

// choose which role to use in the current session - every user
async function selectRole(
  role: string,
): Promise<ResponseApi<SelectRolesResponse>> {
  const token = userStore.getLoginToken()?.trim();

  const response = await fetchApi<SelectRolesResponse>(
    "users/auth/select-role",
    {
      headers: {
        Authorization: "Bearer " + token,
      },
      method: "POST",
      body: JSON.stringify({ role } as SelectRoleRequest),
    },
  );

  if (response.success) {
    userStore.setActiveRole(role);
    userStore.setAccessToken(response.data.accessToken.value);
    userStore.setAccessTokenExpirationDate(response.data.accessToken.expiresAt);
    enqueueSnackbar(i18next.t("RoleSelection.roleSelected"), {
      variant: ToastType.SUCCESS,
    });
  }

  return response;
}

// see all roles from a user
async function getUserRoles(
  email: string,
): Promise<ResponseApi<UserRolesResponse>> {
  return await fetchApi<UserRolesResponse>(
    `users/roles?email=${encodeURIComponent(email)}`,
    {
      method: "GET",
    },
  );
}

// create user - Admin
async function create(input: UserRequest): Promise<ResponseApi<UserResponse>> {
  const token = userStore.getLoginToken()?.trim();

  return await fetchApi<UserResponse>("users", {
    method: "POST",
    headers: {
      Authorization: "Bearer " + token,
    },
    body: JSON.stringify(input),
  });
}

// getAll - Admin
async function getAll(
  offset = 0,
  limit = 10,
): Promise<ResponseApi<UserResponse[]>> {
  const query = buildQuery({ offset, limit });
  const token = userStore.getLoginToken()?.trim();

  return await fetchApi<UserResponse[]>(`users${query}`, {
    method: "GET",
    headers: {
      Authorization: "Bearer " + token,
    },
  });
}

// change roles - Admin
async function changeUserRoles(
  userId: number,
  roles: string[],
  areaId: number | null,
): Promise<ResponseApi<void>> {
  const token = userStore.getLoginToken()?.trim();

  return await fetchApi<void>(`users/${userId}/roles`, {
    method: "PUT",
    headers: {
      Authorization: "Bearer " + token,
    },
    body: JSON.stringify({ roles, areaId }),
  });
}


// change password - Admin
async function changeUserPassword(
  userId: number,
  newPassword: string,
): Promise<ResponseApi<void>> {
  const token = userStore.getLoginToken()?.trim();

  return await fetchApi<void>(`users/${userId}/password`, {
    method: "PUT",
    headers: {
      Authorization: "Bearer " + token,
    },
    body: JSON.stringify({ newPassword }),
  });
}
