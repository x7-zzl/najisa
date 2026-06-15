<template>
  <el-card shadow="never">
    <div class="toolbar">
      <el-input 
        v-model="searchKey" 
        placeholder="用户名/昵称/手机号" 
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
        :disabled="selectedRows.length === 0"
        style="margin-left: 15px;"
      >
        <el-icon style="margin-right: 5px"><Delete /></el-icon>批量删除
      </el-button>
    </div>

    <el-table 
      :data="tableData" 
      border 
      style="width: 100%; margin-top: 20px;" 
      v-loading="loading"
      stripe
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      
      <el-table-column prop="userName" label="用户名" min-width="120" />
      <el-table-column prop="nickName" label="昵称" min-width="120" />
      
      <el-table-column prop="roleName" label="角色身份" width="120" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.roleId === 1 ? 'danger' : 'success'" effect="plain">
            {{ scope.row.roleName }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="isEnabled" label="账号状态" width="100" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.isEnabled === 1 ? 'danger' : 'success'">
            {{ scope.row.isEnabledStatus }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="phoneNumber" label="手机号" width="130" />
      <el-table-column prop="emailAddress" label="邮箱" min-width="160" show-overflow-tooltip />
      <el-table-column prop="createTime" label="创建时间" width="160" />

      <el-table-column label="操作" width="120" fixed="right" align="center">
        <template #default="scope">
          <div class="action-buttons">
            <el-tooltip content="修改信息" placement="top">
              <el-button 
                circle 
                size="small" 
                @click="handleEdit(scope.row)"
                class="edit-btn"
              >
                <el-icon><Edit /></el-icon>
              </el-button>
            </el-tooltip>
            <el-tooltip content="删除用户" placement="top">
              <el-button 
                circle 
                size="small" 
                type="danger" 
                @click="handleDelete(scope.row)"
                :loading="deletingId === scope.row.id"
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

    <el-dialog 
      title="修改用户信息" 
      v-model="dialogVisible" 
      width="500px"
      destroy-on-close
    >
      <el-form :model="form" label-width="90px" style="padding-right: 20px;">
        <el-form-item label="用户名">
          <el-input v-model="form.userName" disabled />
        </el-form-item>
        
        <el-form-item label="角色身份">
          <el-input v-model="form.roleName" disabled />
          <small style="color: #F56C6C;">角色身份由系统分配，不可修改</small>
        </el-form-item>

        <el-form-item label="账号状态">
          <el-radio-group v-model="form.isEnabled">
            <el-radio :label="0">正常</el-radio>
            <el-radio :label="1">禁用</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="昵称">
          <el-input v-model="form.nickName" placeholder="请输入昵称" />
        </el-form-item>
        
        <el-form-item label="手机号">
          <el-input v-model="form.phoneNumber" placeholder="请输入手机号" />
        </el-form-item>
        
        <el-form-item label="邮箱">
          <el-input v-model="form.emailAddress" placeholder="请输入邮箱" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitLoading">确定保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Delete, Edit } from '@element-plus/icons-vue'

const loading = ref(false)
const submitLoading = ref(false)
const deletingId = ref(null)
const tableData = ref([])
const searchKey = ref('')
const dialogVisible = ref(false)
const selectedRows = ref([])

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 表单对象
const form = reactive({
  id: '',
  userName: '',
  roleName: '',
  isEnabled: 0,
  nickName: '',
  phoneNumber: '',
  emailAddress: ''
})

// 获取列表数据
const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.post('/basicUser/queryListPage', {
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

const handleSearch = () => {
  pagination.pageNum = 1
  fetchData()
}

const handleSizeChange = (val) => {
  pagination.pageSize = val
  fetchData()
}

const handleCurrentChange = (val) => {
  pagination.pageNum = val
  fetchData()
}

const handleSelectionChange = (rows) => {
  selectedRows.value = rows
}

// 批量删除
const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) return
  const ids = selectedRows.value.map(row => row.id)
  try {
    await ElMessageBox.confirm(`确定要批量删除选中的 ${ids.length} 个用户吗？`, '警告', {
      type: 'warning',
      confirmButtonText: '确定删除',
      cancelButtonText: '取消'
    })
    const res = await request.post('/basicUser/batchDelete', ids)
    if (res.code === 200) {
      ElMessage.success('批量删除成功')
      fetchData()
    }
  // eslint-disable-next-line no-empty
  } catch (e) {}
}

// 打开修改弹窗
const handleEdit = (row) => {
  // 将行数据拷贝到表单
  Object.assign(form, {
    id: row.id,
    userName: row.userName,
    roleName: row.roleName,
    isEnabled: row.isEnabled, // 0或1
    nickName: row.nickName,
    phoneNumber: row.phoneNumber,
    emailAddress: row.emailAddress
  })
  dialogVisible.value = true
}

// 删除单条
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除用户【${row.userName}】吗？`, '警告', {
      type: 'warning'
    })
    deletingId.value = row.id
    const res = await request.post(`/basicUser/deleteById?id=${row.id}`)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchData()
    }
  // eslint-disable-next-line no-empty
  } catch (e) {} finally {
    deletingId.value = null
  }
}

// 提交表单
const submitForm = async () => {
  submitLoading.value = true
  try {
    // 构造提交数据（排除掉不可修改的 roleName，或者原样传回）
    const res = await request.post('/basicUser/update', form)
    if (res.code === 200) {
      ElMessage.success('修改成功')
      dialogVisible.value = false
      fetchData()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('Update Error:', error)
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  fetchData()
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

:deep(.el-tag) {
  border-radius: 4px;
}
</style>