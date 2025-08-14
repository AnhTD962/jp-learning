<template>
  <div class="min-h-screen bg-gray-100">
    <!-- Fixed Navbar -->
    <div class="fixed top-0 left-0 right-0 z-30">
      <Navbar :is-login="isLogin" :error="error" :modal-open="modalOpen" :isSidebarOpen="isSidebarOpen"
        @open-modal="openModal" @close-modal="closeModal" @switch-to-register="switchToRegister"
        @switch-to-login="switchToLogin" @handle-login="handleLogin" @handle-register="handleRegister"
        @toggle-sidebar="toggleSidebar" />
    </div>

    <div class="flex pt-16">
      <!-- Sidebar -->
      <transition name="slide">
        <Sidebar v-if="showSidebar && isSidebarOpen"
          class="fixed top-16 left-0 w-64 h-[calc(100vh-4rem)] bg-white shadow z-20" />
      </transition>
      
      <!-- Main content -->
      <main :class="['transition-all duration-300 p-6 w-full', showSidebar && isSidebarOpen ? 'ml-64' : 'ml-0']">
        <router-view />
      </main>
    </div>

    <!-- Modal -->
    <Modal v-if="modalOpen" @close="closeModal">
      <div class="mb-4">
        <LoginPage v-if="isLogin" :error="error" @submit="handleLogin" />
        <RegisterPage v-else :error="error" @submit="handleRegister" />
      </div>

      <div class="text-center mt-2">
        <span v-if="isLogin">
          Don't have an account?
          <button class="text-blue-600 underline" @click="switchToRegister">Register</button>
        </span>
        <span v-else>
          Already have an account?
          <button class="text-blue-600 underline" @click="switchToLogin">Login</button>
        </span>
      </div>
    </Modal>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import Navbar from './components/Navbar.vue'
import Sidebar from './components/Sidebar.vue'
import Modal from './components/Modal.vue'
import LoginPage from './views/LoginPage.vue'
import RegisterPage from './views/RegisterPage.vue'
import { useAuthStore } from './store/authStore'

const route = useRoute()
const auth = useAuthStore()
auth.checkAuth() 

const showSidebar = computed(() => route.path !== '/login' && route.path !== '/register')
const modalOpen = ref(false)
const isLogin = ref(true)
const error = ref('')
const isSidebarOpen = ref(window.innerWidth >= 768) // open by default on md+

function toggleSidebar() {
  isSidebarOpen.value = !isSidebarOpen.value
}

function openModal(type) {
  isLogin.value = type === 'login'
  error.value = ''
  modalOpen.value = true
}
function closeModal() {
  modalOpen.value = false
  error.value = ''
}
function switchToRegister() {
  isLogin.value = false
  error.value = ''
}
function switchToLogin() {
  isLogin.value = true
  error.value = ''
}
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
    isLogin.value = true                    // 👈 switch to login form
    error.value = 'Account created! Please log in.' // 👈 show message
  } catch (err) {
    error.value = err.message || 'Register failed'
  }
}

// Automatically handle resize
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
