<template>
  <div class="publish-page">
    <el-card class="publish-card" shadow="never">
      <div class="header-row">
        <div class="title">发布文章</div>
        <el-button text @click="goBack">返回</el-button>
      </div>

      <el-form :model="form" label-width="90px">
        <el-form-item>
          <template #label>
            <span class="required-star">*</span>标题
          </template>
          <el-input v-model="form.title" maxlength="80" show-word-limit placeholder="请输入标题" />
        </el-form-item>

        <el-form-item label="摘要">
          <el-input
            v-model="form.summary"
            type="textarea"
            :autosize="{ minRows: 3, maxRows: 5 }"
            maxlength="200"
            show-word-limit
            placeholder="请输入摘要（可选，留空将从正文自动截取）"
          />
        </el-form-item>

        <el-form-item>
          <template #label>
            <span class="required-star">*</span>正文
          </template>
          <el-input
            v-model="form.content"
            type="textarea"
            :autosize="{ minRows: 8, maxRows: 16 }"
            placeholder="请输入正文内容"
          />
        </el-form-item>


        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="submit">发布</el-button>
          <el-button :disabled="submitting" @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { publishArticle } from '@/utils/request'

const router = useRouter()
const submitting = ref(false)

const buildEmptyForm = () => ({
  title: '',
  summary: '',
  content: '',
  isTop: false,
  isHot: false
})

const form = ref(buildEmptyForm())

const goBack = () => {
  router.push('/system/gujie')
}

const reset = () => {
  form.value = buildEmptyForm()
}

const submit = async () => {
  const userId = localStorage.getItem('loginUserId')
  if (!userId) {
    ElMessage.error('请先登录')
    router.push('/')
    return
  }
  if (!form.value.title || !form.value.title.trim()) {
    ElMessage.warning('请输入标题')
    return
  }
  if (!form.value.content || !form.value.content.trim()) {
    ElMessage.warning('请输入正文内容')
    return
  }

  submitting.value = true
  try {
    const payload = {
      title: form.value.title.trim(),
      summary: form.value.summary ? form.value.summary.trim() : '',
      content: form.value.content.trim(),
      authorId: userId,
      isTop: form.value.isTop ? 1 : 0,
      isHot: form.value.isHot ? 1 : 0
    }
    const res = await publishArticle(payload)
    if (res.code === 200) {
      ElMessage.success('发布成功')
      router.push('/system/gujie')
    } else {
      ElMessage.error(res.message || '发布失败')
    }
  } catch (e) {
    console.error('发布失败:', e)
    ElMessage.error('发布失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.publish-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0;
  min-height: calc(100vh - 100px);
}

.publish-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  backdrop-filter: blur(10px);
  border: none;
}

.header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.title {
  font-size: 16px;
  font-weight: 700;
  color: #303133;
}

.flag-row {
  display: flex;
  gap: 16px;
  align-items: center;
}

.required-star {
  color: #f56c6c;
  margin-right: 4px;
  font-weight: 900;
}
</style>
