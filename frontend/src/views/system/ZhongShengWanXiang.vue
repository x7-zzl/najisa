<template>
  <div class="zhongsheng-page">
    <div class="filter-bar">
      <el-input
        v-model="searchKey"
        placeholder="搜索视频标题/简介..."
        class="search-input"
        clearable
        @clear="handleSearch"
        @keyup.enter="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
        <template #append>
          <el-button @click="handleSearch">
            <el-icon><Search /></el-icon>
          </el-button>
        </template>
      </el-input>

      <el-button class="publish-btn" type="primary" circle @click="goPublish">
        <el-icon><Plus /></el-icon>
      </el-button>
    </div>

    <div class="content-area">
      <div v-if="loading" class="loading-container">
        <el-icon class="is-loading" style="font-size: 48px; color: #409eff;">
          <Loading />
        </el-icon>
        <p style="margin-top: 16px; color: #909399;">正在加载视频数据...</p>
      </div>

      <div v-else-if="videoList.length > 0" class="video-grid">
        <div v-for="video in videoList" :key="video.id" class="video-card" @click="goVideoDetail(video.id)">
          <div class="card-cover">
            <img v-if="video.coverUrl" :src="video.coverUrl" class="cover-img" />
            <div v-else class="cover-placeholder">
              <div class="cover-text">暂无封面</div>
            </div>
            <div class="card-badges">
              <span v-if="video.isNew === 1" class="badge new-badge">新</span>
              <span v-if="video.isHot === 1" class="badge hot-badge">热</span>
            </div>
          </div>

          <div class="card-content">
            <div class="card-title">{{ video.title }}</div>
            <div class="card-intro">{{ video.intro || '暂无简介' }}</div>
            <div class="card-meta">
              <span class="meta-item">{{ video.authorName || '用户已注销' }}</span>
              <span class="meta-divider">·</span>
              <span class="meta-item">{{ formatDate(video.createTime) }}</span>
            </div>
            <div class="card-stats">
              <span class="stat">赞 {{ video.likeCount || 0 }}</span>
              <span class="stat">藏 {{ video.collectCount || 0 }}</span>
              <span class="stat">币 {{ video.coinCount || 0 }}</span>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="empty-container">
        <el-empty description="暂无视频数据">
          <el-button type="primary" @click="handleSearch">刷新数据</el-button>
        </el-empty>
      </div>
    </div>

    <div v-if="pagination.total > 0" class="pagination-wrapper">
      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[8, 16, 24, 32]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        background
      />
    </div>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Plus, Loading } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { getVideoList } from '@/utils/request'
import { useRouter } from 'vue-router'

const loading = ref(false)
const videoList = ref([])
const searchKey = ref('')

const pagination = reactive({
  pageNum: 1,
  pageSize: 8,
  total: 0
})

const router = useRouter()

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return dayjs(dateStr).format('YYYY-MM-DD HH:mm')
}

const fetchVideos = async () => {
  loading.value = true
  try {
    const res = await getVideoList({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      keyword: searchKey.value || undefined
    })
    if (res.code === 200) {
      pagination.total = res.data.total || 0
      videoList.value = res.data.records || []
    } else {
      ElMessage.error(res.message || '加载失败')
    }
  } catch (e) {
    console.error('获取视频列表失败:', e)
    ElMessage.error('加载失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  fetchVideos()
}

const handleCurrentChange = (val) => {
  pagination.pageNum = val
  fetchVideos()
}

const handleSizeChange = (val) => {
  pagination.pageSize = val
  pagination.pageNum = 1
  fetchVideos()
}

const goVideoDetail = (id) => {
  router.push(`/system/video/${id}`)
}

const goPublish = () => {
  router.push('/system/video-publish')
}

onMounted(() => {
  fetchVideos()
})
</script>

<style scoped>
.zhongsheng-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 16px;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 100px);
  overflow: hidden;
}

.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
  background: rgba(255, 255, 255, 0.95);
  padding: 12px 16px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  backdrop-filter: blur(10px);
}

.search-input {
  flex: 1;
  min-width: 280px;
}

.publish-btn {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.required-star {
  color: #f56c6c;
  margin-right: 4px;
  font-weight: 900;
}

.content-area {
  flex: 1;
  min-height: 0;
  overflow: auto;
  padding-bottom: 12px;
  display: flex;
  flex-direction: column;
  padding-top: 4px;
  padding-right: 4px;
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

.loading-container {
  text-align: center;
  padding: 60px 20px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  backdrop-filter: blur(10px);
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 12px;
  flex: 1;
  align-content: start;
}

.video-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
  backdrop-filter: blur(10px);
  height: 280px;
  display: flex;
  flex-direction: column;
}

.video-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.12);
}

.card-cover {
  position: relative;
  height: 145px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  overflow: hidden;
  flex-shrink: 0;
}

.cover-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-placeholder {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
}

.cover-text {
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 1px;
}

.card-badges {
  position: absolute;
  top: 10px;
  right: 10px;
  display: flex;
  gap: 8px;
}

.badge {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 999px;
  font-weight: 700;
  color: #fff;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.12);
  backdrop-filter: blur(10px);
}

.hot-badge {
  background: rgba(245, 108, 108, 0.92);
}

.new-badge {
  background: rgba(64, 158, 255, 0.92);
}

.card-content {
  padding: 12px;
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.card-title {
  font-size: 14px;
  font-weight: 800;
  color: #303133;
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-intro {
  font-size: 13px;
  color: #606266;
  line-height: 1.6;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  flex: 1;
  overflow-wrap: anywhere;
}

.card-meta {
  font-size: 12px;
  color: #909399;
  margin-top: 6px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 6px;
}

.meta-divider {
  color: rgba(144, 147, 153, 0.75);
}

.card-stats {
  display: flex;
  gap: 10px;
  margin-top: 6px;
  font-size: 12px;
  color: #409eff;
  font-weight: 700;
}

.empty-container {
  padding: 60px 20px;
  text-align: center;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  backdrop-filter: blur(10px);
  display: flex;
  flex-direction: column;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.pagination-wrapper {
  align-self: center;
  display: inline-flex;
  justify-content: center;
  padding: 10px 14px;
  margin-top: 12px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(0, 0, 0, 0.06);
  max-width: 100%;
}

:deep(.el-pagination) {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 8px;
}

:deep(.el-pagination__total),
:deep(.el-pagination__jump) {
  color: #606266;
}

:deep(.el-pagination.is-background .btn-prev),
:deep(.el-pagination.is-background .btn-next),
:deep(.el-pagination.is-background .el-pager li) {
  border-radius: 8px;
}

:deep(.el-pagination__sizes .el-select) {
  width: 120px;
}

@media (max-width: 980px) {
  .video-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .video-card {
    height: 300px;
  }
}

@media (max-width: 720px) {
  .video-grid {
    grid-template-columns: 1fr;
    min-height: 0;
  }
  .search-input {
    min-width: 100%;
  }
}
</style>
