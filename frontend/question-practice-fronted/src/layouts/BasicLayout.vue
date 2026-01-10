<template>
  <div id="basic-layout">
    <a-layout :style="{ height: '100vh' }">
      <a-layout-header class="header">
        <global-header />
      </a-layout-header>
      <a-layout-content class="content">
        <router-view />
      </a-layout-content>
      <a-layout-footer class="footer">
        <p>by jie</p>
      </a-layout-footer>
    </a-layout>
  </div>
</template>

<script setup lang="ts">
import GlobalHeader from '@/components/GlobalHeader.vue'
</script>
<style scoped>
.header {
  background: white;
  color: unset;
  padding-inline: 20px;
  margin-bottom: 16px;
  margin-top: 12px;
}
.content {
  padding: 20px;
}
.footer {
  text-align: center;
  background: #efefef;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
}
</style>
