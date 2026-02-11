import axios, { type AxiosInstance } from 'axios'
import { message } from 'ant-design-vue'

const myAxios: AxiosInstance = axios.create({
  baseURL: 'http://127.0.0.1:8848/api',
  timeout: 60000,
  withCredentials: true,
})

// 添加请求拦截器
axios.interceptors.request.use(function (config) {
  // 在发送请求之前做些什么
  return config;
}, function (error) {
  // 对请求错误做些什么
  return Promise.reject(error);
});

// 添加响应拦截器
axios.interceptors.response.use(function (response) {
  const {data} = response
  //未登录
  if(data.code === 40100){
    if (
      !response.request.responseURL.includes('/admin/user/get/login') &&
      !window.location.pathname.includes('/login')
    ) {
      message.error('请先登录')
      window.location.href = '/login'
    }
  }
  return response;
}, function (error) {
  // 超出 2xx 范围的状态码都会触发该函数。
  // 对响应错误做点什么
  return Promise.reject(error);
});
export default myAxios
