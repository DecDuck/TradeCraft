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
            <TabList
              v-if="meta.images.length > 1"
              class="grid grid-cols-4 gap-6"
            >
              <Tab
                v-for="image in meta.images"
                :key="image.id"
                class="transition-all relative flex h-24 cursor-pointer bg-zinc-800 items-center justify-center rounded-md text-sm font-medium uppercase text-zinc-900 hover:bg-zinc-700 hover:scale-105"
                v-slot="{ selected }"
              >
                <span class="sr-only">{{ image.name }}</span>
                <span class="absolute inset-0 rounded-md overflow-hidden">
                  <img
                    :src="image.src"
                    alt=""
                    class="absolute left-1/2 h-full w-auto aspect-1 object-cover object-center bg-zinc-900 rounded-3xl p-2"
                    :style="`transform: translate(-50%) scale(75%) ${image.transform}`"
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
            <TabPanel v-for="image in meta.images" :key="image.id">
              <img
                :src="image.src"
                :alt="image.alt"
                :class="[
                  image.render ? `` : `bg-zinc-800 shadow-xl shadow-zinc-900`,
                  ' w-full h-full rounded-3xl object-cover object-center',
                ]"
                :style="`transform: ${
                  image.render ? '' : `${image.transform} scale(.65);`
                }`"
              />
            </TabPanel>
          </TabPanels>
        </TabGroup>

        <!-- Product info -->
        <div class="mt-10 px-4 sm:mt-16 sm:px-0 lg:mt-0">
          <h1 class="text-3xl font-bold tracking-tight text-zinc-100">
            {{ meta.name }}
          </h1>

          <div class="mt-3">
            <h2 class="sr-only">Product information</h2>
            <p v-if="!meta.sale" class="text-3xl tracking-tight text-zinc-100">
              ${{ product.centsPerUnit / 100 }}
            </p>
            <span
              v-else
              class="inline-flex items-center gap-x-4 text-3xl tracking-tight"
            >
              <p class="text-cyan-200 font-bold">
                ${{ meta.saleFinalPrice / 100 }}
              </p>
              <p class="text-zinc-100 line-through">
                ${{ product.centsPerUnit / 100 }}
              </p>
              <div
                class="text-lg bg-cyan-200 text-zinc-950 font-semibold py-1 px-3 rounded-full"
              >
                {{ meta.saleAmount }}% off!
              </div>
            </span>
          </div>

          <div class="mt-6">
            <h3 class="sr-only">Description</h3>

            <div
              class="space-y-6 text-base text-zinc-400"
              v-html="product.description"
            />
          </div>

          <form class="mt-6">
            <div v-if="false">
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
                Add to cart
              </button>
            </div>
          </form>

          <section aria-labelledby="details-heading" class="mt-12">
            <h2 id="details-heading" class="sr-only">Additional details</h2>

            <div class="divide-y divide-zinc-800 border-t border-zinc-800">
              <Disclosure
                as="div"
                v-for="detail in Object.entries(product.features)"
                :key="detail[0]"
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
                      >{{ detail[0] }}</span
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
                    <li v-for="item in detail[1]" :key="item">{{ item }}</li>
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

<script setup lang="ts">
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
import { MinusIcon, PlusIcon } from "@heroicons/vue/24/outline";
import type { Listing } from "~/types/listing";

const route = useRoute();

const id = route.params.listingid.toString();

const product: Listing = await $fetch(
  `/api/v1/market/l/fetch?id=${encodeURIComponent(id)}`
);

const meta: {
  sale: boolean;
  saleAmount: number;
  saleFinalPrice: number;
  name: string;
  images: any[];
} = {
  sale: false,
  saleAmount: 0,
  saleFinalPrice: 0,
  name: "",
  images: [],
};

if (product.saleMultipler != 1) {
  meta.sale = true;
  meta.saleAmount = 100 - product.saleMultipler * 100;
  meta.saleFinalPrice = Math.round(
    product.centsPerUnit * product.saleMultipler
  );
}

meta.name = createListingName(product.items);

let pictureTransforms = -1;

meta.images = product.items.map((e, i) => {
  const isBlock = e.key.startsWith("block");
  if (isBlock) {
    return {
      id: i,
      src: createAssetUrl(e.key),
      alt: `${e.name} product image`,
      render: true,
    };
  } else {
    pictureTransforms++;
    pictureTransforms %= product.pictureTransforms.length;
    return {
      id: 1,
      src: createAssetUrl(e.key),
      alt: `${e.name} product image`,
      transform: product.pictureTransforms,
    };
  }
});

useHead({
  title: meta.name,
});
</script>
