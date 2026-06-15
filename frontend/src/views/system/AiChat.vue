<template>
  <div class="ai-chat-page">
    <!-- 左侧：对话列表 -->
    <div class="chat-sidebar">
      <div class="sidebar-header">
        <span class="sidebar-title">聊天记录</span>
        <el-button type="primary" size="small" @click="handleNewChat" :loading="creatingChat">
          <el-icon style="margin-right: 4px"><Plus /></el-icon>新对话
        </el-button>
      </div>
      <div class="sidebar-list" v-loading="listLoading">
        <div
          v-for="chat in chatList"
          :key="chat.chatId"
          class="chat-item"
          :class="{ active: currentChatId === chat.chatId }"
          @click="selectChat(chat)"
        >
          <el-icon class="chat-item-icon"><ChatLineSquare /></el-icon>
          <span class="chat-item-text">{{ chat.title || ('对话 ' + String(chat.chatId).slice(-6)) }}</span>
        </div>
        <div v-if="!listLoading && chatList.length === 0" class="empty-tip">
          暂无对话记录
        </div>
      </div>
    </div>

    <!-- 右侧：聊天区域 -->
    <div class="chat-main">
      <!-- 消息展示区 -->
      <div class="message-area" ref="messageAreaRef">
        <div v-if="messagesLoading" class="loading-wrap">
          <el-icon class="is-loading" :size="24"><Loading /></el-icon>
          <span>加载中...</span>
        </div>
        <template v-else>
          <div v-if="messages.length === 0 && currentChatId" class="empty-messages">
            开始你的对话吧
          </div>
          <div v-if="!currentChatId" class="empty-messages">
            选择一个对话或创建新对话
          </div>
          <div
            v-for="(msg, idx) in messages"
            :key="idx"
            class="message-row"
            :class="msg.role === 'user' ? 'msg-right' : 'msg-left'"
          >
            <!-- AI消息 -->
            <template v-if="msg.role !== 'user'">
              <el-icon class="msg-avatar ai-avatar" :size="28"><Monitor /></el-icon>
              <div class="msg-bubble ai-bubble">{{ msg.content }}</div>
            </template>
            <!-- 用户消息 -->
            <template v-else>
              <div class="msg-bubble user-bubble">{{ msg.content }}</div>
              <el-avatar class="msg-avatar user-avatar" :size="28">{{ avatarText }}</el-avatar>
            </template>
          </div>
          <!-- AI正在回复 -->
          <div v-if="sending" class="message-row msg-left">
            <el-icon class="msg-avatar ai-avatar" :size="28"><Monitor /></el-icon>
            <div class="msg-bubble ai-bubble typing">
              <span class="dot"></span><span class="dot"></span><span class="dot"></span>
            </div>
          </div>
        </template>
      </div>

      <!-- 底部输入栏 -->
      <div class="input-bar">
        <el-button class="attach-btn" circle disabled title="附件功能（开发中）">
          <el-icon :size="18"><Paperclip /></el-icon>
        </el-button>
        <el-input
          v-model="inputText"
          placeholder="输入消息，可上传图片、音频或视频..."
          class="msg-input"
          @keyup.enter="handleSend"
          :disabled="sending"
        />
        <el-button
          class="send-btn"
          circle
          type="primary"
          :disabled="!inputText.trim() || sending"
          @click="handleSend"
        >
          <el-icon :size="18"><Promotion /></el-icon>
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { Plus, ChatLineSquare, Monitor, Paperclip, Promotion, Loading } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import aiRequest from '@/utils/aiRequest'

const chatList = ref([])
const currentChatId = ref(null)
const messages = ref([])
const inputText = ref('')
const sending = ref(false)
const listLoading = ref(false)
const messagesLoading = ref(false)
const creatingChat = ref(false)
const messageAreaRef = ref(null)

const avatarText = computed(() => {
  const name = (localStorage.getItem('loginUserName') || '').trim()
  return name ? name[0].toUpperCase() : 'U'
})

// 滚动到底部
const scrollToBottom = async () => {
  await nextTick()
  if (messageAreaRef.value) {
    messageAreaRef.value.scrollTop = messageAreaRef.value.scrollHeight
  }
}

// 加载对话列表
const loadChatList = async () => {
  listLoading.value = true
  try {
    const res = await aiRequest.get('/ai/history/chat')
    const data = res.data || res
    if (Array.isArray(data)) {
      chatList.value = data
    } else if (data && Array.isArray(data.data)) {
      chatList.value = data.data
    } else {
      chatList.value = []
    }
  } catch (e) {
    console.error('加载对话列表失败', e)
    chatList.value = []
  } finally {
    listLoading.value = false
  }
}

// 选择对话，加载消息
const selectChat = async (chat) => {
  const chatId = chat.chatId || chat.id
  if (currentChatId.value === chatId) return
  currentChatId.value = chatId
  messagesLoading.value = true
  messages.value = []
  try {
    const res = await aiRequest.get(`/ai/history/chat/${chatId}`)
    const data = res.data || res
    if (Array.isArray(data)) {
      messages.value = data
    } else if (data && Array.isArray(data.data)) {
      messages.value = data.data
    } else if (data && data.messages) {
      messages.value = data.messages
    } else {
      messages.value = []
    }
    scrollToBottom()
  } catch (e) {
    console.error('加载对话消息失败', e)
    ElMessage.error('加载对话消息失败')
  } finally {
    messagesLoading.value = false
  }
}

// 发送消息
const handleSend = async () => {
  const text = inputText.value.trim()
  if (!text || sending.value) return

  // 添加用户消息到界面
  messages.value.push({ role: 'user', content: text })
  inputText.value = ''
  sending.value = true
  scrollToBottom()

  try {
    const formData = new FormData()
    formData.append('prompt', text)

    let url = '/ai/chat'
    if (currentChatId.value) {
      url = `/ai/chat?chatId=${currentChatId.value}`
    }

    const res = await aiRequest.post(url, formData)
    const data = res.data || res

    // 尝试解析AI回复
    let aiContent = ''
    if (typeof data === 'string') {
      aiContent = data
    } else if (data && data.content) {
      aiContent = data.content
    } else if (data && data.message) {
      aiContent = data.message
    } else if (data && data.reply) {
      aiContent = data.reply
    } else {
      aiContent = JSON.stringify(data)
    }

    messages.value.push({ role: 'assistant', content: aiContent })

    // 如果是新对话，从响应中获取chatId并刷新列表
    if (!currentChatId.value) {
      if (data && (data.chatId || data.id)) {
        currentChatId.value = data.chatId || data.id
      }
      await loadChatList()
    }

    scrollToBottom()
  } catch (e) {
    console.error('发送消息失败', e)
    ElMessage.error('发送消息失败，请检查AI服务')
    // 移除用户消息回滚
    messages.value.pop()
  } finally {
    sending.value = false
  }
}

// 新建对话
const handleNewChat = async () => {
  currentChatId.value = null
  messages.value = []
}

onMounted(() => {
  loadChatList()
})
</script>

<style scoped>
.ai-chat-page {
  display: flex;
  height: calc(100vh - 64px - 32px);
  background: #f5f5f5;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

/* === 左侧边栏 === */
.chat-sidebar {
  width: 280px;
  min-width: 280px;
  background: #fff;
  border-right: 1px solid #e8e8e8;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.sidebar-title {
  font-size: 16px;
  font-weight: 700;
  color: #303133;
}

.sidebar-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.chat-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
  margin-bottom: 2px;
}

.chat-item:hover {
  background: #f5f7fa;
}

.chat-item.active {
  background: #ecf5ff;
  color: #409eff;
}

.chat-item-icon {
  flex-shrink: 0;
  font-size: 16px;
  color: #909399;
}

.chat-item.active .chat-item-icon {
  color: #409eff;
}

.chat-item-text {
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.empty-tip {
  text-align: center;
  color: #c0c4cc;
  padding: 40px 0;
  font-size: 14px;
}

/* === 右侧聊天区 === */
.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #fafafa;
}

.message-area {
  flex: 1;
  overflow-y: auto;
  padding: 24px 32px;
}

.loading-wrap {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #909399;
  padding: 60px 0;
}

.empty-messages {
  text-align: center;
  color: #c0c4cc;
  padding: 80px 0;
  font-size: 15px;
}

/* === 消息行 === */
.message-row {
  display: flex;
  align-items: flex-start;
  margin-bottom: 20px;
  gap: 10px;
}

.msg-right {
  justify-content: flex-end;
}

.msg-left {
  justify-content: flex-start;
}

.msg-avatar {
  flex-shrink: 0;
  margin-top: 2px;
}

.ai-avatar {
  background: #f0f0f0;
  border-radius: 6px;
  padding: 4px;
  color: #606266;
}

.user-avatar {
  background: #409eff;
  color: #fff;
  font-size: 13px;
}

.msg-bubble {
  max-width: 65%;
  padding: 10px 16px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.6;
  word-break: break-word;
  white-space: pre-wrap;
}

.ai-bubble {
  background: #fff;
  color: #303133;
  border: 1px solid #e8e8e8;
  border-radius: 2px 12px 12px 12px;
}

.user-bubble {
  background: #409eff;
  color: #fff;
  border-radius: 12px 2px 12px 12px;
}

/* 打字动画 */
.typing {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 14px 20px;
}

.typing .dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #909399;
  animation: bounce 1.4s infinite ease-in-out both;
}

.typing .dot:nth-child(1) { animation-delay: 0s; }
.typing .dot:nth-child(2) { animation-delay: 0.2s; }
.typing .dot:nth-child(3) { animation-delay: 0.4s; }

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0.6); opacity: 0.4; }
  40% { transform: scale(1); opacity: 1; }
}

/* === 底部输入栏 === */
.input-bar {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 24px;
  background: #fff;
  border-top: 1px solid #f0f0f0;
}

.attach-btn {
  flex-shrink: 0;
  border: none;
  color: #909399;
}

.msg-input {
  flex: 1;
}

:deep(.msg-input .el-input__wrapper) {
  border-radius: 20px;
  padding: 6px 16px;
  box-shadow: 0 0 0 1px #dcdfe6 inset;
}

:deep(.msg-input .el-input__wrapper:focus-within) {
  box-shadow: 0 0 0 1px #409eff inset;
}

.send-btn {
  flex-shrink: 0;
}
</style>
