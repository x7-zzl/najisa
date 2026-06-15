<template>
  <div class="user-profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>{{ isReadOnly ? '个人信息' : '个人信息修改' }}</span>
        </div>
      </template>

      <el-form :model="userForm" label-width="100px" class="profile-form">
        <!-- 头像上传 -->
        <el-form-item label="用户头像">
          <div v-if="isReadOnly" class="avatar-uploader">
            <img v-if="userForm.userAvatar" :src="userForm.userAvatar" class="avatar" />
            <div v-else class="avatar-placeholder">
              <el-icon class="avatar-uploader-icon"><Plus /></el-icon>
              <span>暂无头像</span>
            </div>
          </div>
          <template v-else>
            <el-upload
              class="avatar-uploader"
              action=""
              :show-file-list="false"
              :http-request="handleAvatarUpload"
              :before-upload="beforeAvatarUpload"
            >
              <img v-if="userForm.userAvatar" :src="userForm.userAvatar" class="avatar" />
              <div v-else class="avatar-placeholder">
                <el-icon class="avatar-uploader-icon"><Plus /></el-icon>
                <span>上传头像</span>
              </div>
            </el-upload>
            <div class="upload-tip">支持 jpg/png 格式，大小不超过 2MB</div>
          </template>
        </el-form-item>

        <el-form-item label="用户名">
          <el-input v-model="userForm.userName" disabled />
        </el-form-item>

        <el-form-item label="昵称">
          <el-input v-model="userForm.nickName" placeholder="请输入昵称" :disabled="isReadOnly" />
        </el-form-item>

        <el-form-item label="性别">
          <el-radio-group v-model="userForm.sex" :disabled="isReadOnly">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="年龄">
          <el-input-number v-model="userForm.age" :min="1" :max="150" :disabled="isReadOnly" />
        </el-form-item>

        <el-form-item label="身份证号">
          <el-input v-model="userForm.idCard" placeholder="请输入身份证号码" :disabled="isReadOnly" />
        </el-form-item>

        <el-form-item label="手机号">
          <el-input v-model="userForm.phoneNumber" placeholder="请输入手机号" :disabled="isReadOnly" />
        </el-form-item>

        <el-form-item label="邮箱地址">
          <el-input v-model="userForm.emailAddress" placeholder="请输入邮箱地址" :disabled="isReadOnly" />
        </el-form-item>

        <el-form-item v-if="!isReadOnly">
          <el-button type="primary" @click="submitForm" :loading="submitting">保存修改</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useRoute } from 'vue-router'
import { getUserProfile, updateUserProfile, uploadFile, deleteLocalFileByUrl } from '@/utils/request'

const route = useRoute()
const loginUserId = localStorage.getItem('loginUserId')
const targetUserId = computed(() => route.query.userId || loginUserId)
const isReadOnly = computed(() => {
  if (route.query.readonly === '1') return true
  if (route.query.userId && route.query.userId !== loginUserId) return true
  return false
})
const userForm = ref({
  id: '',
  userName: '',
  nickName: '',
  idCard: '',
  age: 0,
  sex: '男',
  phoneNumber: '',
  emailAddress: '',
  userAvatar: ''
})

const submitting = ref(false)
const originalAvatarUrl = ref('')
const hasSaved = ref(false)

// 加载用户信息
const fetchUserProfile = async () => {
  if (!targetUserId.value) return
  try {
    const res = await getUserProfile(targetUserId.value)
    if (res.code === 200) {
      userForm.value = res.data
      originalAvatarUrl.value = res.data?.userAvatar || ''
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('获取用户信息失败')
  }
}

// 上传头像前的校验
const beforeAvatarUpload = (file) => {
  const isJPGorPNG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPGorPNG) {
    ElMessage.error('上传头像图片只能是 JPG 或 PNG 格式!')
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!')
  }
  return isJPGorPNG && isLt2M
}

// 处理头像上传
const handleAvatarUpload = async (options) => {
  try {
    const res = await uploadFile(options.file)
    if (res.code === 200) {
      const oldUrl = userForm.value.userAvatar || ''
      const newUrl = res.data
      if (oldUrl && oldUrl !== originalAvatarUrl.value && oldUrl !== newUrl) {
        await deleteLocalFileByUrl(oldUrl)
      }
      userForm.value.userAvatar = res.data
      ElMessage.success('头像上传成功')
    } else {
      ElMessage.error(res.message || '头像上传失败')
    }
  } catch (error) {
    console.error('头像上传失败:', error)
    ElMessage.error('头像上传失败')
  }
}

// 提交表单
const submitForm = async () => {
  if (isReadOnly.value) {
    return
  }
  submitting.value = true
  try {
    const res = await updateUserProfile(userForm.value)
    if (res.code === 200) {
      hasSaved.value = true
      ElMessage.success('个人信息更新成功')
      // 更新本地存储的头像和昵称（如果需要）
      localStorage.setItem('userAvatar', userForm.value.userAvatar || '')
      localStorage.setItem('loginUserName', userForm.value.nickName || userForm.value.userName)
      // 触发页面刷新或通过事件总线通知 Layout 更新
      window.location.reload() 
    } else {
      ElMessage.error(res.message || '更新失败')
    }
  } catch (error) {
    console.error('更新个人信息失败:', error)
    ElMessage.error('更新个人信息失败')
  } finally {
    submitting.value = false
  }
}

const resetForm = () => {
  fetchUserProfile()
}

onMounted(() => {
  fetchUserProfile()
})

watch(
  () => route.query.userId,
  () => {
    fetchUserProfile()
  }
)

onUnmounted(async () => {
  if (isReadOnly.value) return
  if (hasSaved.value) return
  const url = userForm.value.userAvatar || ''
  if (url && url !== originalAvatarUrl.value) {
    await deleteLocalFileByUrl(url)
  }
})
</script>

<style scoped>
.user-profile-container {
  display: flex;
  justify-content: center;
  padding: 20px;
}

.profile-card {
  width: 100%;
  max-width: 600px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-radius: 12px;
}

.card-header {
  font-weight: bold;
  font-size: 18px;
  color: #303133;
}

.profile-form {
  margin-top: 20px;
}

.avatar-uploader {
  text-align: center;
}

.avatar-uploader .avatar {
  width: 100px;
  height: 100px;
  display: block;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #ebeef5;
}

.avatar-placeholder {
  width: 100px;
  height: 100px;
  border: 1px dashed #d9d9d9;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #8c939d;
  transition: border-color 0.3s;
}

.avatar-placeholder:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  margin-bottom: 8px;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}
</style>
