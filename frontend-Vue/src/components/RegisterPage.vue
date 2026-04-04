<script setup lang="ts">
import { ref } from 'vue';

/**
 * Defines the events emitted by this component.
 * @event submit - Emitted when validation passes. Payload contains email, password, and username.
 */
const emit = defineEmits<{
  (e: 'submit', data: { email: string; password: string; username: string }): void;
}>();

/**
 * Reactive state for form inputs and UI controls.
 */
const email = ref('');
const password = ref('');
const confirmPassword = ref('');
const username = ref('');

/**
 * Toggles visibility of the password fields.
 * @default false
 */
const showPassword = ref(false);
const showConfirmPassword = ref(false);

/**
 * Stores validation error messages to display to the user.
 */
const errorMessage = ref('');

/**
 * Toggles the visibility of the main password input field.
 * Switches between 'text' and 'password' input types.
 */
const toggleShowPassword = () => {
  showPassword.value = !showPassword.value;
};

/**
 * Toggles the visibility of the confirmation password input field.
 * Switches between 'text' and 'password' input types.
 */
const toggleShowConfirmPassword = () => {
  showConfirmPassword.value = !showConfirmPassword.value;
};

/**
 * Handles the form submission process.
 * * Performs the following validations:
 * - Checks if the password and confirmation password match.
 * - Checks if the password meets the minimum length requirement (6 characters).
 * * If validation fails, it sets the `errorMessage`.
 * If validation passes, it emits the `submit` event with the user data.
 */
const handleSubmit = () => {
  errorMessage.value = '';

  // Check if passwords match
  if (password.value !== confirmPassword.value) {
    errorMessage.value = 'Wachtwoorden komen niet overeen!';
    return;
  }

  // Check password length
  if (password.value.length < 6) {
    errorMessage.value = 'Wachtwoord moet minimaal 6 tekens zijn.';
    return;
  }

  // Emit data if valid
  emit('submit', {
    email: email.value,
    password: password.value,
    username: username.value
  });
};
</script>

<template>
  <form @submit.prevent="handleSubmit" class="register-form">
    <div class="form-group">
      <label for="username">Gebruikersnaam:</label>
      <input id="username" v-model="username" type="text" required placeholder="Kies een gebruikersnaam" />
    </div>

    <div class="form-group">
      <label for="email">E-mail:</label>
      <input id="email" v-model="email" type="email" required placeholder="jouw@email.nl" />
    </div>

    <div class="form-group password-group">
      <label for="password">Wachtwoord:</label>
      <div class="password-input-wrapper">
        <input
          id="password"
          v-model="password"
          :type="showPassword ? 'text' : 'password'"
          required
          placeholder="Minimaal 6 tekens"
        />
        <span @click="toggleShowPassword" class="eye-icon">
        <img
          v-if="showPassword"
          src="@/images/verbergen.png"
          alt="Wachtwoord verbergen"
          class="eye-img"
        />
        <img
          v-else
          src="@/images/tonen.png"
          alt="Wachtwoord tonen"
          class="eye-img"
        />
      </span>

      </div>
    </div>

    <div class="form-group password-group">
      <label for="confirmPassword">Herhaal Wachtwoord:</label>
      <div class="password-input-wrapper">
        <input
          id="confirmPassword"
          v-model="confirmPassword"
          :type="showConfirmPassword ? 'text' : 'password'"
          required
          placeholder="Bevestig wachtwoord"
        />
        <span @click="toggleShowConfirmPassword" class="eye-icon">
        <img
          v-if="showConfirmPassword"
          src="@/images/verbergen.png"
          alt="Wachtwoord verbergen"
          class="eye-img"
        />
        <img
          v-else
          src="@/images/tonen.png"
          alt="Wachtwoord tonen"
          class="eye-img"
        />
      </span>

      </div>
    </div>
    <div v-if="errorMessage" class="error-text">{{ errorMessage }}</div>

    <button type="submit" class="btn-primary">Account aanmaken</button>
  </form>
</template>
