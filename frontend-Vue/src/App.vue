<script setup lang="ts">
import Navbar from "@/components/Navbar.vue";
import { onMounted } from "vue";
import { useRouter } from "vue-router";
import { userInfo } from "@/storages/userStorage";
import { getPersona } from "@/services/UserService";

const store = userInfo();
const router = useRouter();

onMounted(async () => {
  store.checkToken();

  if (store.isLoggedIn) {
    try {
      const res = await getPersona();
      const persona = res.persona;

      if (persona === "nico") {
        router.push("/news");
      } else if (persona === "yasser") {
        router.push("/yasser");
      } else {
        router.push("/");
      }
    } catch (e) {
      console.error("Persona ophalen mislukt");
    }
  }
});
</script>

<template>
  <Navbar />
</template>
