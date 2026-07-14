import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const aiService = axios.create({
  baseURL: 'http://localhost:8089',
  timeout: 30000
})

aiService.interceptors.request.use(
  config => {
    const token = localStorage.getItem('user_token')
    if (token && token !== '[object Object]' && token !== 'null' && token !== 'undefined') {
      config.headers['Authorization'] = 'Bearer ' + token
    } else {
      localStorage.removeItem('user_token')
    }
    return config
  },
  error => Promise.reject(error)
)

aiService.interceptors.response.use(
  response => response.data,
  error => {
    if (error.response) {
      const status = error.response.status
      if (status === 401 || status === 403) {
        ElMessage.error('身份验证失效，请重新登录')
        localStorage.removeItem('user_token')
        localStorage.removeItem('isLoggedIn')
        router.push('/')
        return Promise.reject(error)
      }
    }
    ElMessage.error('AI服务连接异常，请检查服务是否启动')
    return Promise.reject(error)
  }
)

export default aiService
