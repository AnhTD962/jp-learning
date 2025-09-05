import apiClient from "./apiClient";

export default {
  async login(credentials) {
    const res = await apiClient.post("/auth/login", credentials);
    return res.data;
  },

  async register(data) {
    const res = await apiClient.post("/auth/register", data);
    return res.data;
  },

  async refreshToken(refreshToken) {
    const res = await apiClient.post("/auth/refresh", null, {
      headers: { Authorization: `Bearer ${refreshToken}` },
    });
    return res.data;
  },

  async forgotPassword(email) {
    const res = await apiClient.post("/auth/forgot-password", { email });
    return res.data;
  },

  async changePassword(token, currentPassword, newPassword, confirmPassword) {
    const res = await apiClient.post(
      "/auth/change-password",
      { currentPassword, newPassword, confirmPassword },
      {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      }
    );
    return res.data;
  },
};
