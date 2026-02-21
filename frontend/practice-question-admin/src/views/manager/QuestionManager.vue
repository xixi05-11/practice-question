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
        v-model:value="searchForm.content"
        placeholder="内容"
        style="width: 180px"
        allowClear
        @pressEnter="handleSearch"
      />
      <Input
        v-model:value="searchForm.tags"
        placeholder="标签"
        style="width: 180px"
        allowClear
        @pressEnter="handleSearch"
      />
      <Select
        v-model:value="searchForm.difficulty"
        placeholder="难度筛选"
        style="width: 120px"
        allowClear
      >
        <Select.Option value="0">简单</Select.Option>
        <Select.Option value="1">中等</Select.Option>
        <Select.Option value="2">困难</Select.Option>
      </Select>
      <Button type="primary" @click="handleSearch">搜索</Button>
      <Button @click="handleReset">重置</Button>
    </div>
    <Button type="primary" @click="handleAdd">添加题目</Button>
  </div>
  <a-table :columns="columns" :data-source="data" :pagination="pagination" @change="onChange" :scroll="{ x: 1000 }" />
  <Drawer v-model:open="drawerVisible" :title="isEdit ? '编辑题目' : '题目详情'" width="700" @close="handleDrawerClose">
    <template #extra>
      <Space v-if="!isEdit">
        <Button @click="handleEditFromDetail">编辑</Button>
        <Button danger @click="handleDeleteFromDetail">删除</Button>
      </Space>
    </template>
    <div style="display: flex; flex-direction: column; gap: 20px;">
      <div v-if="!isEdit">
        <div style="font-weight: 500; margin-bottom: 8px;">ID</div>
        <div>{{ detailForm.id || '-' }}</div>
      </div>
      <div>
        <div style="font-weight: 500; margin-bottom: 8px;">标题</div>
        <Input v-if="isEdit" v-model:value="detailForm.title" placeholder="请输入标题" />
        <div v-else>{{ detailForm.title || '-' }}</div>
      </div>
      <div>
        <div style="font-weight: 500; margin-bottom: 8px;">难度</div>
        <Select v-if="isEdit" v-model:value="detailForm.difficulty" placeholder="请选择难度" style="width: 100%;">
          <Select.Option :value="0">简单</Select.Option>
          <Select.Option :value="1">中等</Select.Option>
          <Select.Option :value="2">困难</Select.Option>
        </Select>
        <Tag v-else :color="getDifficultyColor(detailForm.difficulty)">{{ getDifficultyText(detailForm.difficulty) }}</Tag>
      </div>
      <div>
        <div style="font-weight: 500; margin-bottom: 8px;">标签</div>
        <Input v-if="isEdit" v-model:value="tagInput" placeholder="请输入标签，用逗号分隔" />
        <div v-else-if="detailForm.tagList && detailForm.tagList.length > 0" style="display: flex; gap: 4px; flex-wrap: wrap;">
          <Tag v-for="tag in detailForm.tagList" :key="tag" color="blue">{{ tag }}</Tag>
        </div>
        <div v-else>-</div>
      </div>
      <div>
        <div style="font-weight: 500; margin-bottom: 8px;">题目内容</div>
        <Input.TextArea v-if="isEdit" v-model:value="detailForm.content" placeholder="请输入内容" :rows="6" />
        <div v-else style="white-space: pre-wrap; background: #f5f5f5; padding: 12px; border-radius: 4px;">{{ detailForm.content || '-' }}</div>
      </div>
      <div>
        <div style="font-weight: 500; margin-bottom: 8px;">答案</div>
        <Input.TextArea v-if="isEdit" v-model:value="detailForm.answer" placeholder="请输入答案" :rows="4" />
        <div v-else style="white-space: pre-wrap; background: #f5f5f5; padding: 12px; border-radius: 4px;">{{ detailForm.answer || '-' }}</div>
      </div>
      <div v-if="isEdit">
        <div style="font-weight: 500; margin-bottom: 8px;">所属题库</div>
        <Transfer
          v-model:target-keys="detailForm.questionBankIdList"
          :data-source="bankList"
          :titles="['可选题库', '已选']"
          :render="item => item.title"
          :one-way-line="false"
          show-search
          :filter-option="(inputValue, item) => item.title.toLowerCase().includes(inputValue.toLowerCase())"
          :show-select-all="false"
          :locale="{ itemUnit: '', itemsUnit: '' }"
          style="display: flex; justify-content: center;"
        />
      </div>
      <div v-else>
        <div style="font-weight: 500; margin-bottom: 8px;">所属题库</div>
        <div v-if="detailForm.questionBank && detailForm.questionBank.length > 0">
          <Tag v-for="bank in detailForm.questionBank" :key="bank" style="margin-bottom: 4px;">{{ bank }}</Tag>
        </div>
        <div v-else>-</div>
      </div>
      <div v-if="!isEdit">
        <div style="font-weight: 500; margin-bottom: 8px;">点赞数</div>
        <div>{{ detailForm.thumbNum || 0 }}</div>
      </div>
      <div v-if="!isEdit">
        <div style="font-weight: 500; margin-bottom: 8px;">创建人</div>
        <div>{{ detailForm.userVO ? (detailForm.userVO.userName || detailForm.userVO.userAccount) : '-' }}</div>
      </div>
      <div v-if="!isEdit">
        <div style="font-weight: 500; margin-bottom: 8px;">创建时间</div>
        <div>{{ formatTime(detailForm.createTime) }}</div>
      </div>
    </div>
    <template #footer v-if="isEdit">
      <Space>
        <Button @click="handleDrawerClose">取消</Button>
        <Button type="primary" @click="handleSave">保存</Button>
      </Space>
    </template>
  </Drawer>
</template>
<script lang="ts" setup>
import { listQuestionVoByPage, deleteQuestion, addQuestion, updateQuestion } from '@/api/managerQuestionController.ts'
import { getQuestionBankList } from '@/api/managerBankController.ts'
import { onMounted, ref, computed, h } from 'vue'
import { Button, message, Input, Space, Select, Tag, Drawer, Transfer } from 'ant-design-vue'
import { formatTime } from '@/tools/formatTime'

const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const data = ref([])
const bankList = ref<any[]>([])

const searchForm = ref({
  title: '',
  content: '',
  tags: '',
  difficulty: undefined as string | undefined,
})

const drawerVisible = ref(false)
const isEdit = ref(false)
const tagInput = ref('')
const detailForm = ref({
  id: 0,
  title: '',
  content: '',
  tagList: [] as string[],
  answer: '',
  difficulty: undefined as number | undefined,
  questionBankIdList: [] as number[],
  questionBank: [] as string[],
  thumbNum: 0,
  userVO: null as any,
  createTime: '',
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
    title: 'ID',
    dataIndex: 'id',
    width: 180,
  },
  {
    title: '标题',
    dataIndex: 'title',
    ellipsis: true,
  },
  {
    title: '难度',
    dataIndex: 'difficulty',
    width: 100,
    customRender: ({ record }) => {
      return h(Tag, { color: getDifficultyColor(record.difficulty) }, getDifficultyText(record.difficulty))
    },
  },
  {
    title: '标签',
    dataIndex: 'tagList',
    width: 150,
    customRender: ({ record }) => {
      if (!record.tagList || record.tagList.length === 0) return '-'
      const tags = record.tagList.slice(0, 2)
      const more = record.tagList.length > 2
      return h('div', { style: { display: 'flex', gap: '4px', flexWrap: 'wrap' } }, 
        [
          ...tags.map((tag: string) => h(Tag, { color: 'blue', size: 'small' }, tag)),
          more ? h(Tag, { size: 'small' }, `+${record.tagList.length - 2}`) : null,
        ]
      )
    },
  },
  {
    title: '题库',
    dataIndex: 'questionBank',
    width: 100,
    customRender: ({ record }) => {
      const count = record.questionBank ? record.questionBank.length : 0
      return count > 0 ? `${count}个` : '-'
    },
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 180,
    customRender: ({ record }) => formatTime(record.createTime),
  },
  {
    title: '操作',
    width: 150,
    fixed: 'right' as const,
    customRender: ({ record }) => {
      return h(Space, {}, () => [
        h(
          Button,
          {
            type: 'link',
            size: 'small',
            onClick: () => handleView(record),
          },
          '详情',
        ),
        h(
          Button,
          {
            type: 'link',
            size: 'small',
            onClick: () => handleEditFromTable(record),
          },
          '编辑',
        ),
        h(
          Button,
          {
            type: 'link',
            size: 'small',
            danger: true,
            onClick: async () => {
              try {
                const res = await deleteQuestion({ id: record.id })
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

function getDifficultyColor(difficulty: number) {
  const colorMap = { 0: 'green', 1: 'orange', 2: 'red' }
  return colorMap[difficulty] || 'default'
}

function getDifficultyText(difficulty: number) {
  const textMap = { 0: '简单', 1: '中等', 2: '困难' }
  return textMap[difficulty] || '-'
}

const getBankList = async () => {
  const res = await getQuestionBankList({ pageNum: 1, pageSize: 1000 })
  if (res.data.code === 0 && res.data.data) {
    bankList.value = res.data.data.records.map((bank: any) => ({
      key: bank.id,
      title: bank.title,
      description: bank.description || '',
    }))
  }
}

const getUserVOList = async () => {
  const params: any = {
    pageNum: currentPage.value,
    pageSize: pageSize.value,
    title: searchForm.value.title || undefined,
    content: searchForm.value.content || undefined,
    tagList: searchForm.value.tags ? searchForm.value.tags.split(',').map(tag => tag.trim()).filter(tag => tag) : undefined,
    difficulty: searchForm.value.difficulty ? Number(searchForm.value.difficulty) : undefined,
  }
  const res = await listQuestionVoByPage(params)
  if (res.data.code === 0 && res.data.data) {
    data.value = res.data.data.records
    total.value = res.data.data.total
  }
}

onMounted(() => {
  getBankList()
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
    content: '',
    tags: '',
    difficulty: undefined,
  }
  currentPage.value = 1
  getUserVOList()
}

function handleAdd() {
  isEdit.value = true
  detailForm.value = {
    id: 0,
    title: '',
    content: '',
    tagList: [],
    answer: '',
    difficulty: undefined,
    questionBankIdList: [],
    questionBank: [],
    thumbNum: 0,
    userVO: null,
    createTime: '',
  }
  tagInput.value = ''
  drawerVisible.value = true
}

function handleView(record) {
  isEdit.value = false
  detailForm.value = {
    id: record.id,
    title: record.title,
    content: record.content,
    tagList: record.tagList || [],
    answer: record.answer,
    difficulty: record.difficulty,
    questionBankIdList: [],
    questionBank: record.questionBank || [],
    thumbNum: record.thumbNum || 0,
    userVO: record.userVO,
    createTime: record.createTime,
  }
  tagInput.value = (record.tagList || []).join(',')
  drawerVisible.value = true
}

function handleEditFromTable(record) {
  isEdit.value = true
  const questionBankIdList: number[] = []
  if (record.questionBank && record.questionBank.length > 0) {
    record.questionBank.forEach((bankName: string) => {
      const bank = bankList.value.find(b => b.title === bankName)
      if (bank) {
        questionBankIdList.push(bank.key)
      }
    })
  }
  detailForm.value = {
    id: record.id,
    title: record.title,
    content: record.content,
    tagList: record.tagList || [],
    answer: record.answer,
    difficulty: record.difficulty,
    questionBankIdList,
    questionBank: record.questionBank || [],
    thumbNum: record.thumbNum || 0,
    userVO: record.userVO,
    createTime: record.createTime,
  }
  tagInput.value = (record.tagList || []).join(',')
  drawerVisible.value = true
}

function handleEditFromDetail() {
  isEdit.value = true
  const questionBankIdList: number[] = []
  if (detailForm.value.questionBank && detailForm.value.questionBank.length > 0) {
    detailForm.value.questionBank.forEach((bankName: string) => {
      const bank = bankList.value.find(b => b.title === bankName)
      if (bank) {
        questionBankIdList.push(bank.key)
      }
    })
  }
  detailForm.value.questionBankIdList = questionBankIdList
}

function handleDeleteFromDetail() {
  deleteQuestion({ id: detailForm.value.id }).then((res) => {
    if (res.data.code === 0 && res.data.data) {
      message.success('删除成功')
      drawerVisible.value = false
      getUserVOList()
    } else {
      message.error(res.data.message || '删除失败')
    }
  }).catch(() => {
    message.error('删除失败')
  })
}

function handleSave() {
  if (!detailForm.value.title) {
    message.warning('请输入标题')
    return
  }
  if (!detailForm.value.content) {
    message.warning('请输入内容')
    return
  }
  
  detailForm.value.tagList = tagInput.value.split(',').map(tag => tag.trim()).filter(tag => tag)
  
  const api = detailForm.value.id ? updateQuestion : addQuestion
  const params = detailForm.value.id
    ? {
        id: detailForm.value.id,
        title: detailForm.value.title,
        content: detailForm.value.content,
        tagList: detailForm.value.tagList,
        answer: detailForm.value.answer,
        difficulty: detailForm.value.difficulty,
        questionBankIdList: detailForm.value.questionBankIdList,
      }
    : {
        title: detailForm.value.title,
        content: detailForm.value.content,
        tagList: detailForm.value.tagList,
        answer: detailForm.value.answer,
        difficulty: detailForm.value.difficulty,
        questionBankIdList: detailForm.value.questionBankIdList,
      }
  console.log('保存的参数:', params)
  console.log('题库ID列表:', detailForm.value.questionBankIdList)
  api(params).then((res) => {
    if (res.data.code === 0) {
      message.success(detailForm.value.id ? '编辑成功' : '添加成功')
      drawerVisible.value = false
      getUserVOList()
    } else {
      message.error(res.data.message || (detailForm.value.id ? '编辑失败' : '添加失败'))
    }
  }).catch(() => {
    message.error(detailForm.value.id ? '编辑失败' : '添加失败')
  })
}

function handleDrawerClose() {
  drawerVisible.value = false
  detailForm.value = {
    id: 0,
    title: '',
    content: '',
    tagList: [],
    answer: '',
    difficulty: undefined,
    questionBankIdList: [],
    questionBank: [],
    thumbNum: 0,
    userVO: null,
    createTime: '',
  }
  tagInput.value = ''
}
</script>
