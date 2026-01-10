<template>
  <div id="change-password-view">
    <h2>修改密码</h2>
    <a-form :model="formState" name="change-password" autocomplete="off" @finish="onFinish">
      <!-- 输入当前密码 -->
      <a-form-item
        name="oldPassword"
        :rules="[
          { required: true, message: '请输入当前密码!' },
          { min: 8, message: '密码长度不能小于8位' },
        ]"
      >
        <a-input-password v-model:value="formState.oldPassword" placeholder="输入当前密码" />
      </a-form-item>

      <!-- 输入新密码 -->
      <a-form-item
        name="userPassword"
        :rules="[
          { required: true, message: '请输入新密码!' },
          { min: 8, message: '密码长度不能小于8位' },
        ]"
      >
        <a-input-password v-model:value="formState.userPassword" placeholder="输入新密码" />
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
        <router-link to="/user/email/resetPwd">验证码修改</router-link>
      </div>

      <!-- 提交按钮 -->
      <a-form-item>
        <a-button
          type="primary"
          html-type="submit"
          style="width: 90%; display: block; margin: 0 auto"
        >
          修改密码
        </a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script lang="ts" setup>
import { reactive } from 'vue'
import { resetPwd } from '@/api/userController.ts'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'

const loginUserStore = useLoginUserStore()
const router = useRouter()

interface FormState {
  oldPassword: string
  userPassword: string
  checkPassword: string
}

const formState = reactive<FormState>({
  oldPassword: '',
  userPassword: '',
  checkPassword: '',
})

// 密码确认验证
const checkPasswordConfirmation = (_: any, value: string) => {
  if (value && value !== formState.userPassword) {
    return Promise.reject('两次输入的密码不一致!')
  }
  return Promise.resolve()
}

// 提交表单
const onFinish = async (values: API.UserChangePwdRequest) => {
  const res = await resetPwd({
    id: loginUserStore.loginUser.id,
    oldPassword: values.oldPassword,
    userPassword: values.userPassword,
    checkPassword: values.checkPassword,
  })
  if (res.data.code === 0 && res.data.data) {
    loginUserStore.setLoginUser({
      userName: '未登录',
    })
    message.success('密码修改成功')
    await router.push({
      path: '/user/login',
      replace: true,
    })
  } else {
    message.error('密码修改失败' + res.data.message)
  }
}
</script>

<style scoped>
#change-password-view {
  max-width: 400px;
  margin: 0 auto;
}
.tip {
  text-align: right;
  margin-bottom: 20px;
  font-size: 13px;
}
</style>
