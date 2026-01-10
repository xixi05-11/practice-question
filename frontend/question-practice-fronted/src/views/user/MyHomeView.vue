<template>
  <div class="profile-header">
    <a-card class="header-card">
      <div class="header-content">
        <!-- 左侧：头像 -->
        <a-avatar :size="96" :src="user.avatar || defaultAvatar" class="avatar" />

        <!-- 右侧：用户信息 -->
        <a-descriptions class="info" :column="2" size="middle">
          <a-descriptions-item label="账号">
            {{ user.userAccount }}
          </a-descriptions-item>

          <a-descriptions-item label="昵称">
            {{ user.userName }}
          </a-descriptions-item>
          <a-descriptions-item label="邮箱">
            {{ user.email }}
          </a-descriptions-item>

          <a-descriptions-item label="角色">
            {{ user.role === 'admin' ? '管理员' : '普通用户' }}
          </a-descriptions-item>

          <a-descriptions-item label="注册时间">
            {{ formatTime(user.createTime) }}
          </a-descriptions-item>

          <a-descriptions-item label="个人简介">
            <div class="profile-row">
              <span class="profile-text">
                {{ user.profile || '这个人很懒，什么都没写～' }}
              </span>

            </div>
          </a-descriptions-item>
        </a-descriptions>
      </div>
    </a-card>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import { useLoginUserStore } from '@/stores/useLoginUserStore'
import defaultAvatar from '@/assets/default-avatar.png'
const loginUserStore = useLoginUserStore()

const user = computed(() => loginUserStore.loginUser)

// 时间格式化函数
const formatTime = (time: string | Date | undefined): string => {
  if (!time) return '-'

  try {
    const date = new Date(time)
    if (isNaN(date.getTime())) return '-'

    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    const seconds = String(date.getSeconds()).padStart(2, '0')

    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
  } catch (error) {
    console.error('时间格式化错误:', error)
    return '-'
  }
}
</script>
<style scoped>
.profile-header {
  padding: 20px;
}

.header-card {
  border-radius: 12px;
}

/* 核心：左右布局 */
.header-content {
  display: flex;
  align-items: flex-start;
  gap: 32px;
}

/* 头像 */
.avatar {
  flex-shrink: 0;
}

/* 右侧信息区域 */
.info {
  flex: 1;
  margin-left: 30px;
}
</style>
