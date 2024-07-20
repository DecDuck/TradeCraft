<!--
  This example requires some changes to your config:
  
  ```
  // tailwind.config.js
  module.exports = {
    // ...
    plugins: [
      // ...
      require('@tailwindcss/aspect-ratio'),
    ],
  }
  ```
-->
<template>
  <div class="bg-zinc-900 min-h-screen">
    <!-- Mobile menu -->
    <TransitionRoot as="template" :show="open">
      <Dialog class="relative z-40 lg:hidden" @close="open = false">
        <TransitionChild
          as="template"
          enter="transition-opacity ease-linear duration-300"
          enter-from="opacity-0"
          enter-to="opacity-100"
          leave="transition-opacity ease-linear duration-300"
          leave-from="opacity-100"
          leave-to="opacity-0"
        >
          <div class="fixed inset-0 bg-black bg-opacity-25" />
        </TransitionChild>

        <div class="fixed inset-0 z-40 flex">
          <TransitionChild
            as="template"
            enter="transition ease-in-out duration-300 transform"
            enter-from="-translate-x-full"
            enter-to="translate-x-0"
            leave="transition ease-in-out duration-300 transform"
            leave-from="translate-x-0"
            leave-to="-translate-x-full"
          >
            <DialogPanel
              class="relative flex w-full max-w-sm flex-col overflow-y-auto bg-zinc-900 pb-12 shadow-xl"
            >
              <div class="w-full flex flex-row justify-between px-4 pb-2 pt-5">
                <div class="flex">
                  <button
                    type="button"
                    class="-m-2 inline-flex items-center justify-center rounded-md p-2 text-zinc-500"
                    @click="open = false"
                  >
                    <span class="sr-only">Close menu</span>
                    <XMarkIcon class="h-6 w-6" aria-hidden="true" />
                  </button>
                </div>
                <div class="flex items-center">
                  <!-- Search -->
                  <a href="#" class="p-2 text-zinc-500 hover:text-zinc-600">
                    <span class="sr-only">Search</span>
                    <MagnifyingGlassIcon class="h-6 w-6" aria-hidden="true" />
                  </a>

                  <!-- Account -->
                  <NuxtLink
                    href="/account"
                    class="p-2 text-zinc-500 hover:text-zinc-600"
                  >
                    <span class="sr-only">Account</span>
                    <UserIcon class="h-6 w-6" aria-hidden="true" />
                  </NuxtLink>

                  <!-- Cart -->
                  <div class="ml-2 flow-root">
                    <a href="#" class="group -m-2 flex items-center p-2">
                      <ShoppingBagIcon
                        class="h-6 w-6 flex-shrink-0 text-zinc-500 group-hover:text-zinc-600"
                        aria-hidden="true"
                      />
                      <span
                        class="ml-2 text-sm font-medium text-zinc-400 group-hover:text-zinc-500"
                        >0</span
                      >
                      <span class="sr-only">items in cart, view bag</span>
                    </a>
                  </div>
                </div>
              </div>

              <!-- Links -->
              <TabGroup as="div" class="mt-2">
                <div class="border-b border-zinc-700">
                  <TabList class="-mb-px flex space-x-8 px-4">
                    <Tab
                      as="template"
                      v-for="category in navigation.categories"
                      :key="category.name"
                      v-slot="{ selected }"
                    >
                      <button
                        :class="[
                          selected
                            ? 'border-cyan-200 text-cyan-200'
                            : 'border-transparent text-zinc-200',
                          'flex-1 whitespace-nowrap border-b-2 px-1 py-4 text-base font-medium',
                        ]"
                      >
                        {{ category.name }}
                      </button>
                    </Tab>
                  </TabList>
                </div>
                <TabPanels as="template">
                  <TabPanel
                    v-for="category in navigation.categories"
                    :key="category.name"
                    class="space-y-10 px-4 pb-8 pt-10"
                  >
                    <div class="space-y-4">
                      <div
                        v-for="(item, itemIdx) in category.featured"
                        :key="itemIdx"
                        class="group aspect-h-1 aspect-w-1 relative overflow-hidden rounded-md bg-zinc-900"
                      >
                        <img
                          :src="item.imageSrc"
                          :alt="item.imageAlt"
                          class="object-cover object-center group-hover:opacity-75"
                        />
                        <div class="flex flex-col justify-end">
                          <div
                            class="bg-zinc-900 bg-opacity-60 p-4 text-base sm:text-sm"
                          >
                            <a
                              :href="item.href"
                              class="font-medium text-zinc-200"
                            >
                              <span
                                class="absolute inset-0"
                                aria-hidden="true"
                              />
                              {{ item.name }}
                            </a>
                            <p
                              aria-hidden="true"
                              class="mt-0.5 text-zinc-300 sm:mt-1"
                            >
                              Shop now
                            </p>
                          </div>
                        </div>
                      </div>
                    </div>
                    <div
                      v-for="(column, columnIdx) in category.sections"
                      :key="columnIdx"
                      class="space-y-10"
                    >
                      <div v-for="section in column" :key="section.name">
                        <p
                          :id="`${category.id}-${section.id}-heading-mobile`"
                          class="font-medium text-zinc-200"
                        >
                          {{ section.name }}
                        </p>
                        <ul
                          role="list"
                          :aria-labelledby="`${category.id}-${section.id}-heading-mobile`"
                          class="mt-6 flex flex-col space-y-6"
                        >
                          <li
                            v-for="item in section.items"
                            :key="item.name"
                            class="flow-root"
                          >
                            <a
                              :href="item.href"
                              class="-m-2 block p-2 text-gray-400"
                              >{{ item.name }}</a
                            >
                          </li>
                        </ul>
                      </div>
                    </div>
                  </TabPanel>
                </TabPanels>
              </TabGroup>

              <div class="space-y-6 border-t border-zinc-700 px-4 py-6">
                <div
                  v-for="page in navigation.pages"
                  :key="page.name"
                  class="flow-root"
                >
                  <a
                    :href="page.href"
                    class="-m-2 block p-2 font-medium text-zinc-200"
                    >{{ page.name }}</a
                  >
                </div>
              </div>
            </DialogPanel>
          </TransitionChild>
        </div>
      </Dialog>
    </TransitionRoot>

    <header class="relative bg-zinc-900">
      <nav aria-label="Top" class="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        <div class="border-b border-zinc-700">
          <div class="flex h-16 items-center justify-between">
            <div class="flex flex-1 items-center lg:hidden">
              <button
                type="button"
                class="-ml-2 rounded-md p-2 text-zinc-500"
                @click="open = true"
              >
                <span class="sr-only">Open menu</span>
                <Bars3Icon class="h-6 w-6" aria-hidden="true" />
              </button>
            </div>

            <!-- Flyout menus -->
            <PopoverGroup class="hidden lg:block lg:flex-1 lg:self-stretch">
              <div class="flex h-full space-x-8">
                <Popover
                  v-for="category in navigation.categories"
                  :key="category.name"
                  class="flex"
                  v-slot="{ open }"
                >
                  <div class="relative flex">
                    <PopoverButton
                      :class="[
                        open
                          ? 'text-cyan-200'
                          : 'text-zinc-500 hover:text-zinc-600',
                        'relative z-10 flex items-center justify-center text-sm font-medium transition-colors duration-200 ease-out',
                      ]"
                    >
                      {{ category.name }}
                      <span
                        :class="[
                          open ? 'bg-cyan-200' : '',
                          'absolute inset-x-0 bottom-0 h-0.5 transition-colors duration-200 ease-out sm:mt-5 sm:translate-y-px sm:transform',
                        ]"
                        aria-hidden="true"
                      />
                    </PopoverButton>
                  </div>

                  <transition
                    enter-active-class="transition ease-out duration-200"
                    enter-from-class="opacity-0"
                    enter-to-class="opacity-100"
                    leave-active-class="transition ease-in duration-150"
                    leave-from-class="opacity-100"
                    leave-to-class="opacity-0"
                  >
                    <PopoverPanel class="absolute inset-x-0 top-full z-50">
                      <!-- Presentational element used to render the bottom shadow, if we put the shadow on the actual panel it pokes out the top, so we use this shorter element to hide the top of the shadow -->
                      <div
                        class="absolute inset-0 top-1/2 bg-white shadow"
                        aria-hidden="true"
                      />

                      <div
                        class="relative bg-zinc-900 border-b border-zinc-700"
                      >
                        <div class="mx-auto max-w-7xl px-8">
                          <div class="grid grid-cols-2 gap-x-8 gap-y-10 py-16">
                            <div
                              class="grid grid-cols-2 grid-rows-1 gap-8 text-sm"
                            >
                              <div
                                v-for="(item, itemIdx) in category.featured"
                                :key="item.name"
                                :class="[
                                  itemIdx === 0 ? 'aspect-w-2 col-span-2' : '',
                                  'group aspect-h-1 aspect-w-1 relative overflow-hidden rounded-md bg-zinc-900',
                                ]"
                              >
                                <img
                                  :src="item.imageSrc"
                                  :alt="item.imageAlt"
                                  class="transition-all object-cover object-center group-hover:opacity-75"
                                />
                                <div class="flex flex-col justify-end">
                                  <div
                                    class="bg-zinc-900 bg-opacity-60 p-4 text-sm"
                                  >
                                    <a
                                      :href="item.href"
                                      class="font-medium text-zinc-200"
                                    >
                                      <span
                                        class="absolute inset-0"
                                        aria-hidden="true"
                                      />
                                      {{ item.name }}
                                    </a>
                                    <p
                                      aria-hidden="true"
                                      class="mt-0.5 text-zinc-300 sm:mt-1"
                                    >
                                      Shop now
                                    </p>
                                  </div>
                                </div>
                              </div>
                            </div>
                            <div
                              class="grid grid-cols-3 gap-x-8 gap-y-10 text-sm text-zinc-400"
                            >
                              <div
                                v-for="(column, columnIdx) in category.sections"
                                :key="columnIdx"
                                class="space-y-10"
                              >
                                <div
                                  v-for="section in column"
                                  :key="section.name"
                                >
                                  <p
                                    :id="`${category.id}-${section.id}-heading`"
                                    class="font-medium text-zinc-200"
                                  >
                                    {{ section.name }}
                                  </p>
                                  <ul
                                    role="list"
                                    :aria-labelledby="`${category.id}-${section.id}-heading`"
                                    class="mt-4 space-y-4"
                                  >
                                    <li
                                      v-for="item in section.items"
                                      :key="item.name"
                                      class="flex"
                                    >
                                      <a
                                        :href="item.href"
                                        class="hover:text-zinc-500"
                                        >{{ item.name }}</a
                                      >
                                    </li>
                                  </ul>
                                </div>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </PopoverPanel>
                  </transition>
                </Popover>

                <a
                  v-for="page in navigation.pages"
                  :key="page.name"
                  :href="page.href"
                  class="flex items-center text-sm font-medium text-zinc-500 hover:text-zinc-600"
                  >{{ page.name }}</a
                >
              </div>
            </PopoverGroup>

            <!-- Logo -->
            <NuxtLink href="/" class="flex">
              <span class="sr-only">Your Company</span>
              <img class="h-8 w-auto" :src="'/api/v1/branding/banner'" alt="" />
            </NuxtLink>

            <div class="flex flex-1 items-center justify-end">
              <!-- Search -->
              <a href="#" class="ml-6 p-2 text-zinc-500 hover:text-zinc-600">
                <span class="sr-only">Search</span>
                <MagnifyingGlassIcon class="h-6 w-6" aria-hidden="true" />
              </a>

              <!-- Account -->
              <NuxtLink
                href="/account"
                class="hidden lg:block p-2 text-zinc-500 hover:text-zinc-600 lg:ml-4"
              >
                <span class="sr-only">Account</span>
                <UserIcon class="h-6 w-6" aria-hidden="true" />
              </NuxtLink>

              <!-- Cart -->
              <div class="ml-4 flow-root lg:ml-6">
                <a href="#" class="group -m-2 flex items-center p-2">
                  <ShoppingBagIcon
                    class="h-6 w-6 flex-shrink-0 text-zinc-500 group-hover:text-zinc-600"
                    aria-hidden="true"
                  />
                  <span
                    class="ml-2 text-sm font-medium text-zinc-400 group-hover:text-zinc-500"
                    >0</span
                  >
                  <span class="sr-only">items in cart, view bag</span>
                </a>
              </div>
            </div>
          </div>
        </div>
      </nav>
    </header>

    <div class="grow flex flex-col justify-center">
      <slot />
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import {
  Dialog,
  DialogPanel,
  Popover,
  PopoverButton,
  PopoverGroup,
  PopoverPanel,
  Tab,
  TabGroup,
  TabList,
  TabPanel,
  TabPanels,
  TransitionChild,
  TransitionRoot,
} from "@headlessui/vue";
import {
  Bars3Icon,
  MagnifyingGlassIcon,
  ShoppingBagIcon,
  UserIcon,
  XMarkIcon,
} from "@heroicons/vue/24/outline";

const navigation = {
  categories: [
    {
      id: "women",
      name: "Women",
      featured: [
        {
          name: "New Arrivals",
          href: "#",
          imageSrc:
            "https://tailwindui.com/img/ecommerce-images/mega-menu-category-01.jpg",
          imageAlt:
            "Models sitting back to back, wearing Basic Tee in black and bone.",
        },
        {
          name: "Basic Tees",
          href: "#",
          imageSrc:
            "https://tailwindui.com/img/ecommerce-images/mega-menu-category-02.jpg",
          imageAlt:
            "Close up of Basic Tee fall bundle with off-white, ochre, olive, and black tees.",
        },
        {
          name: "Accessories",
          href: "#",
          imageSrc:
            "https://tailwindui.com/img/ecommerce-images/mega-menu-category-03.jpg",
          imageAlt:
            "Model wearing minimalist watch with black wristband and white watch face.",
        },
      ],
      sections: [
        [
          {
            id: "shoes",
            name: "Shoes & Accessories",
            items: [
              { name: "Sneakers", href: "#" },
              { name: "Boots", href: "#" },
              { name: "Flats", href: "#" },
              { name: "Sandals", href: "#" },
              { name: "Heels", href: "#" },
              { name: "Socks", href: "#" },
            ],
          },
          {
            id: "collection",
            name: "Shop Collection",
            items: [
              { name: "Everything", href: "#" },
              { name: "Core", href: "#" },
              { name: "New Arrivals", href: "#" },
              { name: "Sale", href: "#" },
              { name: "Accessories", href: "#" },
            ],
          },
        ],
        [
          {
            id: "clothing",
            name: "All Clothing",
            items: [
              { name: "Basic Tees", href: "#" },
              { name: "Artwork Tees", href: "#" },
              { name: "Tops", href: "#" },
              { name: "Bottoms", href: "#" },
              { name: "Swimwear", href: "#" },
              { name: "Underwear", href: "#" },
            ],
          },
          {
            id: "accessories",
            name: "All Accessories",
            items: [
              { name: "Watches", href: "#" },
              { name: "Wallets", href: "#" },
              { name: "Bags", href: "#" },
              { name: "Sunglasses", href: "#" },
              { name: "Hats", href: "#" },
              { name: "Belts", href: "#" },
            ],
          },
        ],
        [
          {
            id: "brands",
            name: "Brands",
            items: [
              { name: "Full Nelson", href: "#" },
              { name: "My Way", href: "#" },
              { name: "Re-Arranged", href: "#" },
              { name: "Counterfeit", href: "#" },
              { name: "Significant Other", href: "#" },
            ],
          },
        ],
      ],
    },
    {
      id: "men",
      name: "Men",
      featured: [
        {
          name: "Accessories",
          href: "#",
          imageSrc:
            "https://tailwindui.com/img/ecommerce-images/home-page-03-category-01.jpg",
          imageAlt:
            "Wooden shelf with gray and olive drab green baseball caps, next to wooden clothes hanger with sweaters.",
        },
        {
          name: "New Arrivals",
          href: "#",
          imageSrc:
            "https://tailwindui.com/img/ecommerce-images/product-page-04-detail-product-shot-01.jpg",
          imageAlt:
            "Drawstring top with elastic loop closure and textured interior padding.",
        },
        {
          name: "Artwork Tees",
          href: "#",
          imageSrc:
            "https://tailwindui.com/img/ecommerce-images/category-page-02-image-card-06.jpg",
          imageAlt:
            "Three shirts in gray, white, and blue arranged on table with same line drawing of hands and shapes overlapping on front of shirt.",
        },
      ],
      sections: [
        [
          {
            id: "shoes",
            name: "Shoes & Accessories",
            items: [
              { name: "Sneakers", href: "#" },
              { name: "Boots", href: "#" },
              { name: "Sandals", href: "#" },
              { name: "Socks", href: "#" },
            ],
          },
          {
            id: "collection",
            name: "Shop Collection",
            items: [
              { name: "Everything", href: "#" },
              { name: "Core", href: "#" },
              { name: "New Arrivals", href: "#" },
              { name: "Sale", href: "#" },
            ],
          },
        ],
        [
          {
            id: "clothing",
            name: "All Clothing",
            items: [
              { name: "Basic Tees", href: "#" },
              { name: "Artwork Tees", href: "#" },
              { name: "Pants", href: "#" },
              { name: "Hoodies", href: "#" },
              { name: "Swimsuits", href: "#" },
            ],
          },
          {
            id: "accessories",
            name: "All Accessories",
            items: [
              { name: "Watches", href: "#" },
              { name: "Wallets", href: "#" },
              { name: "Bags", href: "#" },
              { name: "Sunglasses", href: "#" },
              { name: "Hats", href: "#" },
              { name: "Belts", href: "#" },
            ],
          },
        ],
        [
          {
            id: "brands",
            name: "Brands",
            items: [
              { name: "Re-Arranged", href: "#" },
              { name: "Counterfeit", href: "#" },
              { name: "Full Nelson", href: "#" },
              { name: "My Way", href: "#" },
            ],
          },
        ],
      ],
    },
  ],
  pages: [
    { name: "Company", href: "#" },
    { name: "Stores", href: "#" },
  ],
};

const open = ref(false);

const user = useUser();
const router = useRouter();
const route = useRoute();

try {
  user.value = await $fetch("/api/v1/auth/fetch");
} catch (e) {
  router.push("/link");
}

router.afterEach(() => {
  open.value = false;
});

const profilePictureURL = computed(
  () => `https://visage.surgeplay.com/bust/${user.value?.uuid}`
);

const activeIndex = computed(() =>
  navigation.findIndex((e) => route.path.startsWith(e.href))
);
</script>
