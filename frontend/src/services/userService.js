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
};
