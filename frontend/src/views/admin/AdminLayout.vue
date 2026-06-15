<template>
  <el-container class="admin-layout">
    <el-aside width="220px" class="admin-aside">
      <div class="logo-area">
        <span class="logo-text">后台管理</span>
      </div>
      <el-menu
        :default-active="activePath"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        class="admin-menu"
        router
      >
        <el-menu-item index="/admin/user">
          <el-icon><User /></el-icon>
          <span>用户账号管理</span>
        </el-menu-item>
        
        <el-menu-item index="/admin/insect">
          <el-icon><Collection /></el-icon> 
          <span>蛊虫信息管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="admin-header">
        <div class="breadcrumb">
          后台管理系统 / {{ currentTitle }}
        </div>
        <div class="user-info">
          <el-button type="danger" size="small" @click="logout" class="logout-btn">
            <el-icon style="margin-right: 4px"><SwitchButton /></el-icon>
            退出
          </el-button>
        </div>
      </el-header>

      <el-main class="admin-main">
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { User, Collection, SwitchButton } from '@element-plus/icons-vue' // 注意这里引入了 Collection
import { ElMessage, ElMessageBox } from 'element-plus'
import { logout as apiLogout } from '@/utils/request'

const route = useRoute()
const router = useRouter()

const activePath = computed(() => route.path)
const currentTitle = computed(() => route.meta.title || '管理页面')

const logout = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要退出后台管理系统吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 调用后端退出接口
    await apiLogout()
    
    // 清除本地缓存
    localStorage.clear()
    ElMessage.success('已安全退出')
    router.push('/')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Logout error:', error)
      // 即使后端报错，也要清理本地缓存并跳转
      localStorage.clear()
      router.push('/')
    }
  }
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
  width: 100vw;
}

.admin-aside {
  background-color: #304156;
  color: #fff;
}

.logo-area {
  height: 60px;
  line-height: 60px;
  text-align: center;
  background-color: #2b2f3a;
  font-weight: bold;
  font-size: 18px;
  letter-spacing: 1px;
}

.admin-menu {
  border-right: none;
  height: calc(100vh - 60px);
}

.admin-header {
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 60px;
}

.breadcrumb {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.user-info {
  display: flex;
  align-items: center;
}

.logout-btn {
  background: linear-gradient(135deg, #ff6b6b 0%, #f56c6c 100%);
  border: none;
  color: white;
  border-radius: 6px;
  padding: 8px 16px;
  transition: all 0.3s ease;
}

.logout-btn:hover {
  background: linear-gradient(135deg, #ff5252 0%, #e64a4a 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(245, 108, 108, 0.2);
}

.logout-btn:active {
  transform: translateY(0);
}

.admin-main {
  background: #f0f2f5;
  padding: 20px;
  min-height: calc(100vh - 60px);
}
</style>