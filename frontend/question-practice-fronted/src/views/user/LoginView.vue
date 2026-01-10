<template>
  <div id="login-view">
    <h2>用户登录</h2>
    <a-form :model="formState" name="basic" autocomplete="off" @finish="onFinish">
      <a-form-item name="text" :rules="[{ required: true, message: '请输入账户!' }]">
        <a-input v-model:value="formState.text" placeholder="输入账号或邮箱" />
      </a-form-item>

      <a-form-item
        name="password"
        :rules="[
          { required: true, message: '请输入密码!' },
          { min: 8, message: '密码长度不能小于8位' },
        ]"
      >
        <a-input-password v-model:value="formState.password" placeholder="输入密码" />
      </a-form-item>

      <a-form-item>
        <a-button
          type="primary"
          html-type="submit"
          style="width: 90%; display: block; margin: 0 auto"
        >
          确认
        </a-button>
      </a-form-item>

      <a-row style="display: flex; justify-content: center; gap: 60px">
        <router-link to="/user/register" class="bottom">
          <UserAddOutlined style="font-size: 40.5px" />
          <div>注册</div>
        </router-link>
        <router-link to="/user/email/login" class="bottom">
          <MailOutlined style="font-size: 41px" />
          <div>验证码登录</div>
        </router-link>
      </a-row>
    </a-form>
  </div>
</template>
<script lang="ts" setup>
import { reactive } from 'vue'
import { login } from '@/api/userController.ts'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { MailOutlined, UserAddOutlined } from '@ant-design/icons-vue'

const loginUserStore = useLoginUserStore()
const router = useRouter()
interface FormState {
  text: string
  password: string
}

const formState = reactive<FormState>({
  text: '',
  password: '',
})
const onFinish = (values: API.loginParams) => {
  login(values).then((res) => {
    if (res.data.code === 0 && res.data.data) {
      loginUserStore.setLoginUser(res.data.data)
      router.push({
        path: '/',
        replace: true,
      })
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
.bottom {
  display: flex;
  flex-direction: column; /* 纵向排列 */
  align-items: center; /* 水平居中 */
  gap: 8px; /* 上下间距 */
  margin-top: 50px;
}
</style>
