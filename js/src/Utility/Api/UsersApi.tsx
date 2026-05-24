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
export type UserProfileResponse = {
  id: number;
  name: string;
  email: string;
  areaId: number | null;
  area: string | null;
  roles: string[];
};

type UserResponseList = {
  results: UserResponse[];
};

type InvestigatorOptionsListResponse = {
  results: {
    id: number;
    name: string;
    areaId: number | null;
    area: string | null;
  }[];
};

async function getInvestigators(
  areaId: number,
): Promise<ResponseApi<InvestigatorOptionsListResponse>> {
  const token = userStore.getAccessToken()?.trim();

  const response = await fetchApi<InvestigatorOptionsListResponse>(
    `users/investigators?areaId=${areaId}`,
    {
      method: "GET",
      headers: {
        Authorization: "Bearer " + token,
      },
    },
  );
  if (!response.success) {
    enqueueSnackbar(response.message, { variant: ToastType.ERROR });
  }
  return response;
}

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
    userStore.setAccessToken(response.data.accessToken.value);
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
  const token = userStore.getAccessToken()?.trim();

  const response = await fetchApi<UserResponse>("users", {
    method: "POST",
    headers: {
      Authorization: "Bearer " + token,
    },
    body: JSON.stringify(input),
  });
  if (!response.success) {
    enqueueSnackbar(response.message, { variant: ToastType.ERROR });
  } else {
    enqueueSnackbar(i18next.t("DashboardAdmin.createUser.successMessage"), {
      variant: ToastType.SUCCESS,
    });
  }

  return response;
}

// getAll - Admin
async function getAll(
  offset = 0,
  limit = 10,
): Promise<ResponseApi<UserResponseList>> {
  const query = buildQuery({ offset, limit });
  const token = userStore.getAccessToken()?.trim();

  const response = await fetchApi<UserResponseList>(`users${query}`, {
    method: "GET",
    headers: {
      Authorization: "Bearer " + token,
    },
  });
  return response;
}

// change roles - Admin
async function changeUserRoles(
  userId: number,
  roles: string[],
  areaId: number | null,
): Promise<ResponseApi<void>> {
  const token = userStore.getAccessToken()?.trim();

  const response = await fetchApi<void>(`users/${userId}/roles`, {
    method: "PUT",
    headers: {
      Authorization: "Bearer " + token,
    },
    body: JSON.stringify({ roles, areaId }),
  });

  if (!response.success) {
    enqueueSnackbar(response.message, { variant: ToastType.ERROR });
  } else {
    enqueueSnackbar(i18next.t("DashboardAdmin.changeRolesModal.success"), {
      variant: ToastType.SUCCESS,
    });
  }

  return response;
}

// change password - Admin
async function changeUserPassword(
  userId: number,
  newPassword: string,
): Promise<ResponseApi<void>> {
  const token = userStore.getAccessToken()?.trim();

  const response = await fetchApi<void>(`users/${userId}/password`, {
    method: "PUT",
    headers: {
      Authorization: "Bearer " + token,
    },
    body: JSON.stringify({ newPassword }),
  });

  if (!response.success) {
    enqueueSnackbar(response.message, { variant: ToastType.ERROR });
  } else {
    enqueueSnackbar(i18next.t("DashboardAdmin.changePasswordModal.success"), {
      variant: ToastType.SUCCESS,
    });
  }

  return response;
}
async function getUserInformation() {
  return await fetchApi<UserProfileResponse>("users/me", {
    method: "GET",
    headers: {
      Authorization: `Bearer ${userStore.getAccessToken()}`,
    },
  });
}

export const UsersApi = {
  selectRole,
  getUserRoles,
  create,
  getAll,
  changeUserRoles,
  changeUserPassword,
  getUserInformation,
  getInvestigators,
};
