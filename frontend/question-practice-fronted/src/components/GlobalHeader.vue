<template>
  <div class="global-header">
    <a-row :wrap="false">
      <a-col flex="200px"
        ><div class="title-logo">
          <img class="logo" src="../assets/logo.png" alt="logo" />
          <div class="title">染题</div>
        </div></a-col
      >
      <a-col flex="auto">
        <a-menu
          v-model:selectedKeys="current"
          mode="horizontal"
          :items="items"
          @click="doMenuClick"
        />
      </a-col>
      <a-col flex="120px">
        <div v-if="loginUserStore.loginUser.id">
          <a-dropdown>
            <div style="display: flex; align-items: center; gap: 8px">
              <div v-if="loginUserStore.loginUser.avatar">
                <a-avatar :size="40" :src="loginUserStore.loginUser.avatar" />
              </div>
              <div v-else>
                <a-avatar :size="40" :src="defaultAvatar"></a-avatar>
              </div>
              <span>{{ loginUserStore.loginUser.userName }}</span>
            </div>
            <template #overlay>
              <a-menu>
                <a-menu-item>
                  <a href="/user/home" style="display: flex; align-items: center; gap: 8px">
                    <UserOutlined />个人中心
                  </a>
                </a-menu-item>
                <a-menu-item>
                  <a href="/user/resetPwd" style="display: flex; align-items: center; gap: 8px">
                    <LockOutlined />修改密码
                  </a>
                </a-menu-item>
                <a-menu-item>
                  <div @click="doLogout" style="display: flex; align-items: center; gap: 8px">
                    <LoginOutlined />退出登录
                  </div>
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
        <div v-else>
          <a-button type="primary" href="/user/login">登录</a-button>
        </div>
      </a-col>
    </a-row>
  </div>
</template>
<script lang="ts" setup>
import { h, ref } from 'vue'
import { HomeOutlined, LoginOutlined, UserOutlined, LockOutlined } from '@ant-design/icons-vue'
import { type MenuProps, message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import defaultAvatar from '@/assets/default-avatar.png'
import { logout } from '@/api/userController.ts'
//用户信息
const loginUserStore = useLoginUserStore()

// 菜单
const current = ref<string[]>()
const items = ref<MenuProps['items']>([
  {
    key: '/',
    icon: h(HomeOutlined),
    label: '主页',
    title: '主页',
  },
  {
    key: '/about',
    label: '关于',
    title: '关于',
  },
])

// 点击菜单
const router = useRouter()
const doMenuClick = ({ key }) => {
  router.push({
    path: key,
  })
}
router.afterEach((to) => {
  current.value = [to.path]
})

// 退出登录
const doLogout = async () => {
  const res = await logout()
  if (res.data.code === 0) {
    loginUserStore.setLoginUser({
      userName: '未登录',
    })
    message.info('退出登录成功')
    await router.replace('/user/login')
  }
}
</script>

<style scoped>
.title-logo {
  display: flex;
  align-items: center;
}
.logo {
  width: 48px;
}
.title {
  font-size: 24px;
  color: #63396e;
  margin-left: 8px;
}
</style>
