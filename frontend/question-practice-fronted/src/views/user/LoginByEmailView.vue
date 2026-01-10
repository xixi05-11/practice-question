<template>
  <div id="login-view">
    <h2>验证码登录</h2>

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

      <div class="tip">
        <router-link to="/user/login">密码登录</router-link>
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
import { loginByEmailCode, sendEmail } from '@/api/userController.ts'
import { UserOperationEnum } from '@/enums/userOperationEnum.ts'

const loginUserStore = useLoginUserStore()
const router = useRouter()

interface FormState {
  email: string
  emailCode: string
}

const formState = reactive<FormState>({
  email: '',
  emailCode: '',
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

const onFinish = (values: API.EmailRequest) => {
  loginByEmailCode(values).then((res) => {
    if (res.data.code === 0 && res.data.data) {
      loginUserStore.setLoginUser(res.data.data)
      router.replace('/')
      message.success('登录成功')
    } else {
      message.error('登录失败' + res.data.message)
    }
  })
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
