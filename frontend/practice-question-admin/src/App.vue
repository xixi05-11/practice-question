<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup lang="ts">
import { useLoginUserStore } from '@/stores/useLoginUserStore'
const loginUserStore = useLoginUserStore()
loginUserStore.fetchLoginUser()
</script>
