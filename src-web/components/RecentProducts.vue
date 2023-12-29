<template>
  <ProductRow :products="products" name="Recently added" fullpage="/marketplace/l/recent" />
</template>
<script setup lang="ts">
const products = await $fetch(`/api/v1/market/l/all`);
</script>
