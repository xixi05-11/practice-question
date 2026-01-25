import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from '@/views/user/LoginView.vue'
import LoginByEmailView from '@/views/user/LoginByEmailView.vue'
import RegisterView from '@/views/user/RegisterView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/user/login',
      name: 'login',
      component: LoginView,
    },
    {
      path: '/user/email/login',
      name: 'loginByEmail',
      component: LoginByEmailView,
    },
    {
      path: '/user/register',
      name: 'register',
      component: RegisterView,
    },
    {
      path: '/user/resetPwd',
      name: 'resetPwd',
      component: () => import('@/views/user/ResetPwdView.vue'),
    },
    {
      path: '/user/email/resetPwd',
      name: 'resetPwdByEmail',
      component: () => import('@/views/user/ResetPwdByEmailView.vue'),
    },
    {
      path: '/user/home',
      name: 'myHome',
      component: () => import('@/views/user/MyHomeView.vue'),
    },
    {
      path:'/bank/:id',
      name: 'bank',
      component: () => import('@/views/bank/BankDetailView.vue'),
    },
    {
      path: '/question/:id',
      name: 'question',
      component: () => import('@/views/question/QuestionDetailView.vue'),
    },
    {
      path: '/about',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/AboutView.vue'),
    },
  ],
})

export default router
