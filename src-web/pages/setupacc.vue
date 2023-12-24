<template>
  <Card class="m-4 flex flex-col justify-center px-6 py-12 lg:px-8">
    <div class="sm:mx-auto sm:w-full sm:max-w-sm">
      <img
        class="mx-auto h-10 w-auto"
        :src="'/api/v1/branding/banner'"
        :alt="appConfig.app_name"
      />
      <h2
        class="mt-10 text-center text-2xl font-bold leading-9 tracking-tight text-zinc-100"
      >
        Add or update your username & password
      </h2>
      <p class="mt-6 text-center text-zinc-400">
        Add a username and password to your account in order to sign in without
        linking.
      </p>
    </div>

    <div class="mt-10 mb-3 sm:mx-auto sm:w-full sm:max-w-sm">
      <form class="space-y-6" @submit.prevent="submit">
        <div>
          <label
            for="username"
            class="block text-sm font-medium leading-6 text-zinc-300"
            >Username</label
          >
          <div class="mt-2">
            <input
              id="username"
              name="username"
              type="text"
              autocomplete="username"
              placeholder="myUsername"
              required
              v-model="username"
              class="block w-full bg-zinc-800 rounded-md border-0 py-1.5 text-zinc-100 shadow-sm ring-1 ring-inset ring-zinc-700 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-cyan-200 sm:text-sm sm:leading-6"
            />
          </div>
        </div>

        <div>
          <div class="flex items-center justify-between">
            <label
              for="password"
              class="block text-sm font-medium leading-6 text-zinc-300"
              >Password</label
            >
          </div>
          <div class="mt-2">
            <input
              id="password"
              name="password"
              type="password"
              autocomplete="current-password"
              placeholder="●●●●●●●●●●●●"
              required
              v-model="password"
              class="block w-full bg-zinc-800 rounded-md border-0 py-1.5 text-zinc-100 shadow-sm ring-1 ring-inset ring-zinc-700 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-cyan-200 sm:text-sm sm:leading-6"
            />
          </div>
        </div>

        <div>
          <SpinnerButton
            type="submit"
            :loading="loading"
            class="w-full h-8 justify-center rounded-md bg-cyan-600 px-3 py-1.5 text-sm font-semibold leading-6 text-white shadow-sm hover:bg-cyan-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-cyan-600"
          >
            Apply
          </SpinnerButton>
        </div>

        <div v-if="error != null" class="mt-6 rounded-md bg-red-600/5 p-4">
          <div class="flex">
            <div class="flex-shrink-0">
              <XCircleIcon class="h-5 w-5 text-red-400" aria-hidden="true" />
            </div>
            <div class="ml-3">
              <h3 class="text-sm font-medium text-red-400">
                {{ error }}
              </h3>
            </div>
          </div>
        </div>
      </form>
    </div>
  </Card>
</template>

<script setup lang="ts">
import { XCircleIcon } from "@heroicons/vue/20/solid";
const username = ref("");
const password = ref("");
const loading = ref(false);

const error = ref(null);

const router = useRouter();
const user = useUser();

const appConfig = useState<{ app_name: string }>("appConfig");

function submit() {
  $fetch("/api/v1/auth/setupacc", {
    method: "POST",
    body: { username: username.value, password: password.value },
  })
    .then(async () => {
      user.value = await $fetch("/api/v1/auth/fetch");
      router.push("/settings");
    })
    .catch((err) => {
      error.value =
        err.data || "An unknown error occurred. Please reload the page.";
    });
}

useHead({
  title: "Setup Account",
});
</script>
