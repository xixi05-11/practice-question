import { ref } from 'vue'
import { defineStore } from 'pinia'
import { getLoginUser } from '@/api/managerUserController.ts'

/**
 * 登录用户状态管理
 */
export const useLoginUserStore = defineStore('loginUser', () => {
  const loginUser = ref<API.UserVO>({
    userName: '未登录',
  })

  /**
   *  获取登录用户
   */
  async function fetchLoginUser() {
    const res = await getLoginUser()
    if (res.data.code === 0 && res.data.data) {
      loginUser.value = res.data.data
    }
  }

  /**
   * 设置登录用户
   * @param user
   */
  function setLoginUser(user: API.UserVO) {
    loginUser.value = user
  }
  return { loginUser, fetchLoginUser, setLoginUser }
})
