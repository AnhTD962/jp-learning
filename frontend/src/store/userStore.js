import { defineStore } from "pinia";
import userService from "../services/userService";

export const useUserStore = defineStore("user", {
  state: () => ({
    users: [],
    page: 0,
    size: 10,
    total: 0,
    search: "",
    loading: false,
    error: null,
  }),

  actions: {
    async fetchUsers(page = 0, size = 10, search = "") {
      try {
        this.loading = true;
        this.error = null;
        this.page = page;
        this.size = size;

        const data = await userService.getAllUsers(page, size, search);
        this.users = data.content || data;
        this.total = data.totalElements ?? this.users.length;
      } catch (err) {
        this.error = err.response?.data || err.message;
      } finally {
        this.loading = false;
      }
    },

    async lockUser(userId) {
      return await userService.lockUser(userId);
    },

    async unlockUser(userId) {
      return await userService.unlockUser(userId);
    },

    async updateRoles(userId, roles) {
      return await userService.updateUserRoles(userId, roles);
    },
  },
});
