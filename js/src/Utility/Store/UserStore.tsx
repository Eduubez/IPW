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
  getAcessTokenExpirationDate: () => {
    return localStorage.getItem("acessTokenExpirationDate");
  },
  setAcessTokenExpirationDate: (date: string) => {
    localStorage.setItem("acessTokenExpirationDate", date);
  },
  hasTokenExpired: () => {
    const date = userStore.getAcessTokenExpirationDate();
    const expirationDate = date ? new Date(date) : null;
    if (!expirationDate) return true;
    return new Date() > expirationDate;
  },
  clear: () => {
    localStorage.removeItem("loggedIn");
    localStorage.removeItem("roles");
    localStorage.removeItem("loginToken");
    localStorage.removeItem("activeRole");
    localStorage.removeItem("acessTokenExpirationDate");
  },
};
