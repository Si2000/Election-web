<script setup lang="ts">
import {computed, ref} from "vue";
import { RouterLink, RouterView, useRouter } from "vue-router";
import { userInfo } from '@/storages/userStorage'; // Importeer de Pinia store
import { storeToRefs } from 'pinia'; // Nodig om state reactief te houden

/**
 * State to track visibility of the mobile hamburger menu.
 * @default false
 */
const isMenuOpen = ref(false);

/**
 * Toggles the state of the mobile navigation menu (open/close).
 */
const toggleMenu = () => {
  isMenuOpen.value = !isMenuOpen.value;
};

/**
 * Pinia store instance for user information.
 */
const store = userInfo();

/**
 * Reactive references to store state.
 * @property isLoggedIn - Checks if the current user is authenticated.
 * @property getUsername - Retrieves the current user's display name.
 */
const { isLoggedIn, getUsername } = storeToRefs(store); // Maak state reactief

/**
 * Vue Router instance for programmatic navigation.
 */
const router = useRouter();

/**
 * State to track visibility of the user profile dropdown menu (on hover).
 * @default false
 */
const isDropdownOpen = ref(false);
const { role } = storeToRefs(store);
const isAdmin = computed(() => role.value === "ROLE_ADMIN");
console.log(isAdmin);
/**
 * Handles the user logout process.
 * - Calls the logout action in the store.
 * - Closes the profile dropdown.
 * - Redirects the user to the home page.
 */
const handleLogout = () => {
  store.logout();
  isDropdownOpen.value = false;
  router.push('/home');
};
</script>

<template>
  <div id="navbar">
    <div class="navbar-container">
      <div class="left-section">
        <div class="hamburger" @click="toggleMenu">
          <span> </span>
          <span></span>
          <span></span>
        </div>
        <RouterLink to="/home" class="logo">
          <img src="@/images/verkiezinglogo.png" alt="Verkiezing Logo" class="logo-img" />
        </RouterLink>
      </div>

      <div class="navbar-center">
        <RouterLink class="nav-link" to="/home">Home</RouterLink>
        <RouterLink class="nav-link" to="/map">Gemeentekaart</RouterLink>
        <RouterLink class="nav-link" to="/mapProvinces">Provincialekaart</RouterLink>
        <RouterLink class="nav-link" to="/candidates">Kandidaten</RouterLink>
        <RouterLink class="nav-link" to="/ElectionResult">Verkiezingsresultaten</RouterLink>
        <RouterLink class="nav-link" to="/compare">Vergelijk verkiezingen</RouterLink>
        <RouterLink class="nav-link" to="/forum">Forum</RouterLink>
        <RouterLink v-if="isAdmin" class="nav-link" to="/adminPanel">Admin Paneel</RouterLink>
      </div>

      <div class="navbar-right">

        <div v-if="!isLoggedIn" class="auth-buttons">
          <RouterLink class="nav-btn login" to="/login">Inloggen</RouterLink>
          <RouterLink class="nav-btn register" to="/register">Registreren</RouterLink>
        </div>

        <div
          v-else
          class="profile-dropdown-wrapper"
          @mouseover="isDropdownOpen = true"
          @mouseleave="isDropdownOpen = false"
        >
          <button class="profile-icon-btn">
            <svg class="profile-icon-svg" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
              <path fill-rule="evenodd" d="M18.685 19.097A9.723 9.723 0 0 0 21.75 12c0-5.385-4.365-9.75-9.75-9.75S2.25 6.615 2.25 12a9.723 9.723 0 0 0 3.065 7.097A9.716 9.716 0 0 0 12 21.75a9.716 9.716 0 0 0 6.685-2.653Zm-12.54-1.285A7.486 7.486 0 0 1 12 15a7.486 7.486 0 0 1 5.855 2.812A8.224 8.224 0 0 1 12 20.25a8.224 8.224 0 0 1-5.855-2.438ZM15.75 9a3.75 3.75 0 1 1-7.5 0 3.75 3.75 0 0 1 7.5 0Z" clip-rule="evenodd" />
            </svg>
          </button>

          <transition name="fade-down">
            <div v-if="isDropdownOpen" class="profile-dropdown-menu">
              <div class="dropdown-header">
                Welkom, <strong>{{ getUsername }}</strong>
              </div>
                <div class="dropdown-divider"></div>
                  <RouterLink to="/account" class="dropdown-item">
                   Mijn Account
                  </RouterLink>
                <div class="dropdown-divider"></div>
              <button @click="handleLogout" class="dropdown-item logout-item">
                Uitloggen
              </button>
            </div>
          </transition>
        </div>

      </div>
    </div>

    <transition name="slide-down">
      <div v-if="isMenuOpen" class="dropdown-menu">
        <RouterLink class="dropdown-link" to="/home" @click="toggleMenu">Home</RouterLink>
        <RouterLink class="dropdown-link" to="/map" @click="toggleMenu">Gemeente Map</RouterLink>
        <RouterLink class="dropdown-link" to="/mapProvinces" @click="toggleMenu">Provinciale Map</RouterLink>
        <RouterLink class="dropdown-link" to="/candidates" @click="toggleMenu">Kandidaten</RouterLink>
        <RouterLink class="dropdown-link" to="/ElectionResult" @click="toggleMenu">Verkiezingsresultaten</RouterLink>
        <RouterLink class="dropdown-link" to="/forum" @click="toggleMenu">Forum</RouterLink>
      </div>
    </transition>

  </div>
  <div class="router-view">
    <RouterView />
  </div>
</template>
