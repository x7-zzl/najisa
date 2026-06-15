<template>
  <div class="publish-page">
    <div class="top-bar">
      <el-button text @click="goBack">返回</el-button>
    </div>

    <div class="content-area">
      <el-card class="publish-card" shadow="never">
        <div class="header-row">
          <div class="title">发布视频</div>
        </div>

        <el-form :model="form" label-width="90px">
          <el-form-item>
            <template #label>
              <span class="required-star">*</span>标题
            </template>
            <el-input v-model="form.title" maxlength="200" show-word-limit placeholder="请输入标题" />
          </el-form-item>

          <el-form-item label="简介">
            <el-input
              v-model="form.intro"
              type="textarea"
              :autosize="{ minRows: 3, maxRows: 6 }"
              maxlength="1000"
              show-word-limit
              placeholder="请输入简介（可选）"
            />
          </el-form-item>

          <el-form-item label="封面">
            <el-upload
              class="upload"
              action=""
              :show-file-list="false"
              :http-request="uploadCover"
              :before-upload="beforeCoverUpload"
            >
              <img v-if="form.coverUrl" :src="form.coverUrl" class="upload-preview" />
              <div v-else class="upload-placeholder">上传封面</div>
            </el-upload>
          </el-form-item>

          <el-form-item>
            <template #label>
              <span class="required-star">*</span>视频
            </template>
            <el-upload
              class="upload"
              action=""
              :show-file-list="false"
              :http-request="uploadVideo"
              :before-upload="beforeVideoUpload"
            >
              <div class="upload-placeholder">{{ form.videoUrl ? '已上传，点击可重新上传' : '上传视频' }}</div>
            </el-upload>
          </el-form-item>

          <el-form-item v-if="form.videoUrl">
            <template #label>
              <span class="preview-label">预览</span>
            </template>
            <video class="video-preview" :src="form.videoUrl" controls />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" :loading="submitting" @click="submit">发布</el-button>
            <el-button :disabled="submitting" @click="reset">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { publishVideo, uploadFile, deleteLocalFileByUrl } from '@/utils/request'

const router = useRouter()
const submitting = ref(false)
const saved = ref(false)

const form = reactive({
  title: '',
  intro: '',
  coverUrl: '',
  videoUrl: ''
})

const reset = async () => {
  if (submitting.value) return
  await cleanupTemp()
  form.title = ''
  form.intro = ''
  form.coverUrl = ''
  form.videoUrl = ''
  saved.value = false
}

const goBack = async () => {
  if (!saved.value) {
    await cleanupTemp()
  }
  router.back()
}

const beforeCoverUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('封面大小不能超过 10MB')
    return false
  }
  return true
}

const beforeVideoUpload = (file) => {
  const isVideo = file.type.startsWith('video/')
  const isLt500M = file.size / 1024 / 1024 < 500
  if (!isVideo) {
    ElMessage.error('只能上传视频文件')
    return false
  }
  if (!isLt500M) {
    ElMessage.error('视频大小不能超过 500MB')
    return false
  }
  return true
}

const uploadCover = async (options) => {
  try {
    const res = await uploadFile(options.file)
    if (res.code === 200) {
      const oldUrl = form.coverUrl
      const newUrl = res.data
      if (oldUrl && oldUrl !== newUrl) {
        await deleteLocalFileByUrl(oldUrl)
      }
      form.coverUrl = newUrl
      ElMessage.success('封面上传成功')
    } else {
      ElMessage.error(res.message || '封面上传失败')
    }
  } catch (e) {
    console.error('封面上传失败:', e)
    ElMessage.error('封面上传失败')
  }
}

const uploadVideo = async (options) => {
  try {
    const res = await uploadFile(options.file)
    if (res.code === 200) {
      const oldUrl = form.videoUrl
      const newUrl = res.data
      if (oldUrl && oldUrl !== newUrl) {
        await deleteLocalFileByUrl(oldUrl)
      }
      form.videoUrl = newUrl
      ElMessage.success('视频上传成功')
    } else {
      ElMessage.error(res.message || '视频上传失败')
    }
  } catch (e) {
    console.error('视频上传失败:', e)
    ElMessage.error('视频上传失败')
  }
}

const cleanupTemp = async () => {
  const urls = [form.coverUrl, form.videoUrl].filter(Boolean)
  for (const url of urls) {
    try {
      await deleteLocalFileByUrl(url)
    } catch (e) {
      void e
    }
  }
}

const submit = async () => {
  const userId = localStorage.getItem('loginUserId')
  if (!userId) {
    ElMessage.error('请先登录')
    router.push('/')
    return
  }
  if (!form.title || !form.title.trim()) {
    ElMessage.warning('请输入标题')
    return
  }
  if (!form.videoUrl) {
    ElMessage.warning('请先上传视频')
    return
  }
  submitting.value = true
  try {
    const res = await publishVideo({
      title: form.title.trim(),
      intro: form.intro ? form.intro.trim() : '',
      authorId: userId,
      videoUrl: form.videoUrl,
      coverUrl: form.coverUrl
    })
    if (res.code === 200) {
      saved.value = true
      ElMessage.success('发布成功')
      router.push('/system/zhongsheng')
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

onUnmounted(async () => {
  if (saved.value) return
  await cleanupTemp()
})
</script>

<style scoped>
.publish-page {
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
  font-weight: 800;
  color: #303133;
}

.required-star {
  color: #f56c6c;
  margin-right: 4px;
  font-weight: 900;
}

.preview-label {
  color: #909399;
  font-weight: 800;
}

.upload {
  display: inline-flex;
}

.upload-placeholder {
  width: 240px;
  height: 44px;
  border-radius: 12px;
  border: 1px dashed rgba(0, 0, 0, 0.18);
  color: #606266;
  background: rgba(246, 247, 251, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
}

.upload-preview {
  width: 240px;
  height: 136px;
  border-radius: 12px;
  object-fit: cover;
  border: 1px solid rgba(0, 0, 0, 0.08);
}

.video-preview {
  width: 100%;
  max-height: 520px;
  border-radius: 12px;
  background: rgba(0, 0, 0, 0.92);
}
</style>

