<template>
  <NuxtLoadingIndicator
    color="repeating-linear-gradient(to right,#a5f3fc 0%,#22d3ee 100%)"
  />
  <NuxtLayout>
    <NuxtPage />
  </NuxtLayout>
</template>

<script setup lang="ts">
const appConfig = await useState<{ app_name: string }>("appConfig");

const router = useRouter();

const { data, error } = await useAsyncData<{ app_name: string }>(
  "appConfig",
  () => $fetch("/api/v1/branding/config")
);
if (error.value != null || data.value == null) {
  router.push("/link");
} else {
  appConfig.value = data.value;
}

useHead({
  titleTemplate: (title) =>
    title
      ? `${title} | ${appConfig.value?.app_name}`
      : appConfig.value?.app_name ?? "TradeCraft",
});
</script>
