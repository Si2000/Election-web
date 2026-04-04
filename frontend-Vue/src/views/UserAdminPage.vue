<template>
  <div class="admin-container">

    <!-- Search + Sort Section -->
    <div class="search-sort">
      <input
        v-model="searchQuery"
        type="text"
        placeholder="Search by email or username..."
        class="search-box"
      />

      <div class="sort-buttons">
        <button @click="sortByRole" class="btn-sort">Sort by Role</button>
        <button @click="sortByBan" class="btn-sort">Sort by Ban</button>
        <button @click="sortByEmail" class="btn-sort">Sort by Email</button>
      </div>
    </div>

    <!-- User List -->
    <div
      class="user-card"
      v-for="u in filteredUsers"
      :key="u.email"
    >
      <div class="user-info">
        <span class="email">{{ u.email }}</span>

        <!-- ROLE BADGE -->
        <span
          class="role-badge"
          :class="u.role === 'ROLE_ADMIN' ? 'admin' : 'user'"
        >
          {{ u.role.replace('ROLE_', '') }}
        </span>

        <!-- BAN BADGE -->
        <span
          class="ban-status"
          :class="{ banned: u.baned }"
        >
          {{ u.baned ? "BANNED" : "ACTIVE" }}
        </span>
      </div>

      <!-- Actions -->
      <div class="actions">
        <button class="btn btn-admin" @click="makeAdmin(u.email)">Make Admin</button>
        <button class="btn btn-user" @click="makeUser(u.email)">Make User</button>

        <button class="btn btn-ban" v-if="!u.baned" @click="banUser(u.email)">Ban</button>
        <button class="btn btn-unban" v-else @click="unbanUser(u.email)">Unban</button>
      </div>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import {
  getAllUsers,
  updateRole,
  updateBanState
} from "@/services/AdminService";

// User Interface
interface UserDTO {
  email: string;
  username: string;
  role: string;
  baned: boolean;
}

const users = ref<UserDTO[]>([]);
const searchQuery = ref("");

// Load users
onMounted(async () => {
  users.value = await getAllUsers();
});

// Search filter
const filteredUsers = computed(() => {
  return users.value.filter(u =>
    u.email.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    u.username.toLowerCase().includes(searchQuery.value.toLowerCase())
  );
});

// Sort by role
function sortByRole() {
  users.value.sort((a, b) => a.role.localeCompare(b.role));
}

// Sort by ban state
function sortByBan() {
  users.value.sort((a, b) => Number(b.baned) - Number(a.baned));
}

// Sort by email
function sortByEmail() {
  users.value.sort((a, b) => a.email.localeCompare(b.email));
}

// Admin role change
async function makeAdmin(email: string) {
  await updateRole(email, "ROLE_ADMIN");
  users.value = await getAllUsers();
}

// User role change
async function makeUser(email: string) {
  await updateRole(email, "ROLE_USER");
  users.value = await getAllUsers();
}

// Ban
async function banUser(email: string) {
  await updateBanState(email, true);
  users.value = await getAllUsers();
}

// Unban
async function unbanUser(email: string) {
  await updateBanState(email, false);
  users.value = await getAllUsers();
}
</script>

<style scoped>
.admin-container {
  display: flex;
  flex-direction: column;
  margin: 0 auto;
  padding: 30px 20px;
}

/* Search + Sorting */
.search-sort {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 20px;
}

.search-box {
  padding: 10px 14px;
  border-radius: 8px;
  border: 1px solid #ccc;
  width: 100%;
}

.sort-buttons {
  display: flex;
  gap: 10px;
}

.btn-sort {
  padding: 8px 14px;
  background: #e5e7eb;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  font-weight: 600;
  transition: 0.2s;
}

.btn-sort:hover {
  background: #d1d5db;
}

/* User Card */
.user-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 18px 20px;
  margin-bottom: 15px;
  box-shadow: 0px 3px 12px rgba(0,0,0,0.08);
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: transform .15s ease, box-shadow .15s ease;
}

.user-card:hover {
  transform: translateY(-2px);
  box-shadow: 0px 6px 14px rgba(0,0,0,0.12);
}

.user-info {
  font-size: 16px;
  line-height: 1.4;
}

.email {
  font-weight: 600;
  display: block;
}

/* ROLE BADGE */
.role-badge {
  display: inline-block;
  margin-top: 4px;
  margin-right: 6px;
  font-size: 12px;
  font-weight: 600;
  padding: 3px 8px;
  border-radius: 6px;
}

.role-badge.admin {
  background: #2563eb;
  color: white;
}

.role-badge.user {
  background: #6b7280;
  color: white;
}

/* BAN BADGE */
.ban-status {
  display: inline-block;
  margin-top: 4px;
  font-size: 12px;
  font-weight: 600;
  padding: 3px 8px;
  border-radius: 6px;
}

.ban-status.banned {
  background: #ef4444;
  color: white;
}

.ban-status:not(.banned) {
  background: #10b981;
  color: white;
}

/* Buttons */
.actions {
  display: flex;
  gap: 10px;
}

.btn {
  padding: 8px 14px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  font-weight: 600;
  transition: 0.2s;
}

.btn-admin {
  background-color: #2563eb;
  color: white;
}

.btn-admin:hover {
  background-color: #1d4ed8;
}

.btn-user {
  background-color: #10b981;
  color: white;
}

.btn-user:hover {
  background-color: #059669;
}

.btn-ban {
  background-color: #ef4444;
  color: white;
}

.btn-ban:hover {
  background-color: #dc2626;
}

.btn-unban {
  background-color: #facc15;
  color: black;
}

.btn-unban:hover {
  background-color: #eab308;
}
</style>
