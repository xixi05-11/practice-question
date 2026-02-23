<template>
  <div id="app">
    <a-config-provider :locale="zhCN">
      <BasicLayout />
    </a-config-provider>
  </div>
</template>

<script setup lang="ts">
import BasicLayout from '@/layouts/BasicLayout.vue'
import { useLoginUserStore } from '@/stores/useLoginUserStore'
import zhCN from 'ant-design-vue/es/locale/zh_CN'
const loginUserStore = useLoginUserStore()
loginUserStore.fetchLoginUser()
</script>
