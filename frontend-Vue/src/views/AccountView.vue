<script setup lang="ts">
import { ref, onMounted } from 'vue';
import {
  getUserProfile,
  getUserTopics,
  getUserComments,
  updateUserProfile,
  changeUserPassword,
  deleteUserAccount
} from '@/services/UserService';
import { userInfo } from '@/storages/userStorage';
import { useRouter } from 'vue-router';

const store = userInfo();
const router = useRouter();

const activeTab = ref<'general' | 'password' | 'persona' | 'content' | 'danger'>('general');
const loading = ref(true);
const message = ref('');
const error = ref('');

const user = ref<{ id?: number; username: string; email: string }>({
  username: '',
  email: ''
});

const persona = ref<string | null>(null);

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmNewPassword: ''
});

const myTopics = ref<any[]>([]);
const myComments = ref<any[]>([]);

onMounted(async () => {
  try {
    const profile = await getUserProfile();
    user.value = profile;

    await loadPersona();
    await loadMyContent();
  } catch (e) {
    error.value = 'Kon accountgegevens niet laden.';
    console.error(e);
  } finally {
    loading.value = false;
  }
});

async function loadPersona() {
  try {
    const res = await fetch('http://oege.ie.hva.nl:8888/api/user/persona', {
      headers: { Authorization: `Bearer ${store.token}` }
    });
    if (!res.ok) return;
    const data = await res.json();
    persona.value = data.persona ?? null;
  } catch (e) {
    console.error(e);
  }
}

async function loadMyContent() {
  try {
    if (!user.value?.id) return;
    myTopics.value = await getUserTopics(user.value.id);
    myComments.value = await getUserComments(user.value.id);
  } catch (e) {
    console.error(e);
  }
}

async function handleUpdateProfile() {
  message.value = '';
  error.value = '';
  try {
    const before = await getUserProfile();
    const updated = await updateUserProfile(user.value.username, user.value.email);
    store.setToken(localStorage.getItem('authToken') || '', updated.username, store.role || '');
    if (before.email !== user.value.email) {
      message.value = 'Gebruikersnaam opgeslagen. Bevestig je nieuwe e-mailadres via de mail.';
      user.value.email = before.email;
    } else {
      message.value = 'Profiel succesvol opgeslagen!';
    }
  } catch (e: any) {
    error.value = e.message || 'Opslaan mislukt.';
  }
}

async function handleChangePassword() {
  message.value = '';
  error.value = '';

  const { oldPassword, newPassword, confirmNewPassword } = passwordForm.value;
  if (newPassword !== confirmNewPassword) {
    error.value = 'Wachtwoorden komen niet overeen.';
    return;
  }

  try {
    await changeUserPassword(oldPassword, newPassword);
    message.value = 'Wachtwoord gewijzigd!';
    passwordForm.value = { oldPassword: '', newPassword: '', confirmNewPassword: '' };
  } catch (e: any) {
    error.value = e.message || 'Wachtwoord wijzigen mislukt.';
  }
}

async function handleDelete() {
  if (!confirm('Account definitief verwijderen?')) return;
  try {
    await deleteUserAccount();
    store.logout();
    router.push('/register');
  } catch {
    alert('Verwijderen mislukt.');
  }
}

async function choosePersona(type: 'nico' | 'yasser') {
  message.value = '';
  error.value = '';
  try {
    await fetch('http://oege.ie.hva.nl:8888/api/user/persona', {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${store.token}`
      },
      body: JSON.stringify({ persona: type })
    });
    persona.value = type;
    message.value = 'Persona opgeslagen!';
  } catch {
    error.value = 'Persona opslaan mislukt.';
  }
}

function goToTopic(topicId: number) {
  router.push(`/forum/${topicId}`);
}
</script>

<template>
  <div v-if="loading" class="loading">Laden...</div>

  <div v-else class="account-page-wrapper">
    <h1 class="page-title">Accountinstellingen</h1>

    <div class="tab-bar">
      <button :class="{ active: activeTab === 'general' }" @click="activeTab='general'">Algemeen</button>
      <button :class="{ active: activeTab === 'password' }" @click="activeTab='password'">Wachtwoord</button>
      <button :class="{ active: activeTab === 'persona' }" @click="activeTab='persona'">Persona</button>
      <button :class="{ active: activeTab === 'content' }" @click="activeTab='content'">Mijn bijdragen</button>
      <button class="danger-tab" :class="{ active: activeTab === 'danger' }" @click="activeTab='danger'">
        Verwijderen
      </button>
    </div>

    <div v-if="message" class="alert success">{{ message }}</div>
    <div v-if="error" class="alert error">{{ error }}</div>

    <!-- TAB: GENERAL -->
    <section v-if="activeTab==='general'" class="panel animate-fade">
      <div class="panel-header">
        <div>
          <h2 class="panel-title">Algemeen</h2>
          <p class="panel-subtitle">Werk je gebruikersnaam en e-mailadres bij.</p>
        </div>
        <div class="panel-badge">
          <span class="badge-dot"></span>
          <span>Account</span>
        </div>
      </div>

      <div class="panel-body">
        <form class="pro-form" @submit.prevent="handleUpdateProfile">
          <div class="form-grid">
            <div class="form-field">
              <label class="form-label">Gebruikersnaam</label>
              <div class="input-wrap">
                <input v-model="user.username" class="pro-input" type="text" placeholder="Jouw gebruikersnaam" />
              </div>
              <p class="form-hint">Deze naam is zichtbaar voor anderen in het forum.</p>
            </div>

            <div class="form-field">
              <label class="form-label">E-mailadres</label>
              <div class="input-wrap">
                <input v-model="user.email" class="pro-input" type="email" placeholder="naam@email.com" />
              </div>
              <p class="form-hint">Bij wijzigen kan een e-mailbevestiging nodig zijn.</p>
            </div>
          </div>

          <div class="panel-actions">
            <button type="submit" class="btn-primary">Opslaan</button>
          </div>
        </form>
      </div>
    </section>

    <!-- TAB: PASSWORD -->
    <section v-if="activeTab==='password'" class="panel animate-fade">
      <div class="panel-header">
        <div>
          <h2 class="panel-title">Wachtwoord</h2>
          <p class="panel-subtitle">Kies een sterk wachtwoord om je account te beveiligen.</p>
        </div>
        <div class="panel-badge warning">
          <span class="badge-dot"></span>
          <span>Beveiliging</span>
        </div>
      </div>

      <div class="panel-body">
        <form class="pro-form" @submit.prevent="handleChangePassword">
          <div class="form-grid">
            <div class="form-field">
              <label class="form-label">Huidig wachtwoord</label>
              <div class="input-wrap">
                <input v-model="passwordForm.oldPassword" class="pro-input" type="password" placeholder="••••••••" />
              </div>
            </div>

            <div class="form-field">
              <label class="form-label">Nieuw wachtwoord</label>
              <div class="input-wrap">
                <input v-model="passwordForm.newPassword" class="pro-input" type="password" placeholder="Minimaal 8 tekens" />
              </div>
              <p class="form-hint">Tip: gebruik letters, cijfers en symbolen.</p>
            </div>

            <div class="form-field">
              <label class="form-label">Herhaal nieuw wachtwoord</label>
              <div class="input-wrap">
                <input v-model="passwordForm.confirmNewPassword" class="pro-input" type="password" placeholder="Herhaal nieuw wachtwoord" />
              </div>
            </div>
          </div>

          <div class="panel-actions">
            <button type="submit" class="btn-primary">Wachtwoord opslaan</button>
          </div>
        </form>

        <div class="security-note">
          <div class="security-note-title">Beveiligingstip</div>
          <div class="security-note-text">
            Denk je dat je account is misbruikt? Wijzig dan direct je wachtwoord.
          </div>
        </div>
      </div>
    </section>

    <!-- TAB: PERSONA (same style as others) -->
    <section v-if="activeTab === 'persona'" class="panel animate-fade">
      <div class="panel-header">
        <div>
          <h2 class="panel-title">Persona</h2>
          <p class="panel-subtitle">Kies een persona die bepaalt hoe de website zich aanpast aan jouw stijl.</p>
        </div>
        <div class="panel-badge">
          <span class="badge-dot"></span>
          <span>Voorkeur</span>
        </div>
      </div>

      <div class="panel-body">
        <div class="persona-grid">
          <div class="content-card persona-pro-card" :class="{ selected: persona === 'nico' }">
            <div class="content-card-header">
              <h3 class="content-card-title">Rico (Nieuws)</h3>
              <span class="count-pill" v-if="persona === 'nico'">Actief</span>
            </div>
            <div class="persona-body">
              <p class="content-item-text">
                Voor nieuws, updates en politieke gebeurtenissen.
              </p>
              <button class="btn-primary " :style="{ marginTop: '80px' }" @click="choosePersona('nico')">Kies Rico</button>
            </div>
          </div>

          <div class="content-card persona-pro-card" :class="{ selected: persona === 'yasser' }">
            <div class="content-card-header">
              <h3 class="content-card-title">Yasser (Analyse)</h3>
              <span class="count-pill" v-if="persona === 'yasser'">Actief</span>
            </div>
            <div class="persona-body">
              <p class="content-item-text">
                Voor objectieve data, verdieping en politieke kennis.
              </p>
              <button class="btn-primary" :style="{ marginTop: '80px' }" @click="choosePersona('yasser')">Kies Yasser</button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- TAB: CONTENT -->
    <section v-if="activeTab==='content'" class="panel animate-fade">
      <div class="panel-header">
        <div>
          <h2 class="panel-title">Mijn bijdragen</h2>
          <p class="panel-subtitle">Overzicht van jouw topics en reacties.</p>
        </div>
        <div class="panel-badge">
          <span class="badge-dot"></span>
          <span>Forum</span>
        </div>
      </div>

      <div class="panel-body">
        <div class="content-columns">
          <div class="content-card">
            <div class="content-card-header">
              <h3 class="content-card-title">Mijn topics</h3>
              <span class="count-pill">{{ myTopics.length }}</span>
            </div>

            <div v-if="myTopics.length === 0" class="empty-state">
              <div class="empty-title">Nog geen topics</div>
              <div class="empty-subtitle">Start een topic in het forum om het hier te zien.</div>
            </div>

            <ul v-else class="content-list">
              <li
                v-for="t in myTopics"
                :key="t.id"
                class="content-item"
                @click="goToTopic(t.id)"
              >
                <div class="content-item-top">
                  <strong class="content-item-title">{{ t.title }}</strong>
                  <span class="content-item-link">Open</span>
                </div>
                <p class="content-item-text">{{ t.content }}</p>
              </li>
            </ul>
          </div>

          <div class="content-card">
            <div class="content-card-header">
              <h3 class="content-card-title">Mijn reacties</h3>
              <span class="count-pill">{{ myComments.length }}</span>
            </div>

            <div v-if="myComments.length === 0" class="empty-state">
              <div class="empty-title">Nog geen reacties</div>
              <div class="empty-subtitle">Plaats een reactie in een topic om het hier te zien.</div>
            </div>

            <ul v-else class="content-list">
              <li
                v-for="c in myComments"
                :key="c.id"
                class="content-item"
                @click="goToTopic(c.topicId)"
              >
                <div class="content-item-top">
                  <strong class="content-item-title">Reactie</strong>
                  <span class="content-item-link">Open</span>
                </div>
                <p class="content-item-text">{{ c.content }}</p>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </section>

    <!-- TAB: DANGER -->
    <section v-if="activeTab==='danger'" class="panel animate-fade">
      <div class="panel-header">
        <div>
          <h2 class="panel-title danger-text">Account verwijderen</h2>
          <p class="panel-subtitle">Deze actie kan niet ongedaan worden gemaakt.</p>
        </div>
        <div class="panel-badge danger">
          <span class="badge-dot"></span>
          <span>Gevaar</span>
        </div>
      </div>

      <div class="panel-body">
        <div class="danger-box">
          <p>Je account en alle gegevens worden permanent verwijderd.</p>
          <button class="btn-danger" @click="handleDelete">Ja, verwijder mijn account</button>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
/* =========================
   Page
========================= */

.account-page-wrapper {
  max-width: 980px;
  margin: 0 auto;
  padding: 20px;
  font-family: 'Inter', sans-serif;
}

.page-title {
  font-size: 2rem;
  font-weight: 800;
  color: #001b5e;
  margin: 0 0 18px 0;
  letter-spacing: 0.2px;
}

.loading {
  padding: 24px;
}

/* =========================
   Tabs
========================= */

.tab-bar {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-bottom: 16px;

  background: #ffffff;
  border: 1px solid #e6eaf2;
  border-radius: 14px;
  padding: 10px;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.05);
}

.tab-bar button {
  appearance: none;
  border: 1px solid transparent;
  background: #f0f4ff;
  color: #001b5e;
  font-weight: 700;
  border-radius: 10px;
  padding: 10px 14px;
  cursor: pointer;
  transition: background-color 0.15s ease, transform 0.15s ease, border-color 0.15s ease;
  white-space: nowrap;
}

.tab-bar button:hover {
  background: #e7ecf8;
  transform: translateY(-1px);
}

.tab-bar button.active {
  background: #001b5e;
  color: #ffffff;
  border-color: #001b5e;
}

.danger-tab {
  color: #c70000;
  background: #fff5f5;
  border: 1px solid #ffd0d0 !important;
}

.danger-tab:hover {
  background: #ffe8e8;
}

.danger-tab.active {
  background: #c70000;
  color: #ffffff;
  border-color: #c70000 !important;
}

/* =========================
   Alerts
========================= */

.alert {
  padding: 10px 14px;
  border-radius: 10px;
  margin: 10px 0 14px 0;
  border: 1px solid transparent;
}

.alert.success {
  background: #d8f5d4;
  color: #167a16;
  border-color: #bceeb6;
}

.alert.error {
  background: #ffd9d9;
  color: #b90000;
  border-color: #ffbcbc;
}

/* =========================
   Panel (card)
========================= */

.panel {
  background: #ffffff;
  border: 1px solid #e6eaf2;
  border-radius: 16px;
  box-shadow: 0 10px 28px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  padding: 16px 18px;
  background: linear-gradient(180deg, #ffffff, #f6f8ff);
  border-bottom: 1px solid #e6eaf2;
}

.panel-title {
  margin: 0;
  font-size: 1.35rem;
  font-weight: 800;
  color: #001b5e;
}

.panel-subtitle {
  margin: 6px 0 0 0;
  color: #65708a;
  font-size: 0.95rem;
  line-height: 1.4;
}

.panel-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 10px;
  border-radius: 999px;
  border: 1px solid #dbe3f6;
  background: #ffffff;
  color: #001b5e;
  font-weight: 800;
  font-size: 0.85rem;
  white-space: nowrap;
}

.panel-badge.warning {
  border-color: #ffe3b2;
  color: #8a5a00;
}

.panel-badge.danger {
  border-color: #ffd0d0;
  color: #c70000;
}

.badge-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #001b5e;
}

.panel-badge.warning .badge-dot {
  background: #d98900;
}

.panel-badge.danger .badge-dot {
  background: #c70000;
}

.panel-body {
  padding: 18px;
}

.panel-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}

/* =========================
   Forms
========================= */

.pro-form {
  width: 100%;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}

.form-field {
  min-width: 0;
}

.form-label {
  display: block;
  margin-bottom: 8px;
  color: #23324a;
  font-weight: 800;
  font-size: 0.95rem;
}

.input-wrap {
  position: relative;
}

.pro-input {
  width: 100%;
  padding: 11px 12px;
  border-radius: 12px;
  border: 1px solid #cfd6e6;
  background: #ffffff;
  box-sizing: border-box;
  font-size: 0.95rem;
  color: #23324a;
  transition: border-color 0.15s ease, box-shadow 0.15s ease;
}

.pro-input:focus {
  outline: none;
  border-color: #001b5e;
  box-shadow: 0 0 0 3px rgba(0, 27, 94, 0.12);
}

.form-hint {
  margin: 8px 0 0 0;
  color: #65708a;
  font-size: 0.9rem;
  line-height: 1.4;
}

/* =========================
   Buttons
========================= */

.btn-primary {
  background: #001b5e;
  color: #ffffff;
  padding: 10px 16px;
  border-radius: 12px;
  border: 1px solid #001b5e;
  cursor: pointer;
  font-weight: 900;
  transition: background-color 0.15s ease, transform 0.15s ease, border-color 0.15s ease;
}

.btn-primary:hover {
  background: #00308f;
  border-color: #00308f;
  transform: translateY(-1px);
}

.btn-danger {
  background: #c70000;
  color: #ffffff;
  border: 1px solid #c70000;
  padding: 10px 16px;
  border-radius: 12px;
  cursor: pointer;
  font-weight: 900;
  transition: background-color 0.15s ease, transform 0.15s ease;
}

.btn-danger:hover {
  background: #b10000;
  transform: translateY(-1px);
}

/* =========================
   Security note
========================= */

.security-note {
  margin-top: 14px;
  border: 1px solid #e6eaf2;
  background: #f8fafc;
  border-radius: 12px;
  padding: 12px 14px;
}

.security-note-title {
  font-weight: 900;
  color: #23324a;
  margin-bottom: 6px;
}

.security-note-text {
  color: #65708a;
  font-size: 0.95rem;
  line-height: 1.4;
}

/* =========================
   Contributions + Persona layout
========================= */

.content-columns {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}

.content-card {
  border: 1px solid #e6eaf2;
  border-radius: 14px;
  background: #ffffff;
  overflow: hidden;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.04);
}

.content-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 14px;
  background: #f6f8ff;
  border-bottom: 1px solid #e6eaf2;
}

.content-card-title {
  margin: 0;
  color: #001b5e;
  font-weight: 900;
  font-size: 1.05rem;
}

.count-pill {
  padding: 6px 10px;
  border-radius: 999px;
  background: #001b5e;
  color: #ffffff;
  font-weight: 900;
  font-size: 0.85rem;
  white-space: nowrap;
}

.content-list {
  list-style: none;
  padding: 10px;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.content-item {
  cursor: pointer;
  border: 1px solid #e6eaf2;
  border-radius: 12px;
  padding: 12px 12px;
  background: #ffffff;
  transition: background-color 0.15s ease, transform 0.15s ease, border-color 0.15s ease;
}

.content-item:hover {
  background: #f5f7ff;
  border-color: #cfd6e6;
  transform: translateY(-1px);
}

.content-item-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
}

.content-item-title {
  color: #001b5e;
  font-weight: 900;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.content-item-link {
  color: #00308f;
  font-weight: 900;
  font-size: 0.9rem;
  white-space: nowrap;
}

.content-item-text {
  margin: 8px 0 0 0;
  color: #5c6b86;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.empty-state {
  padding: 16px 14px;
  border: 1px dashed #cfd6e6;
  border-radius: 12px;
  margin: 10px;
  background: #fbfcff;
}

.empty-title {
  font-weight: 900;
  color: #23324a;
  margin-bottom: 4px;
}

.empty-subtitle {
  color: #65708a;
  font-size: 0.95rem;
  line-height: 1.4;
}

/* Persona card body */
.persona-body {
  padding: 12px 14px 14px 14px;
}

.persona-pro-card.selected {
  border-color: #001b5e;
  box-shadow: 0 10px 24px rgba(0, 27, 94, 0.12);
}

.persona-pro-card.selected .content-card-header {
  background: #e7ecf8;
}

/* =========================
   Danger
========================= */

.danger-text {
  color: #c70000;
}

.danger-box {
  background: #fff5f5;
  border: 1px solid #ffd0d0;
  border-radius: 12px;
  padding: 14px;
}

/* =========================
   Animations
========================= */

.animate-fade {
  animation: fadeIn 0.18s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(4px); }
  to { opacity: 1; transform: translateY(0); }
}

/* =========================
   Responsive
========================= */

@media (max-width: 900px) {
  .form-grid {
    grid-template-columns: 1fr;
  }

  .content-columns {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 600px) {
  .account-page-wrapper {
    padding: 14px;
  }

  .page-title {
    font-size: 1.6rem;
  }

  .tab-bar {
    padding: 8px;
    gap: 8px;
  }

  .tab-bar button {
    flex: 1;
    padding: 10px 12px;
  }

  .panel-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .panel-actions {
    justify-content: stretch;
  }

  .btn-primary,
  .btn-danger {
    width: 100%;
  }
}
/* Make persona cards larger like before */
.persona-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.persona-pro-card {
  min-height: 240px;
}

.persona-pro-card .persona-body {
  padding: 16px 16px 18px 16px;
}

.persona-pro-card .content-item-text {
  -webkit-line-clamp: 6;
  margin-top: 10px;
}

.persona-pro-card .btn-primary {
  margin-top: 14px;
}

/* Mobile */
@media (max-width: 900px) {
  .persona-grid {
    grid-template-columns: 1fr;
  }

  .persona-pro-card {
    min-height: 0;
  }
}

</style>
