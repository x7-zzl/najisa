<template>
  <div class="detail-page">
    <div class="top-bar">
      <el-button text @click="goBack">返回</el-button>
    </div>

    <div class="content-area">
      <el-card class="detail-card" shadow="never">
        <div v-if="loading" class="loading">
          <el-skeleton :rows="8" animated />
        </div>

        <div v-else-if="article" class="detail">
          <div class="title-row">
            <span v-if="article.isTop" class="tag top-tag">置顶</span>
            <span v-if="article.isHot" class="tag hot-tag">热</span>
            <h2 class="title">{{ article.title }}</h2>
          </div>

          <div class="meta">
            <span class="meta-item">
              <span class="meta-label">作者</span>
              <el-link
                v-if="article.authorId"
                type="primary"
                :underline="false"
                class="author-link"
                @click="goAuthorProfile"
              >
                {{ article.authorName || '用户已注销' }}
              </el-link>
              <span v-else class="meta-value">{{ article.authorName || '用户已注销' }}</span>
            </span>
            <span class="meta-divider">·</span>
            <span class="meta-item meta-date">
              <span class="meta-label">发表于</span>
              <span class="meta-value">{{ formatDate(article.createTime) }}</span>
            </span>
            <span class="meta-divider">·</span>
            <span class="meta-item">
              <span class="meta-label">浏览</span>
              <span class="meta-value">{{ article.viewCount || 0 }}</span>
            </span>
          </div>

          <div class="content">
            {{ article.content || '' }}
          </div>

          <div class="comment-section">
            <div class="comment-header">
              <div class="comment-title">评论区</div>
              <div class="comment-sub">功能开发中</div>
            </div>

            <div class="comment-editor">
              <el-input
                type="textarea"
                :autosize="{ minRows: 3, maxRows: 5 }"
                placeholder="写下你的评论（暂不可提交）"
                disabled
              />
              <div class="comment-actions">
                <el-button type="primary" disabled>发布评论</el-button>
              </div>
            </div>

            <div class="comment-list">
              <div v-for="i in 3" :key="i" class="comment-item">
                <div class="comment-avatar" />
                <div class="comment-body">
                  <div class="comment-line one" />
                  <div class="comment-line two" />
                </div>
              </div>
            </div>
          </div>
        </div>

        <div v-else class="empty">
          <el-empty description="文章不存在或已删除" />
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import dayjs from 'dayjs'
import { ElMessage } from 'element-plus'
import { getArticleById } from '@/utils/request'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const article = ref(null)

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return dayjs(dateStr).format('YYYY-MM-DD HH:mm')
}

const goBack = () => {
  router.back()
}

const goAuthorProfile = () => {
  if (!article.value?.authorId) return
  router.push({
    path: '/system/user-profile',
    query: { userId: article.value.authorId, readonly: '1' }
  })
}

const fetchDetail = async () => {
  const id = route.params.id
  if (!id) {
    article.value = null
    return
  }
  loading.value = true
  try {
    const res = await getArticleById({ id })
    if (res.code === 200) {
      article.value = res.data
    } else {
      ElMessage.error(res.message || '加载失败')
      article.value = null
    }
  } catch (e) {
    console.error('加载文章详情失败:', e)
    ElMessage.error('加载失败，请稍后重试')
    article.value = null
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchDetail()
})
</script>

<style scoped>
.detail-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 16px;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 100px);
  overflow: hidden;
}

.top-bar {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  margin-bottom: 12px;
}

.content-area {
  flex: 1;
  min-height: 0;
  overflow: auto;
  padding-right: 7px;
  padding-top: 2px;
  padding-bottom: 10px;
}

.content-area::-webkit-scrollbar {
  width: 10px;
}

.content-area::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.35);
  border-radius: 999px;
}

.content-area::-webkit-scrollbar-thumb {
  background: linear-gradient(180deg, rgba(255, 213, 79, 0.95), rgba(255, 193, 7, 0.85));
  border-radius: 999px;
  border: 2px solid rgba(255, 255, 255, 0.60);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.12);
}

.content-area::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(180deg, rgba(255, 213, 79, 1), rgba(255, 193, 7, 0.95));
}

.content-area {
  scrollbar-color: rgba(255, 193, 7, 0.85) rgba(255, 255, 255, 0.35);
  scrollbar-width: thin;
}

.detail-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  backdrop-filter: blur(10px);
  border: none;
}

.loading {
  padding: 12px 6px;
}

.title-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.tag {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 999px;
  font-weight: 700;
  color: #fff;
  flex-shrink: 0;
  line-height: 16px;
}

.top-tag {
  background: rgba(144, 147, 153, 0.92);
}

.hot-tag {
  background: rgba(245, 108, 108, 0.92);
}

.title {
  margin: 0;
  font-size: 22px;
  font-weight: 900;
  color: #303133;
  line-height: 1.3;
}

.meta {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  font-size: 13px;
  color: #909399;
  margin-bottom: 16px;
}

.author-link {
  font-weight: 800;
}

.meta-item {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.meta-divider {
  color: rgba(144, 147, 153, 0.75);
}

.meta-label {
  color: rgba(144, 147, 153, 0.9);
}

.meta-value {
  color: #606266;
  font-weight: 700;
}

.meta-date {
  font-variant-numeric: tabular-nums;
}

.content {
  font-size: 14px;
  color: #303133;
  line-height: 1.8;
  white-space: pre-wrap;
  overflow-wrap: anywhere;
  padding: 12px 0 6px;
  border-top: 1px solid rgba(0, 0, 0, 0.06);
}

.comment-section {
  margin-top: 18px;
  padding-top: 14px;
  border-top: 1px solid rgba(0, 0, 0, 0.06);
}

.comment-header {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 12px;
}

.comment-title {
  font-size: 16px;
  font-weight: 900;
  color: #303133;
}

.comment-sub {
  font-size: 12px;
  color: #909399;
}

.comment-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}

.comment-list {
  margin-top: 14px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.comment-item {
  display: flex;
  gap: 12px;
  align-items: flex-start;
  padding: 12px;
  border-radius: 12px;
  background: rgba(246, 247, 251, 0.8);
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.comment-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: rgba(255, 193, 7, 0.45);
  flex-shrink: 0;
}

.comment-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.comment-line {
  height: 10px;
  border-radius: 999px;
  background: rgba(0, 0, 0, 0.07);
}

.comment-line.one {
  width: 46%;
}

.comment-line.two {
  width: 78%;
}

.empty {
  padding: 28px 0;
}
</style>
