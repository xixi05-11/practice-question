<template>
  <div style="margin-bottom: 16px; display: flex; gap: 12px; align-items: center; flex-wrap: wrap;">
    <Input
      v-model:value="searchForm.userAccount"
      placeholder="账号"
      style="width: 180px"
      allowClear
      @pressEnter="handleSearch"
    />
    <Input
      v-model:value="searchForm.userName"
      placeholder="昵称"
      style="width: 180px"
      allowClear
      @pressEnter="handleSearch"
    />
    <Input
      v-model:value="searchForm.email"
      placeholder="邮箱"
      style="width: 180px"
      allowClear
      @pressEnter="handleSearch"
    />
    <Select
      v-model:value="searchForm.userRole"
      placeholder="权限筛选"
      style="width: 120px"
      allowClear
    >
      <Select.Option value="admin">管理员</Select.Option>
      <Select.Option value="user">普通用户</Select.Option>
    </Select>
    <Button type="primary" @click="handleSearch">搜索</Button>
    <Button @click="handleReset">重置</Button>
  </div>
  <a-table :columns="columns" :data-source="data" :pagination="pagination" @change="onChange" />
</template>
<script lang="ts" setup>
import { listUserVoByPage, resetPassword } from '@/api/managerUserController.ts'
import { onMounted, ref, computed, h } from 'vue'
import { Image, Tag, Tooltip, Button, message, Input, Select } from 'ant-design-vue'
import { formatTime } from '@/tools/formatTime'

const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const data = ref([])

const searchForm = ref({
  userAccount: '',
  userName: '',
  email: '',
  userRole: undefined as string | undefined,
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
    title: '账号',
    dataIndex: 'userAccount',
  },
  {
    title: '邮箱',
    dataIndex: 'email',
  },
  {
    title: '昵称',
    dataIndex: 'userName',
  },
  {
    title: '头像',
    dataIndex: 'avatar',
    customRender: ({ record }) => {
      return h(Image, {
        src: record.avatar,
        width: 50,
        height: 50,
        preview: true,
        style: {
          borderRadius: '50%',
          objectFit: 'cover',
          cursor: 'pointer',
        },
      })
    },
  },
  {
    title: '简介',
    dataIndex: 'profile',
    customRender: ({ record }) => {
      if (!record.profile) return '-'
      const text = record.profile.length > 10 ? record.profile.slice(0, 10) + '...' : record.profile
      return h(Tooltip, { title: record.profile }, () => text)
    },
  },
  {
    title: '权限',
    dataIndex: 'role',
    customRender: ({ record }) => {
      return h(Tag, { color: record.role === 'admin' ? 'red' : 'blue' }, record.role)
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
      return h(
        Button,
        {
          type: 'primary',
          size: 'small',
          onClick: async () => {
            try {
              const res = await resetPassword({ id: record.id })
              if (res.data.code === 0 && res.data.data) {
                message.success('重置密码成功')
              } else {
                message.error(res.data.message || '重置密码失败')
              }
            } catch (error) {
              message.error('重置密码失败')
            }
          },
        },
        '重置密码',
      )
    },
  },
])

const getUserVOList = async () => {
  const params: any = {
    pageNum: currentPage.value,
    pageSize: pageSize.value,
    userAccount: searchForm.value.userAccount || undefined,
    userName: searchForm.value.userName || undefined,
    email: searchForm.value.email || undefined,
    userRole: searchForm.value.userRole || undefined,
  }
  const res = await listUserVoByPage(params)
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
    userAccount: '',
    userName: '',
    email: '',
    userRole: undefined,
  }
  currentPage.value = 1
  getUserVOList()
}
</script>
