<template>
    <div :class="['bg-zinc-900 ring-2 ring-zinc-800 rounded-xl px-5 py-4', props.class]">
        <slot />
    </div>
</template>

<script setup lang="ts">
const props = defineProps(['class'])
</script>