<template>
  <div class="login-container">
    <div class="login-content">
      <div class="login-left">
        <h1 class="brand-title">染题</h1>
        <p class="brand-subtitle">让学习更简单</p>
      </div>
      <div class="login-right">
        <div class="login-form-wrapper">
          <h2 class="form-title">账号密码登录</h2>
          <a-form layout="vertical">
            <a-form-item label="账号" v-bind="validateInfos.text">
              <a-input v-model:value="formData.text" placeholder="请输入账号" size="large" />
            </a-form-item>
            <a-form-item label="密码" v-bind="validateInfos.password">
              <a-input-password
                v-model:value="formData.password"
                placeholder="请输入密码"
                size="large"
              />
            </a-form-item>
            <a-form-item>
              <a-button
                type="primary"
                html-type="submit"
                size="large"
                block
                :loading="loading"
                @click="handleLogin"
              >
                登录
              </a-button>
            </a-form-item>
          </a-form>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref } from 'vue'
import { Form, message } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/useLoginUserStore'
import router from '@/router'
import { login } from '@/api/managerUserController.ts'

const useForm = Form.useForm
const loginUserStore = useLoginUserStore()

const formData = ref({
  text: '',
  password: '',
})

const rules = ref({
  text: [{ required: true, message: '请输入账号' }],
  password: [{ required: true, message: '请输入密码' }],
})

const { validate, validateInfos } = useForm(formData.value, rules)

const loading = ref(false)

const handleLogin = async () => {
  loading.value = true
  try {
    await validate()
    const res = await login({
      text: formData.value.text,
      password: formData.value.password
    })
    if (res.data.code === 0 && res.data.data) {
      message.success('登录成功')
      loginUserStore.setLoginUser(res.data.data)
      await router.push('/manager')
    } else {
      message.error(res.data.message)
    }
  } catch (error) {
    console.log('验证失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-content {
  width: 900px;
  height: 500px;
  display: flex;
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.login-left {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.brand-title {
  font-size: 64px;
  font-weight: bold;
  margin: 0;
  letter-spacing: 8px;
}

.brand-subtitle {
  font-size: 20px;
  margin-top: 16px;
  opacity: 0.9;
}

.login-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.login-form-wrapper {
  width: 100%;
  max-width: 320px;
}

.form-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 32px;
  color: #333;
  text-align: center;
}

:deep(.ant-form-item) {
  margin-bottom: 24px;
}

:deep(.ant-form-item-label > label) {
  font-weight: 500;
  color: #666;
}

:deep(.ant-btn-primary) {
  height: 44px;
  font-size: 16px;
  font-weight: 500;
}
</style>
