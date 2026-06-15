<template>
  <el-container class="layout">
    <el-header class="header">
      <div class="left">
        <div class="brand" @click="goToHome">
          <div class="brand-title">天地一家大爱盟</div>
          <div class="brand-sub">Anime Forum</div>
        </div>

        <div class="menu-wrap">
          <el-menu
            mode="horizontal"
            :default-active="activePath"
            class="top-menu"
            :ellipsis="false"
            @select="onSelectMenu"
          >
            <el-menu-item index="/system/gujie">蛊界佚闻</el-menu-item>
            <el-menu-item index="/system/zhongsheng">众生万象</el-menu-item>
            <el-menu-item index="/system/baohuang">宝黄天</el-menu-item>
            <el-menu-item index="/system/tianxia">天下名蛊</el-menu-item>
          </el-menu>
        </div>
      </div>

      <div class="right">
    <!-- 消息铃铛组件 -->
    <div class="notification-wrapper">
      <MessageNotification />
    </div>

    <!-- AI助手入口 -->
    <el-button circle class="ai-chat-btn" @click="goAiChat" title="AI助手">
      <el-icon :size="20"><Monitor /></el-icon>
    </el-button>

    <!-- 用户头像部分 -->
    <el-dropdown trigger="click">
      <span class="user-box">
        <el-avatar :size="34" class="avatar" :src="userAvatar">{{ avatarText }}</el-avatar>
        <span class="username">{{ userName }}</span>
        <el-icon class="down"><ArrowDown /></el-icon>
      </span>

      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item @click="goUserProfile">
            <el-icon><User /></el-icon>
            个人信息
          </el-dropdown-item>
          <el-dropdown-item @click="logout">
            <el-icon><SwitchButton /></el-icon>
            退出登录
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
    </el-header>

    <el-main class="main">
      <div class="main-bg" :style="bgStyle">
        <router-view />
      </div>
    </el-main>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowDown, SwitchButton, Monitor, User } from '@element-plus/icons-vue'
import MessageNotification from '@/components/MessageNotification.vue'
import bgUrl from '@/assets/forum-bg.jpg'
import { logout as apiLogout } from '@/utils/request'

const route = useRoute()
const router = useRouter()

const userName = computed(() => localStorage.getItem('loginUserName') || '游客')
const userAvatar = computed(() => localStorage.getItem('userAvatar'))

const avatarText = computed(() => {
  const n = (userName.value || '').trim()
  return n ? n[0].toUpperCase() : 'U'
})

const activePath = computed(() => route.path)

const onSelectMenu = (index) => router.push(index)

const goAiChat = () => router.push('/system/ai-chat')

const goUserProfile = () => router.push('/system/user-profile')

// ✅ 点击品牌跳转到首页(蛊界佚闻)
const goToHome = () => {
  router.push('/system/gujie')
}

const logout = async () => {
  try {
    await apiLogout()
  } catch (error) {
    console.error('Logout failed:', error)
  } finally {
    localStorage.removeItem('isLoggedIn')
    localStorage.removeItem('loginUserName')
    localStorage.removeItem('user_token')
    localStorage.removeItem('userAvatar')
    localStorage.removeItem('loginUserId')
    ElMessage.success('已退出登录')
    router.push('/')
  }
}

const bgStyle = computed(() => ({
  backgroundImage: `url(${bgUrl})`
}))
</script>

<style scoped>
/* ====== 基础布局 ====== */
.layout {
  min-height: 100vh;
  background: #f6f7fb;
}

/* ====== 顶部(更舒展,不贴顶) ====== */
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;

  height: 64px;
  padding: 0 18px;

  background: rgba(255, 255, 255, 0.90);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(235, 238, 245, 0.9);
}

/* 左侧:品牌 + 菜单 */
.left {
  display: flex;
  align-items: center;
  gap: 18px;
  flex: 1;
  min-width: 0;
}

/* 品牌 - 可点击 */
.brand {
  display: flex;
  flex-direction: column;
  line-height: 1.1;
  white-space: nowrap;
  flex-shrink: 0;
  transform: translateY(1px);
  cursor: pointer;  /* ✅ 添加鼠标指针 */
  transition: opacity 0.3s ease;
}

.brand:hover {
  opacity: 0.7;  /* ✅ 悬停效果 */
}

.brand-title {
  font-weight: 900;
  font-size: 15px;
  color: #1f2d3d;
  letter-spacing: 0.4px;
}

.brand-sub {
  font-size: 12px;
  color: #909399;
}

/* 菜单容器:占满剩余空间,必要时横向滚动 */
.menu-wrap {
  flex: 1;
  min-width: 0;
  display: flex;
  justify-content: flex-start;
  overflow-x: auto;
  overflow-y: hidden;
  padding-top: 2px;
}

.menu-wrap::-webkit-scrollbar {
  height: 6px;
}
.menu-wrap::-webkit-scrollbar-thumb {
  border-radius: 999px;
  background: rgba(0, 0, 0, 0.12);
}

/* el-menu */
.top-menu {
  width: max-content;
  border-bottom: none;
  background: transparent;
}

:deep(.el-menu--horizontal) {
  border-bottom: none !important;
}

/* ====== 菜单项:更轻量、更美观 ====== */
:deep(.el-menu--horizontal .el-menu-item) {
  position: relative;

  height: 42px;
  line-height: 42px;
  margin: 0 6px;
  padding: 0 14px;

  border-radius: 12px;
  font-weight: 700;
  color: #303133;

  transition: background 200ms ease, color 200ms ease, transform 200ms ease;

  white-space: nowrap;
  overflow: visible;
  text-overflow: unset;
}

/* hover:轻微提亮 */
:deep(.el-menu--horizontal .el-menu-item:hover) {
  background: rgba(64, 158, 255, 0.08);
}

/* ✅ 下划线动画:默认隐藏 */
:deep(.el-menu--horizontal .el-menu-item::after) {
  content: "";
  position: absolute;
  left: 14px;
  right: 14px;
  bottom: 6px;
  height: 2px;
  border-radius: 999px;
  background: rgba(64, 158, 255, 0.85);
  transform: scaleX(0);
  transform-origin: center;
  transition: transform 220ms ease;
}

/* ✅ 激活态:淡色胶囊 + 细下划线 */
:deep(.el-menu--horizontal .el-menu-item.is-active) {
  background: rgba(64, 158, 255, 0.12);
  color: #1f2d3d !important;
  transform: translateY(-0.5px);
}

:deep(.el-menu--horizontal .el-menu-item.is-active::after) {
  transform: scaleX(1);
}


/* ====== 右侧用户区 - 添加间距 ====== */
.right {
  display: flex;
  align-items: center;
  gap: 16px; /* 增加间距 */
  flex-shrink: 0;
}

/* 消息通知包裹容器 */
.notification-wrapper {
  margin-right: 8px;
}

/* AI助手按钮 */
.ai-chat-btn {
  border: none;
  background: transparent;
  color: #606266;
  transition: color 0.2s, background 0.2s;
}

.ai-chat-btn:hover {
  color: #409eff;
  background: rgba(64, 158, 255, 0.08);
}


.user-box {
  display: inline-flex;
  align-items: center;
  cursor: pointer;
  user-select: none;
}

.avatar {
  margin-right: 10px;
}

.username {
  font-size: 14px;
  font-weight: 700;
  color: #303133;
}

.down {
  margin-left: 6px;
  color: #909399;
}

/* ====== 主体背景:图片 cover 自适应 ====== */
.main {
  padding: 0;
}

.main-bg {
  position: relative;
  min-height: calc(100vh - 64px);
  padding: 16px;

  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

/* ✅ 遮罩更轻:让背景更清晰但不影响阅读 */
.main-bg::before {
  content: "";
  position: absolute;
  inset: 0;
  background: rgba(246, 247, 251, 0.62);
  backdrop-filter: blur(1px);
  pointer-events: none;
}

.main-bg > * {
  position: relative;
  z-index: 1;
}
</style>