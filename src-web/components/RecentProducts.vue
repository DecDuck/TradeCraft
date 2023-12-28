<template>
  <ProductRow :products="products" name="Brand new deals" fullpage="/marketplace/l/all" />
</template>
<script setup lang="ts">
const products = await $fetch(`/api/v1/market/l/all`);
</script>
