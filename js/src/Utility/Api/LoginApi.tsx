import { fetchApi, type ResponseApi } from "./FetchApi.tsx";
import { userStore } from "../Store/UserStore.tsx";
import { ToastType } from "../../Types/ToastType.tsx";
import { enqueueSnackbar } from "notistack";
import i18next from "i18next";

type LoginRequest = {
  email: string;
  password: string;
};

export type LoginResponse = {
  loginToken: TokenResponse;
  userId: number;
  roles: string[];
};

export type TokenResponse = {
  value: string;
  expiresAt: string;
};

export const AuthApi = { login, logout, refreshToken };

async function login(input: LoginRequest): Promise<ResponseApi<LoginResponse>> {
  const response = await fetchApi<LoginResponse>("users/login", {
    method: "POST",
    body: JSON.stringify(input),
  });
  if (response.success) {
    userStore.setUserId(response.data.userId);
    userStore.setIsLoggedIn();
    userStore.setLoginToken(response.data.loginToken.value);
    enqueueSnackbar(i18next.t("Login.successMessage"), {
      variant: ToastType.SUCCESS,
    });
  }
  return response;
}

async function logout(): Promise<ResponseApi<void>> {
  const response = await fetchApi<void>("users/logout", {
    method: "POST",
    headers: {
      Authorization: `Bearer ${userStore.getAccessToken()}`,
    },
  });

  if(response.success){
  enqueueSnackbar(i18next.t("Login.logout"), {
    variant: ToastType.SUCCESS,
  });
}

  userStore.clear();
  return response;
}

async function refreshToken(): Promise<ResponseApi<void>> {
  return await fetchApi<void>("users/refresh-token", {
    method: "POST",
  });
}
