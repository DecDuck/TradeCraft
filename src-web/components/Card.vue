<template>
    <div :class="['bg-zinc-800/30 rounded-lg px-5 py-4', props.class]">
        <slot />
    </div>
</template>

<script setup lang="ts">
const props = defineProps(['class'])
</script>