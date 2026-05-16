import { decodeJwt, type JWTPayload } from "jose";

let inMemoryAccessToken: JWTPayload | null = null;
let inMemoryLoginToken: JWTPayload | null = null;

function getDecodedAccessToken(): JWTPayload | null {
  if (!inMemoryAccessToken) {
    const token = localStorage.getItem("accessToken");
    if (!token) return null;
    inMemoryAccessToken = decodeJwt(token);
  }
  return inMemoryAccessToken;
}

function getDecodedLoginToken(): JWTPayload | null {
  if (!inMemoryLoginToken) {
    const token = localStorage.getItem("loginToken");
    if (!token) return null;
    inMemoryLoginToken = decodeJwt(token);
  }
  return inMemoryLoginToken;
}

export const userStore = {
  getAccessToken: () => {
    return localStorage.getItem("accessToken");
  },
  setAccessToken: (token: string) => {
    localStorage.setItem("accessToken", token);
    inMemoryAccessToken = decodeJwt(token);
  },
  getRoles: () => {
    const decoded = getDecodedLoginToken();
    return (decoded?.roles as string[]) ?? null;
  },
  getLoginToken: () => {
    return localStorage.getItem("loginToken");
  },
  setLoginToken: (token: string) => {
    localStorage.setItem("loginToken", token);
    inMemoryLoginToken = decodeJwt(token);
  },
  getIsLoggedIn: () => {
    return localStorage.getItem("loggedIn") === "true";
  },
  setIsLoggedIn: () => {
    localStorage.setItem("loggedIn", "true");
  },
  getActiveRole: () => {
    const decoded = getDecodedAccessToken();
    return (decoded?.role as string) ?? null;
  },
  getLoginTokenExpirationDate: () => {
    const decoded = getDecodedLoginToken();
    return decoded?.exp ?? null;
  },
  hasTokenExpired: () => {
    const accessDecoded = getDecodedAccessToken();
    if (accessDecoded) {
      return Date.now() > accessDecoded.exp! * 1000;
    }
    const loginDecoded = getDecodedLoginToken();
    if (!loginDecoded?.exp) return true;
    return Date.now() > loginDecoded.exp * 1000;
  },
  setUserId: (userId: number) => {
    localStorage.setItem("userId", userId.toString());
  },
  getUserId: () => {
    const userId = localStorage.getItem("userId");
    return userId ? parseInt(userId) : null;
  },
  clear: () => {
    inMemoryAccessToken = null;
    inMemoryLoginToken = null;
    localStorage.removeItem("loggedIn");
    localStorage.removeItem("loginToken");
    localStorage.removeItem("userId");
    localStorage.removeItem("accessToken");
  },
};
