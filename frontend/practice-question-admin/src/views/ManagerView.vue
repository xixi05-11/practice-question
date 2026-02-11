<template>
  <div id="manager" class="manager-container">
    <a-menu
      id="menu"
      v-model:selectedKeys="selectedKeys"
      class="menu-sidebar"
      mode="inline"
      :items="items"
      @click="handleClick"
    ></a-menu>
    <div class="content-area">
      <router-view />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, h, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { AppstoreOutlined, SettingOutlined, UserOutlined, FileTextOutlined, DashboardOutlined } from '@ant-design/icons-vue'
import type { MenuProps, ItemType } from 'ant-design-vue'

const route = useRoute()
const router = useRouter()

const selectedKeys = ref<string[]>(['1'])

const pathToKeyMap: Record<string, string> = {
  '/manager/home': '1',
  '/manager/user': '2',
  '/manager/bank': '3',
  '/manager/question': '4',
  '/manager/settings': '5'
}

function getItem(
  label: string,
  key: string,
  icon?: any,
): ItemType {
  return {
    key,
    icon,
    label,
  } as ItemType
}

const items: ItemType[] = [
  getItem('首页', '1', () => h(DashboardOutlined)),
  getItem('用户管理', '2', () => h(UserOutlined)),
  getItem('题库管理', '3', () => h(AppstoreOutlined)),
  getItem('题目管理', '4', () => h(FileTextOutlined)),
  getItem('系统设置', '5', () => h(SettingOutlined)),
]

const keyToPathMap: Record<string, string> = {
  '1': '/manager/home',
  '2': '/manager/user',
  '3': '/manager/bank',
  '4': '/manager/question',
  '5': '/manager/settings'
}

const handleClick: MenuProps['onClick'] = (e) => {
  const path = keyToPathMap[e.key]
  if (path) {
    router.push(path)
  }
}

watch(() => route.path, (newPath) => {
  selectedKeys.value = [pathToKeyMap[newPath] || '1']
}, { immediate: true })
</script>

<style scoped>
.manager-container {
  display: flex;
  min-height: calc(100vh - 120px);
}

.menu-sidebar {
  width: 256px;
  flex-shrink: 0;
}

.content-area {
  flex: 1;
  padding: 24px;
  background: #f0f2f5;
  overflow-y: auto;
}

.home-page,
.settings-page {
  background: white;
  padding: 32px;
  border-radius: 8px;
  min-height: 400px;
}

.home-page h2,
.settings-page h2 {
  margin-top: 0;
  color: #333;
  font-size: 28px;
  margin-bottom: 16px;
}

.home-page p,
.settings-page p {
  color: #666;
  font-size: 16px;
}
</style>
