<template>
    <div class="p-6">
        <h1 class="text-2xl font-bold mb-4">User Management</h1>

        <!-- Search -->
        <div class="flex mb-4 gap-2">
            <input v-model="search" type="text" placeholder="Search by username or email..."
                class="border px-3 py-2 rounded w-64" />
            <button @click="applySearch" class="px-4 py-2 bg-orange-500 text-white rounded hover:bg-orange-600">
                Search
            </button>
        </div>

        <!-- Table -->
        <table class="w-full border-collapse border border-gray-200">
            <thead>
                <tr class="bg-gray-100">
                    <th class="border px-3 py-2 text-left">Username</th>
                    <th class="border px-3 py-2 text-left">Email</th>
                    <th class="border px-3 py-2 text-left">Roles</th>
                    <th class="border px-3 py-2 text-left">Actions</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="user in users" :key="user.id">
                    <td class="border px-3 py-2" v-html="highlight(user.username, search)"></td>
                    <td class="border px-3 py-2" v-html="highlight(user.email, search)"></td>
                    <td class="border px-3 py-2">
                        <span v-for="role in user.roles" :key="role"
                            class="px-2 py-1 mr-1 bg-orange-100 text-orange-700 rounded text-xs">
                            {{ role }}
                        </span>
                    </td>
                    <td class="border px-3 py-2 flex gap-2">
                        <button @click="toggleLock(user)" class="px-2 py-1 bg-gray-200 hover:bg-gray-300 rounded">
                            {{ user.locked ? "Unlock" : "Lock" }}
                        </button>
                        <button @click="editRoles(user)" class="px-2 py-1 bg-orange-200 hover:bg-orange-300 rounded">
                            Edit Roles
                        </button>
                    </td>
                </tr>
                <tr v-if="users.length === 0 && !loading">
                    <td colspan="4" class="text-center py-4 text-gray-500">
                        No users found.
                    </td>
                </tr>
            </tbody>
        </table>

        <!-- Pagination -->
        <div v-if="totalPages > 1" class="flex justify-between items-center mt-4">
            <button @click="prevPage" :disabled="page === 0" class="px-3 py-1 bg-gray-200 rounded disabled:opacity-50">
                Previous
            </button>
            <span>Page {{ page + 1 }} / {{ totalPages }}</span>
            <button @click="nextPage" :disabled="page >= totalPages - 1"
                class="px-3 py-1 bg-gray-200 rounded disabled:opacity-50">
                Next
            </button>
        </div>

        <!-- Modal edit roles -->
        <div v-if="selectedUser" class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-40">
            <div class="bg-white p-6 rounded w-96">
                <h2 class="text-lg font-semibold mb-4">
                    Edit Roles - {{ selectedUser.username }}
                </h2>
                <div class="space-y-2">
                    <label class="flex items-center">
                        <input type="radio" value="STUDENT" v-model="roles" />
                        <span class="ml-2">STUDENT</span>
                    </label>
                    <label class="flex items-center">
                        <input type="radio" value="ADMIN" v-model="roles" />
                        <span class="ml-2">ADMIN</span>
                    </label>
                </div>
                <div class="flex justify-end gap-2 mt-4">
                    <button @click="selectedUser = null" class="px-3 py-1 bg-gray-200 rounded">
                        Cancel
                    </button>
                    <button @click="saveRoles" class="px-3 py-1 bg-orange-500 text-white rounded">
                        Save
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { onMounted, ref, computed, watch } from "vue";
import { useRouter } from "vue-router";
import { storeToRefs } from "pinia";
import { useUserStore } from "../store/userStore";
import { useAuthStore } from "../store/authStore";

const router = useRouter();
const userStore = useUserStore();
const authStore = useAuthStore();
const { users, page, size, total, loading } = storeToRefs(userStore);

const selectedUser = ref(null);
const roles = ref([]);
const search = ref("");
let searchTimeout = null;

watch(search, (newVal) => {
    clearTimeout(searchTimeout);
    searchTimeout = setTimeout(() => {
        userStore.fetchUsers(0, size.value, newVal);
    }, 500);
});

// Khi load trang
onMounted(async () => {
    // Nếu chưa đăng nhập -> redirect về login
    if (!authStore.accessToken) {
        router.push("/");
        return;
    }

    try {
        await userStore.fetchUsers();
    } catch (err) {
        if (err.response?.status === 401) {
            authStore.logout();
            router.push("/");
        }
    }
});

// Khóa/mở khóa user
function toggleLock(user) {
    const action = user.locked ? userStore.unlockUser : userStore.lockUser;
    action(user.id).then(() =>
        userStore.fetchUsers(page.value, size.value)
    );
}

function applySearch() {
    userStore.fetchUsers(0, size.value, search.value);
}

function highlight(text, keyword) {
    if (!keyword) return text;
    const regex = new RegExp(`(${keyword})`, "gi");
    return text.replace(regex, '<span class="bg-yellow-200">$1</span>');
}

function editRoles(user) {
    selectedUser.value = user;
    roles.value = user.roles[0] || "STUDENT"; // lấy role đầu tiên
}

// Lưu roles
function saveRoles() {
    userStore.updateRoles(selectedUser.value.id, [roles.value]).then(() => {
        userStore.fetchUsers(page.value, size.value, search.value);
        selectedUser.value = null;
    });
}

// Phân trang
function prevPage() {
    if (page.value > 0) userStore.fetchUsers(page.value - 1, size.value, search.value);
}

function nextPage() {
    if (page.value < totalPages.value - 1)
        userStore.fetchUsers(page.value + 1, size.value, search.value);
}

const totalPages = computed(() => Math.ceil(total.value / size.value));
</script>

<style scoped>
.highlight {
    background-color: #ffe58f;
    font-weight: bold;
}
</style>