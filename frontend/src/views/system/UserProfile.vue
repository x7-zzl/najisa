<template>
  <div class="profile-page">
    <div class="profile-panel">
      <!-- ====== 顶部：头像 + 身份 ====== -->
      <div class="identity-bar">
        <div class="avatar-wrap">
          <el-avatar :size="64" :src="userForm.userAvatar" class="avatar-img">
            {{ avatarText }}
          </el-avatar>
          <div class="level-badge" :class="badgeClass">
            <el-icon :size="14">
              <Medal v-if="levelIcon === 'medal'" />
              <Trophy v-else-if="levelIcon === 'trophy'" />
              <Crown v-else-if="levelIcon === 'crown'" />
              <StarFilled v-else />
            </el-icon>
          </div>
        </div>
        <div class="identity-text">
          <span class="nick">{{ userForm.nickName || userForm.userName }}</span>
          <span class="level-title">{{ wealthData.levelTitle || '一转蛊师' }}</span>
        </div>
      </div>

      <!-- ====== 中部：财富横条 ====== -->
      <div class="wealth-row">
        <div class="w-item">
          <span class="w-icon stone">
            <el-icon :size="16"><Present /></el-icon>
          </span>
          <span class="w-num">{{ wealthData.originStone }}</span>
          <span class="w-label">元石</span>
        </div>
        <div class="w-div"></div>
        <div class="w-item">
          <span class="w-icon immortal">
            <el-icon :size="16"><StarFilled /></el-icon>
          </span>
          <span class="w-num">{{ wealthData.immortalOriginStone }}</span>
          <span class="w-label">仙元石</span>
        </div>
        <div class="w-div"></div>
        <div class="w-item">
          <span class="w-icon exp">
            <el-icon :size="16"><Lightning /></el-icon>
          </span>
          <span class="w-num">{{ wealthData.experience }}</span>
          <span class="w-label">经验</span>
        </div>
      </div>

      <!-- ====== 底部：可编辑表单 ====== -->
      <div class="form-section">
        <div class="sec-head">
          {{ isReadOnly ? '个人信息' : '编辑资料' }}
          <span v-if="isReadOnly" class="tag">只读</span>
        </div>

        <el-form :model="userForm" label-width="72px" size="default">
          <el-form-item label="用户名">
            <el-input v-model="userForm.userName" disabled />
          </el-form-item>
          <el-form-item label="昵称">
            <el-input v-model="userForm.nickName" placeholder="请输入昵称" :disabled="isReadOnly" />
          </el-form-item>
          <el-row :gutter="12">
            <el-col :span="12">
              <el-form-item label="性别">
                <el-radio-group v-model="userForm.sex" :disabled="isReadOnly" size="small">
                  <el-radio label="男">男</el-radio>
                  <el-radio label="女">女</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="年龄">
                <el-input-number v-model="userForm.age" :min="1" :max="150" :disabled="isReadOnly"
                  size="small" controls-position="right" style="width:100%" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="身份证号">
            <el-input v-model="userForm.idCard" placeholder="请输入身份证号码" :disabled="isReadOnly" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="userForm.phoneNumber" placeholder="请输入手机号" :disabled="isReadOnly" />
          </el-form-item>
          <el-form-item label="邮箱地址">
            <el-input v-model="userForm.emailAddress" placeholder="请输入邮箱地址" :disabled="isReadOnly" />
          </el-form-item>

          <!-- 编辑模式：头像上传 -->
          <el-form-item v-if="!isReadOnly" label="头像">
            <div class="upload-row">
              <el-upload
                action="" :show-file-list="false"
                :http-request="handleAvatarUpload" :before-upload="beforeAvatarUpload"
              >
                <div class="upload-btn">
                  <img v-if="userForm.userAvatar" :src="userForm.userAvatar" class="upload-thumb" />
                  <el-icon v-else :size="18"><Plus /></el-icon>
                </div>
              </el-upload>
              <span class="upload-tip">JPG / PNG，小于 2MB</span>
            </div>
          </el-form-item>

          <el-form-item v-if="!isReadOnly" class="btn-row">
            <el-button type="primary" @click="submitForm" :loading="submitting">保存</el-button>
            <el-button @click="resetForm">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Medal, Trophy, Crown, StarFilled, Present, Lightning } from '@element-plus/icons-vue'
import { useRoute } from 'vue-router'
import { getUserProfile, updateUserProfile, uploadFile, deleteLocalFileByUrl } from '@/utils/request'

const route = useRoute()
const loginUserId = localStorage.getItem('loginUserId')
const targetUserId = computed(() => route.query.userId || loginUserId)
const isReadOnly = computed(() =>
  route.query.readonly === '1' || (route.query.userId && route.query.userId !== loginUserId)
)

const avatarText = computed(() => {
  const n = (userForm.value.nickName || userForm.value.userName || '').trim()
  return n ? n[0].toUpperCase() : 'U'
})

const userForm = ref({
  id: '', userName: '', nickName: '', idCard: '',
  age: 0, sex: '男', phoneNumber: '', emailAddress: '', userAvatar: ''
})
const wealthData = ref({
  level: 1, levelTitle: '一转蛊师', levelCategory: '蛊师',
  originStone: 0, immortalOriginStone: 0, experience: 0
})

const submitting = ref(false)
const originalAvatarUrl = ref('')
const hasSaved = ref(false)

// ---- 根据 levelCategory 决定图标和样式 ----
const levelIcon = computed(() => {
  switch (wealthData.value.levelCategory) {
    case '蛊仙': return 'trophy'
    case '蛊尊': return 'crown'
    case '至尊': return 'star'
    default:     return 'medal'
  }
})

const badgeClass = computed(() => 'badge-' + (levelIcon.value))

// ---- 数据加载 ----
const fetchUserProfile = async () => {
  if (!targetUserId.value) return
  try {
    const res = await getUserProfile(targetUserId.value)
    if (res.code === 200) {
      const d = res.data
      userForm.value = {
        id: d.id || '', userName: d.userName || '', nickName: d.nickName || '',
        idCard: d.idCard || '', age: d.age || 0, sex: d.sex || '男',
        phoneNumber: d.phoneNumber || '', emailAddress: d.emailAddress || '',
        userAvatar: d.userAvatar || ''
      }
      wealthData.value = {
        level: d.level ?? 1,
        levelTitle: d.levelTitle || '一转蛊师',
        levelCategory: d.levelCategory || '蛊师',
        originStone: d.originStone ?? 0,
        immortalOriginStone: d.immortalOriginStone ?? 0,
        experience: d.experience ?? 0
      }
      originalAvatarUrl.value = d.userAvatar || ''
    }
  } catch { ElMessage.error('获取用户信息失败') }
}

// ---- 头像 ----
const beforeAvatarUpload = (file) => {
  const ok = (file.type === 'image/jpeg' || file.type === 'image/png') && file.size / 1024 / 1024 < 2
  if (!ok) ElMessage.error('仅支持 JPG/PNG，且不超过 2MB')
  return ok
}
const handleAvatarUpload = async (options) => {
  try {
    const res = await uploadFile(options.file)
    if (res.code === 200) {
      const old = userForm.value.userAvatar
      if (old && old !== originalAvatarUrl.value && old !== res.data) {
        await deleteLocalFileByUrl(old).catch(() => {})
      }
      userForm.value.userAvatar = res.data
      ElMessage.success('头像上传成功')
    } else { ElMessage.error(res.message || '上传失败') }
  } catch { ElMessage.error('头像上传失败') }
}

// ---- 提交 ----
const submitForm = async () => {
  if (isReadOnly.value) return
  submitting.value = true
  try {
    const res = await updateUserProfile(userForm.value)
    if (res.code === 200) {
      hasSaved.value = true
      localStorage.setItem('userAvatar', userForm.value.userAvatar || '')
      localStorage.setItem('loginUserName', userForm.value.nickName || userForm.value.userName)
      ElMessage.success('个人信息更新成功')
      window.location.reload()
    } else { ElMessage.error(res.message || '更新失败') }
  } catch { ElMessage.error('更新失败') }
  finally { submitting.value = false }
}
const resetForm = () => fetchUserProfile()

onMounted(() => fetchUserProfile())
watch(() => route.query.userId, () => fetchUserProfile())
onUnmounted(async () => {
  if (isReadOnly.value || hasSaved.value) return
  const url = userForm.value.userAvatar || ''
  if (url && url !== originalAvatarUrl.value) await deleteLocalFileByUrl(url).catch(() => {})
})
</script>

<style scoped>
/* ====== 页面容器 ====== */
.profile-page {
  height: calc(100vh - 64px - 32px);
  overflow: hidden;
  display: flex;
  justify-content: center;
}

.profile-panel {
  width: 100%;
  max-width: 520px;
  height: 100%;
  overflow-y: auto;
  padding: 0 4px 24px;
  display: flex;
  flex-direction: column;
  gap: 0;
}

/* 滚动条 */
.profile-panel::-webkit-scrollbar { width: 4px; }
.profile-panel::-webkit-scrollbar-track { background: transparent; }
.profile-panel::-webkit-scrollbar-thumb { background: rgba(0,0,0,0.10); border-radius: 99px; }

/* ====== 身份区 ====== */
.identity-bar {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0 14px;
  gap: 10px;
}

.avatar-wrap {
  position: relative;
}

.avatar-img {
  border: 3px solid rgba(255,255,255,0.9);
  box-shadow: 0 3px 12px rgba(0,0,0,0.10);
}

/* 等级徽章（叠在头像右下） */
.level-badge {
  position: absolute;
  right: -4px;
  bottom: -2px;
  width: 26px;
  height: 26px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  border: 2px solid #fff;
  box-shadow: 0 2px 6px rgba(0,0,0,0.15);
}

/* 蛊师：铜铁渐暖 */
.badge-medal  { background: linear-gradient(135deg, #b8753b, #d4944a); }
/* 蛊仙：银蓝 */
.badge-trophy { background: linear-gradient(135deg, #7ba4c7, #a0c4e8); }
/* 蛊尊：金 */
.badge-crown  { background: linear-gradient(135deg, #f5af19, #e6a817); }
/* 至尊：五彩 */
.badge-star   { background: linear-gradient(135deg, #667eea, #f093fb, #f5af19); }

.identity-text {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
}

.nick {
  font-size: 17px;
  font-weight: 700;
  color: #1f2d3d;
}

.level-title {
  font-size: 13px;
  font-weight: 600;
  color: #909399;
  letter-spacing: 1px;
}

/* ====== 财富横条 ====== */
.wealth-row {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 12px 8px;
  margin: 0 0 12px;
  background: rgba(255,255,255,0.55);
  backdrop-filter: blur(6px);
  border-radius: 14px;
  border: 1px solid rgba(0,0,0,0.04);
}

.w-item {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
}

.w-icon {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}
.w-icon.stone    { background: linear-gradient(135deg, #f5af19, #f12711); }
.w-icon.immortal { background: linear-gradient(135deg, #00d2ff, #3a7bd5); }
.w-icon.exp      { background: linear-gradient(135deg, #11998e, #38ef7d); }

.w-num {
  font-size: 17px;
  font-weight: 700;
  color: #303133;
}

.w-label {
  font-size: 11px;
  color: #909399;
}

.w-div {
  width: 1px;
  height: 22px;
  background: rgba(0,0,0,0.07);
  flex-shrink: 0;
}

/* ====== 表单区 ====== */
.form-section {
  background: rgba(255,255,255,0.55);
  backdrop-filter: blur(6px);
  border-radius: 14px;
  border: 1px solid rgba(0,0,0,0.04);
  padding: 16px 20px 20px;
}

.sec-head {
  font-size: 14px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.tag {
  font-size: 10px;
  color: #909399;
  background: rgba(0,0,0,0.06);
  padding: 2px 8px;
  border-radius: 99px;
}

:deep(.el-form-item) {
  margin-bottom: 12px;
}
:deep(.el-form-item__label) {
  font-size: 13px;
  color: #606266;
  font-weight: 500;
}

/* 头像上传 */
.upload-row {
  display: flex;
  align-items: center;
  gap: 12px;
}
.upload-btn {
  width: 44px;
  height: 44px;
  border: 1px dashed #d9d9d9;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #8c939d;
  overflow: hidden;
  transition: border-color .2s;
}
.upload-btn:hover { border-color: #409eff; }
.upload-thumb {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.upload-tip {
  font-size: 11px;
  color: #b0b3bb;
}

.btn-row {
  margin-top: 4px;
  padding-top: 10px;
  border-top: 1px solid rgba(0,0,0,0.05);
}
</style>
