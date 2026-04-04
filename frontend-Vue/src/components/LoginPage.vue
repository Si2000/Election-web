<script setup lang="ts">
import { ref } from 'vue';

const emit = defineEmits<{
  (e: 'submit', data: { email: string; password: string }): void;
}>();

const email = ref('');
const password = ref('');

const handleSubmit = () => {
  emit('submit', { email: email.value, password: password.value });
};
</script>

<template>
  <form @submit.prevent="handleSubmit" class="login-form">
    <div class="form-group">
      <label for="email">E-mail:</label>
      <input id="email" v-model="email" type="email" required />
    </div>

    <div class="form-group">
      <label for="password">Wachtwoord:</label>
      <input id="password" v-model="password" type="password" required />
    </div>
    <div class="links">
      <router-link to="/forgot-password">Wachtwoord vergeten?</router-link>
    </div>

    <button type="submit" class="btn-primary">Inloggen</button>
  </form>
</template>
