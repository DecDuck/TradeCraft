<template>
  <div>
    <div
      class="mx-auto max-w-2xl px-4 py-16 sm:px-6 sm:py-24 lg:max-w-7xl lg:px-8"
    >
      <div class="lg:grid lg:grid-cols-2 lg:items-start lg:gap-x-8">
        <!-- Image gallery -->
        <TabGroup as="div" class="flex flex-col-reverse">
          <!-- Image selector -->
          <div
            class="mx-auto mt-6 hidden w-full max-w-2xl sm:block lg:max-w-none"
          >
            <TabList class="grid grid-cols-4 gap-6">
              <Tab
                v-for="image in product.images"
                :key="image.id"
                class="transition-all relative flex h-24 cursor-pointer items-center justify-center rounded-md bg-zinc-800 text-sm font-medium uppercase text-zinc-900 hover:bg-zinc-700 hover:scale-105"
                v-slot="{ selected }"
              >
                <span class="sr-only">{{ image.name }}</span>
                <span class="absolute inset-0 overflow-hidden rounded-md">
                  <img
                    :src="image.src"
                    alt=""
                    class="h-full w-full object-cover object-center"
                    :style="`transform: ${image.transform}`"
                  />
                </span>
                <span
                  :class="[
                    selected ? 'ring-cyan-600' : 'ring-transparent',
                    'pointer-events-none absolute inset-0 rounded-md ring',
                  ]"
                  aria-hidden="true"
                />
              </Tab>
            </TabList>
          </div>

          <TabPanels class="aspect-h-1 aspect-w-1 w-full">
            <TabPanel v-for="image in product.images" :key="image.id">
              <img
                :src="image.src"
                :alt="image.alt"
                class="bg-zinc-800 w-full h-full rounded-3xl shadow-xl shadow-zinc-900 object-cover object-center"
                :style="`transform: ${image.transform} scale(.65);`"
              />
            </TabPanel>
          </TabPanels>
        </TabGroup>

        <!-- Product info -->
        <div class="mt-10 px-4 sm:mt-16 sm:px-0 lg:mt-0">
          <h1 class="text-3xl font-bold tracking-tight text-zinc-100">
            {{ product.name }}
          </h1>

          <div class="mt-3">
            <h2 class="sr-only">Product information</h2>
            <p class="text-3xl tracking-tight text-zinc-100">
              {{ product.price }}
            </p>
          </div>

          <!-- Reviews -->
          <div class="mt-3">
            <h3 class="sr-only">Reviews</h3>
            <div class="flex items-center">
              <div class="flex items-center">
                <StarIcon
                  v-for="rating in [0, 1, 2, 3, 4]"
                  :key="rating"
                  :class="[
                    product.rating > rating ? 'text-cyan-200' : 'text-zinc-700',
                    'h-5 w-5 flex-shrink-0',
                  ]"
                  aria-hidden="true"
                />
              </div>
              <p class="sr-only">{{ product.rating }} out of 5 stars</p>
            </div>
          </div>

          <div class="mt-6">
            <h3 class="sr-only">Description</h3>

            <div
              class="space-y-6 text-base text-zinc-400"
              v-html="product.description"
            />
          </div>

          <form class="mt-6">
            <!-- Colors -->
            <div>
              <h3 class="text-md text-zinc-200 font-semibold">Vendor</h3>

              <NuxtLink
                href="/marketplace/vendor/DecDuck"
                class="mt-1 flex items-center gap-x-4 px-1 rounded-md py-3 text-sm font-semibold leading-6 text-white hover:bg-zinc-800"
              >
                <img
                  class="h-8 w-8 rounded-full bg-zinc-800"
                  :src="product.vender.picture"
                  alt=""
                />
                <span class="sr-only">Vendor: {{ product.vender.name }}</span>
                <span aria-hidden="true">{{ product.vender.name }}</span>
              </NuxtLink>
            </div>

            <div class="mt-6 flex">
              <button
                type="submit"
                class="flex max-w-xs flex-1 items-center justify-center rounded-md border border-transparent bg-cyan-600 px-8 py-3 text-base font-medium text-white hover:bg-cyan-700 focus:outline-none focus:ring-2 focus:ring-cyan-500 focus:ring-offset-2 focus:ring-offset-zinc-50 sm:w-full"
              >
                Add to bag
              </button>

              <button
                type="button"
                class="ml-4 flex items-center justify-center rounded-md px-3 py-3 text-zinc-400 hover:bg-zinc-900 hover:text-cyan-200"
              >
                <HeartIcon class="h-6 w-6 flex-shrink-0" aria-hidden="true" />
                <span class="sr-only">Add to favorites</span>
              </button>
            </div>
          </form>

          <section aria-labelledby="details-heading" class="mt-12">
            <h2 id="details-heading" class="sr-only">Additional details</h2>

            <div class="divide-y divide-zinc-800 border-t border-zinc-800">
              <Disclosure
                as="div"
                v-for="detail in product.details"
                :key="detail.name"
                v-slot="{ open }"
              >
                <h3>
                  <DisclosureButton
                    class="relative flex w-full items-center justify-between py-6 text-left"
                  >
                    <span
                      :class="[
                        open ? 'text-cyan-200' : 'text-zinc-100',
                        'text-sm font-medium',
                      ]"
                      >{{ detail.name }}</span
                    >
                    <span class="ml-6 flex items-center">
                      <PlusIcon
                        v-if="!open"
                        class="block h-6 w-6 text-zinc-400"
                        aria-hidden="true"
                      />
                      <MinusIcon
                        v-else
                        class="block h-6 w-6 text-cyan-200"
                        aria-hidden="true"
                      />
                    </span>
                  </DisclosureButton>
                </h3>
                <DisclosurePanel
                  as="div"
                  class="prose prose-invert prose-sm pb-6"
                >
                  <ul role="list">
                    <li v-for="item in detail.items" :key="item">{{ item }}</li>
                  </ul>
                </DisclosurePanel>
              </Disclosure>
            </div>
          </section>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {
  Disclosure,
  DisclosureButton,
  DisclosurePanel,
  Tab,
  TabGroup,
  TabList,
  TabPanel,
  TabPanels,
} from "@headlessui/vue";
import { StarIcon } from "@heroicons/vue/20/solid";
import { HeartIcon, MinusIcon, PlusIcon } from "@heroicons/vue/24/outline";

const product = {
  name: "Raw Chicken",
  price: "$99",
  rating: 5,
  images: [
    {
      id: 1,
      name: "Diamond angle 1",
      src: "/api/v1/asset/item/chicken.png?size=256",
      alt: "Angled front view with bag zipped and handles upright.",
      transform: "skewY(12deg)",
    },
    {
      id: 2,
      name: "Diamond angle 2",
      src: "/api/v1/asset/item/chicken.png?size=1024",
      alt: "Angled front view with bag zipped and handles upright.",
      transform: "skewY(-12deg)",
    },
    {
      id: 3,
      name: "Diamond angle 3",
      src: "/api/v1/asset/item/chicken.png?size=1024",
      alt: "Angled front view with bag zipped and handles upright.",
      transform: "rotate(34deg)",
    },
    // More images...
  ],
  vender: {
    name: "DecDuck",
    picture:
      "https://visage.surgeplay.com/bust/b2f23907-2e05-43b3-a650-155ccb33c38c",
  },
  description: `
      <p>The absolute sexiest Raw Chicken you've ever seen. Check out the fucking awesome promotional pictures we took for this product, I can't believe it. I just want to eat it so so bad.</p>
    `,
  details: [
    {
      name: "Features",
      items: [
        "Multiple strap configurations",
        "Spacious interior with top zip",
        "Leather handle and tabs",
        "Interior dividers",
        "Stainless strap loops",
        "Double stitched construction",
        "Water-resistant",
      ],
    },
    // More sections...
  ],
};
</script>
