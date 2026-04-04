<script setup lang="ts">
import { ref } from 'vue';
import LoginForm from '@/components/LoginPage.vue';
import { loginUser } from '@/services/AuthService';
import { userInfo } from '@/storages/userStorage';
import { useRouter } from 'vue-router';

/**
 * Defines the structure of user login credentials.
 */
interface LoginCredentials {
  email: string;
  password: string;
}

/**
 * Indicates whether a login request is currently in progress.
 */
const loading = ref(false);

/**
 * Holds any login error message or null if no error occurred.
 */
const error = ref<string | null>(null);

/**
 * Pinia store instance for managing user authentication data.
 */
const store = userInfo();

/**
 * Vue Router instance for navigation after successful login.
 */
const router = useRouter();

/**
 * Handles the login process when the user submits credentials.
 *
 *  Authenticates the user via AuthService.
 *  Stores the token and username in Pinia.
 *  Redirects to the home page on success.
 *  Displays an error message on failure.
 *
 * @param credentials - The user's email and password.
 */
const handleLogin = async (credentials: LoginCredentials): Promise<void> => {
  loading.value = true;
  error.value = null;

  try {
    // Call the AuthService to log in
    const response = await loginUser(credentials.email, credentials.password);
    console.log('Logged in:', response);

    // Save token and username in Pinia store
    store.setToken(response.token, response.username, response.role);

    // Redirect user to home page
    await router.push('/home');

  } catch (err: any) {
    if (err.error === 'ACCOUNT_BANNED') {
      alert('Your account has been banned. Please contact the administrator.');
      return;
    }

    if (err.error === 'ACCOUNT_NOT_VERIFIED') {
      alert('Your account has not been activated yet. Please check your email.');
      return;
    }
    error.value = 'Incorrect email or password.';
  }
  finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="auth-container">
    <h2>Welkom bij Nederlandse Verkiezingen</h2>
    <p class="subtitle">Log in of maak een account aan om te beginnen</p>

    <div class="tabs">
      <RouterLink to="/login" class="active">Inloggen</RouterLink>
      <RouterLink to="/register" class="inactive">Registreren</RouterLink>
    </div>

    <LoginForm @submit="handleLogin" />

    <div v-if="loading" class="status-message">Bezig met inloggen...</div>
    <div v-if="error" class="error-message">{{ error }}</div>
  </div>
</template>
