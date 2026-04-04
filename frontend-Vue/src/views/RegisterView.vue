<script setup lang="ts">
import { ref } from 'vue';
import RegisterForm from '@/components/RegisterPage.vue';
import { registerUser } from '@/services/AuthService';
import { useRouter } from 'vue-router';

/**
 * Defines the data structure for registration credentials expected from the form event.
 */
interface RegistrationData {
  email: string;
  password: string;
  username: string;
}

/**
 * Reactive state indicating if the registration process is currently executing.
 * @default false
 */
const loading = ref(false);

/**
 * Stores error messages returned from the backend or validation logic.
 * @default null
 */
const error = ref<string | null>(null);

/**
 * Stores the success message to display to the user upon successful account creation.
 * Used to provide visual feedback before redirecting.
 * @default null
 */
const successMessage = ref<string | null>(null);

/**
 * Vue Router instance used for navigation after successful registration.
 */
const router = useRouter();

/**
 * Handles the 'submit' event emitted by the RegisterForm component.
 * * This function:
 * 1. Resets error and success states and enables the loading indicator.
 * 2. Calls the `registerUser` service.
 * 3. On success: Displays a confirmation message and redirects to '/login' after a 2-second delay.
 * 4. On failure: Captures the error message and stops the loading state.
 *
 * @param data - The registration object containing email, password, and username.
 */
const handleRegister = async (data: RegistrationData) => {
  loading.value = true;
  error.value = null;
  successMessage.value = null;

  try {
    const response = await registerUser(data.email, data.password, data.username);
    console.log('Registered:', response);

    successMessage.value = "Account aangemaakt! Er is een bevestigingsemail verzonden naar " + data.email + ". Waar u uw account kunt activeren. U wordt doorgestuurd...";

    // Delay navigation to allow the user to read the success message
    setTimeout(() => {
      router.push('/login');
    }, 5000);

  } catch (err: any) {
    error.value = err.message || 'Registration failed.';
    loading.value = false; // Stop loading only on error, otherwise keep loading during redirect
  }
};
</script>

<template>
  <div class="auth-container">
    <h2>Welkom bij Nederlandse Verkiezingen</h2>
    <p class="subtitle">Log in of maak een account aan om te beginnen</p>

    <div class="tabs">
      <RouterLink to="/login" class="inactive">Inloggen</RouterLink>
      <RouterLink to="/register" class="active">Registreren</RouterLink>
    </div>

    <div v-if="successMessage">
      <div class="status-message success">{{ successMessage }}</div>
    </div>

    <RegisterForm v-else @submit="handleRegister" />

    <div v-if="loading && !successMessage" class="status-message">Account wordt aangemaakt...</div>
    <div v-if="error" class="error-message">{{ error }}</div>
  </div>
</template>
