<template>
  <div class="insect-detail-page">
    <div class="top-bar">
      <el-button text @click="goBack">返回</el-button>
    </div>

    <div class="content-area">
      <el-card class="shell" shadow="never">
        <div v-if="loading" class="loading">
          <el-skeleton :rows="12" animated />
        </div>

        <div v-else-if="insect" class="detail">
          <div class="header">
            <div class="title-row">
              <div class="title">{{ insect.name }}</div>
              <el-tag v-if="insect.guTypeName" :type="insect.guType === 1 ? 'danger' : 'info'" size="large">
                {{ insect.guTypeName }}
              </el-tag>
            </div>

            <div class="meta">
              <span class="meta-item">
                <span class="meta-label">流派</span>
                <span class="meta-value">{{ insect.genreName || '-' }}</span>
              </span>
              <span class="meta-divider">·</span>
              <span class="meta-item">
                <span class="meta-label">转数</span>
                <span class="meta-value">{{ insect.guLevelName || '-' }}</span>
              </span>
              <span class="meta-divider">·</span>
              <span class="meta-item">
                <span class="meta-label">库存</span>
                <span class="meta-value">
                  <span v-if="(insect.inventory || 0) > 99999999">∞</span>
                  <span v-else>{{ insect.inventory ?? 0 }}</span>
                </span>
              </span>
            </div>
          </div>

          <div class="layout">
            <div class="left">
              <div class="media-shell">
                <el-carousel
                  v-if="images.length > 0"
                  :interval="3500"
                  type="card"
                  height="360px"
                  arrow="always"
                >
                  <el-carousel-item v-for="(img, idx) in images" :key="idx">
                    <img :src="img" class="media-img" />
                  </el-carousel-item>
                </el-carousel>
                <div v-else class="no-media">
                  <el-icon style="font-size: 84px; color: #dcdfe6;"><Picture /></el-icon>
                  <div class="no-media-text">暂无图片</div>
                </div>
              </div>

              <div class="desc-card">
                <div class="section-title">蛊虫描述</div>
                <div class="desc-content">{{ insect.description || '暂无描述' }}</div>
              </div>
            </div>

            <div class="right">
              <div class="info-card">
                <div class="section-title">详细信息</div>
                <div class="info-grid">
                  <div class="info-item">
                    <div class="info-label">蛊虫类别</div>
                    <div class="info-value">
                      <el-tag :type="insect.guType === 1 ? 'danger' : 'info'" size="large">
                        {{ insect.guTypeName || '-' }}
                      </el-tag>
                    </div>
                  </div>
                  <div class="info-item">
                    <div class="info-label">蛊虫流派</div>
                    <div class="info-value">
                      <el-tag type="warning" size="large">{{ insect.genreName || '-' }}</el-tag>
                    </div>
                  </div>
                  <div class="info-item">
                    <div class="info-label">蛊虫转数</div>
                    <div class="info-value">
                      <el-tag type="success" size="large">{{ insect.guLevelName || '-' }}</el-tag>
                    </div>
                  </div>
                  <div class="info-item">
                    <div class="info-label">库存数量</div>
                    <div class="info-value">
                      <el-tag :type="inventoryTagType" size="large" class="inventory-tag">
                        <span v-if="(insect.inventory || 0) > 99999999">∞</span>
                        <span v-else>{{ insect.inventory ?? 0 }}</span>
                      </el-tag>
                    </div>
                  </div>
                  <div class="info-item full">
                    <div class="info-label">创建时间</div>
                    <div class="info-text">{{ formatDateTime(insect.createTime) || '-' }}</div>
                  </div>
                  <div class="info-item full">
                    <div class="info-label">更新时间</div>
                    <div class="info-text">{{ formatDateTime(insect.updateTime) || '-' }}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div v-else class="empty">
          <el-empty description="蛊虫不存在或已删除" />
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '@/utils/request'
import dayjs from 'dayjs'
import { ElMessage } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const insect = ref(null)

const images = computed(() => {
  const i = insect.value
  if (!i) return []
  const arr = []
  if (i.guInsectAvatarOne) arr.push(i.guInsectAvatarOne)
  if (i.guInsectAvatarTwo) arr.push(i.guInsectAvatarTwo)
  if (i.guInsectAvatarThree) arr.push(i.guInsectAvatarThree)
  return arr
})

const inventoryTagType = computed(() => {
  const v = insect.value?.inventory ?? 0
  if (v > 99999999) return 'warning'
  if (v > 10) return 'success'
  if (v > 0) return 'warning'
  return 'danger'
})

const formatDateTime = (dateStr) => {
  if (!dateStr) return ''
  return dayjs(dateStr).format('YYYY-MM-DD HH:mm')
}

const goBack = () => {
  router.back()
}

const fetchDetail = async () => {
  const id = route.params.id
  if (!id) {
    insect.value = null
    return
  }
  loading.value = true
  try {
    const res = await request.get('/basicGuInsect/queryById', { params: { id } })
    if (res.code === 200) {
      insect.value = res.data
    } else {
      ElMessage.error(res.message || '加载失败')
      insect.value = null
    }
  } catch (e) {
    console.error('加载蛊虫详情失败:', e)
    ElMessage.error('加载失败，请稍后重试')
    insect.value = null
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchDetail()
})

watch(
  () => route.params.id,
  () => {
    fetchDetail()
  }
)
</script>

<style scoped>
.insect-detail-page {
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

.shell {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 14px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(0, 0, 0, 0.06);
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

.detail {
  padding: 14px;
}

.header {
  padding-bottom: 14px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  margin-bottom: 14px;
}

.title-row {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  margin-bottom: 8px;
}

.title {
  font-size: 20px;
  font-weight: 900;
  color: #1f2d3d;
  line-height: 1.35;
}

.meta {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  font-size: 13px;
  color: #909399;
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
  font-weight: 800;
}

.layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 360px;
  gap: 14px;
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

.media-shell {
  border-radius: 14px;
  background: rgba(246, 247, 251, 0.85);
  border: 1px solid rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.media-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  background: rgba(246, 247, 251, 0.9);
}

.no-media {
  height: 360px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  color: #909399;
}

.no-media-text {
  font-size: 13px;
  font-weight: 800;
}

.desc-card,
.info-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 14px;
  border: 1px solid rgba(0, 0, 0, 0.06);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  backdrop-filter: blur(10px);
  padding: 14px;
}

.section-title {
  font-size: 14px;
  font-weight: 900;
  color: #303133;
  margin-bottom: 10px;
}

.desc-content {
  font-size: 14px;
  color: #303133;
  line-height: 1.8;
  white-space: pre-wrap;
  overflow-wrap: anywhere;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.info-item.full {
  grid-column: 1 / -1;
}

.info-label {
  font-size: 12px;
  color: #909399;
  font-weight: 800;
}

.info-text {
  font-size: 14px;
  color: #303133;
  font-weight: 800;
  font-variant-numeric: tabular-nums;
}

.inventory-tag {
  font-variant-numeric: tabular-nums;
}

.empty,
.loading {
  padding: 14px;
}

@media (max-width: 1100px) {
  .layout {
    grid-template-columns: 1fr;
    min-height: 0;
  }
  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>
