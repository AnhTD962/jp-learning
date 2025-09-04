import { defineStore } from "pinia";
import api from "../services/apiClient"; // axios instance có interceptor
import notificationService from "../services/notificationService";

export const useNotificationStore = defineStore("notifications", {
  state: () => ({
    notifications: [],
    eventSource: null,
  }),

  getters: {
    unreadCount: (state) => state.notifications.filter((n) => !n.read).length,
  },

  actions: {
    async loadNotifications() {
      try {
        const res = await api.get("/notifications");
        this.notifications = res.data.sort(
          (a, b) => new Date(b.createdAt * 1000) - new Date(a.createdAt * 1000) // epoch giây
        );
      } catch (err) {
        console.error("❌ Error fetching notifications:", err);
      }
    },

    connectToSSE() {
      if (this.eventSource) return;

      this.eventSource = notificationService.connectToSSE((data) => {
        this.notifications.unshift(data);
      });
    },

    closeSSE() {
      if (this.eventSource) {
        this.eventSource.close();
        this.eventSource = null;
      }
    },

    async markAsRead(id) {
      try {
        await api.post(`/notifications/${id}/read`);
        const notif = this.notifications.find((n) => n.id === id);
        if (notif) notif.read = true;
      } catch (err) {
        console.error("❌ Error marking as read:", err);
      }
    },
  },
});
