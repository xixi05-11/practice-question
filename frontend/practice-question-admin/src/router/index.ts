import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import { useLoginUserStore } from '../stores/useLoginUserStore'
import ManagerView from '@/views/ManagerView.vue'
import UserManager from '@/views/manager/UserManager.vue'
import BankManager from '@/views/manager/BankManger.vue'
import QuestionManager from '@/views/manager/QuestionManager.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/login',
    },
    {
      path: '/login',
      name: 'LoginView',
      component: LoginView,
    },
    {
      path: '/manager',
      name: '管理页面',
      component: ManagerView,
      redirect: '/manager/home',
      children: [
        {
          path: 'home',
          name: '首页',
          component: () => import('@/views/manager/Home.vue'),
        },
        {
          path: 'user',
          name: '用户管理',
          component: UserManager,
        },
        {
          path: 'bank',
          name: '题库管理',
          component: BankManager,
        },
        {
          path: 'question',
          name: '题目管理',
          component: QuestionManager,
        },
        {
          path: 'settings',
          name: '系统设置',
          component: () => import('@/views/manager/Setting.vue'),
        },
      ],
    },
  ],
})
export default router
