<template>
  <div class="px-16 py-16 sm:px-6 sm:py-24 lg:max-w-7xl lg:px-8">
    <div class="md:flex md:items-center md:justify-between">
      <h2 class="text-2xl font-bold tracking-tight text-zinc-100">
        {{ props.name }}
      </h2>
      <NuxtLink
        :href="props.fullpage"
        class="hidden text-sm font-medium text-cyan-200 hover:text-cyan-300 md:block"
      >
        Shop the collection
        <span aria-hidden="true"> &rarr;</span>
      </NuxtLink>
    </div>

    <div
      class="mt-6 grid grid-cols-2 gap-x-4 gap-y-10 sm:gap-x-6 sm:grid-cols-4 md:gap-y-0 lg:gap-x-12"
    >
      <div v-for="product in products" :id="product.id" class="group relative">
        <div
          class="w-full overflow-hidden rounded-md bg-zinc-900 group-hover:opacity-75"
        >
          <img
            :src="product.preview"
            :alt="`${product.name} product image`"
            class="h-full w-full p-2 md:p-4 object-cover object-center"
          />
        </div>
        <h3 class="mt-4 text-sm text-zinc-300 font-semibold">
          <NuxtLink
            class="inline-flex gap-x-2 items-center"
            :href="`/marketplace/l/${product.id}`"
          >
            <span class="absolute inset-0" />
            {{ product.name }}
            <div
              v-if="product.sale"
              class="hidden lg:block text-sm bg-cyan-200 rounded-full font-semibold text-zinc-900 px-1.5 py-.5"
            >
              {{ product.saleAmount }}% off
            </div>
          </NuxtLink>
        </h3>
        <p v-if="!product.sale" class="mt-1 text-sm font-medium text-zinc-400">
          ${{ product.centsPerUnit / 100 }}
        </p>
        <span class="inline-flex items-center gap-x-1" v-else>
          <p class="text-sm font-bold text-cyan-200">
            ${{ product.finalPrice / 100 }}
          </p>
          <p class="text-sm text-zinc-400 line-through">
            ${{ product.centsPerUnit / 100 }}
          </p>
        </span>
        <div
          v-if="product.sale"
          class="mt-2 w-fit block lg:hidden text-sm bg-cyan-200 rounded-full font-semibold text-zinc-900 px-1.5 py-.5"
        >
          {{ product.saleAmount }}% off
        </div>
      </div>
    </div>

    <div class="mt-8 text-sm md:hidden">
      <NuxtLink
        :href="props.fullpage"
        class="font-medium text-cyan-200 hover:text-cyan-300"
      >
        Shop more
        <span aria-hidden="true"> &rarr;</span>
      </NuxtLink>
    </div>
  </div>
</template>

<script setup lang="ts">
const props = defineProps(["products", "name", "fullpage"]);

const products = computed(() =>
  props.products.slice(0, 4).map((product: any) => {
    const firstItem = product.items[0];
    product.preview = createAssetUrl(firstItem.key) + "?size=512";
    product.name = createListingName(product.items, product.title);
    if (product.saleMultipler != 1) {
      product.sale = true;
      product.saleAmount = 100 - product.saleMultipler * 100;
      product.finalPrice = Math.round(
        product.centsPerUnit * product.saleMultipler
      );
    }
    return product;
  })
);
</script>
