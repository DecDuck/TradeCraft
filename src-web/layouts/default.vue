<template>
  <div v-if="user" class="min-h-screen bg-zinc-950 text-zinc-100">
    <TransitionRoot as="template" :show="sidebarOpen">
      <Dialog
        as="div"
        class="relative z-50 lg:hidden"
        @close="sidebarOpen = false"
      >
        <TransitionChild
          as="template"
          enter="transition-opacity ease-linear duration-300"
          enter-from="opacity-0"
          enter-to="opacity-100"
          leave="transition-opacity ease-linear duration-300"
          leave-from="opacity-100"
          leave-to="opacity-0"
        >
          <div class="fixed inset-0 bg-zinc-900/80" />
        </TransitionChild>

        <div class="fixed inset-0 flex">
          <TransitionChild
            as="template"
            enter="transition ease-in-out duration-300 transform"
            enter-from="-translate-x-full"
            enter-to="translate-x-0"
            leave="transition ease-in-out duration-300 transform"
            leave-from="translate-x-0"
            leave-to="-translate-x-full"
          >
            <DialogPanel class="relative mr-16 flex w-full max-w-xs flex-1">
              <TransitionChild
                as="template"
                enter="ease-in-out duration-300"
                enter-from="opacity-0"
                enter-to="opacity-100"
                leave="ease-in-out duration-300"
                leave-from="opacity-100"
                leave-to="opacity-0"
              >
                <div
                  class="absolute left-full top-0 flex w-16 justify-center pt-5"
                >
                  <button
                    type="button"
                    class="-m-2.5 p-2.5"
                    @click="sidebarOpen = false"
                  >
                    <span class="sr-only">Close sidebar</span>
                    <XMarkIcon class="h-6 w-6 text-white" aria-hidden="true" />
                  </button>
                </div>
              </TransitionChild>
              <!-- Sidebar component, swap this element with another sidebar if you like -->
              <div
                class="flex grow flex-col gap-y-5 overflow-y-auto bg-zinc-900 px-6 pb-2 ring-2 ring-cyan-300"
              >
                <div class="flex h-16 shrink-0 items-center justify-center">
                  <img
                    class="h-8 w-auto"
                    :src="'/api/v1/branding/banner'"
                    alt="TradeCraft"
                  />
                </div>
                <nav class="flex flex-1 flex-col">
                  <ul role="list" class="flex flex-1 flex-col gap-y-7">
                    <li>
                      <ul role="list" class="-mx-2 space-y-1">
                        <li
                          v-for="(item, itemIdx) in navigation"
                          :key="item.name"
                        >
                          <NuxtLink
                            :href="item.href"
                            :class="[
                              itemIdx == activeIndex
                                ? 'bg-zinc-800 text-cyan-200'
                                : 'text-zinc-400 hover:text-white hover:bg-zinc-800',
                              'group flex gap-x-3 rounded-md p-2 text-sm leading-6 font-semibold',
                            ]"
                          >
                            <component
                              :is="item.icon"
                              class="h-6 w-6 shrink-0"
                              aria-hidden="true"
                            />
                            {{ item.name }}
                          </NuxtLink>
                        </li>
                      </ul>
                    </li>
                    <li>
                      <div
                        class="text-xs font-semibold leading-6 text-zinc-400"
                      >
                        Bookmarks
                      </div>
                      <ul role="list" class="-mx-2 mt-2 space-y-1">
                        <li v-for="bookmark in bookmarks" :key="bookmark.name">
                          <NuxtLink
                            :href="bookmark.href"
                            :class="[
                              bookmark.current
                                ? 'bg-zinc-800 text-white'
                                : 'text-zinc-400 hover:text-white hover:bg-zinc-800',
                              'group flex gap-x-3 rounded-md p-2 text-sm leading-6 font-semibold',
                            ]"
                          >
                            <span
                              class="flex h-6 w-6 shrink-0 items-center justify-center rounded-lg border border-zinc-700 bg-zinc-800 text-[0.625rem] font-medium text-zinc-400 group-hover:text-white"
                              >{{ bookmark.name[0].toUpperCase() }}</span
                            >
                            <span class="truncate">{{ bookmark.name }}</span>
                          </NuxtLink>
                        </li>
                      </ul>
                    </li>
                  </ul>
                </nav>
              </div>
            </DialogPanel>
          </TransitionChild>
        </div>
      </Dialog>
    </TransitionRoot>

    <!-- Static sidebar for desktop -->
    <div
      class="hidden lg:fixed lg:inset-y-0 lg:z-50 lg:flex lg:w-72 lg:flex-col"
    >
      <!-- Sidebar component, swap this element with another sidebar if you like -->
      <div
        class="flex grow flex-col gap-y-5 overflow-y-auto bg-zinc-900 px-6 border-r-2 border-cyan-300"
      >
        <div class="flex h-16 shrink-0 items-center justify-center">
          <img
            class="h-8 w-auto"
            :src="'/api/v1/branding/banner'"
            alt="TradeCraft"
          />
        </div>
        <nav class="flex flex-1 flex-col">
          <ul role="list" class="flex flex-1 flex-col gap-y-7">
            <li>
              <ul role="list" class="-mx-2 space-y-1">
                <li v-for="(item, itemIdx) in navigation" :key="item.name">
                  <NuxtLink
                    :href="item.href"
                    :class="[
                      itemIdx == activeIndex
                        ? 'bg-zinc-800 text-cyan-200'
                        : 'text-zinc-400 hover:text-white hover:bg-zinc-800',
                      'group flex gap-x-3 rounded-md p-2 text-sm leading-6 font-semibold',
                    ]"
                  >
                    <component
                      :is="item.icon"
                      class="h-6 w-6 shrink-0"
                      aria-hidden="true"
                    />
                    {{ item.name }}
                  </NuxtLink>
                </li>
              </ul>
            </li>
            <li>
              <div class="text-xs font-semibold leading-6 text-zinc-400">
                Bookmarks
              </div>
              <ul role="list" class="-mx-2 mt-2 space-y-1">
                <li v-for="bookmark in bookmarks" :key="bookmark.name">
                  <NuxtLink
                    :href="bookmark.href"
                    :class="[
                      bookmark.current
                        ? 'bg-zinc-800 text-white'
                        : 'text-zinc-400 hover:text-white hover:bg-zinc-800',
                      'group flex gap-x-3 rounded-md p-2 text-sm leading-6 font-semibold',
                    ]"
                  >
                    <span
                      class="flex h-6 w-6 shrink-0 items-center justify-center rounded-lg border border-zinc-700 bg-zinc-800 text-[0.625rem] font-medium text-zinc-400 group-hover:text-white"
                      >{{ bookmark.name[0].toUpperCase() }}</span
                    >
                    <span class="truncate">{{ bookmark.name }}</span>
                  </NuxtLink>
                </li>
              </ul>
            </li>
            <li class="-mx-6 mt-auto">
              <NuxtLink
                href="/settings"
                class="flex items-center gap-x-4 px-6 py-3 text-sm font-semibold leading-6 text-white hover:bg-zinc-800"
              >
                <img
                  class="h-8 w-8 rounded-full bg-zinc-800"
                  :src="profilePictureURL"
                  alt=""
                />
                <span class="sr-only">Your profile</span>
                <span aria-hidden="true">{{ user.name }}</span>
              </NuxtLink>
            </li>
          </ul>
        </nav>
      </div>
    </div>

    <div
      class="sticky top-0 z-40 flex items-center gap-x-6 bg-zinc-900 px-4 py-4 shadow-sm sm:px-6 lg:hidden"
    >
      <button
        type="button"
        class="-m-2.5 p-2.5 text-zinc-400 lg:hidden"
        @click="sidebarOpen = true"
      >
        <span class="sr-only">Open sidebar</span>
        <Bars3Icon class="h-6 w-6" aria-hidden="true" />
      </button>
      <div class="flex-1" />
      <NuxtLink href="/settings">
        <span class="sr-only">Your profile</span>
        <img
          class="h-8 w-8 rounded-full bg-zinc-800"
          :src="profilePictureURL"
          alt=""
        />
      </NuxtLink>
    </div>

    <main class="lg:pl-72 min-h-screen flex flex-col items-center overflow-auto">
      <div class="flex flex-col items-center grow">
        <slot />
      </div>
      <div class="uppercase pb-2 text-xs text-zinc-700 font-bold tracking-widest text-center">
        NOT AN OFFICIAL MINECRAFT SERVICE. NOT APPROVED BY OR ASSOCIATED WITH MOJANG OR MICROSOFT. Powered by TradeCraft.
      </div>
    </main>
    
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import {
  Dialog,
  DialogPanel,
  TransitionChild,
  TransitionRoot,
} from "@headlessui/vue";
import {
  Bars3Icon,
  BuildingOffice2Icon,
  BuildingStorefrontIcon,
  ChartPieIcon,
  CurrencyDollarIcon,
  HomeIcon,
  XMarkIcon,
} from "@heroicons/vue/24/outline";

const navigation = [
  { name: "Dashboard", href: "/dashboard", icon: HomeIcon },
  { name: "Market", href: "/market", icon: BuildingStorefrontIcon },
  { name: "Sell", href: "/sell", icon: CurrencyDollarIcon },
  { name: "Companies", href: "/companies", icon: BuildingOffice2Icon },
  { name: "Assets", href: "/assets", icon: ChartPieIcon },
];
const bookmarks = [
  { id: 1, name: "DecDuck's General Goods", href: "#", current: false },
  { id: 2, name: "Globe Foundation", href: "#", current: false },
  { id: 3, name: "Ark", href: "#", current: false },
];

const sidebarOpen = ref(false);

const user = useUser();
const router = useRouter();
const route = useRoute();

try {
  user.value = await $fetch("/api/v1/auth/fetch");
} catch (e) {
  router.push("/link");
}

router.afterEach(() => {
  sidebarOpen.value = false;
});

const profilePictureURL = computed(
  () => `https://visage.surgeplay.com/bust/${user.value?.uuid}`
);

const activeIndex = computed(() =>
  navigation.findIndex((e) => route.path.startsWith(e.href))
);
</script>
