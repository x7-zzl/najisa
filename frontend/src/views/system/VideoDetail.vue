<template>
  <div class="video-detail-page">
    <div class="top-bar">
      <el-button text @click="goBack">返回</el-button>
    </div>

    <div class="content-area">
      <div v-if="loading" class="loading">
        <el-card class="shell" shadow="never">
          <el-skeleton :rows="10" animated />
        </el-card>
      </div>

      <div v-else-if="video" class="layout">
        <div class="left">
          <el-card class="shell player-card" shadow="never">
          <div class="header">
            <div class="title-row">
              <span v-if="video.isNew === 1" class="tag new-tag">新</span>
              <span v-if="video.isHot === 1" class="tag hot-tag">热</span>
              <div class="title">{{ video.title }}</div>
            </div>
              <div class="sub">
                <el-link
                  v-if="video.authorId"
                  type="primary"
                  :underline="false"
                  class="author-link"
                  @click="goAuthorProfile"
                >
                  {{ video.authorName || '用户已注销' }}
                </el-link>
                <span v-else class="muted">{{ video.authorName || '用户已注销' }}</span>
                <span class="dot">·</span>
                <span class="muted">{{ formatDate(video.createTime) }}</span>
              </div>
            </div>

            <div class="player-shell">
              <video v-if="video.videoUrl" class="player" :src="video.videoUrl" controls />
              <el-empty v-else description="暂无视频地址" />
            </div>

            <div class="actions">
              <el-button class="action-pill" plain disabled>点赞 {{ video.likeCount || 0 }}</el-button>
              <el-button class="action-pill" plain disabled>收藏 {{ video.collectCount || 0 }}</el-button>
              <el-button class="action-pill" plain disabled>投币 {{ video.coinCount || 0 }}</el-button>
            </div>
          </el-card>

          <el-card class="shell desc-card" shadow="never">
            <div class="card-title">简介</div>
            <div class="desc">{{ video.intro || '暂无简介' }}</div>
          </el-card>

          <el-card class="shell comment-card" shadow="never">
            <div class="comment-head">
              <div class="card-title">评论</div>
              <div class="comment-sub">功能开发中</div>
            </div>
            <el-input
              type="textarea"
              :autosize="{ minRows: 3, maxRows: 5 }"
              placeholder="发一条友善的评论（暂不可提交）"
              disabled
            />
            <div class="comment-actions">
              <el-button type="primary" disabled>发布评论</el-button>
            </div>
          </el-card>
        </div>

        <div class="right">
          <el-card class="shell author-card" shadow="never">
            <div class="author-main">
              <div class="author-avatar" />
              <div class="author-info">
                <el-link
                  v-if="video.authorId"
                  type="primary"
                  :underline="false"
                  class="author-name"
                  @click="goAuthorProfile"
                >
                  {{ video.authorName || '用户已注销' }}
                </el-link>
                <div v-else class="author-name muted">{{ video.authorName || '用户已注销' }}</div>
                <div class="author-sub muted">点击查看个人信息</div>
              </div>
            </div>

            <div class="author-stats">
              <div class="stat-item">
                <div class="stat-num">{{ video.likeCount || 0 }}</div>
                <div class="stat-label">点赞</div>
              </div>
              <div class="stat-item">
                <div class="stat-num">{{ video.collectCount || 0 }}</div>
                <div class="stat-label">收藏</div>
              </div>
              <div class="stat-item">
                <div class="stat-num">{{ video.coinCount || 0 }}</div>
                <div class="stat-label">投币</div>
              </div>
            </div>
          </el-card>

          <el-card class="shell recommend-card" shadow="never">
            <div class="card-title">推荐视频</div>
            <div v-if="recommendList.length > 0" class="recommend-list">
              <div v-for="v in recommendList" :key="v.id" class="recommend-item" @click="goVideo(v.id)">
                <div class="recommend-cover">
                  <img v-if="v.coverUrl" :src="v.coverUrl" class="recommend-img" />
                  <div v-else class="recommend-placeholder">封面</div>
                </div>
                <div class="recommend-info">
                  <div class="recommend-name">{{ v.title }}</div>
                  <div class="recommend-meta">
                    <span class="muted">{{ v.authorName || '用户已注销' }}</span>
                    <span class="dot">·</span>
                    <span class="muted">{{ formatDate(v.createTime) }}</span>
                  </div>
                </div>
              </div>
            </div>
            <el-empty v-else description="暂无推荐" />
          </el-card>
        </div>
      </div>

      <div v-else class="empty">
        <el-card class="shell" shadow="never">
          <el-empty description="视频不存在或已删除" />
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import dayjs from 'dayjs'
import { ElMessage } from 'element-plus'
import { getVideoById, getVideoList } from '@/utils/request'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const video = ref(null)
const listCache = ref([])

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return dayjs(dateStr).format('YYYY-MM-DD HH:mm')
}

const goBack = () => {
  router.back()
}

const goAuthorProfile = () => {
  if (!video.value?.authorId) return
  router.push({
    path: '/system/user-profile',
    query: { userId: video.value.authorId, readonly: '1' }
  })
}

const goVideo = (id) => {
  router.push(`/system/video/${id}`)
}

const recommendList = computed(() => {
  const id = route.params.id
  return (listCache.value || []).filter(v => String(v.id) !== String(id)).slice(0, 8)
})

const fetchRecommend = async () => {
  try {
    const res = await getVideoList({ pageNum: 1, pageSize: 16 })
    if (res.code === 200) {
      listCache.value = res.data.records || []
    }
  } catch (e) {
    console.error('获取推荐视频失败:', e)
  }
}

const fetchDetail = async () => {
  const id = route.params.id
  if (!id) {
    video.value = null
    return
  }
  loading.value = true
  try {
    const res = await getVideoById({ id })
    if (res.code === 200) {
      video.value = res.data
    } else {
      ElMessage.error(res.message || '加载失败')
      video.value = null
    }
  } catch (e) {
    console.error('加载视频详情失败:', e)
    ElMessage.error('加载失败，请稍后重试')
    video.value = null
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchDetail()
  fetchRecommend()
})

watch(
  () => route.params.id,
  () => {
    fetchDetail()
  }
)
</script>

<style scoped>
.video-detail-page {
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

.shell {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 14px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(0, 0, 0, 0.06);
}

.layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 360px;
  gap: 16px;
  min-height: 0;
}

.left {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.right {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.player-card {
  padding: 14px;
}

.header {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 12px;
}

.title-row {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.tag {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 999px;
  font-weight: 800;
  color: #fff;
  flex-shrink: 0;
  line-height: 16px;
}

.new-tag {
  background: rgba(64, 158, 255, 0.92);
}

.hot-tag {
  background: rgba(245, 108, 108, 0.92);
}

.title {
  font-size: 18px;
  font-weight: 900;
  color: #1f2d3d;
  line-height: 1.35;
}

.sub {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  font-size: 13px;
  color: #909399;
}

.dot {
  color: rgba(144, 147, 153, 0.75);
}

.muted {
  color: #909399;
}

.author-link {
  font-weight: 900;
}

.player-shell {
  border-radius: 14px;
  background: rgba(0, 0, 0, 0.92);
  overflow: hidden;
  box-shadow: 0 8px 18px rgba(0, 0, 0, 0.16);
}

.player {
  width: 100%;
  max-height: 560px;
  display: block;
  background: rgba(0, 0, 0, 0.92);
}

.actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-top: 12px;
}

.action-pill {
  border-radius: 999px;
  font-weight: 800;
}

.desc-card,
.comment-card,
.author-card,
.recommend-card {
  padding: 14px;
}

.card-title {
  font-size: 14px;
  font-weight: 900;
  color: #303133;
  margin-bottom: 10px;
}

.desc {
  font-size: 14px;
  color: #303133;
  line-height: 1.8;
  white-space: pre-wrap;
  overflow-wrap: anywhere;
}

.comment-head {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 10px;
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

.author-main {
  display: flex;
  gap: 12px;
  align-items: center;
}

.author-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: rgba(255, 193, 7, 0.45);
  flex-shrink: 0;
}

.author-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.author-name {
  font-size: 14px;
  font-weight: 900;
}

.author-sub {
  font-size: 12px;
}

.author-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid rgba(0, 0, 0, 0.06);
}

.stat-item {
  text-align: center;
}

.stat-num {
  font-size: 16px;
  font-weight: 900;
  color: #303133;
}

.stat-label {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}

.recommend-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.recommend-item {
  display: flex;
  gap: 10px;
  cursor: pointer;
  padding: 8px;
  border-radius: 12px;
  transition: background 0.2s ease;
}

.recommend-item:hover {
  background: rgba(246, 247, 251, 0.9);
}

.recommend-cover {
  width: 128px;
  height: 72px;
  border-radius: 10px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  overflow: hidden;
  flex-shrink: 0;
}

.recommend-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.recommend-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: #909399;
  font-weight: 800;
}

.recommend-info {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.recommend-name {
  font-size: 13px;
  font-weight: 900;
  color: #303133;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.recommend-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
}

.loading .shell,
.empty .shell {
  padding: 14px;
}

@media (max-width: 1100px) {
  .layout {
    grid-template-columns: 1fr;
    min-height: 0;
  }
}
</style>
