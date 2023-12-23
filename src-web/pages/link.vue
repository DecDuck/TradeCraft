<template>
  <!--
      This example requires updating your template:
  
      ```
      <html class="h-full">
      <body class="h-full">
      ```
    -->
  <main
    class="grid min-h-screen place-items-center bg-zinc-900 px-6 py-24 sm:py-32 lg:px-8"
  >
    <div class="flex flex-col items-center text-center">
      <img :src="'/api/v1/branding/banner'" class="h-12 w-auto" />
      <p class="mt-6 max-w-md text-base leading-7 text-zinc-400">
        To access the Web UI, log into Minecraft and link your account with this
        session. Use the command /tc authorize [code] to link your account.
      </p>
      <h1
        class="mt-4 text-3xl font-bold tracking-tight text-cyan-200 space-x-4 sm:text-5xl font-mono"
      >
        <span v-for="letter in code.split('')">{{ letter }}</span>
      </h1>

      <div class="mt-10 flex items-center justify-center gap-x-6">
        <a
          href="#"
          class="rounded-md bg-zinc-600 px-3.5 py-2.5 text-sm font-semibold text-white shadow-sm hover:bg-zinc-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-zinc-600"
          >Link your account</a
        >
      </div>
    </div>
  </main>
</template>

<script setup lang="ts">
definePageMeta({
  layout: false,
});

const code: string = await $fetch('/api/v1/auth/link/begin');
const router = useRouter();

function complete(){
    $fetch('/api/v1/auth/link/finish', {method: "POST", body: code})
    .then(() => {
        router.push('/');
    })
    .catch(() => {

    });
}

useHead({
  title: "Link your account",
});
</script>
