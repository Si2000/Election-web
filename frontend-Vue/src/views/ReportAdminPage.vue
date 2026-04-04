<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import { getReports, deleteReport } from "@/services/AdminService";
import { getTopicById, getCommentById } from "@/services/TopicService";

/**
 * Admin Report DTO
 */
interface ReportAdmin {
  id: number;
  reason: string;
  reportedAt: string;
  reporterUsername: string;
  type: "TOPIC" | "COMMENT";
  targetId: number;
  targetAuthor: string;
}

/**
 * State
 */
const reports = ref<ReportAdmin[]>([]);
const loading = ref(true);

/**
 * Modal state
 */
const showModal = ref(false);
const modalTitle = ref("");
const modalContent = ref("");
const selectedReport = ref<ReportAdmin | null>(null);

/**
 * Fetch reports
 */
onMounted(async () => {
  try {
    reports.value = await getReports();
  } finally {
    loading.value = false;
  }
});

/**
 * Computed
 */
const filteredReports = computed(() => reports.value);

/**
 * Helpers
 */
function formatDate(datetime: string) {
  if (!datetime) return "-";
  const [datePart, timePart] = datetime.split("T");
  const [y, m, d] = datePart.split("-");
  const [h, min] = timePart.split(":");
  return `${d}-${m}-${y} ${h}:${min}`;
}

/**
 * Open modal
 */
async function viewReportContent(report: ReportAdmin) {
  try {
    selectedReport.value = report;

    if (report.type === "TOPIC") {
      const topic = await getTopicById(report.targetId);
      modalTitle.value = topic.title;
      modalContent.value = topic.content;
    } else {
      const comment = await getCommentById(report.targetId);
      modalTitle.value = `Reactie van ${comment.authorName}`;
      modalContent.value = comment.content;
    }

    showModal.value = true;
  } catch (e) {
    modalTitle.value = "Fout";
    modalContent.value = "Kon content niet laden";
  }
}

/**
 * Accept report
 * → delete report + target
 */
async function acceptReport() {
  if (!selectedReport.value) return;

  await deleteReport(selectedReport.value.id, true);
  reports.value = reports.value.filter(r => r.id !== selectedReport.value!.id);
  closeModal();
}

/**
 * Reject report
 * → delete report only
 */
async function rejectReport() {
  if (!selectedReport.value) return;

  await deleteReport(selectedReport.value.id, false);
  reports.value = reports.value.filter(r => r.id !== selectedReport.value!.id);
  closeModal();
}

function closeModal() {
  showModal.value = false;
  selectedReport.value = null;
}
</script>

<template>
  <div class="report-container">
    <h1>Admin Reports</h1>

    <div v-if="loading" class="loading">Loading...</div>

    <div v-else-if="filteredReports.length === 0" class="empty">
      Geen reports gevonden.
    </div>

    <div
      v-for="report in filteredReports"
      :key="report.id"
      class="report-card"
    >
      <div class="report-content">
        <p><strong>Type:</strong> {{ report.type }}</p>
        <p><strong>Target:</strong> {{ report.targetAuthor }}</p>
        <p><strong>Reporter:</strong> {{ report.reporterUsername }}</p>
        <p><strong>Reden:</strong> {{ report.reason }}</p>
        <p><strong>Datum:</strong> {{ formatDate(report.reportedAt) }}</p>
      </div>

      <div class="right-buttons">
        <button class="view-btn" @click="viewReportContent(report)">
          Bekijk
        </button>
      </div>
    </div>

    <!-- Modal -->
    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal">
        <h2>{{ modalTitle }}</h2>

        <p class="modal-content">{{ modalContent }}</p>

        <div class="modal-buttons">
          <button class="accept-btn" @click="acceptReport">
            Accepteren
          </button>

          <button class="reject-btn" @click="rejectReport">
            Afwijzen
          </button>

          <button class="close-btn" @click="closeModal">
            Sluiten
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.report-container {
  width: 90%;
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
  font-family: "Segoe UI", Tahoma, sans-serif;
}

h1 {
  font-size: 28px;
  margin-bottom: 20px;
  color: #111827;
}

/* States */
.loading {
  color: #6b7280;
}

.empty {
  color: #9ca3af;
}

/* Report Card */
.report-card {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 18px 22px;
  margin-bottom: 16px;

  display: flex;
  justify-content: space-between;
  align-items: center;
}

.report-content p {
  margin: 4px 0;
  color: #374151;
}

/* Buttons */
.right-buttons {
  display: flex;
  gap: 10px;
}

.view-btn {
  background: #2563eb;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
}

.view-btn:hover {
  background: #1d4ed8;
}

/* Modal */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background: white;
  padding: 24px 28px;
  border-radius: 14px;
  width: 520px;
  max-width: 90%;
}

.modal h2 {
  margin-top: 0;
  margin-bottom: 12px;
}

.modal-content {
  white-space: pre-wrap;
  color: #374151;
}

/* Modal Buttons */
.modal-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.accept-btn {
  background: #16a34a;
  color: white;
  padding: 8px 14px;
  border-radius: 8px;
  border: none;
  font-weight: 600;
  cursor: pointer;
}

.reject-btn {
  background: #dc2626;
  color: white;
  padding: 8px 14px;
  border-radius: 8px;
  border: none;
  font-weight: 600;
  cursor: pointer;
}

.close-btn {
  background: #374151;
  color: white;
  padding: 8px 14px;
  border-radius: 8px;
  border: none;
  font-weight: 600;
  cursor: pointer;
}
</style>
