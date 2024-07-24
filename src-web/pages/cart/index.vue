<template>
  <main class="mx-auto max-w-2xl px-4 pb-24 pt-16 sm:px-6 lg:max-w-7xl lg:px-8">
    <h1 class="text-3xl font-bold tracking-tight text-zinc-200 sm:text-4xl">
      Shopping Cart
    </h1>

    <form
      class="mt-12 lg:grid lg:grid-cols-12 lg:items-start lg:gap-x-12 xl:gap-x-16"
    >
      <section aria-labelledby="cart-heading" class="lg:col-span-7">
        <h2 id="cart-heading" class="sr-only">Items in your shopping cart</h2>

        <ul
          role="list"
          class="divide-y divide-zinc-700 border-b border-t border-zinc-700"
        >
          <li
            v-for="(product, productIdx) in products"
            :key="product.id"
            class="flex py-6 sm:py-10"
          >
            <div class="flex-shrink-0">
              <img
                :src="product.imageSrc"
                :alt="product.imageAlt"
                class="h-24 w-24 rounded-md object-cover object-center sm:h-48 sm:w-48"
              />
            </div>

            <div class="ml-4 flex flex-1 flex-col justify-between sm:ml-6">
              <div
                class="relative pr-9 sm:grid sm:grid-cols-2 sm:gap-x-6 sm:pr-0"
              >
                <div>
                  <div class="flex justify-between">
                    <h3 class="text-sm">
                      <a
                        :href="product.href"
                        class="font-bold text-zinc-300 hover:text-zinc-200"
                        >{{ product.name }}</a
                      >
                    </h3>
                  </div>
                  <div class="mt-1 flex text-sm">
                    <p class="text-zinc-400">{{ product.color }}</p>
                    <p
                      v-if="product.size"
                      class="ml-4 border-l border-zinc-700 pl-4 text-zinc-400"
                    >
                      {{ product.size }}
                    </p>
                  </div>
                  <p class="mt-1 text-sm font-bold text-cyan-200">
                    {{ product.price }}
                  </p>
                </div>

                <div class="mt-4 sm:mt-0 sm:pr-9">
                  <label :for="`quantity-${productIdx}`" class="sr-only"
                    >Quantity, {{ product.name }}</label
                  >
                  <select
                    :id="`quantity-${productIdx}`"
                    :name="`quantity-${productIdx}`"
                    class="max-w-full rounded-md bg-zinc-900 border border-zinc-700 py-1.5 text-left text-base font-bold leading-5 text-zinc-200 shadow-sm focus:border-cyan-200 focus:outline-none focus:ring-1 focus:ring-cyan-200 sm:text-sm"
                  >
                    <option value="1">1</option>
                    <option value="2">2</option>
                    <option value="3">3</option>
                    <option value="4">4</option>
                    <option value="5">5</option>
                    <option value="6">6</option>
                    <option value="7">7</option>
                    <option value="8">8</option>
                  </select>

                  <div class="absolute right-0 top-0">
                    <button
                      type="button"
                      class="-m-2 inline-flex p-2 text-gray-400 hover:text-gray-500"
                    >
                      <span class="sr-only">Remove</span>
                      <XMarkIconMini class="h-5 w-5" aria-hidden="true" />
                    </button>
                  </div>
                </div>
              </div>

              <p class="mt-4 flex space-x-2 text-sm text-zinc-400">
                <CheckIcon
                  v-if="product.inStock"
                  class="h-5 w-5 flex-shrink-0 text-green-500"
                  aria-hidden="true"
                />
                <ClockIcon
                  v-else
                  class="h-5 w-5 flex-shrink-0 text-zinc-700"
                  aria-hidden="true"
                />
                <span>{{
                  product.inStock ? "In stock" : `Ships in ${product.leadTime}`
                }}</span>
              </p>
            </div>
          </li>
        </ul>
      </section>

      <!-- Order summary -->
      <section
        aria-labelledby="summary-heading"
        class="mt-16 rounded-lg bg-zinc-800/20 px-4 py-6 sm:p-6 lg:col-span-5 lg:mt-0 lg:p-8"
      >
        <h2 id="summary-heading" class="text-lg font-bold text-zinc-200">
          Order summary
        </h2>

        <dl class="mt-6 space-y-4">
          <div class="flex items-center justify-between">
            <dt class="text-sm text-zinc-400">Subtotal</dt>
            <dd class="text-sm font-bold text-cyan-200">$99.00</dd>
          </div>
          <div
            class="flex items-center justify-between border-t border-zinc-700 pt-4"
          >
            <dt class="flex items-center text-sm text-zinc-400">
              <span>Shipping estimate</span>
              <a
                href="#"
                class="ml-2 flex-shrink-0 text-zinc-400 hover:text-gray-500"
              >
                <span class="sr-only"
                  >Learn more about how shipping is calculated</span
                >
                <QuestionMarkCircleIcon class="h-5 w-5" aria-hidden="true" />
              </a>
            </dt>
            <dd class="text-sm font-bold text-cyan-200">$5.00</dd>
          </div>
          <div
            class="flex items-center justify-between border-t border-zinc-700 pt-4"
          >
            <dt class="flex text-sm text-zinc-400">
              <span>Tax estimate</span>
              <a
                href="#"
                class="ml-2 flex-shrink-0 text-gray-400 hover:text-gray-500"
              >
                <span class="sr-only"
                  >Learn more about how tax is calculated</span
                >
                <QuestionMarkCircleIcon class="h-5 w-5" aria-hidden="true" />
              </a>
            </dt>
            <dd class="text-sm font-bold text-cyan-200">$8.32</dd>
          </div>
          <div
            class="flex items-center justify-between border-t border-zinc-700 pt-4"
          >
            <dt class="text-base font-bold text-zinc-200">Order total</dt>
            <dd class="text-base font-bold text-cyan-200">$112.32</dd>
          </div>
        </dl>

        <div class="mt-6">
          <button
            type="submit"
            class="w-full rounded-md border border-transparent bg-cyan-200 px-4 py-3 text-base font-bold text-zinc-900 shadow-sm hover:bg-cyan-300 focus:outline-none focus:ring-2 focus:ring-cyan-200 focus:ring-offset-2 focus:ring-offset-zinc-900"
          >
            Checkout
          </button>
        </div>
      </section>
    </form>

    <!-- Related products -->
    <section aria-labelledby="related-heading" class="mt-24">
      <h2 id="related-heading" class="text-lg font-bold text-gray-900">
        You may also like&hellip;
      </h2>

      <div
        class="mt-6 grid grid-cols-1 gap-x-6 gap-y-10 sm:grid-cols-2 lg:grid-cols-4 xl:gap-x-8"
      >
        <div
          v-for="relatedProduct in relatedProducts"
          :key="relatedProduct.id"
          class="group relative"
        >
          <div
            class="aspect-h-1 aspect-w-1 w-full overflow-hidden rounded-md lg:aspect-none group-hover:opacity-75 lg:h-80"
          >
            <img
              :src="relatedProduct.imageSrc"
              :alt="relatedProduct.imageAlt"
              class="h-full w-full object-cover object-center lg:h-full lg:w-full"
            />
          </div>
          <div class="mt-4 flex justify-between">
            <div>
              <h3 class="text-sm text-gray-700">
                <a :href="relatedProduct.href">
                  <span aria-hidden="true" class="absolute inset-0" />
                  {{ relatedProduct.name }}
                </a>
              </h3>
              <p class="mt-1 text-sm text-gray-500">
                {{ relatedProduct.color }}
              </p>
            </div>
            <p class="text-sm font-bold text-gray-900">
              {{ relatedProduct.price }}
            </p>
          </div>
        </div>
      </div>
    </section>
  </main>
</template>


<script setup lang="ts">
import { CheckIcon, ClockIcon, QuestionMarkCircleIcon, XMarkIcon as XMarkIconMini } from '@heroicons/vue/20/solid'

const products = [


  {
    id: 1,
    name: 'Basic Tee',
    href: '#',
    price: '$32.00',
    color: 'Sienna',
    inStock: true,
    size: 'Large',
    imageSrc: 'https://tailwindui.com/img/ecommerce-images/shopping-cart-page-01-product-01.jpg',
    imageAlt: "Front of men's Basic Tee in sienna.",
  },
  {
    id: 2,
    name: 'Basic Tee',
    href: '#',
    price: '$32.00',
    color: 'Black',
    inStock: false,
    leadTime: '3–4 weeks',
    size: 'Large',
    imageSrc: 'https://tailwindui.com/img/ecommerce-images/shopping-cart-page-01-product-02.jpg',
    imageAlt: "Front of men's Basic Tee in black.",
  },
  {
    id: 3,
    name: 'Nomad Tumbler',
    href: '#',
    price: '$35.00',
    color: 'White',
    inStock: true,
    imageSrc: 'https://tailwindui.com/img/ecommerce-images/shopping-cart-page-01-product-03.jpg',
    imageAlt: 'Insulated bottle with white base and black snap lid.',
  },
]
const relatedProducts = [


  {
    id: 1,
    name: 'Billfold Wallet',
    href: '#',
    imageSrc: 'https://tailwindui.com/img/ecommerce-images/shopping-cart-page-01-related-product-01.jpg',
    imageAlt: 'Front of Billfold Wallet in natural leather.',
    price: '$118',
    color: 'Natural',
  },
  // More products...
]

useHead({
    title: "Cart"
})
</script>