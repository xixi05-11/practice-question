<template>
  <div id="bank-detail">
    <a-card class="bank-detail-card">
      <a-row>
        <a-col style="margin-right: 30px">
          <a-image :src="bank?.coverUrl" style="height: 100px"></a-image>
        </a-col>
        <a-col>
          <h1 class="bank-title">{{ bank?.title }}</h1>
          <p class="bank-description">{{ bank?.description }}</p>
        </a-col>
      </a-row>
    </a-card>

    <!--题目列表-->
    <a-table
      class="bank-detail-table"
      :columns="columns"
      :row-key="(record: API.QuestionVO) => record.id"
      :data-source="questionList"
      :pagination="paginationProps"
      @change="handleTableChange"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'question'">
          <router-link :to="`/question/${record.id}`">{{ record.title }}</router-link>
        </template>
        <template v-else-if="column.key === 'mark'">
          <p v-if="record.status === 0"></p>
          <p v-else-if="record.status === 1">
            <CheckCircleOutlined style="color: green; margin-right: 5px" />
            已掌握
          </p>
          <p v-else-if="record.status === 2">
            <QuestionCircleOutlined style="color: #c8a125; margin-right: 3px" /> 稍后再刷
          </p>
          <p v-else-if="record.status === 3">
            <CloseCircleOutlined style="color: red; margin-right: 5px" />易错，未掌握
          </p>
        </template>
        <template v-else-if="column.key === 'tagList'">
          <span>
            <a-tag v-for="tag in record.tagList" :key="tag" color="blue">{{ tag }}</a-tag>
          </span>
        </template>
        <template v-else-if="column.key === 'difficulty'">
          <a-tag v-if="record.difficulty === 0" color="green">简单</a-tag>
          <a-tag v-else-if="record.difficulty === 1" color="orange">中等</a-tag>
          <a-tag v-else-if="record.difficulty === 2" color="red">困难</a-tag>
        </template>
        <template v-else-if="column.key === 'thumbNum'">
          <div class="question-thumb" @click="doThumbClick(record)">
            <a-row>
              <a-col>
                <div v-if="thumbLoading.get(record.id)">
                  <a-spin size="small" />
                </div>
                <div v-else-if="record.hasThumb === true">
                  <LikeOutlined style="color: red" />
                </div>
                <div v-else>
                  <LikeOutlined />
                </div>
              </a-col>
              <a-col>
                <div v-if="record.thumbNum < 1000">
                  {{ record.thumbNum }}
                </div>
                <div v-else>{{ (record.thumbNum / 1000).toFixed(1) }}k</div>
              </a-col>
            </a-row>
          </div>
        </template>
        <template v-else>
          {{ record[column.dataIndex] }}
        </template>
      </template>
    </a-table>
  </div>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'
import { getQuestionBankById } from '@/api/questionBankController.ts'
import { message } from 'ant-design-vue'
import { computed, onMounted, reactive, ref } from 'vue'
import {
  CheckCircleOutlined,
  CloseCircleOutlined,
  QuestionCircleOutlined,
  LikeOutlined,
} from '@ant-design/icons-vue'
import { cancelThumb, doThumb, isThumb } from '@/api/thumbController.ts'
// 表格列
const columns = [
  {
    title: '标记',
    key: 'mark',
    dataIndex: 'status',
    width: 100,
  },
  {
    title: '题目',
    key: 'question',
    dataIndex: 'title',
    width: 350,
  },
  {
    title: '难度',
    key: 'difficulty',
    dataIndex: 'difficulty',
    width: 80,
  },
  {
    title: '标签',
    key: 'tagList',
    dataIndex: 'tagList',
    width: 120,
  },
  {
    title: '点赞数',
    key: 'thumbNum',
    dataIndex: 'thumbNum',
    width: 80,
  },
]
const questionList = ref<API.QuestionVO[]>([])
const bank = ref<API.QuestionBankVO>()
const route = useRoute()
const id = route.path.split('/')[2]
const thumbLoading = ref<Map<number, boolean>>(new Map())

//分页参数
interface PaginationState {
  current: number
  pageSize: number
  total: number
}

// 分页状态
const pagination = reactive<PaginationState>({
  current: 1,
  pageSize: 10,
  total: 0,
})
// 获取题库详情
const fetchBankDetail = async () => {
  if (!id) {
    message.error('题库不存在')
    return
  }

  try {
    const res = await getQuestionBankById({
      id: Number(id),
    })
    if (res.data.code === 0 && res.data.data) {
      bank.value = res.data.data
      // 计算总数量用于分页
      const allQuestions = res.data.data.questionVOList || []
      pagination.total = allQuestions.length

      // 批量查询点赞状态
      await fetchThumbStatus(allQuestions)

      // 根据当前页计算应该显示的数据
      const start = (pagination.current - 1) * pagination.pageSize
      const end = start + pagination.pageSize
      questionList.value = allQuestions.slice(start, end)
    } else {
      message.error(res.data.message || '题库不存在')
    }
  } catch (error) {
    console.error('获取题库详情失败:', error)
    message.error('获取题库详情失败')
  }
}

// 批量查询点赞状态
const fetchThumbStatus = async (questions: API.QuestionVO[]) => {
  const thumbPromises = questions.map(async (question) => {
    try {
      const res = await isThumb({
        questionId: question.id,
      })
      if (res.data.code === 0) {
        question.hasThumb = res.data.data
      }
    } catch (error) {
      console.error(`查询题目 ${question.id} 点赞状态失败:`, error)
    }
  })
  await Promise.all(thumbPromises)
}

// 分页配置
const paginationProps = computed(() => ({
  current: pagination.current,
  pageSize: pagination.pageSize,
  total: pagination.total,
  showSizeChanger: true,
  showQuickJumper: true,
  pageSizeOptions: ['5', '10', '20', '50'],
  showTotal: (total: number) => `共 ${total} 条`,
}))

// 表格分页变更处理
const handleTableChange = (page) => {
  pagination.current = page.current
  pagination.pageSize = page.pageSize
  fetchBankDetail() // 重新获取数据
}

// 点赞功能处理函数
// 功能说明：处理题目的点赞/取消点赞操作，包含防重复点击、加载状态显示等功能
// 参数：question - 题目对象，包含 id、hasThumb、thumbNum 等信息
const doThumbClick = async (question: API.QuestionVO) => {
  // 参数校验：确保题目ID存在
  if (!question.id) {
    message.error('题目ID不存在')
    return
  }

  const questionId = question.id

  // 防重复点击：检查当前题目是否正在处理中
  if (thumbLoading.value.get(questionId)) {
    return
  }

  // 保存原始点赞状态
  const originalHasThumb = question.hasThumb

  // 设置加载状态，防止用户重复点击
  thumbLoading.value.set(questionId, true)

  try {
    // 根据当前点赞状态执行不同的操作
    if (originalHasThumb) {
      // 取消点赞逻辑
      const res = await cancelThumb({
        questionId: questionId,
      })
      if (res.data.code === 0) {
        // 找到对应题目并更新状态
        const index = questionList.value.findIndex((item) => item.id === questionId)
        if (index !== -1) {
          // 使用展开运算符创建新对象，触发Vue的响应式更新
          questionList.value[index] = {
            ...questionList.value[index],
            thumbNum: questionList.value[index].thumbNum - 1,
            hasThumb: false,
          }
        }
        message.success('取消点赞成功')
      } else {
        message.error(res.data.message || '取消点赞失败')
      }
    } else {
      // 点赞逻辑
      const res = await doThumb({
        questionId: questionId,
      })
      if (res.data.code === 0) {
        // 找到对应题目并更新状态
        const index = questionList.value.findIndex((item) => item.id === questionId)
        if (index !== -1) {
          // 使用展开运算符创建新对象，触发Vue的响应式更新
          questionList.value[index] = {
            ...questionList.value[index],
            thumbNum: questionList.value[index].thumbNum + 1,
            hasThumb: true,
          }
        }
        message.success('点赞成功')
      } else {
        message.error(res.data.message || '点赞失败')
      }
    }
  } catch (error) {
    console.error('点赞操作失败:', error)
    message.error('操作失败，请重试')
  } finally {
    // 无论成功或失败，都要清除加载状态
    thumbLoading.value.delete(questionId)
  }
}
onMounted(() => {
  if (id) {
    fetchBankDetail()
  }
})
</script>
<style scoped>
.bank-detail-card {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding-top: 1px;
  width: 80%;
  max-width: 1000px;
}
.bank-title {
  font-size: 35px;
  font-weight: bold;
  margin-bottom: 12px;
  color: #1d1d1d;
}
.bank-description {
  font-size: 16px;
  line-height: 1.6;
  color: #595959;
  white-space: pre-wrap; /* 保留换行符 */
}
.bank-detail-table {
  width: 80%;
  margin: 20px auto 0;
}
</style>
