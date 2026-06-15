<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2 class="login-title">天地一家大爱盟</h2>
      
      <div v-if="showSuccessTip" class="success-tip-box">
        <span class="success-tip-text">注册成功!已为您自动填充账号密码</span>
      </div>

      <el-form 
        :model="form" 
        ref="formRef" 
        label-width="60px" 
        class="login-form"
        @submit.prevent
      >
        <el-form-item label="账 号" class="form-item">
          <el-input 
            v-model="form.account" 
            autocomplete="off"
            placeholder="用户名"
            class="input-item"
            type="text"
            :disabled="isLoginLoading"
          ></el-input>
        </el-form-item>

        <el-form-item label="密 码" class="form-item">
          <el-input 
            v-model="form.password" 
            :type="showPassword ? 'text' : 'password'" 
            autocomplete="off"
            placeholder="密码"
            class="input-item"
            :disabled="isLoginLoading"
          >
            <template #suffix>
              <span 
                class="password-icon" 
                @click="showPassword = !showPassword"
              >
                <el-icon v-if="showPassword"><Eye /></el-icon>
                <el-icon v-else><EyeClosed /></el-icon>
              </span>
            </template>
          </el-input>
        </el-form-item>

        <div class="remember-bar">
          <el-checkbox v-model="rememberLogin">记住登录</el-checkbox>
          <a href="#" class="find-pwd" @click.prevent="ElMessage.info('功能开发中...')">找回密码</a>
        </div>

        <div class="tip-text">
          <a href="#" class="register-link" @click.prevent="router.push('/register')">没有账号?点击注册</a>
        </div>

        <el-form-item class="btn-item">
          <el-button 
            type="primary" 
            @click="login" 
            class="login-btn" 
            :loading="isLoginLoading"
          >登 录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { SHA256 } from 'crypto-js'
import request from '../utils/request'
// 确保图标已导入
import { Eye, EyeClosed } from '@element-plus/icons-vue' 

const router = useRouter()
const route = useRoute()

const form = reactive({
  account: '',
  password: ''
})

const isLoginLoading = ref(false)
const showPassword = ref(false)
const showSuccessTip = ref(false)
const rememberLogin = ref(false)

// 加密函数(用于保存密码)
const encrypt = (str) => {
  try {
    return btoa(unescape(encodeURIComponent(str)))
  } catch (e) {
    return ''
  }
}

// 解密函数(用于读取密码)
const decrypt = (str) => {
  try {
    return decodeURIComponent(escape(atob(str)))
  } catch (e) {
    return ''
  }
}

// 保存记住登录信息
const saveRememberLogin = () => {
  if (rememberLogin.value) {
    // 用户选择了记住登录,保存账号和加密后的密码
    localStorage.setItem('isRememberLogin', 'true')
    localStorage.setItem('savedLoginAccount', form.account.trim())
    localStorage.setItem('savedLoginPwd', encrypt(form.password.trim()))
  } else {
    // 用户没有选择记住登录,清除保存的信息
    localStorage.removeItem('isRememberLogin')
    localStorage.removeItem('savedLoginAccount')
    localStorage.removeItem('savedLoginPwd')
  }
}

// 权限检查并跳转
const checkRoleAndRedirect = async () => {
  try {
    const roleRes = await request.post('/basicUser/isAdminRole', {
      userName: form.account.trim()
    })

    if (roleRes.code === 200) {
        ElMessage.success('登录成功')
        // 兼容后端返回字符串 "true" 或布尔值 true
        if (roleRes.data === 'true' || roleRes.data === true) {
          router.push('/admin') // 跳转后台
        } else {
          router.push('/system') // 跳转前台
        }
    } else {
        ElMessage.error(roleRes.msg || '权限验证失败')
        router.push('/system') 
    }
  } catch (e) {
    console.error('权限获取失败', e)
    router.push('/system') 
  } finally {
    isLoginLoading.value = false
  }
}

// 登录逻辑
const login = async () => {
  if (!form.account || !form.password) {
    ElMessage.warning('请输入完整信息')
    return
  }

  isLoginLoading.value = true
  try {
    const passwordHash = SHA256(form.password.trim()).toString()

    const res = await request.post('/basicUser/login', {
      userName: form.account.trim(),
      password: passwordHash 
    })

    if (res.code === 200) {
      // 获取后端返回的数据
      const { token, userId, userName, nickName, userAvatar } = res.data
      
      localStorage.setItem('user_token', token) 
      localStorage.setItem('isLoggedIn', 'true')
      localStorage.setItem('loginUserId', userId)
      localStorage.setItem('loginUserName', nickName || userName)
      if (userAvatar) {
        localStorage.setItem('userAvatar', userAvatar)
      }

      // 【核心修复】登录成功后,保存或清除"记住登录"信息
      saveRememberLogin()

      // 检查角色并跳转
      await checkRoleAndRedirect()
    } else {
      ElMessage.error(res.data || '登录失败')
      isLoginLoading.value = false
    }
  } catch (e) {
    console.error('登录异常:', e)
    isLoginLoading.value = false
  }
}

onMounted(() => {
  // 1. 处理注册后跳转回来的自动填充
  const { username, password } = route.query
  if (username && password) {
    form.account = username
    form.password = password
    showSuccessTip.value = true
    setTimeout(() => { showSuccessTip.value = false }, 3000)
  } else {
    // 2. 记住登录逻辑回显(只有在没有注册跳转参数时才加载)
    if (localStorage.getItem('isRememberLogin') === 'true') {
      form.account = localStorage.getItem('savedLoginAccount') || ''
      const savedPwd = localStorage.getItem('savedLoginPwd')
      if (savedPwd) {
        form.password = decrypt(savedPwd)
      }
      rememberLogin.value = true
    }
  }
})
</script>

<style scoped>
/* 全局样式重置 */
:global(html),
:global(body) {
  margin: 0 !important;
  padding: 0 !important;
  height: 100% !important;
  overflow: hidden !important;
  font-family: "Microsoft Yahei", sans-serif;
}

/* 登录容器 */
.login-container {
  width: 100vw !important;
  height: 100vh !important;
  min-width: 100vw !important;
  min-height: 100vh !important;
  background: linear-gradient(135deg, #e0c3fc 0%, #8ec5fc 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box !important;
}

/* 登录卡片 */
.login-card {
  width: 420px;
  padding: 40px 30px;
  border-radius: 16px;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);
  background: #fff;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  pointer-events: auto;
}
.login-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 16px 40px rgba(0, 0, 0, 0.2);
}

/* 登录标题 */
.login-title {
  text-align: center;
  color: #2d3748;
  font-size: 24px;
  font-weight: 700;
  margin: 0 0 20px 0;
  letter-spacing: 2px;
}

/* 注册成功提示 */
.success-tip-box {
  text-align: center;
  padding: 10px;
  margin-bottom: 20px;
  background-color: #f0f9ff;
  border-radius: 8px;
}
.success-tip-text {
  color: #1677ff;
  font-size: 14px;
}

/* 表单项 */
.form-item {
  margin-bottom: 18px;
}

/* 输入框样式 */
.input-item {
  border-radius: 10px;
  height: 48px;
  font-size: 14px;
  pointer-events: auto !important;
}
:deep(.el-input__wrapper) {
  border-radius: 10px !important;
  height: 48px !important;
  box-shadow: none !important;
  border-color: #e2e8f0 !important;
  pointer-events: auto !important;
}
:deep(.el-input__inner) {
  height: 100% !important;
  line-height: 48px !important;
  font-size: 14px !important;
}
:deep(.el-input__wrapper:focus-within) {
  border-color: #8ec5fc !important;
  box-shadow: 0 0 0 3px rgba(142, 197, 252, 0.25) !important;
}

/* 密码显隐图标 */
.password-icon {
  display: inline-flex !important;
  align-items: center !important;
  justify-content: center !important;
  width: 28px !important;
  height: 28px !important;
  color: #666 !important;
  cursor: pointer !important;
  font-size: 18px !important;
  z-index: 999 !important;
  margin-right: 4px !important;
}
.password-icon:hover {
  color: #4299e1 !important;
}
.password-icon.disabled {
  color: #ccc !important;
  cursor: not-allowed !important;
  pointer-events: none;
}

/* 记住登录 + 找回密码 区域 */
.remember-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 8px 0 15px 0;
  font-size: 13px;
}
.remember-checkbox {
  color: #718096;
  cursor: pointer;
}
.find-pwd {
  color: #6aaef7;
  text-decoration: none;
  cursor: pointer;
  transition: color 0.3s ease;
}
.find-pwd:hover {
  color: #4299e1;
}

/* 注册引导文本 */
.tip-text {
  text-align: center;
  font-size: 12px;
  color: #718096;
  margin: 0 0 20px 0;
}
.register-link {
  color: #6aaef7;
  text-decoration: none;
  transition: all 0.3s ease;
  cursor: pointer;
}
.register-link:hover {
  color: #4299e1;
  text-decoration: underline;
}

/* 登录按钮 */
.btn-item {
  margin-bottom: 0;
  text-align: center;
}
.login-btn {
  width: 100%;
  height: 48px;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(90deg, #8ec5fc 0%, #6aaef7 100%);
  border: none;
  transition: all 0.3s ease;
}
:deep(.el-button--primary.login-btn:hover) {
  background: linear-gradient(90deg, #7bb8f8 0%, #4299e1 100%);
}
:deep(.el-button--primary.login-btn:active) {
  transform: scale(0.98);
}

/* 输入框插槽样式 */
:deep(.el-input__suffix) {
  pointer-events: auto !important;
  padding-right: 4px !important;
}
:deep(.el-input__suffix-inner) {
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
}
</style>