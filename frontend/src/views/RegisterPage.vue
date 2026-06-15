<template>
  <div class="register-container">
    <el-card class="register-card">
      <h2 class="register-title">天地一家大爱盟</h2>
      <el-form 
        :model="form" 
        ref="formRef" 
        label-width="60px" 
        class="register-form"
        @submit.prevent
      >
        <el-form-item label="账 号" class="form-item">
          <el-input 
            v-model="form.account" 
            placeholder="注册账号"
            class="input-item"
            type="text"
            :disabled="isRegisterLoading"
          ></el-input>
        </el-form-item>

        <el-form-item label="密 码" class="form-item">
          <el-input 
            v-model="form.password" 
            :type="showPassword ? 'text' : 'password'"
            placeholder="密码"
            class="input-item"
            :disabled="isRegisterLoading"
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

        <div class="tip-text">
          <a href="#" class="login-link" @click.prevent="router.push('/')">已有账号？点击登录</a>
        </div>

        <el-form-item class="btn-item">
          <el-button 
            type="primary" 
            @click="register" 
            class="register-btn" 
            :loading="isRegisterLoading"
          >注 册</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { SHA256 } from 'crypto-js' // 引入 SHA256 加密
import request from '../utils/request'

const router = useRouter()
const form = reactive({
  account: '',
  password: ''
})

const isRegisterLoading = ref(false)
const showPassword = ref(false)

const register = async () => {
  if (!form.account || !form.password) {
    ElMessage.warning('请输入完整信息')
    return
  }

  isRegisterLoading.value = true

  try {
    // 【关键修改】：对原始密码进行哈希预处理后再发送
    const passwordHash = SHA256(form.password.trim()).toString()

    const res = await request.post('/basicUser/register', {
      userName: form.account.trim(),
      password: passwordHash
    })

    if (res.code === 200) {
      ElMessage.success('注册成功！')
      // 跳转回登录页并自动带上账号密码
      router.push({
        path: '/',
        query: { 
          username: form.account.trim(), 
          password: form.password.trim() 
        }
      })
    } else {
      // 提示后端具体的校验失败信息
      ElMessage.error(res.data || '注册失败')
    }
  } catch (e) {
    console.error('注册异常:', e)
  } finally {
    isRegisterLoading.value = false
  }
}
</script>

<style scoped>
/* 样式保持不变 */
:global(html),
:global(body) {
  margin: 0 !important;
  padding: 0 !important;
  height: 100% !important;
  overflow: hidden !important;
  font-family: "Microsoft Yahei", sans-serif;
}

.register-container {
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

.register-card {
  width: 420px;
  padding: 40px 30px;
  border-radius: 16px;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);
  background: #fff;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  pointer-events: auto;
}
.register-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 16px 40px rgba(0, 0, 0, 0.2);
}

.register-title {
  text-align: center;
  color: #2d3748;
  font-size: 24px;
  font-weight: 700;
  margin: 0 0 35px 0;
  letter-spacing: 2px;
}

.form-item {
  margin-bottom: 22px;
}

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

.password-icon {
  display: inline-flex !important;
  align-items: center !important;
  justify-content: center !important;
  width: 28px !important;
  height: 28px !important;
  color: #666 !important;
  cursor: pointer !important;
  font-size: 20px !important;
  z-index: 9999 !important;
  margin-right: 8px !important;
}
.password-icon:hover {
  color: #4299e1 !important;
}
.password-icon.disabled {
  color: #ccc !important;
  cursor: not-allowed !important;
  pointer-events: none;
}

.tip-text {
  text-align: center;
  font-size: 12px;
  color: #718096;
  margin: 10px 0 25px 0;
}
.login-link {
  color: #6aaef7;
  text-decoration: none;
  transition: all 0.3s ease;
  cursor: pointer;
}
.login-link:hover {
  color: #4299e1;
  text-decoration: underline;
}

.btn-item {
  margin-bottom: 0;
  text-align: center;
}
.register-btn {
  width: 100%;
  height: 48px;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(90deg, #8ec5fc 0%, #6aaef7 100%);
  border: none;
  transition: all 0.3s ease;
}
:deep(.el-button--primary.register-btn:hover) {
  background: linear-gradient(90deg, #7bb8f8 0%, #4299e1 100%);
}
:deep(.el-button--primary.register-btn:active) {
  transform: scale(0.98);
}

:deep(.el-input__suffix) {
  pointer-events: auto !important;
  width: 40px !important;
  height: 100% !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
}
</style>