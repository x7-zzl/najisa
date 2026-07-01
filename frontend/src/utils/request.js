import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const service = axios.create({
  baseURL: 'http://localhost:8088/umr',
  timeout: 5000
})

service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('user_token')
    
    // 【关键修改】：增加严格校验，过滤掉 "[object Object]"、"null"、"undefined" 等错误字符串
    if (token && token !== '[object Object]' && token !== 'null' && token !== 'undefined') {
      config.headers['Authorization'] = 'Bearer ' + token
    } else {
      // 如果发现本地存的是错误格式，干脆清理掉
      localStorage.removeItem('user_token')
    }
    return config
  },
  error => Promise.reject(error)
)

service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 200) {
      return res
    }
    if (res.code === 401 || res.code === 403) {
      ElMessage.error(res.data || res.message || '身份验证失效')
      localStorage.removeItem('user_token')
      localStorage.removeItem('isLoggedIn')
      router.push('/')
      return Promise.reject(new Error('Auth Error'))
    }
    return res 
  },
  error => {
    ElMessage.error('网络连接异常，请检查后端服务')
    return Promise.reject(error)
  }
)

export default service

export const logout = () => {
  return service({
    url: '/basicUser/logout',
    method: 'post'
  })
}

export const getUserProfile = (userId) => {
  return service({
    url: '/basicUser/getUserProfile',
    method: 'get',
    params: { userId }
  })
}

export const updateUserProfile = (data) => {
  return service({
    url: '/basicUser/updateUserProfile',
    method: 'post',
    data
  })
}

export const uploadFile = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return service({
    url: '/basicFile/uploadLocalReturnUrl',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export const deleteLocalFileByUrl = (url) => {
  return service({
    url: '/basicFile/deleteLocalByUrl',
    method: 'post',
    params: { url }
  })
}

export const getArticleList = (data) => {
  return service({
    url: '/basicArticle/queryListPage',
    method: 'post',
    data
  })
}

export const publishArticle = (data) => {
  return service({
    url: '/basicArticle/add',
    method: 'post',
    data
  })
}

export const getArticleById = (params) => {
  return service({
    url: '/basicArticle/queryById',
    method: 'get',
    params
  })
}

export const getVideoList = (data) => {
  return service({
    url: '/basicVideo/queryListPage',
    method: 'post',
    data
  })
}

export const publishVideo = (data) => {
  return service({
    url: '/basicVideo/add',
    method: 'post',
    data
  })
}

export const getVideoById = (params) => {
  return service({
    url: '/basicVideo/queryById',
    method: 'get',
    params
  })
}

// ====== 每日签到 ======
export const dailySignIn = (userId) => {
  return service({
    url: '/basicUserWealth/dailySignIn',
    method: 'post',
    params: { userId }
  })
}

export const checkSignInStatus = (userId) => {
  return service({
    url: '/basicUserWealth/checkSignInStatus',
    method: 'get',
    params: { userId }
  })
}
