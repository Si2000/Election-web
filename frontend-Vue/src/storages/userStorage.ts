import { defineStore } from "pinia";

export const userInfo = defineStore("userStorage", {
  state: () => ({
    token: null as string | null,
    username: "Gast",
    role: null as string | null
  }),

  getters: {
    getUsername: (state) => state.username,
    isLoggedIn: (state) => state.token !== null,
    getRole: (state) => state.role
  },

  actions: {
    /**
     * Save token, username and role
     */
    setToken(newToken: string, newUsername: string, role: string) {
      this.token = newToken;
      this.username = newUsername;
      this.role = role;

      localStorage.setItem("authToken", newToken);
      localStorage.setItem("username", newUsername);
      localStorage.setItem("role", role);
    },

    /**
     * Logout user
     */
    logout() {
      this.token = null;
      this.username = "Gast";
      this.role = null;

      localStorage.removeItem("authToken");
      localStorage.removeItem("username");
      localStorage.removeItem("role");
      window.location.reload()
    },

    /**
     * Restore session after refresh
     */
    checkToken() {
      const token = localStorage.getItem("authToken");
      const username = localStorage.getItem("username");
      const role = localStorage.getItem("role");

      if (token && username && role) {
        this.token = token;
        this.username = username;
        this.role = role;
      }
    }
  }
});
