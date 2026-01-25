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
  padding-inline: 20px;
  margin-top: 12px;
}

.content {
  flex: 1; /* ⭐ 占据剩余空间 */
  padding: 20px;
}

.footer {
  text-align: center;
  background: #efefef;
  height: 48px;
  line-height: 48px;
}
</style>
