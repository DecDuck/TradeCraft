<template>
  <div class="mx-auto px-4 py-4 md:py-8 max-w-3xl grid grid-cols-3 gap-4">
    <Card class="col-span-3 sm:col-span-1 flex flex-col items-center gap-y-2">
      <h1 class="text-zinc-200 text-md md:text-xl font-bold">
        {{ user?.name ?? user?.uuid }}
      </h1>
      <img class="md:p-6" :src="skinUrl" />
      <button
        class="block sm:hidden rounded-md bg-cyan-600 px-3 py-2 text-xs sm:text-sm font-semibold text-white shadow-sm hover:bg-cyan-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-cyan-600"
      >
        View inventory
      </button>
    </Card>
    <Card class="hidden sm:block col-span-2">
      <div v-if="inventory.data.value" class="grid grid-cols-9 gap-2 w-fit">
        <div
          class="w-8 h-8 bg-zinc-800 rounded-lg"
          v-for="item in inventory.data.value.inventory"
        >
          <img v-if="item?.key" class="w-full h-full aspect-square" :src="createAssetUrl(item.key, 32)" />
        </div></div
    ></Card>
    <Card class="col-span-3 md:col-span-2 pb-5">
      <div v-if="!user?.loginConfigured">
        <h2 class="text-3xl font-bold tracking-tight text-zinc-100 sm:text-4xl">
          Tired of linking?
        </h2>
        <p class="mt-3 text-zinc-400 text-md">
          Linking your account every time can get annoying and repetitive.
          Configure a username and password to log in with to avoid having to
          link each time!
        </p>
        <div class="mt-6 flex items-center gap-x-6">
          <NuxtLink
            href="/account/auth"
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
            <h3 class="text-sm font-bold text-green-500">
              Your account is set up for alternative login.
            </h3>
            <div class="mt-4">
              <div class="flex">
                <NuxtLink
                  href="/account/auth"
                  class="rounded-md bg-cyan-600 px-2.5 py-1.5 text-xs sm:text-sm font-semibold text-white shadow-sm hover:bg-cyan-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-cyan-600"
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

const inventory = await useFetch<{
  inventory: Array<{ id: string; amount: number; key: string }>;
}>("/api/v1/inventory/fetch");

useHead({
  title: "Account",
});
</script>
