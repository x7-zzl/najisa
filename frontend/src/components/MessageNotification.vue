<template>
  <div class="message-notification">
    <el-badge :value="unreadCount" :hidden="unreadCount === 0">
      <el-button circle class="message-btn" @click="toggleDrawer">
        <el-icon :size="20"><Bell /></el-icon>
      </el-button>
    </el-badge>

    <!-- 修改后的抽屉，增加 append-to-body 和更高的 z-index -->
    <el-drawer
      v-model="drawerVisible"
      title="系统消息"
      direction="rtl"
      size="400px"
      :append-to-body="true"
      :z-index="9999"
      :close-on-click-modal="true"
      :destroy-on-close="false"
      @open="loadMessages"
      class="message-drawer"
    >
      <div class="drawer-content">
        <div class="drawer-actions">
          <el-button size="small" @click="markAllRead" type="primary" plain>
            全部已读
          </el-button>
          <el-button size="small" @click="loadMessages" circle>
            <el-icon><Refresh /></el-icon>
          </el-button>
        </div>

        <div v-if="messageList.length > 0" class="message-list">
          <div
            v-for="msg in messageList"
            :key="msg.id"
            class="message-item"
            :class="{ 'unread': msg.isRead === 0, 'read': msg.isRead === 1 }"
            @click="handleMessageClick(msg)"
          >
            <div class="message-header">
              <span class="message-title">{{ msg.title }}</span>
              <el-tag v-if="msg.isRead === 0" size="small" type="danger">未读</el-tag>
              <el-tag v-else size="small" type="info">已读</el-tag>
            </div>
            
            <div class="message-content">{{ msg.content }}</div>
            
            <div class="message-footer">
              <span class="message-time">{{ formatTime(msg.createTime) }}</span>
            </div>
          </div>
        </div>

        <el-empty v-else description="暂无消息" />
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Bell, Refresh } from '@element-plus/icons-vue'
import request from '@/utils/request'

const drawerVisible = ref(false)
const messageList = ref([])
const unreadCount = ref(0)
let websocket = null

const loadUnreadCount = async () => {
  try {
    const res = await request.get('/sysMessage/unreadCount')
    if (res.code === 200) {
      unreadCount.value = res.data || 0
    }
  } catch (error) {
    console.error('加载未读消息数量失败:', error)
  }
}

const loadMessages = async () => {
  try {
    const res = await request.get('/sysMessage/unread')
    if (res.code === 200) {
      messageList.value = res.data || []
    }
  } catch (error) {
    console.error('加载消息列表失败:', error)
  }
}

const toggleDrawer = () => {
  drawerVisible.value = !drawerVisible.value
}

const handleMessageClick = async (msg) => {
  if (msg.isRead === 0) {
    try {
      const res = await request.post('/sysMessage/markRead', null, {
        params: { messageId: msg.id }
      })
      if (res.code === 200) {
        msg.isRead = 1
        unreadCount.value = Math.max(0, unreadCount.value - 1)
        // 重新排序：未读在前，已读在后
        sortMessages()
      }
    } catch (error) {
      console.error('标记已读失败:', error)
    }
  }
}

const markAllRead = async () => {
  try {
    const res = await request.post('/sysMessage/markAllRead')
    if (res.code === 200) {
      ElMessage.success('已全部标记为已读')
      messageList.value.forEach(msg => {
        msg.isRead = 1
      })
      unreadCount.value = 0
    }
  } catch (error) {
    console.error('标记全部已读失败:', error)
  }
}

// 消息排序：未读在前，已读在后，同状态按时间倒序
const sortMessages = () => {
  messageList.value.sort((a, b) => {
    if (a.isRead !== b.isRead) {
      return a.isRead - b.isRead
    }
    const timeA = new Date(a.createTime).getTime()
    const timeB = new Date(b.createTime).getTime()
    return timeB - timeA
  })
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date

  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  
  return date.toLocaleDateString()
}

const initWebSocket = () => {
  const userId = localStorage.getItem('loginUserName') || 'guest'
  const wsUrl = `ws://localhost:8088/umr/websocket/${userId}`

  websocket = new WebSocket(wsUrl)

  websocket.onopen = () => {
    console.log('WebSocket连接成功')
  }

  websocket.onmessage = (event) => {
    const raw = event?.data
    if (typeof raw !== 'string' || !raw) return

    const trimmed = raw.trim()
    if (trimmed === '连接成功') return

    let message = null
    if (trimmed.startsWith('{') || trimmed.startsWith('[')) {
      try {
        message = JSON.parse(trimmed)
      } catch (error) {
        console.error('解析WebSocket消息失败:', error)
        return
      }
    } else {
      message = { content: trimmed }
    }

    unreadCount.value += 1
    if (drawerVisible.value) {
      loadMessages()
    }
    ElMessage({
      message: message.content || '您有新的消息',
      type: 'info',
      duration: 3000
    })
  }

  websocket.onerror = (error) => {
    console.error('WebSocket错误:', error)
  }

  websocket.onclose = () => {
    console.log('WebSocket连接关闭')
    setTimeout(() => {
      initWebSocket()
    }, 5000)
  }
}

const closeWebSocket = () => {
  if (websocket) {
    websocket.close()
    websocket = null
  }
}

onMounted(() => {
  loadUnreadCount()
  initWebSocket()
})

onUnmounted(() => {
  closeWebSocket()
})
</script>

<style scoped>
.message-notification {
  position: relative;
  z-index: 1000;
}

.message-btn {
  border: none;
  background: rgba(255, 255, 255, 0.2);
}

.message-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

/* 确保抽屉内容正确显示 */
.drawer-content {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.drawer-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 0 16px;
  flex-shrink: 0;
}

.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 0 16px;
}

.message-item {
  padding: 16px;
  margin-bottom: 12px;
  background: #f5f7fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  border-left: 3px solid transparent;
}

.message-item:hover {
  background: #ebeef5;
  transform: translateX(-2px);
}

.message-item.unread {
  background: #ecf5ff;
  border-left-color: #409eff;
}

.message-item.read {
  opacity: 0.7;
}

.message-item.read .message-title {
  color: #909399;
  font-weight: 500;
}

.message-item.read .message-content {
  color: #a8abb2;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.message-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.message-content {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
  line-height: 1.5;
}

.message-footer {
  display: flex;
  justify-content: flex-end;
}

.message-time {
  font-size: 12px;
  color: #909399;
}
</style>

<!-- 全局样式，确保抽屉层级正确 -->
<style>
.message-drawer .el-drawer {
  z-index: 9999 !important;
}

.message-drawer .el-drawer__header {
  margin-bottom: 16px;
  padding: 16px;
  border-bottom: 1px solid #ebeef5;
}

.message-drawer .el-drawer__body {
  padding: 16px 0;
}

.message-drawer .el-overlay {
  z-index: 9998 !important;
}
</style>
