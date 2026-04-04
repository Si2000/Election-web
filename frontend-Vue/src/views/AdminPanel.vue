<template>
  <div class="header-content">
    <h1>Verkiezingsresultaten – Tweede Kamer</h1>
  </div>
  <div class="admin-container">
    <div id="sidebar">
      <ul>
        <li
          @click="clickDashboard"
          :class="{ active: IsClickDashboard }"
        >
          Dashboard
        </li>

        <li
          @click="clickReport"
          :class="{ active: IsClickReport }"
        >
          Report
        </li>

        <li
          @click="clickReportedUsers"
          :class="{ active: IsClickReportedUsers }"
        >
          Gerapporteerde Gebruikers
        </li>
      </ul>
    </div>

    <!-- Welkom -->
    <div v-if="!IsClickReport && !IsClickDashboard && !IsClickReportedUsers" class="dashboard">
      <div id="welcome">
        <h1>Welkom AdminPanel</h1>
      </div>
    </div>

    <!-- Dashboard -->
    <div v-if="IsClickDashboard" class="dashboard">
      <UserAdminPage />
    </div>

    <!-- Reports -->
    <div v-if="IsClickReport" class="dashboard">
      <ReportAdminPage />
    </div>

    <!-- Gerapporteerde Gebruikers -->
    <div v-if="IsClickReportedUsers" class="dashboard">
      <h2>Gerapporteerde Gebruikers</h2>
      <table>
        <thead>
        <tr>
          <th>Gebruiker</th>
          <th>Aantal reports</th>
          <th>Redenen</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="user in reportedUsers" :key="user.id">
          <td>{{ user.username }}</td>
          <td>{{ user.reportCount }}</td>
          <td>
            <ul>
              <li v-for="reason in user.reasons" :key="reason">{{ reason }}</li>
            </ul>
          </td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
import UserAdminPage from "@/views/UserAdminPage.vue";
import ReportAdminPage from "@/views/ReportAdminPage.vue";
import { ref } from "vue";
import { getReportedUsers } from "@/services/AdminService";

// 1. Interface
interface ReportedUser {
  id: number;
  username: string;
  reportCount: number;
  reasons: string[];
}

// ...

// 2. Type toevoegen
const reportedUsers = ref<ReportedUser[]>([]);
// Sidebar state

const IsClickDashboard = ref(false);
const IsClickReport = ref(false);
const IsClickReportedUsers = ref(false);



// Sidebar click handlers
function clickDashboard() {
  IsClickDashboard.value = true;
  IsClickReport.value = false;
  IsClickReportedUsers.value = false;
}

function clickReport() {
  IsClickDashboard.value = false;
  IsClickReport.value = true;
  IsClickReportedUsers.value = false;
}

function clickReportedUsers() {
  IsClickDashboard.value = false;
  IsClickReport.value = false;
  IsClickReportedUsers.value = true;
  loadReportedUsers();
}

// Load gerapporteerde gebruikers
async function loadReportedUsers() {
  try {
    reportedUsers.value = await getReportedUsers();
  } catch (err) {
    console.error("Kon gerapporteerde gebruikers niet ophalen:", err);
  }
}
</script>

<style scoped>
.header-content {
  height: 70px;
}

#welcome {
  display: flex;
  align-items: center;
  justify-content: center;
}

.admin-container {
  display: flex;
}

.dashboard {
  width: 75%;
}

#sidebar {
  width: 25%;
  min-height: 100vh;
  background: #ffffff;
  color: #333;
  padding: 20px 0;
  border-right: 1px solid #e5e7eb;
}

#sidebar ul {
  list-style: none;
  margin: 0;
  padding: 0;
}

#sidebar li {
  padding: 14px 24px;
  margin-bottom: 4px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 500;
  transition: background 0.18s ease, padding-left 0.18s ease;
  border-radius: 8px;
}

#sidebar li:hover {
  background: #f3f4f6;
  padding-left: 32px;
}

#sidebar li.active {
  background: #e8f0fe;
  color: #1d4ed8;
  border-left: 4px solid #2563eb;
  padding-left: 28px;
}

/* ===== Gerapporteerde gebruikers (ALLEEN DIT IS MOOIER GEMAAKT) ===== */

.dashboard h2 {
  margin-bottom: 16px;
  font-weight: 700;
  color: #001b5e;
}

table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  background: #ffffff;
  border-radius: 14px;
  border: 1px solid #e5e7eb;
  overflow: hidden;
  box-shadow: 0 8px 22px rgba(0, 0, 0, 0.06);
  margin-top: 12px;
}

table th {
  background: #f3f4f6;
  text-align: left;
  padding: 14px 16px;
  font-weight: 600;
  font-size: 0.95rem;
  color: #374151;
  border-bottom: 1px solid #e5e7eb;
}

table td {
  padding: 14px 16px;
  font-size: 0.95rem;
  color: #374151;
  vertical-align: top;
  border-bottom: 1px solid #f1f5f9;
}

table th,
table td {
  border-left: none;
  border-right: none;
}

table tbody tr {
  transition: background 0.15s ease;
}

table tbody tr:hover {
  background: #f9fafb;
}

table tbody tr:last-child td {
  border-bottom: none;
}

table tbody td:first-child {
  font-weight: 600;
  color: #001b5e;
}

/* Redenen als badges */
table ul {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

table ul li {
  background: #e8f0fe;
  color: #1d4ed8;
  border: 1px solid #c7d2fe;
  padding: 6px 10px;
  border-radius: 999px;
  font-size: 0.8rem;
  font-weight: 600;
  line-height: 1;
}

</style>
