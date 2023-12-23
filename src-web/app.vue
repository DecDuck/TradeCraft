<template>
  <NuxtLayout>
    <NuxtPage />
  </NuxtLayout>
</template>

<script setup lang="ts">
import type {TradeCraftUser} from './types/user';
const appConfig: { app_name: string } = await $fetch("/api/v1/branding/config");

const router = useRouter();



useHead({
  titleTemplate: (title) =>
    title ? `${title} | ${appConfig["app_name"]}` : appConfig["app_name"],
});
</script>
