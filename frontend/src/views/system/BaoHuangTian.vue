<template>
  <div class="page">
    <div class="page-head">
      <div class="h-title">宝黄天</div>
      <div class="h-desc">图片模块（占位）</div>

      <div class="h-actions">
        <el-input v-model="keyword" placeholder="搜索图片（占位）" clearable class="search">
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
    </div>

    <el-row :gutter="14">
      <el-col :span="6" v-for="img in filteredImages" :key="img.id">
        <el-card shadow="hover" class="img-card">
          <div class="img-box">图片占位</div>
          <div class="img-title">{{ img.title }}</div>
          <div class="img-actions">
            <el-button size="small" @click="mockOpen(img)">查看</el-button>
            <el-button size="small" type="primary" plain @click="mockSave(img)">收藏</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <div class="foot">
      <el-pagination background layout="prev, pager, next" :total="40" :page-size="12" />
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

const keyword = ref('')

const images = ref([
  { id: 1, title: '壁纸（占位）' },
  { id: 2, title: '同人图（占位）' },
  { id: 3, title: '截图（占位）' },
  { id: 4, title: '表情包（占位）' },
  { id: 5, title: '原画（占位）' },
  { id: 6, title: '海报（占位）' },
  { id: 7, title: '设定集（占位）' },
  { id: 8, title: '头像（占位）' }
])

const filteredImages = computed(() => {
  const k = keyword.value.trim()
  if (!k) return images.value
  return images.value.filter((i) => i.title.includes(k))
})

const mockOpen = (img) => ElMessage.success(`查看：${img.title}（占位）`)
const mockSave = (img) => ElMessage.success(`已收藏：${img.title}（占位）`)
</script>

<style scoped>
.page {
  max-width: 1100px;
  margin: 0 auto;
}

.page-head {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 12px;
  gap: 12px;
}

.h-title {
  font-size: 18px;
  font-weight: 900;
  color: #1f2d3d;
}

.h-desc {
  font-size: 13px;
  color: #909399;
  margin-left: 10px;
  flex: 1;
}

.h-actions {
  display: flex;
  justify-content: flex-end;
}

.search {
  width: 260px;
}

.img-card {
  border-radius: 14px;
  margin-bottom: 14px;
}

.img-box {
  height: 140px;
  border-radius: 12px;
  background: #eef1f6;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
  margin-bottom: 10px;
}

.img-title {
  font-weight: 800;
  color: #303133;
  margin-bottom: 10px;
}

.img-actions {
  display: flex;
  gap: 8px;
}

.foot {
  margin-top: 6px;
  display: flex;
  justify-content: center;
}
</style>
