import { useAuthStore } from '../store/authStore'

export const authGuard = (to, from, next) => {
    const authStore = useAuthStore()
    authStore.checkAuth()

    if (!authStore.isLoggedIn && to.path !== '/') {
        next('/')
    } else {
        next()
    }
}
