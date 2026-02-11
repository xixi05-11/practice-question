<template>
  <div style="margin-bottom: 16px; display: flex; gap: 12px; align-items: center; flex-wrap: wrap; justify-content: space-between;">
    <div style="display: flex; gap: 12px; align-items: center; flex-wrap: wrap;">
      <Input
        v-model:value="searchForm.title"
        placeholder="标题"
        style="width: 180px"
        allowClear
        @pressEnter="handleSearch"
      />
      <Input
        v-model:value="searchForm.description"
        placeholder="描述"
        style="width: 180px"
        allowClear
        @pressEnter="handleSearch"
      />
      <Button type="primary" @click="handleSearch">搜索</Button>
      <Button @click="handleReset">重置</Button>
    </div>
    <Button type="primary" @click="handleAdd">添加题库</Button>
  </div>
  <a-table :columns="columns" :data-source="data" :pagination="pagination" @change="onChange" />
  <Modal v-model:open="addModalVisible" :title="isEdit ? '编辑题库' : '添加题库'" @ok="handleAddConfirm" @cancel="handleAddCancel" width="380px">
    <div style="display: flex; flex-direction: column; gap: 20px; padding: 8px 0;">
      <div>
        <div style="margin-bottom: 8px; font-weight: 500;">标题</div>
        <Input v-model:value="addForm.title" placeholder="请输入标题" />
      </div>
      <div>
        <div style="margin-bottom: 8px; font-weight: 500;">描述</div>
        <Input.TextArea v-model:value="addForm.description" placeholder="请输入描述" :rows="4" />
      </div>
      <div>
        <div style="margin-bottom: 8px; font-weight: 500;">封面</div>
        <div style="display: flex; flex-direction: column; align-items: center; gap: 12px;">
          <Upload
            :before-upload="beforeUpload"
            :custom-request="handleUpload"
            :show-upload-list="false"
            accept="image/*"
          >
            <Button :icon="h(UploadOutlined)">点击上传</Button>
          </Upload>
          <div v-if="addForm.coverUrl" style="display: flex; justify-content: center; margin-top: 8px;">
            <Image
              :src="addForm.coverUrl"
              :width="120"
              :height="120"
              :preview="true"
              style="object-fit: cover; border-radius: 8px; cursor: pointer;"
            />
          </div>
        </div>
      </div>
    </div>
  </Modal>
</template>
<script lang="ts" setup>
import { getQuestionBankList, deleteQuestionBank, addQuestionBank, editQuestionBank, uploadQuestionBank } from '@/api/managerBankController.ts'
import { onMounted, ref, computed, h } from 'vue'
import { Image, Tooltip, Button, message, Input, Modal, Space, Upload } from 'ant-design-vue'
import type { UploadProps } from 'ant-design-vue'
import { UploadOutlined } from '@ant-design/icons-vue'
import { formatTime } from '@/tools/formatTime'

const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const data = ref([])

const searchForm = ref({
  title: '',
  description: '',
})

const addModalVisible = ref(false)
const isEdit = ref(false)
const addForm = ref({
  id: 0,
  title: '',
  description: '',
  coverUrl: '',
})

const pagination = computed(() => ({
  current: currentPage.value,
  pageSize: pageSize.value,
  total: total.value,
  showSizeChanger: true,
  showTotal: (total: number) => `共 ${total} 条`,
  pageSizeOptions: ['5', '10', '20', '50'],
}))

const columns = computed(() => [
  {
    title: 'id',
    dataIndex: 'id',
  },
  {
    title: '标题',
    dataIndex: 'title',
  },
  {
    title: '描述',
    dataIndex: 'description',
    customRender: ({ record }) => {
      if (!record.description) return '-'
      const text = record.description.length > 10 ? record.description.slice(0, 10) + '...' : record.description
      return h(Tooltip, { title: record.description }, () => text)
    },
  },
  {
    title: '封面',
    dataIndex: 'coverUrl',
    customRender: ({ record }) => {
      if (!record.coverUrl) return '-'
      return h(Image, {
        src: record.coverUrl,
        width: 50,
        height: 50,
        preview: true,
        style: {
          borderRadius: '8px',
          objectFit: 'cover',
          cursor: 'pointer',
        },
      })
    },
  },
  {
    title: '创建人',
    dataIndex: 'userVO',
    customRender: ({ record }) => {
      if (!record.userVO) return '-'
      const user = record.userVO
      const tooltipContent = `ID: ${user.id || '-'}\n账号: ${user.userAccount || '-'}\n昵称: ${user.userName || '-'}\n邮箱: ${user.email || '-'}\n权限: ${user.role || '-'}\n创建时间: ${user.createTime || '-'}`
      return h(Tooltip, { title: tooltipContent, overlayStyle: { whiteSpace: 'pre-wrap' } }, () => user.userName || user.userAccount || '-')
    },
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    customRender: ({ record }) => formatTime(record.createTime),
  },
  {
    title: '操作',
    customRender: ({ record }) => {
      return h(Space, {}, () => [
        h(
          Button,
          {
            type: 'primary',
            size: 'small',
            onClick: () => handleEdit(record),
          },
          '编辑',
        ),
        h(
          Button,
          {
            type: 'primary',
            size: 'small',
            danger: true,
            onClick: async () => {
              try {
                const res = await deleteQuestionBank({ id: record.id })
                if (res.data.code === 0 && res.data.data) {
                  message.success('删除成功')
                  getUserVOList()
                } else {
                  message.error(res.data.message || '删除失败')
                }
              } catch (error) {
                message.error('删除失败')
              }
            },
          },
          '删除',
        ),
      ])
    },
  },
])

const getUserVOList = async () => {
  const params: any = {
    pageNum: currentPage.value,
    pageSize: pageSize.value,
    title: searchForm.value.title || undefined,
    description: searchForm.value.description || undefined,
  }
  const res = await getQuestionBankList(params)
  if (res.data.code === 0 && res.data.data) {
    data.value = res.data.data.records
    total.value = res.data.data.total
  }
}

onMounted(() => {
  getUserVOList()
})

function onChange(pagination, filters, sorter, extra) {
  currentPage.value = pagination.current
  pageSize.value = pagination.pageSize
  getUserVOList()
}

function handleSearch() {
  currentPage.value = 1
  getUserVOList()
}

function handleReset() {
  searchForm.value = {
    title: '',
    description: '',
  }
  currentPage.value = 1
  getUserVOList()
}

function handleAdd() {
  isEdit.value = false
  addForm.value = {
    id: 0,
    title: '',
    description: '',
    coverUrl: '',
  }
  addModalVisible.value = true
}

function handleEdit(record) {
  isEdit.value = true
  addForm.value = {
    id: record.id,
    title: record.title,
    description: record.description,
    coverUrl: record.coverUrl,
  }
  addModalVisible.value = true
}

function handleAddConfirm() {
  if (!addForm.value.title) {
    message.warning('请输入标题')
    return
  }
  const api = isEdit.value ? editQuestionBank : addQuestionBank
  const params = isEdit.value
    ? {
        id: addForm.value.id,
        title: addForm.value.title,
        description: addForm.value.description,
        coverUrl: addForm.value.coverUrl,
      }
    : {
        title: addForm.value.title,
        description: addForm.value.description,
        coverUrl: addForm.value.coverUrl,
      }
  api(params).then((res) => {
    if (res.data.code === 0) {
      message.success(isEdit.value ? '编辑成功' : '添加成功')
      addModalVisible.value = false
      addForm.value = {
        id: 0,
        title: '',
        description: '',
        coverUrl: '',
      }
      getUserVOList()
    } else {
      message.error(res.data.message || (isEdit.value ? '编辑失败' : '添加失败'))
    }
  }).catch(() => {
    message.error(isEdit.value ? '编辑失败' : '添加失败')
  })
}

function handleAddCancel() {
  addModalVisible.value = false
  addForm.value = {
    id: 0,
    title: '',
    description: '',
    coverUrl: '',
  }
}

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    message.error('只能上传图片文件!')
    return false
  }
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isLt5M) {
    message.error('图片大小不能超过5MB!')
    return false
  }
  return true
}

const handleUpload: UploadProps['customRequest'] = async (options) => {
  const { file, onSuccess, onError } = options
  const formData = new FormData()
  formData.append('file', file)

  try {
    const res = await uploadQuestionBank(formData)
    if (res.data.code === 0 && res.data.data) {
      addForm.value.coverUrl = res.data.data
      message.success('上传成功')
      onSuccess?.(res.data.data)
    } else {
      message.error(res.data.message || '上传失败')
      onError?.(new Error('上传失败'))
    }
  } catch (error) {
    message.error('上传失败')
    onError?.(error as Error)
  }
}
</script>
