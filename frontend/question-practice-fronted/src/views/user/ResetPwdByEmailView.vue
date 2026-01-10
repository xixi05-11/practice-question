<template>
  <div id="resetPwd-email-view">
    <h2>验证码修改密码</h2>

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
          <a-button
            type="primary"
            style="width: 120px"
            :disabled="countDown > 0"
            @click="handleSendCode"
          >
            {{ countDown > 0 ? `${countDown}s 后重试` : '获取验证码' }}
          </a-button>
        </a-input-group>
      </a-form-item>

      <!-- 输入新密码 -->
      <a-form-item
        name="password"
        :rules="[
          { required: true, message: '请输入新密码!' },
          { min: 8, message: '密码长度不能小于8位' },
        ]"
      >
        <a-input-password v-model:value="formState.password" placeholder="输入新密码" />
      </a-form-item>

      <!-- 确认新密码 -->
      <a-form-item
        name="checkPassword"
        :rules="[
          { required: true, message: '请确认新密码!' },
          { validator: checkPasswordConfirmation },
        ]"
      >
        <a-input-password v-model:value="formState.checkPassword" placeholder="确认新密码" />
      </a-form-item>
      <div class="tip">
        <router-link to="/user/resetPwd">原密码修改</router-link>
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
import { resetPwdByEmailCode, sendEmail } from '@/api/userController.ts'
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

// 密码确认验证
const checkPasswordConfirmation = (_: any, value: string) => {
  if (value && value !== formState.password) {
    return Promise.reject('两次输入的密码不一致!')
  }
  return Promise.resolve()
}

const onFinish = async (values: API.EmailRequest) => {
  const res = await resetPwdByEmailCode(values)
  if (res.data.code === 0) {
    loginUserStore.setLoginUser({
      userName: '未登录',
    })
    await router.replace('/user/login')
    message.success('重置密码成功')
  } else {
    message.error('重置密码失败' + res.data.message)
  }
}
</script>

<style scoped>
#resetPwd-email-view {
  max-width: 400px;
  margin: 0 auto;
}

.tip {
  text-align: right;
  margin-bottom: 20px;
  font-size: 13px;
}
</style>
