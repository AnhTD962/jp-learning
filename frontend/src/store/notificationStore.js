import { defineStore } from 'pinia'
import notificationService from '../services/notificationService'

export const useNotificationStore = defineStore('notifications', {
    state: () => ({
        notifications: [],
        unreadCount: 0,
    }),
    actions: {
        connectToSSE() {
            notificationService.connectToSSE((data) => {
                this.notifications.unshift(data)
                this.unreadCount++
            })
        },
        addNotification(notification) {
            this.notifications.unshift(notification)
            this.unreadCount++
        },
        markAsRead(id) {
            const notif = this.notifications.find(n => n.id === id)
            if (notif && !notif.read) {
                notif.read = true
                this.unreadCount--
            }
        },
    },
})
