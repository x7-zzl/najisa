<template>
  <div class="gujie-page">
    <div class="filter-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索每日新闻..."
        class="search-input"
        clearable
        @clear="handleSearch"
        @keyup.enter="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>

      <el-button class="publish-btn" circle type="primary" @click="goPublish">
        <el-icon><Plus /></el-icon>
      </el-button>
      </div>

    <div class="content-area">
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="4" animated />
      </div>

      <div v-else-if="articleList.length > 0" class="article-list">
        <div
          v-for="article in articleList"
          :key="article.id"
          class="article-card"
          @click="viewArticle(article)"
        >
          <div class="article-left">
            <div class="article-title-row">
              <span v-if="article.isTop" class="tag top-tag">置顶</span>
              <span v-if="article.isHot" class="tag hot-tag">热</span>
              <h3 class="article-title">{{ article.title }}</h3>
            </div>
            <p class="article-summary">{{ article.summary || '暂无摘要...' }}</p>
          </div>

          <div class="article-right">
            <div class="article-meta">
              <span class="meta-item">作者 {{ article.authorName || '未知用户' }}</span>
              <span class="meta-divider">·</span>
              <span class="meta-item">{{ formatDate(article.createTime) }}</span>
              <span class="meta-divider">·</span>
              <span class="meta-item">浏览 {{ article.viewCount }}</span>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="empty-container">
        <el-empty description="暂无相关帖子" />
      </div>
    </div>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[5, 10, 15, 20]"
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
import { ref, onMounted } from 'vue'
import { Search, Plus } from '@element-plus/icons-vue'
import { getArticleList } from '@/utils/request'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import { useRouter } from 'vue-router'

const loading = ref(false)
const articleList = ref([])
const searchKeyword = ref('')
const router = useRouter()
const isAutoPaging = ref(false)
const pagination = ref({
  pageNum: 1,
  pageSize: 5,
  total: 0
})

// 获取文章列表
const fetchArticles = async () => {
  loading.value = true
  try {
    const res = await getArticleList({
      pageNum: pagination.value.pageNum,
      pageSize: pagination.value.pageSize,
      keyword: searchKeyword.value || undefined
    })
    if (res.code === 200) {
      pagination.value.total = res.data.total || 0

      const pageSize = pagination.value.pageSize || 1
      const maxPage = Math.max(1, Math.ceil(pagination.value.total / pageSize))
      if (!isAutoPaging.value && pagination.value.pageNum > maxPage) {
        isAutoPaging.value = true
        pagination.value.pageNum = maxPage
        await fetchArticles()
        isAutoPaging.value = false
        return
      }

      articleList.value = res.data.records || []
    }
  } catch (error) {
    console.error('获取文章列表失败:', error)
    ElMessage.error('加载失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.value.pageNum = 1
  fetchArticles()
}

// 分页切换
const handleCurrentChange = (val) => {
  pagination.value.pageNum = val
  fetchArticles()
}

const handleSizeChange = (val) => {
  pagination.value.pageSize = val
  pagination.value.pageNum = 1
  fetchArticles()
}

// 进入文章详情 (暂未实现)
const viewArticle = (article) => {
  router.push(`/system/article/${article.id}`)
}

const goPublish = () => {
  router.push('/system/article-publish')
}

// 格式化时间
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return dayjs(dateStr).format('YYYY-MM-DD HH:mm')
}

onMounted(() => {
  fetchArticles()
})
</script>

<style scoped>
.gujie-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 16px;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 100px);
}

/* 筛选栏 - 与天下明蛊一致 */
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

.content-area {
  flex: 1;
  min-height: 0;
  overflow: auto;
  padding-bottom: 3px;
  display: flex;
  flex-direction: column;
  padding-top: 2px;
  padding-right: 4px;
}

.content-area::-webkit-scrollbar {
  width: 1px;
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

/* 加载状态 */
.loading-container {
  text-align: center;
  padding: 32px 20px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  backdrop-filter: blur(10px);
  flex: 1;
}

/* 列表容器 */
.article-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding-bottom: 12px;
  flex: 1;
  min-height: 0;
  justify-content: flex-start;
}

/* 文章卡片 */
.article-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  padding: 12px 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(0, 0, 0, 0.06);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  overflow: hidden;
  min-height: 89px;
}

.article-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.12);
  border-color: rgba(64, 158, 255, 0.35);
  border-left-color: rgba(255, 193, 7, 0.85);
}

.article-left {
  flex: 1;
  min-width: 0;
}

.article-right {
  flex-shrink: 0;
  display: flex;
  align-items: flex-start;
}

.article-title-row {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
  justify-content: flex-start;
  flex-wrap: nowrap;
}

.tag {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 999px;
  font-weight: 700;
  color: #ffffff;
  flex-shrink: 0;
  line-height: 16px;
}

.top-tag {
  background: rgba(144, 147, 153, 0.92);
}

.hot-tag {
  background: rgba(245, 108, 108, 0.92);
}

.article-title {
  font-size: 15px;
  font-weight: 800;
  color: #303133;
  margin: 0;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  min-width: 0;
  flex: 1;
  text-align: left;
}

.article-meta {
  font-size: 12px;
  color: #909399;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 6px;
  justify-content: flex-end;
  text-align: right;
}

.article-summary {
  font-size: 13px;
  color: #606266;
  line-height: 1.6;
  margin: 6px 0 0;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  overflow-wrap: anywhere;
  word-break: break-word;
}

/* 分页 */
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

/* 空状态 */
.empty-container {
  padding: 48px 20px;
  text-align: center;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  backdrop-filter: blur(10px);
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

@media (max-width: 980px) {
  .article-card {
    flex-direction: column;
    gap: 10px;
  }
  .article-meta {
    justify-content: flex-start;
    text-align: left;
  }
}
</style>
