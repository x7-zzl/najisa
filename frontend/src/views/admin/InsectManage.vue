<template>
  <el-card shadow="never">
    <div class="toolbar">
      <el-input 
        v-model="searchKey" 
        placeholder="蛊虫名称" 
        style="width: 250px;" 
        clearable 
        @clear="handleSearch"
        @keyup.enter="handleSearch"
      >
        <template #append>
          <el-button @click="handleSearch"><el-icon><Search /></el-icon></el-button>
        </template>
      </el-input>
      
      <el-button 
        type="danger" 
        @click="handleBatchDelete" 
        :disabled="selectedRows.length === 0 || enumLoading"
        style="margin-left: 15px;"
      >
        <el-icon style="margin-right: 5px"><Delete /></el-icon>批量删除
      </el-button>
      
      <el-button 
        type="success" 
        @click="handleAdd" 
        :disabled="enumLoading"
        style="margin-left: 15px;"
      >
        <el-icon style="margin-right: 5px"><Plus /></el-icon>新增蛊虫
      </el-button>
    </div>

    <el-table 
      :data="tableData" 
      border 
      style="width: 100%; margin-top: 20px;" 
      v-loading="loading || enumLoading"
      stripe
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      
  <el-table-column prop="name" label="名称" min-width="150">
    <template #default="scope">
      <div class="name-cell" @click="showImagePreview(scope.row)">
        <div class="name-content">
          <span class="name-text">{{ scope.row.name }}</span>
          <el-tooltip 
            v-if="hasImages(scope.row)" 
            content="点击查看图片" 
            placement="top"
          >
            <el-icon class="image-icon">
              <Picture />
            </el-icon>
          </el-tooltip>
        </div>
      </div>
    </template>
  </el-table-column>
      
      <el-table-column prop="guTypeName" label="类别" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.guType === 1 ? 'danger' : 'info'">
            {{ scope.row.guTypeName }}
          </el-tag>
        </template>
      </el-table-column>
      
      <el-table-column prop="genreName" label="流派" width="120" />
      
      <el-table-column prop="guLevelName" label="转数" width="100" />
      
      <el-table-column prop="description" label="描述" min-width="180">
        <template #default="scope">
          <span v-if="scope.row.description" class="description-text">{{ scope.row.description }}</span>
          <span v-else class="empty-text">-</span>
        </template>
      </el-table-column>
      
      <el-table-column prop="inventory" label="数量" width="100" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.inventory > 10 ? 'success' : scope.row.inventory > 0 ? 'warning' : 'danger'">
            <span v-if="scope.row.inventory > 99999999" style="font-size: 20px; font-weight: bold;">∞</span>
            <span v-else>{{ scope.row.inventory }}</span>
          </el-tag>
        </template>
      </el-table-column>
      
      <el-table-column label="操作" width="160" fixed="right" align="center">
        <template #default="scope">
          <div class="action-buttons">
            <el-tooltip content="编辑" placement="top">
              <el-button 
                circle 
                size="small" 
                @click="handleEdit(scope.row)"
                class="edit-btn"
                :disabled="enumLoading"
              >
                <el-icon><Edit /></el-icon>
              </el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button 
                circle 
                size="small" 
                type="danger" 
                @click="handleDelete(scope.row)"
                :loading="deletingId === scope.row.id"
                class="delete-btn"
                :disabled="enumLoading"
              >
                <el-icon><Delete /></el-icon>
              </el-button>
            </el-tooltip>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container">
      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        background
      />
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog 
      :title="isEdit ? '编辑蛊虫' : '新增蛊虫'" 
      v-model="dialogVisible" 
      width="620px"
      destroy-on-close
      @close="handleDialogClose"
    >
      <div v-if="enumLoading" style="text-align: center; padding: 40px 0;">
        <el-icon class="is-loading" style="font-size: 24px;"><Loading /></el-icon>
        <p style="margin-top: 10px; color: #909399;">加载枚举选项中...</p>
      </div>
      
      <el-form :model="form" ref="formRef" label-width="80px" v-else>
        <el-form-item label="名称" required>
          <el-input v-model="form.name" placeholder="请输入蛊虫名称" />
        </el-form-item>
        
        <el-form-item label="类别" required>
          <el-radio-group v-model="form.guType">
            <el-radio 
              v-for="option in enumOptions.guType" 
              :key="option.value"
              :label="option.value"
            >
              {{ option.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="流派" required>
          <el-select v-model="form.genre" placeholder="请选择流派" style="width: 100%">
            <el-option 
              v-for="option in enumOptions.genre" 
              :key="option.value"
              :label="option.label" 
              :value="option.value"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="转数" required>
          <el-select v-model="form.guLevel" placeholder="请选择转数" style="width: 100%">
            <el-option 
              v-for="option in enumOptions.guLevel" 
              :key="option.value"
              :label="option.label" 
              :value="option.value"
            />
          </el-select>
        </el-form-item>

        <!-- 图片上传区域 -->
        <el-form-item label="蛊虫图片">
          <div class="image-upload-container">
            <!-- 图片1 -->
            <div class="image-upload-item">
              <el-upload
                class="avatar-uploader"
                :show-file-list="false"
                :http-request="(options) => handleUpload(options, 1)"
                :before-upload="beforeUpload"
                accept="image/*"
              >
                <img v-if="form.guInsectAvatarOne" :src="form.guInsectAvatarOne" class="avatar" />
                <div v-else class="upload-placeholder">
                  <el-icon class="avatar-uploader-icon"><Plus /></el-icon>
                  <div class="upload-text">图片1</div>
                </div>
              </el-upload>
              <el-button 
                v-if="form.guInsectAvatarOne" 
                size="small" 
                type="danger" 
                @click="removeImage(1)"
                class="remove-btn"
              >
                删除
              </el-button>
            </div>

            <!-- 图片2 -->
            <div class="image-upload-item">
              <el-upload
                class="avatar-uploader"
                :show-file-list="false"
                :http-request="(options) => handleUpload(options, 2)"
                :before-upload="beforeUpload"
                accept="image/*"
              >
                <img v-if="form.guInsectAvatarTwo" :src="form.guInsectAvatarTwo" class="avatar" />
                <div v-else class="upload-placeholder">
                  <el-icon class="avatar-uploader-icon"><Plus /></el-icon>
                  <div class="upload-text">图片2</div>
                </div>
              </el-upload>
              <el-button 
                v-if="form.guInsectAvatarTwo" 
                size="small" 
                type="danger" 
                @click="removeImage(2)"
                class="remove-btn"
              >
                删除
              </el-button>
            </div>

            <!-- 图片3 -->
            <div class="image-upload-item">
              <el-upload
                class="avatar-uploader"
                :show-file-list="false"
                :http-request="(options) => handleUpload(options, 3)"
                :before-upload="beforeUpload"
                accept="image/*"
              >
                <img v-if="form.guInsectAvatarThree" :src="form.guInsectAvatarThree" class="avatar" />
                <div v-else class="upload-placeholder">
                  <el-icon class="avatar-uploader-icon"><Plus /></el-icon>
                  <div class="upload-text">图片3</div>
                </div>
              </el-upload>
              <el-button 
                v-if="form.guInsectAvatarThree" 
                size="small" 
                type="danger" 
                @click="removeImage(3)"
                class="remove-btn"
              >
                删除
              </el-button>
            </div>
          </div>
          <div class="upload-tip">支持jpg、png格式,单张图片不超过10MB</div>
        </el-form-item>
        
        <el-form-item label="描述">
          <el-input 
            v-model="form.description" 
            type="textarea" 
            :autosize="{ minRows: 4, maxRows: 8 }"
            placeholder="请输入蛊虫描述"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button 
          type="primary" 
          @click="submitForm" 
          :loading="submitLoading"
          :disabled="enumLoading"
        >
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 图片预览对话框 -->
    <el-dialog 
      v-model="previewDialogVisible" 
      :title="previewInsectName"
      width="600px"
      destroy-on-close
    >
      <div v-if="previewImages.length === 0" class="no-image-tip">
        <el-empty description="该蛊虫暂无图片" />
      </div>
      <div v-else class="preview-carousel-container">
        <el-carousel 
          :interval="3000" 
          type="card" 
          height="400px"
          arrow="always"
          indicator-position="outside"
        >
          <el-carousel-item v-for="(image, index) in previewImages" :key="index">
            <img :src="image" class="preview-image" />
          </el-carousel-item>
        </el-carousel>
      </div>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Delete, Edit, Plus, Loading } from '@element-plus/icons-vue'

const loading = ref(false)
const enumLoading = ref(false) 
const submitLoading = ref(false)
const deletingId = ref(null)
const tableData = ref([])
const searchKey = ref('')
const dialogVisible = ref(false)
const isEdit = ref(false)
const selectedRows = ref([])

// 图片预览相关
const previewDialogVisible = ref(false)
const previewInsectName = ref('')
const previewImages = ref([])
const originalImages = ref({ 1: '', 2: '', 3: '' })

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const enumOptions = reactive({
  guType: [],
  genre: [],
  guLevel: []
})

const form = reactive({
  id: '',
  name: '',
  guType: 0,
  genre: null,
  guLevel: null,
  description: '',
  guInsectAvatarOne: '',
  guInsectAvatarTwo: '',
  guInsectAvatarThree: ''
})

// 显示图片预览
const showImagePreview = (row) => {
  previewInsectName.value = row.name
  previewImages.value = []
  
  // 收集所有存在的图片URL
  if (row.guInsectAvatarOne) {
    previewImages.value.push(row.guInsectAvatarOne)
  }
  if (row.guInsectAvatarTwo) {
    previewImages.value.push(row.guInsectAvatarTwo)
  }
  if (row.guInsectAvatarThree) {
    previewImages.value.push(row.guInsectAvatarThree)
  }
  
  previewDialogVisible.value = true
}

// 上传前校验 - 修改为10MB
const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过 10MB!')
    return false
  }
  return true
}

// 自定义上传方法
const handleUpload = async (options, index) => {
  const { file } = options
  const formData = new FormData()
  formData.append('file', file)

  try {
    // 调用上传接口
    const res = await request.post('/basicFile/uploadLocalReturnUrl', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })

    if (res.code === 200) {
      const deleteTemp = async (url) => {
        if (!url) return
        if (url === originalImages.value[index]) return
        try {
          await request.post('/basicFile/deleteLocalByUrl', null, { params: { url } })
        } catch (e) { void e }
      }
      // 根据index设置对应的图片URL
      if (index === 1) {
        await deleteTemp(form.guInsectAvatarOne)
        form.guInsectAvatarOne = res.data
      } else if (index === 2) {
        await deleteTemp(form.guInsectAvatarTwo)
        form.guInsectAvatarTwo = res.data
      } else if (index === 3) {
        await deleteTemp(form.guInsectAvatarThree)
        form.guInsectAvatarThree = res.data
      }
      ElMessage.success('图片上传成功')
    } else {
      ElMessage.error(res.message || '图片上传失败')
    }
  } catch (error) {
    console.error('图片上传异常:', error)
    ElMessage.error('图片上传失败')
  }
}

// 删除图片
const removeImage = async (index) => {
  const deleteTemp = async (url) => {
    if (!url) return
    if (url === originalImages.value[index]) return
    try {
      await request.post('/basicFile/deleteLocalByUrl', null, { params: { url } })
    } catch (e) { void e }
  }
  if (index === 1) {
    await deleteTemp(form.guInsectAvatarOne)
    form.guInsectAvatarOne = ''
  } else if (index === 2) {
    await deleteTemp(form.guInsectAvatarTwo)
    form.guInsectAvatarTwo = ''
  } else if (index === 3) {
    await deleteTemp(form.guInsectAvatarThree)
    form.guInsectAvatarThree = ''
  }
  ElMessage.success('图片已删除')
}

// 封装设置默认值的函数
const setFormDefaults = () => {
  // 类别默认选中第一个(通常是凡蛊,value为0)
  if (enumOptions.guType.length > 0) {
    form.guType = enumOptions.guType[0].value
  } else {
    form.guType = 0
  }
  
  // 流派默认选中第一个
  if (enumOptions.genre.length > 0) {
    form.genre = enumOptions.genre[0].value
  }
  
  // 转数默认选中第一个
  if (enumOptions.guLevel.length > 0) {
    form.guLevel = enumOptions.guLevel[0].value
  }
}

const hasImages = (row) => {
  return row.guInsectAvatarOne || row.guInsectAvatarTwo || row.guInsectAvatarThree
}


const fetchEnumOptions = async () => {
  enumLoading.value = true
  try {
    const res = await request.get('/basicGuInsect/getEnumOptions')
    if (res.code === 200) {
      enumOptions.guType = res.data.guType || []
      enumOptions.genre = res.data.genre || []
      enumOptions.guLevel = res.data.guLevel || []
      
      // 只有在非编辑模式下才设置默认值
      if (!isEdit.value) {
        setFormDefaults()
      }
    }
  } finally {
    enumLoading.value = false
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.post('/basicGuInsect/queryListPage', {
      pageNo: pagination.pageNum,
      pageSize: pagination.pageSize,
      keyword: searchKey.value
    })
    if (res.code === 200) {
      tableData.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } finally {
    loading.value = false
  }
}

const handleSearch = () => { pagination.pageNum = 1; fetchData(); }
const handleSizeChange = (val) => { pagination.pageSize = val; fetchData(); }
const handleCurrentChange = (val) => { pagination.pageNum = val; fetchData(); }
const handleSelectionChange = (rows) => { selectedRows.value = rows; }

const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) return
  const insectIds = selectedRows.value.map(row => row.id)
  try {
    await ElMessageBox.confirm('确定要批量删除选中的蛊虫吗?', '警告', { type: 'warning' })
    const res = await request.post('/basicGuInsect/batchDelete', insectIds)
    if (res.code === 200) {
      ElMessage.success('批量删除成功')
      fetchData()
    }
  // eslint-disable-next-line no-empty
  } catch (e) { }
}

const handleAdd = () => {
  isEdit.value = false
  form.id = ''
  form.name = ''
  form.description = ''
  form.guInsectAvatarOne = ''
  form.guInsectAvatarTwo = ''
  form.guInsectAvatarThree = ''
  originalImages.value = { 1: '', 2: '', 3: '' }
  // 确保打开时设置默认值
  setFormDefaults()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, {
    id: row.id,
    name: row.name,
    guType: row.guType,
    genre: row.genre,
    guLevel: row.guLevel,
    description: row.description,
    guInsectAvatarOne: row.guInsectAvatarOne || '',
    guInsectAvatarTwo: row.guInsectAvatarTwo || '',
    guInsectAvatarThree: row.guInsectAvatarThree || ''
  })
  originalImages.value = {
    1: row.guInsectAvatarOne || '',
    2: row.guInsectAvatarTwo || '',
    3: row.guInsectAvatarThree || ''
  }
  dialogVisible.value = true
}

const handleDialogClose = () => {
  const cleanup = async () => {
    const pairs = [
      [1, form.guInsectAvatarOne],
      [2, form.guInsectAvatarTwo],
      [3, form.guInsectAvatarThree]
    ]
    for (const [idx, url] of pairs) {
      if (url && url !== originalImages.value[idx]) {
        try {
          await request.post('/basicFile/deleteLocalByUrl', null, { params: { url } })
        } catch (e) { void e }
      }
    }
  }
  cleanup()
  // 关闭时重置,防止残余数据
  form.id = ''
  form.name = ''
  form.description = ''
  form.guInsectAvatarOne = ''
  form.guInsectAvatarTwo = ''
  form.guInsectAvatarThree = ''
  originalImages.value = { 1: '', 2: '', 3: '' }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除蛊虫【${row.name}】吗?`, '警告', { type: 'warning' })
    const res = await request.post(`/basicGuInsect/deleteById?id=${row.id}`)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchData()
    }
  // eslint-disable-next-line no-empty
  } catch (e) {}
}

const submitForm = async () => {
  if (!form.name?.trim()) return ElMessage.warning('蛊虫名称为必填项')
  
  submitLoading.value = true
  try {
    const url = isEdit.value ? '/basicGuInsect/update' : '/basicGuInsect/add'
    
    // 准备提交的数据
    const submitData = {
      id: form.id,
      name: form.name,
      guType: form.guType,
      genre: form.genre,
      guLevel: form.guLevel,
      description: form.description,
      guInsectAvatarOne: form.guInsectAvatarOne,
      guInsectAvatarTwo: form.guInsectAvatarTwo,
      guInsectAvatarThree: form.guInsectAvatarThree
    }
    
    const res = await request.post(url, submitData)
    if (res.code === 200) {
      ElMessage.success('操作成功')
      dialogVisible.value = false
      fetchData()
    }
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  fetchEnumOptions().then(() => {
    fetchData()
  })
})
</script>

<style scoped>
.toolbar {
  display: flex;
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 8px;
}

.edit-btn {
  background-color: #f0f9ff;
  border-color: #d9ecff;
  color: #409eff;
}

.edit-btn:hover {
  background-color: #409eff;
  border-color: #409eff;
  color: white;
}

.delete-btn:hover {
  background-color: #f56c6c;
  border-color: #f56c6c;
  color: white;
}

.description-text {
  font-size: 12px;
  color: #606266;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.empty-text {
  color: #c0c4cc;
  font-style: italic;
}

/* 蛊虫名称可点击样式 */
.insect-name-link {
  color: #409eff;
  cursor: pointer;
  transition: all 0.3s;
  text-decoration: underline;
  text-decoration-style: dashed;
  text-underline-offset: 3px;
}

.insect-name-link:hover {
  color: #66b1ff;
  font-weight: 500;
}

/* 图片上传样式 */
.image-upload-container {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.image-upload-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.3s;
}

.avatar-uploader:hover {
  border-color: #409eff;
}

.avatar {
  width: 148px;
  height: 148px;
  display: block;
  object-fit: cover;
}

.upload-placeholder {
  width: 148px;
  height: 148px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: #fafafa;
  transition: all 0.3s;
}

.upload-placeholder:hover {
  background-color: #f0f9ff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
}

.upload-text {
  margin-top: 8px;
  font-size: 12px;
  color: #8c939d;
}

.remove-btn {
  width: 100%;
  max-width: 148px;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
  width: 100%;
}

/* 图片预览样式 */
.preview-carousel-container {
  padding: 20px 0;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
  display: block;
}

.no-image-tip {
  padding: 40px 0;
  text-align: center;
}

:deep(.el-carousel__item) {
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table .el-table__cell) {
  padding: 8px 0;
}

:deep(.el-table .el-button--small) {
  padding: 6px;
  font-size: 14px;
}

:deep(.el-table .el-button--small.is-circle) {
  width: 32px;
  height: 32px;
}

:deep(.el-tag) {
  border-radius: 12px;
}

:deep(.el-upload) {
  display: block;
}

.name-cell {
  cursor: pointer;
  padding: 4px 0;
  transition: all 0.3s ease;
}

.name-cell:hover {
  transform: translateX(3px);
}

.name-cell:hover .name-text {
  color: #409eff;
  font-weight: 500;
}

.name-cell:hover .image-icon {
  color: #409eff;
  transform: scale(1.1);
}

.name-content {
  display: flex;
  align-items: center;
  gap: 6px;
}

.name-text {
  color: #333;
  font-weight: 500;
  transition: all 0.3s ease;
  position: relative;
}

.name-text::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0;
  height: 1px;
  background-color: #409eff;
  transition: width 0.3s ease;
}

.name-cell:hover .name-text::after {
  width: 100%;
}

.image-icon {
  font-size: 14px;
  color: #909399;
  transition: all 0.3s ease;
  opacity: 0.8;
}

/* 为有图片的单元格添加特殊样式 */
:deep(.has-images .el-table__cell) {
  background-color: rgba(64, 158, 255, 0.02);
}

/* 添加其他样式保持一致性 */
:deep(.el-table .el-table__cell) {
  padding: 12px 0;
}

:deep(.el-table__row:hover .name-text) {
  color: #409eff;
}
</style>
