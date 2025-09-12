import apiClient from "./apiClient";

export default {
  async getUserProfile() {
    const res = await apiClient.get("/users/me");
    return res.data;
  },

  async updateUserProfile(payload) {
    const res = await apiClient.put("/users/me", payload);
    return res.data;
  },

  async getAllUsers(page = 0, size = 10, search = "") {
    const res = await apiClient.get(
      `/users?page=${page}&size=${size}&search=${search}`
    );
    return res.data;
  },

  async getUserById(id) {
    const res = await apiClient.get(`/users/${id}`);
    return res.data;
  },

  async updateUserRoles(id, roles) {
    const res = await apiClient.put(`/users/${id}/roles`, { roles });
    return res.data;
  },

  async lockUser(id) {
    const res = await apiClient.put(`/users/${id}/lock`);
    return res.data;
  },

  async unlockUser(id) {
    const res = await apiClient.put(`/users/${id}/unlock`);
    return res.data;
  },
};
