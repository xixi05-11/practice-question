<template>
  <div id="login-view">
    <h2>注册</h2>

    <a-form :model="formState" name="basic" autocomplete="off" @finish="onFinish">
      <!-- 邮箱 -->
      <a-form-item
        name="email"
        :rules="[
          { required: true, message: '请输入邮箱!' },
          { type: 'email', message: '邮箱格式不正确!' },
        ]"
      >
        <a-input v-model:value="formState.email" placeholder="输入邮箱" />
      </a-form-item>

      <!-- 验证码 -->
      <a-form-item name="emailCode" :rules="[{ required: true, message: '请输入验证码!' }]">
        <a-input-group compact>
          <a-input
            v-model:value="formState.emailCode"
            style="width: calc(100% - 120px)"
            placeholder="输入验证码"
          />
          <a-button type="primary" style="width: 120px" @click="handleSendCode">
            获取验证码
          </a-button>
        </a-input-group>
      </a-form-item>

      <!-- 密码 -->
      <a-form-item
        name="password"
        :rules="[
          { required: true, message: '请输入密码!' },
          { min: 8, message: '密码长度不能小于8位' },
        ]"
      >
        <a-input-password v-model:value="formState.password" placeholder="输入密码" />
      </a-form-item>

      <!-- 确认密码 -->
      <a-form-item
        name="checkPassword"
        :rules="[
          { required: true, message: '请输入密码!' },
          { min: 8, message: '密码长度不能小于8位' },
        ]"
      >
        <a-input-password v-model:value="formState.checkPassword" placeholder="再次输入密码" />
      </a-form-item>

      <div class="tip">
        <router-link to="/user/login">返回登录</router-link>
      </div>

      <a-form-item>
        <a-button
          type="primary"
          html-type="submit"
          style="width: 90%; display: block; margin: 0 auto"
        >
          确认
        </a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { getUserVoById, register, sendEmail } from '@/api/userController.ts'
import { UserOperationEnum } from '@/enums/userOperationEnum.ts'

const loginUserStore = useLoginUserStore()
const router = useRouter()

interface FormState {
  email: string
  emailCode: string
  password: string
  checkPassword: string
}

const formState = reactive<FormState>({
  email: '',
  emailCode: '',
  password: '',
  checkPassword: '',
})

// 验证码发送 + 倒计时
const countDown = ref(0)
let timer: number | null = null
const handleSendCode = () => {
  if (countDown.value > 0) return
  if (!formState.email) {
    message.warning('请先输入邮箱')
    return
  }

  sendEmail({ email: formState.email, operation: UserOperationEnum.RESET_PWD.value }).then(() => {
    message.success('验证码已发送')
  })
  //  启动 60 秒倒计时
  countDown.value = 60
  timer = window.setInterval(() => {
    countDown.value--
    if (countDown.value <= 0) {
      clearInterval(timer!)
      timer = null
    }
  }, 1000)
}

const onFinish = async (values: API.EmailRequest) => {
  const res = await register(values)
  if (res.data.code === 0 && res.data.data) {
    const r = await getUserVoById(res.data.data?.id)
    if (r.data.code === 0 && r.data.data) {
      loginUserStore.setLoginUser(r.data.data)
    }
    await router.replace('/user/login')
    message.success('注册成功')
  } else {
    message.error('注册失败' + res.data.message)
  }
}
</script>

<style scoped>
#login-view {
  max-width: 400px;
  margin: 0 auto;
}

.tip {
  text-align: right;
  margin-bottom: 20px;
  font-size: 13px;
}
</style>
