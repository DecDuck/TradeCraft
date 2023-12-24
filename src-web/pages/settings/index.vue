<template>
  <div class="px-4 py-4 md:py-8 max-w-3xl grid grid-cols-3 gap-4">
    <Card class="flex flex-col items-center">
      <h1 class="text-zinc-200 text-md md:text-2xl font-bold">
        {{ user?.name ?? user?.uuid }}
      </h1>
      <img class="md:p-6" :src="skinUrl" />
    </Card>
    <Card class="col-span-2"> </Card>
    <Card class="col-span-3 md:col-span-2 pb-5">
      <div v-if="!user?.loginConfigured">
        <h2 class="text-3xl font-bold tracking-tight text-zinc-100 sm:text-4xl">
          Tired of linking?
        </h2>
        <p class="mt-3 text-zinc-400 text-md">
          Linking your accounte every time you want to use the online dashboard
          can get annoying and repetitive. Configure a username and password to
          log in with to avoid having to link each time!
        </p>
        <div class="mt-6 flex items-center gap-x-6">
          <NuxtLink
            href="/setupacc"
            class="rounded-md bg-cyan-600 px-3.5 py-2.5 text-sm font-semibold text-white shadow-sm hover:bg-cyan-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-cyan-600"
            >Get started</NuxtLink
          >
        </div>
      </div>
      <div v-else>
        <div class="flex">
          <div class="flex-shrink-0">
            <CheckCircleIcon
              class="h-5 w-5 text-green-500"
              aria-hidden="true"
            />
          </div>
          <div class="ml-3">
            <h3 class="text-sm font-medium text-green-500">
              Your account is set up for alternative login.
            </h3>
            <div class="mt-4">
              <div class="flex">
                <NuxtLink
                  href="/setupacc"
                  class="rounded-md bg-cyan-600 px-2.5 py-1.5 text-sm font-semibold text-white shadow-sm hover:bg-cyan-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-cyan-600"
                >
                  Change your username & password
                </NuxtLink>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Card>
    <Card
      class="col-span-3 md:col-span-1 flex text-center text-zinc-500 text-sm flex-col items-center justify-center gap-y-2"
    >
      <img :src="'/api/v1/branding/banner'" />
      <p>Powered by TradeCraft</p>
    </Card>
  </div>
</template>

<script setup lang="ts">
import { CheckCircleIcon } from "@heroicons/vue/20/solid";

const user = useUser();

const skinUrl = computed(
  () => `https://visage.surgeplay.com/full/384/${user.value?.uuid}`
);

useHead({
  title: "Settings",
});
</script>
