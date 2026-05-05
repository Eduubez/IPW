export const userStore = {
  getRoles:() => {
    const roles = localStorage.getItem("roles");
    return roles ? JSON.parse(roles) : null;
  },
  getLoginToken: () => {
    return localStorage.getItem("loginToken");
  },
  setLoginToken: (token: string) => {
    localStorage.setItem("loginToken", token);
  },
  getIsLoggedIn: () => {
    return localStorage.getItem("loggedIn") === "true";
  },
  setIsLoggedIn: () => {
    localStorage.setItem("loggedIn", "true");
  },
  getActiveRole: () => {
    const activeRole = localStorage.getItem("activeRole");
    return activeRole ? activeRole : null;
  },
  setRoles: (roles: string[]) => {
    localStorage.setItem("roles", JSON.stringify(roles));
  },
  setActiveRole: (role: string) => {
    localStorage.setItem("activeRole", role);
  },
  getAccessTokenExpirationDate: () => {
    return localStorage.getItem("accessTokenExpirationDate");
  },
  setAccessTokenExpirationDate: (date: string) => {
    localStorage.setItem("accessTokenExpirationDate", date);
  },
  hasTokenExpired: () => {
    const date = userStore.getAccessTokenExpirationDate();
    const expirationDate = date ? new Date(date) : null;
    if (!expirationDate) return true;
    return new Date() > expirationDate;
  },
  setUserId: (userId: number) => {
    localStorage.setItem("userId", userId.toString());
  },
  getUserId: () => {
    const userId = localStorage.getItem("userId");
    return userId ? parseInt(userId) : null;
  },
  getAccessToken: () => {
    return localStorage.getItem("accessToken");
  },
  setAccessToken: (token: string) => {
    localStorage.setItem("accessToken", token);
  },
  clear: () => {
    localStorage.removeItem("loggedIn");
    localStorage.removeItem("roles");
    localStorage.removeItem("loginToken");
    localStorage.removeItem("activeRole");
    localStorage.removeItem("accessTokenExpirationDate");
    localStorage.removeItem("userId");
    localStorage.removeItem("accessToken");
  },
};
