<template>
  <div class="tianxia-page">
    <!-- 搜索和筛选栏 -->
    <div class="filter-bar">
      <el-input
        v-model="searchKey"
        placeholder="搜索蛊虫名称..."
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

      <el-select
        v-model="filterType"
        placeholder="蛊虫类别"
        clearable
        @change="handleSearch"
        class="filter-select"
      >
        <el-option label="全部类别" :value="null" />
        <el-option label="凡蛊" :value="0" />
        <el-option label="仙蛊" :value="1" />
      </el-select>

      <el-select
        v-model="filterGenre"
        placeholder="蛊虫流派"
        clearable
        @change="handleSearch"
        class="filter-select"
      >
        <el-option label="全部流派" :value="null" />
        <el-option
          v-for="option in genreOptions"
          :key="option.value"
          :label="option.label"
          :value="option.value"
        />
      </el-select>

      <el-select
        v-model="filterLevel"
        placeholder="蛊虫转数"
        clearable
        @change="handleSearch"
        class="filter-select"
      >
        <el-option label="全部转数" :value="null" />
        <el-option
          v-for="option in levelOptions"
          :key="option.value"
          :label="option.label"
          :value="option.value"
        />
      </el-select>
    </div>

    <div class="content-area">
      <div v-if="loading" class="loading-container">
        <el-icon class="is-loading" style="font-size: 48px; color: #409eff;">
          <Loading />
        </el-icon>
        <p style="margin-top: 16px; color: #909399;">正在加载蛊虫数据...</p>
      </div>

      <div v-else-if="insectList.length > 0" class="insect-grid">
      <div
        v-for="insect in insectList"
        :key="insect.id"
        class="insect-card"
        @click="goInsectDetail(insect.id)"
      >
        <!-- 图片轮播区域 -->
        <div class="card-image-wrapper">
          <el-carousel
            v-if="getInsectImages(insect).length > 0"
            :interval="4000"
            height="160px"
            indicator-position="inside"
            arrow="hover"
            @click.stop
          >
            <el-carousel-item
              v-for="(img, idx) in getInsectImages(insect)"
              :key="idx"
            >
              <img :src="img" class="card-image" />
            </el-carousel-item>
          </el-carousel>
          <div v-else class="card-no-image">
            <el-icon style="font-size: 48px; color: #dcdfe6;">
              <Picture />
            </el-icon>
            <p>暂无图片</p>
          </div>

          <!-- 类别标签 -->
          <div class="card-type-badge" :class="'type-' + insect.guType">
            {{ insect.guTypeName }}
          </div>
        </div>

        <!-- 卡片内容 -->
        <div class="card-content">
          <h3 class="card-title">{{ insect.name }}</h3>

          <div class="card-info">
            <div class="info-item">
              <el-icon class="info-icon" style="color: #e6a23c;">
                <Star />
              </el-icon>
              <span class="info-label">流派:</span>
              <span class="info-value">{{ insect.genreName }}</span>
            </div>

            <div class="info-item">
              <el-icon class="info-icon" style="color: #67c23a;">
                <TrophyBase />
              </el-icon>
              <span class="info-label">转数:</span>
              <span class="info-value">{{ insect.guLevelName }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

      <div v-else class="empty-container">
      <el-empty description="暂无蛊虫数据">
        <el-button type="primary" @click="handleSearch">刷新数据</el-button>
      </el-empty>
    </div>
    </div>

    <!-- 分页 -->
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
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import {
  Search,
  Loading,
  Picture,
  Star,
  TrophyBase
} from '@element-plus/icons-vue'

const loading = ref(false)
const insectList = ref([])
const searchKey = ref('')
const filterType = ref(null)
const filterGenre = ref(null)
const filterLevel = ref(null)
const isAutoPaging = ref(false)
const router = useRouter()

// 枚举选项
const genreOptions = ref([])
const levelOptions = ref([])

const pagination = reactive({
  pageNum: 1,
  pageSize: 8,
  total: 0
})

// 获取蛊虫图片列表
const getInsectImages = (insect) => {
  const images = []
  if (insect.guInsectAvatarOne) images.push(insect.guInsectAvatarOne)
  if (insect.guInsectAvatarTwo) images.push(insect.guInsectAvatarTwo)
  if (insect.guInsectAvatarThree) images.push(insect.guInsectAvatarThree)
  return images
}

// 加载枚举选项
const fetchEnumOptions = async () => {
  try {
    const res = await request.get('/basicGuInsect/getEnumOptions')
    if (res.code === 200) {
      genreOptions.value = res.data.genre || []
      levelOptions.value = res.data.guLevel || []
    }
  } catch (error) {
    console.error('加载枚举选项失败:', error)
  }
}

// 加载蛊虫列表
const fetchInsectList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      keyword: searchKey.value || undefined
    }

    // 添加筛选条件
    if (filterType.value !== null) {
      params.guType = filterType.value
    }
    if (filterGenre.value !== null) {
      params.genre = filterGenre.value
    }
    if (filterLevel.value !== null) {
      params.guLevel = filterLevel.value
    }

    const res = await request.post('/basicGuInsect/queryListPage', params)

    if (res.code === 200) {
      insectList.value = res.data.records || []
      pagination.total = res.data.total || 0

      const pageSize = pagination.pageSize || 1
      const maxPage = Math.max(1, Math.ceil(pagination.total / pageSize))
      if (!isAutoPaging.value && pagination.pageNum > maxPage) {
        isAutoPaging.value = true
        pagination.pageNum = maxPage
        await fetchInsectList()
        isAutoPaging.value = false
        return
      }
    } else {
      ElMessage.error(res.message || '加载失败')
    }
  } catch (error) {
    console.error('加载蛊虫列表失败:', error)
    ElMessage.error('网络连接失败,请检查后端服务')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  fetchInsectList()
}

// 分页
const handleSizeChange = (val) => {
  pagination.pageSize = val
  pagination.pageNum = 1
  fetchInsectList()
}

const handleCurrentChange = (val) => {
  pagination.pageNum = val
  fetchInsectList()
}

const goInsectDetail = (id) => {
  router.push(`/system/insect/${id}`)
}

// 初始化
onMounted(() => {
  fetchEnumOptions()
  fetchInsectList()
})
</script>

<style scoped>
/* 页面容器 - 统一样式 */
.tianxia-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 16px;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 100px);
  overflow: hidden;
}

/* 内容区滚动，分页固定在底部 */
.content-area {
  flex: 1;
  min-height: 0;
  overflow: auto;
  padding-bottom: 10px;
  display: flex;
  flex-direction: column;
  padding-top: 2px;
  padding-right: 7px;
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

/* 筛选栏 - 紧凑样式 */
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
  min-width: 250px;
}

.filter-select {
  width: 160px;
}

/* 加载状态 */
.loading-container {
  text-align: center;
  padding: 60px 20px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.insect-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 12px;
  flex: 1;
  align-content: start;
}

/* 蛊虫卡片 - 缩小高度 */
.insect-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
  backdrop-filter: blur(10px);
  height: 260px;
  display: flex;
  flex-direction: column;
}

.insect-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.12);
}

/* 图片区域 - 缩小高度 */
.card-image-wrapper {
  position: relative;
  height: 145px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  overflow: hidden;
  flex-shrink: 0;
}

.card-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.card-no-image {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
}

.card-no-image p {
  margin-top: 6px;
  font-size: 13px;
}

/* 类别标签 - 缩小 */
.card-type-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 700;
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.12);
}

.card-type-badge.type-0 {
  background: rgba(144, 147, 153, 0.9);
  color: white;
}

.card-type-badge.type-1 {
  background: rgba(245, 108, 108, 0.9);
  color: white;
}

/* 卡片内容 - 优化间距 */
.card-content {
  padding: 12px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.card-title {
  font-size: 15px;
  font-weight: 700;
  color: #303133;
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
  flex: 1;
}

.info-item {
  display: flex;
  align-items: center;
  font-size: 13px;
}

.info-icon {
  margin-right: 6px;
  font-size: 15px;
}

.info-label {
  color: #909399;
  margin-right: 6px;
  font-weight: 500;
}

.info-value {
  color: #606266;
  font-weight: 600;
}

/* 空状态 */
.empty-container {
  padding: 60px 20px;
  text-align: center;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  backdrop-filter: blur(10px);
  display: flex;
  flex-direction: column;
  justify-content: center;
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

/* 响应式布局 */
@media (max-width: 1400px) {
  .insect-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 1024px) {
  .insect-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .insect-grid {
    grid-template-columns: 1fr;
  }

  .filter-bar {
    flex-direction: column;
  }

  .search-input,
  .filter-select {
    width: 100%;
  }
}

/* 确保每页显示8个卡片的自适应高度 */
@media (min-width: 1025px) {
  .insect-grid {
    /* 4列布局,每页2行,共8个卡片 */
    grid-template-columns: repeat(4, 1fr);
  }
}
</style>
