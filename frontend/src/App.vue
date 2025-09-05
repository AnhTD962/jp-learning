<template>
  <div class="min-h-screen bg-gray-100">
    <!-- Fixed Navbar -->
    <div class="fixed top-0 left-0 right-0 z-30">
      <Navbar :modal-open="modalOpen" :isSidebarOpen="isSidebarOpen" @open-modal="openModal" @close-modal="closeModal"
        @toggle-sidebar="toggleSidebar" />
    </div>

    <div class="flex pt-16">
      <!-- Sidebar -->
      <transition name="slide">
        <Sidebar v-if="showSidebar && isSidebarOpen"
          class="fixed top-16 left-0 w-64 h-[calc(100vh-4rem)] bg-white shadow z-20" />
      </transition>

      <!-- Main content -->
      <main :class="[
        'transition-all duration-300 p-6 w-full',
        showSidebar && isSidebarOpen ? 'ml-64' : 'ml-0'
      ]">
        <router-view />
      </main>
    </div>

    <!-- Auth Modal -->
    <Modal v-if="modalOpen" @close="closeModal">
      <div class="mb-4">
        <LoginPage v-if="currentModal === 'login'" :error="error" @submit="handleLogin"
          @switch-to-forgot="switchToForgot" @switch-to-register="switchToRegister" />

        <RegisterPage v-else-if="currentModal === 'register'" :error="error" @submit="handleRegister"
          @switch-to-login="switchToLogin" />

        <ForgotPassword v-else-if="currentModal === 'forgot'" @switch-to-login="switchToLogin" />
      </div>
    </Modal>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, provide } from 'vue'
import { useRoute } from 'vue-router'
import Navbar from './components/Navbar.vue'
import Sidebar from './components/Sidebar.vue'
import Modal from './components/Modal.vue'
import LoginPage from './views/LoginPage.vue'
import RegisterPage from './views/RegisterPage.vue'
import ForgotPassword from './views/ForgotPassword.vue'
import { useAuthStore } from './store/authStore'

const route = useRoute()
const auth = useAuthStore()
auth.checkAuth()

const showSidebar = computed(() => route.path !== '/login' && route.path !== '/register')
const modalOpen = ref(false)
const currentModal = ref('login') // 'login' | 'register' | 'forgot'
const error = ref('')
const isSidebarOpen = ref(window.innerWidth >= 768)

// sidebar
function toggleSidebar() {
  isSidebarOpen.value = !isSidebarOpen.value
}

// modal
function openModal(type) {
  currentModal.value = type
  error.value = ''
  modalOpen.value = true
}
function closeModal() {
  modalOpen.value = false
  error.value = ''
}
function switchToRegister() {
  currentModal.value = 'register'
  error.value = ''
}
function switchToLogin() {
  currentModal.value = 'login'
  error.value = ''
}
function switchToForgot() {
  currentModal.value = 'forgot'
  error.value = ''
}

function openAuthModal(type = "login") {
  currentModal.value = type
  error.value = ''
  modalOpen.value = true
}
provide("openAuthModal", openAuthModal)

// auth
async function handleLogin(form) {
  try {
    await auth.login(form)
    if (auth.isLoggedIn) closeModal()
  } catch (err) {
    error.value = err.message || 'Login failed'
  }
}
async function handleRegister(form) {
  try {
    await auth.register(form)
    currentModal.value = 'login'
    error.value = 'Account created! Please log in.'
  } catch (err) {
    error.value = err.message || 'Register failed'
  }
}

// resize
onMounted(() => {
  window.addEventListener('resize', () => {
    isSidebarOpen.value = window.innerWidth >= 768
  })
})
</script>

<style scoped>
.slide-enter-active,
.slide-leave-active {
  transition: transform 0.3s ease;
}

.slide-enter-from,
.slide-leave-to {
  transform: translateX(-100%);
}
</style>
