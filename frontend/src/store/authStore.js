import { defineStore } from "pinia";
import authService from "../services/authService";
import userService from "../services/userService"; // 👈 service để gọi /users/me

export const useAuthStore = defineStore("auth", {
  state: () => ({
    user: null,
    accessToken: "",
    refreshToken: "",
    isAuthenticated: false,
    isLoading: false,
    error: null,
  }),
  getters: {
    isLoggedIn: (state) => !!state.accessToken,
    isAdmin: (state) => state.user?.roles?.includes("ADMIN"),
    currentUser: (state) => state.user,
  },
  actions: {
    async login(credentials) {
      this.isLoading = true;
      try {
        const data = await authService.login(credentials);

        this.accessToken = data.accessToken;
        this.refreshToken = data.refreshToken;
        this.user = {
          id: data.id,
          username: data.username,
          email: data.email,
          roles: data.roles,
        };
        this.isAuthenticated = true;

        localStorage.setItem("accessToken", this.accessToken);
        localStorage.setItem("refreshToken", this.refreshToken);
        localStorage.setItem("user", JSON.stringify(this.user));
      } catch (e) {
        this.error = e.response?.data || e.message;
      } finally {
        this.isLoading = false;
      }
    },

    async register(data) {
      return await authService.register(data);
    },

    logout() {
      this.accessToken = "";
      this.refreshToken = "";
      this.user = null;
      this.isAuthenticated = false;
      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");
      localStorage.removeItem("user"); // 👈 xoá luôn user
    },

    async refreshTokens() {
      const data = await authService.refreshToken(this.refreshToken);

      this.accessToken = data.accessToken;
      this.refreshToken = data.refreshToken;
      this.user = {
        id: data.id,
        username: data.username,
        email: data.email,
        roles: data.roles,
      };

      localStorage.setItem("accessToken", this.accessToken);
      localStorage.setItem("refreshToken", this.refreshToken);
      localStorage.setItem("user", JSON.stringify(this.user));
    },

    async checkAuth() {
      this.accessToken = localStorage.getItem("accessToken");
      this.refreshToken = localStorage.getItem("refreshToken");
      const userData = localStorage.getItem("user");

      this.user = userData ? JSON.parse(userData) : null;
      this.isAuthenticated = !!this.accessToken;

      // 👇 luôn gọi API để sync lại user mới nhất
      if (this.isAuthenticated) {
        try {
          const freshUser = await userService.getUserProfile();
          this.user = freshUser;
          localStorage.setItem("user", JSON.stringify(this.user));
        } catch (err) {
          console.error("Failed to fetch current user:", err);
          this.logout(); // token invalid thì logout
        }
      }
    },
  },
});
